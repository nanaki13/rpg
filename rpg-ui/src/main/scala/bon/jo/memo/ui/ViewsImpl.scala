package bon.jo.memo.ui

import bon.jo.html.HTMLDef._
import bon.jo.html.HtmlEventDef._
import bon.jo.memo.Entities
import bon.jo.html.HtmlRep.PrXmlId
import bon.jo.html.SimpleView.i
import bon.jo.html.SimpleView
import org.scalajs.dom.console
import org.scalajs.dom.html.{Div, Input}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


class ViewsImpl(implicit executionContext: ExecutionContext):


  val viewsDef: ViewsDef = ViewsDef()

  import ViewsDef.KewWordHtml.WithClose._

  val keywWordI: Input = i
  val kewWordDiv: Div = $c.div[Div]

  object keyWordView extends SimpleView[Entities.KeyWord](() => $va div List($l span List($t("titre :")
    , keywWordI, kewWordDiv)), (kw) => {
    val e: ViewsDef.KewWordHtml = kw.html
    e.close.$click { _ =>
      scalajs.js.special.debugger()
      Daos.keyWordDao.delete(kw.id.get) recover  {
        case exception =>
          scalajs.js.special.debugger()
          console.log("EXXXX")
          console.log(exception)
          throw (exception)
      } foreach{
        b =>
          scalajs.js.special.debugger()
          console.log("eeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
          console.log(e.list)
          e.list.foreach(kewWordDiv.removeChild)

      }
    }
    kewWordDiv ++= e.list
  })

  def addKwEvent: Unit =
    keyWordView.btnInput.$click { _ =>
      val m = Entities.KeyWord(None, keywWordI.value)
      val req: Future[Unit] = Daos.keyWordDao.create(m).map(o => o.foreach(keyWordView.+=))
      req.onComplete {
        case Failure(exception) => throw (exception)
        case Success(_) =>
      }

    }




