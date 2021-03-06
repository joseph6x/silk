package controllers.workspace

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, FileInputStream}
import java.net.URL

import controllers.core.{Stream, Widgets}
import models.JsonError
import org.silkframework.config._
import org.silkframework.runtime.activity.Activity
import org.silkframework.runtime.plugin.PluginRegistry
import org.silkframework.runtime.resource.{InMemoryResourceManager, UrlResource}
import org.silkframework.runtime.serialization.Serialization
import org.silkframework.workspace.activity.{ProjectActivity, TaskActivity, ProjectExecutor}
import org.silkframework.workspace.io.{SilkConfigExporter, SilkConfigImporter, WorkspaceIO}
import org.silkframework.workspace.xml.XmlWorkspaceProvider
import org.silkframework.workspace.{Project, Task, User}
import play.api.libs.iteratee.Enumerator
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

object WorkspaceApi extends Controller {

  def projects = Action {
    Ok(JsonSerializer.projectsJson)
  }

  def newProject(project: String) = Action {
    if(User().workspace.projects.exists(_.name == project)) {
      Conflict(JsonError(s"Project with name '$project' already exists. Creation failed."))
    } else {
      val newProject = User().workspace.createProject(project)
      Created(JsonSerializer.projectJson(newProject))
    }
  }

  def deleteProject(project: String) = Action {
    User().workspace.removeProject(project)
    Ok
  }

  def importProject(project: String) = Action { implicit request => {
    for(data <- request.body.asMultipartFormData;
        file <- data.files) {
      // Read the project from the received file
      val inputStream = new FileInputStream(file.ref.file)
      if(file.filename.endsWith(".zip")) {
        // We assume that this is a project using the default XML serialization.
        val xmlWorkspace = new XmlWorkspaceProvider(new InMemoryResourceManager())
        xmlWorkspace.importProject(project, inputStream)
        WorkspaceIO.copyProjects(xmlWorkspace, User().workspace.provider)
        User().workspace.reload()
      } else {
        // Try to import the project using the current workspaces import mechanism (if overloaded)
        User().workspace.importProject(project, inputStream)
      }
      inputStream.close()
    }
    Ok
  }}

  def exportProject(project: String) = Action {
    // Export the project into a byte array
    val outputStream = new ByteArrayOutputStream()
    val fileName = User().workspace.exportProject(project, outputStream)
    val bytes = outputStream.toByteArray
    outputStream.close()

    Ok(bytes).withHeaders("Content-Disposition" -> s"attachment; filename=$fileName")
  }

  def executeProject(projectName: String) = Action {
    val project = User().workspace.project(projectName)
    implicit val prefixes = project.config.prefixes
    implicit val resources = project.resources

    val projectExecutors = PluginRegistry.availablePlugins[ProjectExecutor]
    if(projectExecutors.isEmpty)
      BadRequest("No project executor available")
    else {
      val projectExecutor = projectExecutors.head()
      Activity(projectExecutor.apply(project)).start()
      Ok
    }
  }

  def importLinkSpec(projectName: String) = Action { implicit request => {
    val project = User().workspace.project(projectName)
    implicit val resources = project.resources

    request.body match {
      case AnyContentAsMultipartFormData(data) =>
        for(file <- data.files) {
          val config = Serialization.fromXml[LinkingConfig](scala.xml.XML.loadFile(file.ref.file))
          SilkConfigImporter(config, project)
        }
        Ok
      case AnyContentAsXml(xml) =>
        val config = Serialization.fromXml[LinkingConfig](xml.head)
        SilkConfigImporter(config, project)
        Ok
      case _ =>
        UnsupportedMediaType("Link spec must be provided either as Multipart form data or as XML. Please set the Content-Type header accordingly, e.g. to application/xml")
    }
  }}

  def exportLinkSpec(projectName: String, taskName: String) = Action {
    val project = User().workspace.project(projectName)
    val task = project.task[LinkSpecification](taskName)
    implicit val prefixes = project.config.prefixes

    val silkConfig = SilkConfigExporter.build(project, task.data)

    Ok(Serialization.toXml(silkConfig))
  }

  def updatePrefixes(project: String) = Action { implicit request => {
    val prefixMap = request.body.asFormUrlEncoded.getOrElse(Map.empty).mapValues(_.mkString)
    val projectObj = User().workspace.project(project)
    projectObj.config = projectObj.config.copy(prefixes = Prefixes(prefixMap))

    Ok
  }}

  def getResources(projectName: String) = Action {
    val project = User().workspace.project(projectName)

    Ok(JsonSerializer.projectResources(project))
  }

  def getResource(projectName: String, resourceName: String) = Action {
    val project = User().workspace.project(projectName)
    val resource = project.resources.get(resourceName)
    val enumerator = Enumerator.fromStream(resource.load)

    Ok.chunked(enumerator).withHeaders("Content-Disposition" -> "attachment")
  }

  def putResource(projectName: String, resourceName: String) = Action { implicit request => {
    val project = User().workspace.project(projectName)
    val resource = project.resources.get(resourceName)

    request.body match {
      case AnyContentAsMultipartFormData(formData) if formData.files.nonEmpty =>
        try {
          val file = formData.files.head.ref.file
          val inputStream = new FileInputStream(file)
          resource.write(inputStream)
          inputStream.close()
          Ok
        } catch {
          case ex: Exception => BadRequest(JsonError(ex))
        }
      case AnyContentAsMultipartFormData(formData) if formData.dataParts.contains("resource-url") =>
        try {
          val dataParts = formData.dataParts("resource-url")
          val url = dataParts.head
          val urlResource = UrlResource(new URL(url))
          val inputStream = urlResource.load
          resource.write(inputStream)
          inputStream.close()
          Ok
        } catch {
          case ex: Exception => BadRequest(JsonError(ex))
        }
      case AnyContentAsRaw(buffer) =>
        val bytes = buffer.asBytes().getOrElse(Array[Byte]())
        resource.write(bytes)
        Ok
      case _ =>
        // Put empty resource
        resource.write(Array[Byte]())
        Ok
    }
  }}

  def deleteResource(projectName: String, resourceName: String) = Action {
    val project = User().workspace.project(projectName)
    project.resources.delete(resourceName)

    Ok
  }

  def getProjectActivities(projectName: String) = Action {
    val project = User().workspace.project(projectName)
    Ok(JsonSerializer.projectActivities(project))
  }

  def getTaskActivities(projectName: String, taskName: String) = Action {
    val project = User().workspace.project(projectName)
    val task = project.anyTask(taskName)
    Ok(JsonSerializer.taskActivities(task))
  }

  def startActivity(projectName: String, taskName: String, activityName: String, blocking: Boolean) = Action { request =>
    val project = User().workspace.project(projectName)
    val config = activityConfig(request)
    val activityControl =
      if(taskName.nonEmpty) {
        val activity = project.anyTask(taskName).activity(activityName)
        if(config.nonEmpty)
          activity.update(config)
        activity.control
      } else {
        val activity = project.activity(activityName)
        if(config.nonEmpty)
          activity.update(config)
        activity.control
      }

    if(activityControl.status().isRunning) {
      BadRequest(JsonError(s"Cannot start activity '$activityName'. Already running."))
    } else {
      if(blocking)
        activityControl.startBlocking()
      else
        activityControl.start()
      Ok
    }
  }

  def cancelActivity(projectName: String, taskName: String, activityName: String) = Action {
    val project = User().workspace.project(projectName)
    val activity =
      if(taskName.nonEmpty) {
        val task = project.anyTask(taskName)
        task.activity(activityName).control
      } else {
        project.activity(activityName).control
      }

    activity.cancel()
    Ok
  }

  def restartActivity(projectName: String, taskName: String, activityName: String) = Action {
    val project = User().workspace.project(projectName)
    val activity =
      if(taskName.nonEmpty) {
        val task = project.anyTask(taskName)
        task.activity(activityName).control
      } else {
        project.activity(activityName).control
      }

    activity.reset()
    activity.start()
    Ok
  }

  def getActivityConfig(projectName: String, taskName: String, activityName: String) = Action {
    val project = User().workspace.project(projectName)
    val activityConfig =
      if(taskName.nonEmpty) {
        val task = project.anyTask(taskName)
        task.activity(activityName).config
      } else {
        project.activity(activityName).config
      }

    Ok(JsonSerializer.activityConfig(activityConfig))
  }

  def postActivityConfig(projectName: String, taskName: String, activityName: String) = Action { request =>
    val config = activityConfig(request)
    if (config.nonEmpty) {
      val project = User().workspace.project(projectName)
      if (taskName.nonEmpty) {
        val task = project.anyTask(taskName)
        task.activity(activityName).update(config)
      } else {
        project.activity(activityName).update(config)
      }
      Ok
    } else {
      BadRequest(JsonError("No config supplied."))
    }
  }

  def getActivityStatus(projectName: String, taskName: String, activityName: String) = Action {
    val project = User().workspace.project(projectName)
    if(taskName.nonEmpty) {
      val task = project.anyTask(taskName)
      val activity = task.activity(activityName)
      Ok(JsonSerializer.activityStatus(projectName, taskName, activityName, activity.status))
    } else {
      val activity = project.activity(activityName)
      Ok(JsonSerializer.activityStatus(projectName, taskName, activityName, activity.status))
    }
  }

  def activityUpdates(projectName: String, taskName: String, activityName: String) = Action {
    val projects =
      if (projectName.nonEmpty) User().workspace.project(projectName) :: Nil
      else User().workspace.projects

    def tasks(project: Project) =
      if (taskName.nonEmpty) project.anyTask(taskName) :: Nil
      else project.allTasks

    def projectActivities(project: Project) =
      if (taskName.nonEmpty) Nil
      else project.activities

    def taskActivities(task: Task[_]) =
      if (activityName.nonEmpty) task.activity(activityName) :: Nil
      else task.activities

    val projectActivityStreams =
      for (project <- projects; activity <- projectActivities(project)) yield
        Widgets.statusStream(Enumerator(activity.status) andThen Stream.status(activity.control.status), project = project.name, task = "", activity = activity.name)

    val taskActivityStreams =
      for (project <- projects;
           task <- tasks(project);
           activity <- taskActivities(task)) yield
        Widgets.statusStream(Enumerator(activity.status) andThen Stream.status(activity.control.status), project = project.name, task = task.name, activity = activity.name)

    Ok.chunked(Enumerator.interleave(projectActivityStreams ++ taskActivityStreams))
  }

  private def activityConfig(request: Request[AnyContent]): Map[String, String] = {
    request.body match {
      case AnyContentAsFormUrlEncoded(values) =>
        values.mapValues(_.head)
      case _ =>
        request.queryString.mapValues(_.head)
    }
  }

}