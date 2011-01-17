package de.fuberlin.wiwiss.silk.workbench.lift.snippet

import de.fuberlin.wiwiss.silk.workbench.project.Project
import net.liftweb.http.js.JE.{JsRaw}
import net.liftweb.http.js.JsCmds.Script
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JE.{Call, Str, Num, JsArray}
import xml.{XML, NodeSeq}
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.{JsObj, JsCmds}
import de.fuberlin.wiwiss.silk.config.{ConfigWriter, ConfigReader}
import de.fuberlin.wiwiss.silk.instance.Path

class LinkSpec
{
  def render(xhtml : NodeSeq) : NodeSeq =
  {
    /**
     * Updates the Link Specification
     */
    def updateLinkSpec(linkSpecStr : String) =
    {
      try
      {
        val config = Project().config
        val sourceMap = config.sources.map(source => (source.id, source)).toMap

        val linkSpecXml = XML.loadString(linkSpecStr)
        val linkSpec = ConfigReader.readLinkSpecification(linkSpecXml, config.prefixes, sourceMap)

        Project.updateLinkSpec(linkSpec)
        JsRaw("alert('Updated Link Specification')").cmd
      }
      catch
      {
        case ex : Exception => JsRaw("alert('Error updating Link Specification. Details: " + ex.getMessage.encJs + "')").cmd
      }
    }

    bind("entry", xhtml,
         "update" -> SHtml.ajaxButton("Update", () => SHtml.ajaxCall(Call("serializeLinkSpec"), updateLinkSpec)._2.cmd),
         "download" -> SHtml.submit("Download", () => S.redirectTo("config")),
         "linkSpec" -> Script(generateLinkSpecVar & generatePathsFunction))
  }

  private def generateLinkSpecVar() =
  {
    //Serialize the link condition to a JavaScript string
    //TODO remove last replace?
    val linkSpecStr = ConfigWriter.serializeLinkSpec(Project().linkSpec).toString.replace("\n", " ").replace(" function=", " transformfunction=")

    val linkSpecVar = "var linkSpec = '" + linkSpecStr + "';"

    JsRaw(linkSpecVar).cmd
  }

  private def generatePathsFunction() =
  {
    JsCmds.Function("retrievePaths", Nil, SHtml.ajaxInvoke(generatePathsObj)._2.cmd)
  }

  private def generatePathsObj() =
  {
    JsCmds.JsReturn(
    new JsObj
    {
      val props = ("source", generateSelectedPathsObj(true)) ::
                  ("target", generateSelectedPathsObj(false)) :: Nil
    })
  }

  private def generateSelectedPathsObj(selectSource : Boolean) =
  {
    val dataset = Project().linkSpec.datasets.select(selectSource)

    val instanceSpec = Project().cache.instanceSpecs.select(selectSource)

    new JsObj
    {
      val props = ("id", Str(dataset.source.id)) ::
                  ("paths", JsArray(instanceSpec.paths.map(generatePathObj) : _*)) ::
                  ("availablePaths", Num(instanceSpec.paths.size)) ::
                  ("restrictions", Str(instanceSpec.restrictions)) :: Nil
    }
  }

  private def generatePathObj(path : Path) =
  {
    new JsObj
    {
      val props = ("path", Str(path.toString)) ::
                  ("frequency", Num(1.0)) :: Nil
    }
  }
}
