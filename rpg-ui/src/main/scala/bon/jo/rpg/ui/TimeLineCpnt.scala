package bon.jo.rpg.ui

import bon.jo.html.HTMLDef.{$c, $t, $va, HtmlOps}
import bon.jo.rpg.BattleTimeLine
import bon.jo.html.DomBuilder._
import bon.jo.rpg.BattleTimeLine._
import bon.jo.rpg.raw._
import bon.jo.rpg.raw
import bon.jo.rpg.stat.Perso.WithUI
import bon.jo.rpg.stat.{Perso, GameElement}
import org.scalajs.dom.html.{Div, Span}
import org.scalajs.dom.{console, window}
import bon.jo.rpg.resolve.given
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
import bon.jo.rpg.BattleTimeLine.TimeLineOps
import bon.jo.rpg.ui.PlayerUI
import scala.concurrent.ExecutionContext
//type Resolve = bon.jo.rpg.AffectResolver[TimedTrait[GameElement], List[TimedTrait[GameElement]]]
class TimeLineCpnt(val withUI: WithUI)(using el:  TimeLineOps):


  import withUI.given

 // implicit val ui: Any = withUI.o.ui
  val tlView: Div = $c.div
  tlView.draggable = true

  tlView._class ="time-line"
  tlView.style.position = "absolute"
  tlView.style.top = "10px"
  tlView.style.right = s"0"

  val htmlName = el.timedObjs.map(_.simpleName).map(t => $t span t)
  htmlName.map {
    e =>

      e.style.position = "absolute"
      e
  }.foreach(e => tlView.appendChild({
    val in = e.wrap(tag.div)
    in.style.height = "1em"
    val s1: Span = $c.span
    s1.style.width = s"${el.params.chooseAction}px"
    s1.style.backgroundColor = "blue"
    s1.style.height = "1em"
    s1.style.display = "inline-block"
    val s2: Span = $c.span
    s2.style.width = s"${el.params.action - el.params.chooseAction}px"
    s2.style.backgroundColor = "red"
    s2.style.height = "1em"
    s2.style.display = "inline-block"
    val s3: Span = $c.span
    s3.style.width = s"12em"
    s3.style.backgroundColor = "green"
    s3.style.opacity = "0"
    s3.style.height = "1em"
    s3.style.display = "inline-block"
    in ++= (s1, s2, s3)
    in
  }))

  def update(e: Iterable[TimedTrait[_]]) =
    htmlName zip e foreach {
      case (element, value) =>
        element.style.left = value.pos.toString + "px"
    }



        
  
  def doEvent(): Int =

   
    el.uiUpdate = update
    import withUI.given
    lazy val gameLoop: Int = window.setInterval(()=>{
      if el.pause == 0 then {
        el.doStep
      }else{
        window.clearInterval(gameLoop)
      }

    },25)
    gameLoop



  el.resume = doEvent

