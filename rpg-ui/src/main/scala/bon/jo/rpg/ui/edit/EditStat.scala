package bon.jo.rpg.ui.edit

import bon.jo.html.DomBuilder.*
import bon.jo.html.ImuutableHtmlCpnt
import bon.jo.common
import bon.jo.common.Affects
import bon.jo.html.HTMLDef.{$c, $l, $t, $va, HtmlOps}
import bon.jo.html.HtmlRep
import bon.jo.html.SimpleView
import bon.jo.rpg.stat.raw.*
import bon.jo.ui.{ReadableCpnt, UpdatableCpnt}
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.HTMLElement


object EditStat extends HtmlRep[IntBaseStat, EditStat]:
  implicit val alg: common.Alg[HTMLElement] = new common.Alg[HTMLElement] {
    override def +(a: HTMLElement, b: HTMLElement): HTMLElement =
     $l div List(a.wrap(tag.div) := (_._class = "col black-on-white"), b.wrap(tag.div) := (_._class = "col")) := { cont =>

        cont._class = "row"

      }

    override def -(a: HTMLElement, b: HTMLElement): HTMLElement = ???

    override def *(a: HTMLElement, b: HTMLElement): HTMLElement = ???

    override def /(a: HTMLElement, b: HTMLElement): HTMLElement = ???
  }

  override def html(memo: IntBaseStat): EditStat =
    new EditStat(memo)

  implicit val value: HtmlRep[IntBaseStat, EditStat] = this





class EditStat(initial: IntBaseStat) extends ImuutableHtmlCpnt with UpdatableCpnt[IntBaseStat] with ReadableCpnt[IntBaseStat]:

  import EditStat.alg


  type HtmlStat = AnyRefBaseStat[HTMLElement]

  val inputs: AnyRefBaseStat[Input] = initial.map(v => $c.input[Input] := {
    e =>
    e.value = v.toString
      e._class = "input-stat form-control"
  })
  val redrawButton = SimpleView.bsButton("redraw")


  def inputsAsHtml: AnyRefBaseStat[HTMLElement] = inputs

  def names: HtmlStat = AnyRefBaseStat.names.map(na => ($t span na))

  def inputsNamed: HtmlStat = names + inputsAsHtml


  override def create(): IterableOnce[HTMLElement] =
    inputsNamed.toPropList :+ redrawButton


  implicit val affInput: Affects.AffectOps[Input, Int] =
    (a, b) => {
      a.value = b.toString
    }


  override def update(value: Option[IntBaseStat]): Unit =
    value.foreach {
      inputs := _
    }


  override def read: IntBaseStat = inputs.map(_.value.toInt)


