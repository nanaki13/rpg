package bon.jo.rpg.ui.edit

import bon.jo.rpg.dao.ImageDao
import bon.jo.rpg.ui.Image
import bon.jo.html.ImuutableHtmlCpnt
import bon.jo.html.DomBuilder.html.$
import bon.jo.html.DomBuilder.html.$t
import org.scalajs.dom.raw.HTMLElement
import scala.concurrent.ExecutionContext
import scala.concurrent.Promise
import org.scalajs.dom.raw.HTMLImageElement
import scala.concurrent.Future
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.DomShell.ExtendedElement
class ImageChoose(using dao :ImageDao)(using ExecutionContext) extends ImuutableHtmlCpnt {
  val root = $.div{
  $._class("root-image-choose")
  $.text("Choisir une image")
  }

  def create(): IterableOnce[HTMLElement] = Some(root)

  def clear(): Unit = root.clear()
  def chooseImage(): Future[Image] = 
    
    dao.readAll(0,0).flatMap{
      imgs =>
        val retour = Promise[Image]()
        
        imgs.foreach{ img =>
          val imgEl = $t.img[HTMLImageElement]{
          $._class("img-choose")  
          $t.doOnMe(_.src = img.path)}
          val imageCnt = $.div{
            $.childs(imgEl)
            $._class("img-choose-cnt")
          }
          root.appendChild(imageCnt)
          imageCnt.$click{_ => retour.success(Image(imgEl.src))}
        }
        retour.future
    }   
}
