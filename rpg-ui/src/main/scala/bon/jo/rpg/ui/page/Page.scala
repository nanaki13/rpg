package bon.jo.rpg.ui.page

import org.scalajs.dom.raw.Location
import org.scalajs.dom.experimental.URLSearchParams
import org.scalajs.dom.raw.Window

trait Page[ID]:
  val id : ID
  val label : String


object Page:
  case class Ctx[ID](pages :Iterable[Page[ID]])
  inline def location(using Location) : Location = summon
  inline def ctx[ID](using Ctx[ID]) : Ctx[ID] = summon
  inline def window(using Window) : Window = summon
  def setNavigationBar[ID](p : Page[ID])(using Window) = window.history.replaceState(null,window.document.title,window.location.host + s"?page=${p.id}") 
  def find[ID](using Location,Ctx[ID]):Option[Page[ID]] = 
    val paramParser: URLSearchParams = new URLSearchParams(location.search)
    val pageStringOption = Option(paramParser.get("page"))
    pageStringOption.flatMap(pageId => ctx.pages.find(_.id == pageId))
  def apply[ID](id : ID)(using Ctx[ID]):Page[ID] = ctx.pages.find(_.id == id).get 
    
