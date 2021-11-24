package bon.jo.memo.ui

import bon.jo.memo.Entities
import bon.jo.memo.Entities.MemoType
import bon.jo.html.SimpleView
import SimpleView.{i, s, sv, ta, DSelect}
import org.scalajs.dom.html.{Input, Select, TextArea}
import bon.jo.html.DomShell.ExtendedElement
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.SimpleView
import scala.scalajs.js.JSON

class MemoCtxView(var currentMemoOption: Option[Entities.MemoKeywords]):

  var editing = false

  def initialInput(): Unit =
    currentMemoOption foreach {
      currentMemo =>
        editing = true
        tInput.value = currentMemo.memo.title
        currentMemo.memo.memoType match
          case MemoType.Text =>
            contentInput.value = currentMemo.memo.content
            contentInput.show(true)
            memoList.html.show(false)
          case MemoType.Json =>
        memoType.select(currentMemo.memo.memoType.toString)
    }


  val memoList: MemoListView = new MemoListView()

  val tInput: Input = i
  val contentInput: TextArea = ta
  given (MemoType => String) =
    case MemoType.Json => "List"
    case MemoType.Text => "text"
  given List[MemoType] = MemoType.values.toList
  val memoType: Select = sv[MemoType]


  def newMemo: Entities.Memo =
    currentMemoOption map { currentMemo =>
      val mt = if editing then MemoType.valueOf(memoType.value) else currentMemo.memo.memoType
      val cnt = mt match
        case MemoType.Text => if editing then contentInput.value else currentMemo.memo.content
        case MemoType.Json => JSON.stringify(memoList.read().pure())

      if editing then
        new Entities.Memo(tInput.value, cnt, mt)
      else
        new Entities.Memo(currentMemo.memo.title, cnt, mt)
    } getOrElse {
      val mt = MemoType.valueOf(memoType.value)
      val cnt = mt match
        case MemoType.Text => contentInput.value
        case MemoType.Json => JSON.stringify(memoList.read().pure())
      new Entities.Memo(tInput.value, cnt, mt)
    }



  def hideOrShow(): Unit = MemoType.valueOf(memoType.value) match
    case MemoType.Text =>
      contentInput.show(true)
      memoList.html.show(false)
    case MemoType.Json =>
      contentInput.show(false)
      memoList.html.show(true)

  def makeSwitchView(): Unit =
    hideOrShow()
    memoType.$change { _ => {
      hideOrShow()
    }

    }
