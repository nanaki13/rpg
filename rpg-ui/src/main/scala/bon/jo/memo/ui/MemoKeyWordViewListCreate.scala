package bon.jo.memo.ui

import bon.jo.html.HTMLDef._
import bon.jo.memo.Entities
import bon.jo.memo.Entities.KeyWord
import org.scalajs.dom.html.{Div, Input}
import bon.jo.html.SimpleView
import bon.jo.html.PopUp
import scala.concurrent.{ExecutionContext, Future}

class MemoKeyWordViewListCreate(val propose: Propose[KeyWord, Input], listView: Div,
                                val memoKeywWordtx: MemoCtxView,addMemo : (Entities.MemoKeywords)=>Unit)
                               (implicit executionContext: ExecutionContext)
  extends SimpleView[Entities.MemoKeywords](() =>
    ($va div (
      $t span "titre",
      memoKeywWordtx.tInput,
      $t span "type :",
      memoKeywWordtx.memoType,
      $t div "content",
      $va div(memoKeywWordtx.contentInput, memoKeywWordtx.memoList.html).toList,
      listView,
      $t div "KeyWord : ",
      propose.html
    ).toList) := ( _._class="col" ) ,addMemo):




  def addEventNewMemoKeyWord(iterable: Iterable[KeyWord]): Unit =


    btnInput.$spinner {
      () => {
        val m = Entities.MemoKeywords(memoKeywWordtx.newMemo, iterable.toSet)
        val req: Future[Unit] = Daos.memoKeyWord.create(m).map(o => o.foreach(+=))
          .map(_ =>PopUp(s"${m.memo.title} sauvegarder !"))
        req.recover {
          case exception => PopUp("Problème de sauvegarder"); throw (exception)
        }
      }
    }
