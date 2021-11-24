package bon.jo.memo.ui

import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep.{HtmlCpnt, HtmlRepParam}
import bon.jo.html.SimpleView
import bon.jo.memo.Entities.KeyWord
import bon.jo.ui.UpdatableCpnt
import org.scalajs.dom.raw.HTMLElement

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object FindViewDef:
  case class FindParam(
                        buttonName: String
                      )

  class FindView(val findParam: FindParam, val ctx: FindViewCtx) extends HtmlCpnt with UpdatableCpnt[FindParam]:
    implicit val ec: ExecutionContext = ctx.ec
    private val input = SimpleView.i
    private val button = SimpleView.bsButton(findParam.buttonName)

    override val get: Iterable[HTMLElement] = List(input, button)

    button.$click { _ =>
      ctx.memoTemplate.memosCOnr.clean()
      Daos.memoKeyWord.findByKeyWord(input.value).onComplete {
        case Failure(exception) => throw exception
        case Success(d) => d.foreach(ctx.memoTemplate.addMemo)
      }
    }
    input.$Action {
      button.click()
    }

    override def update(value: Option[FindParam]): Unit = ???

  case class FindViewCtx(memoTemplate: MemoTemplate, kws: Iterable[KeyWord], ec: ExecutionContext)

  object FindViewProvider extends HtmlRepParam[FindParam, FindViewCtx,FindView]:
    override def html(memo: FindParam, p: Option[FindViewCtx]): FindView = new FindView(memo, p.get)

  implicit val pr: HtmlRepParam[FindParam, FindViewCtx,FindView] = FindViewProvider
