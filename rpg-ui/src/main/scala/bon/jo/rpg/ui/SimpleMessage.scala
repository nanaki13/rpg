package bon.jo.rpg.ui

import bon.jo.html.HTMLDef.{$t, HtmlOps}
import bon.jo.html.PopUp
import bon.jo.rpg.ui.MessageImpl
import bon.jo.rpg.ui.PlayerMessage
import org.scalajs.dom.window

trait SimpleMessage extends PlayerMessage:

  override type T = MessageImpl

  val messageDiv = $t div "" := { d =>
    d.style.color = "white"
  }

  def message(str: String, timeToDisplay: Int): Unit =
    PopUp(str)
//    val s = $t div (str)
//    messageDiv.appendChild(s)
//    lazy val t: Int = window.setTimeout(() => {
//      window.clearTimeout(t)
//      messageDiv.removeChild(s)
//    }, timeToDisplay)
//    t

  def message(str: String): MessageImpl =
    val ret = MessageImpl($t div (str))
    ret.str._class = "alert alert-warning"
    messageDiv.appendChild(ret.str)
    ret

  def clear(str: MessageImpl): Unit =
    messageDiv.removeChild(str.str)
