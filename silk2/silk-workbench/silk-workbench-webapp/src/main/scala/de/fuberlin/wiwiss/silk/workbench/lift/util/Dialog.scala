package de.fuberlin.wiwiss.silk.workbench.lift.util

import xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds.{OnLoad, Script}
import java.util.UUID

/**
 * Basic dialog with a number of fields.
 */
trait Dialog extends Form {
  /** The title of this dialog */
  def title : String

  /** The parameters of this dialog e.g. ("with" -> "700") */
  protected def dialogParams : List[(String, String)] = ("autoOpen" -> "false") :: ("width" -> "700") :: ("modal" -> "true") :: Nil

  /** The id of this form */
  private lazy val id : String = UUID.randomUUID.toString

  /**
   * Command which opens this dialog.
   */
  def openCmd = {
    //Update all fields and open the dialog
    updateCmd &
    JsRaw("$('#" + id + "').dialog('open');").cmd
  }

  /**
   * Command which closes this dialog.
   */
  def closeCmd = JsRaw("$('#" + id + "').dialog('close');").cmd

  /**
   * Renders this dialog.
   */
  def render(xhtml : NodeSeq) : NodeSeq = {
    def submit() = {
      try {
        onSubmit()
        closeCmd
      } catch {
        case ex : Exception => JsRaw("alert('" + ex.getMessage.encJs + "');").cmd
      }
    }

    <div id={id} title={title}> {
      SHtml.ajaxForm(
        super.render ++
        <tr>
          <td>
          </td>
          <td>
          { SHtml.ajaxSubmit("OK", submit) }
          </td>
        </tr> ++
        Script(initCmd)
      )
    }
    </div>
  }

  /**
   * Command which initializes this dialog.
   */
  private def initCmd = OnLoad(JsRaw("$('#" + id + "').dialog({ " + dialogParams.map(_.productIterator.mkString(": ")).mkString(", ") + " })").cmd)

  /**
   * Called when the dialog is submitted.
   * Must be overloaded by sub classes in order to read the input values.
   */
  protected def onSubmit()
}