package bon.jo.rpg.ui

import bon.jo.html.DomShell.ExtendedElement
import bon.jo.html.HTMLDef.{$c, $l, $t, $va, HtmlOps}
import bon.jo.html.HtmlRep.HtmlCpnt
import bon.jo.html.SimpleView.row
import bon.jo.html.DomBuilder._
import bon.jo.rpg.stat.AnyRefBaseStat.Impl
import bon.jo.rpg.stat.raw.{AnyRefBaseStat, IntBaseStat, Perso}
import bon.jo.ui.UpdatableCpnt
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{HTMLElement, Node}
import bon.jo.html.DomBuilder.*
import bon.jo.html.DomBuilder.html.*
import bon.jo.html.DomBuilder.html.$.*
import bon.jo.app.ChildParent
import bon.jo.html.HtmlEventDef.ExH
import org.scalajs.dom.raw.HTMLImageElement

class Progress:

  val textPr = $.span(text(""))
  val pBar =  $.div{
    childs(textPr)
    attr("class" -> "progress-bar bg-danger" ,"role" ->"progressbar" ,"style" ->"width: 100%", "aria-valuenow" ->"100", "aria-valuemin" ->"0" ,"aria-valuemax" ->"100")}
  val html =  $.div{
    _class("progress")
    childs(pBar)

  }
  def update(percent : Int,value: Int,max : Int) = 
    textPr.textContent = s"${value}/${max}"
    pBar.style.width = s"$percent%"  

object PerCpnt:

  opaque type ParentChild[A,B] = (A,B)
  object ParentChild:
    def apply[A,B](a : A, b : B) : ParentChild[A,B] = (a,b)
  extension[A,B](v : ParentChild[A,B]) 
    def parent = v._1
    def child = v._2

  def imageCtx(p : Perso):ParentChild[HTMLElement,HTMLImageElement] =
    val image :HTMLImageElement = html.$t.img[HTMLImageElement](html.$t.doOnMe(_.alt="perso image"))
      image.style.width = "15em"
      image.style.height = "15em"


      if p.image.path != "" then image.src = p.image.path


    val imageCnt = html.$.div{
      $.childs(image)
      $._class("perso-edit-img")
    }
    ParentChild(imageCnt,image)
class PerCpnt(val perso: Perso) extends HtmlCpnt with UpdatableCpnt[Perso]:


  val m: ChildParent.Maker = (name, value) => {
    val ref = $t span (value.toString)
    val cont = $va div List($t span (s"$name:"), ref)
    (ref, cont)
  }




  val hpVarCpnt = Progress()
  def updateHp(p : Perso) = 
    val ini = p.hpVar.toFloat
    val max = p.stats.hp.toFloat
    var nHp = (ini/max*100f).round
    nHp = if nHp > 0 then 
      nHp
    else 0 
    hpVarCpnt.update(nHp,p.hpVar,p.stats.hp)
    if(p.hpVar <= 0 ){
        head._class = "dead"
    }

  updateHp(perso)

  def caracrAllContP(value: IntBaseStat): AnyRefBaseStat[(String, ChildParent)] =
    value.named.map{case (e,b) =>m(e,b : Any)}


  val nameDiv: Div = $c.div[Div] := divNameLevel(perso)
  //val descDiv: Span = $c.span[Span] := (_._class="perso-desc") := spadescLevel(perso)

  def divNameLevel(perso: Perso)(s: Div) =
    s.clear()
    s += $t div perso.name
    s += ($t div (s"Lvl. ${perso.lvl}"))
  def spadescLevel(perso: Perso)(s: Div) =
    s.clear()
    s += $t span perso.desc


  val htmlCarac: AnyRefBaseStat[(String,ChildParent)] = caracrAllContP(perso.stats)

  def htmlList: List[ChildParent] = htmlCarac.toPropList.map(_._2)

  val armR: List[ChildParent] = perso.rightHand.map(caracrAllContP).map { e =>
    e.toPropList.map(_._2)
  } getOrElse Nil
  val armL: List[ChildParent] = perso.leftHand.map(caracrAllContP).map { e =>
   e.toPropList.map(_._2)
  } getOrElse Nil

  import bon.jo.rpg.stat.BaseState.ImplicitCommon._

  val lcomputedStat = caracrAllContP(perso.twoAndStat().to[IntBaseStat]).map(_._2).map(_.parent).toPropList

  def contStat: List[HTMLElement] = htmlList map (_.parent)

  def contArmL: List[HTMLElement] = armL map (_.parent)

  def contArmR: List[HTMLElement] = armR map (_.parent)

  def statWithMod: List[HTMLElement] = armR map (_.parent)


  

  override val get: IterableOnce[HTMLElement] =
    val img = PerCpnt.imageCtx(perso)
    val hideStat = $.div{$.text("+")}
    val carChild = List(row(List($t("stat") +: contStat, $t("L") +: contArmL, $t("G") +: contArmR, $t("stat+") +: lcomputedStat)))
    val carac =   $.div{
      $._class("perso-carac")
      $.childs($.div{
          $.childs(carChild  : _ *)
          $._class("perso-carac-cont")
          }
      )}
    hideStat.style.zIndex = "1000"
    hideStat.style.cursor = "pointer"
    hideStat.$click{
      e => {
        if hideStat.textContent == "+"
        then
          hideStat.textContent = "-"
      
          hideStat.parentElement.appendChild(carac)

        else
          hideStat.textContent = "+"
          carac.removeFromDom()
      }
    }
    val ret = $va div List(img.parent,
      $va div List((nameDiv.wrap(tag.div))) := { me =>
        me._class = "perso-title"
      }, $.div{
          $._class("flex")
          $.childs($.div($.text("HP")),hpVarCpnt.html)
        },hideStat
      )
    ret._class = "perso-root"

    Option(ret)

  override def update(value: Option[Perso]): Unit =
    value match
      case Some(value) =>
        nameDiv := divNameLevel(value)
        //descDiv  := spadescLevel(value)
        org.scalajs.dom.console.log(s"updateHp ${value.hpVar}")
        updateHp(value)
        //  nameDiv.innerText = value.name
        htmlCarac.hp._2.child.innerText = value.stats.hp.toString
      //   attDiv.innerText = value.str.toString
      case None =>




