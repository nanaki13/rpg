package bon.jo.rpg.ui

import bon.jo.html.HTMLDef.{$c, $t, $va, HtmlOps}
import bon.jo.rpg.BattleTimeLine
import bon.jo.html.DomBuilder.*
import bon.jo.html.DomBuilder.html.$
import bon.jo.rpg.BattleTimeLine.*
import bon.jo.rpg.raw.*
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
import bon.jo.rpg.StepResult
import bon.jo.rpg.Team
//type Resolve = bon.jo.rpg.AffectResolver[TimedTrait[GameElement], List[TimedTrait[GameElement]]]
class TimeLineCpnt(val withUI: WithUI,end : (winner : Team,losser : List[Team])=> Unit)(using el:  TimeLineOps):


  import withUI.given

 // implicit val ui: Any = withUI.o.ui
  
  val teams = el.timedObjs.map(_.team).toSet.toList
  val t1 = $.div($._class("timeline-t timeline-t1"))
  val t2 = $.div($._class("timeline-t timeline-t2"))
  //t1.style.width = s"${el.params.action}px"
  //t2.style.width = s"${el.params.action}px"
  val tlView = $.div{
    $.childs(t1,t2)
    $._class("time-line")
  }
  tlView.draggable = true

  tlView._class ="time-line"
  tlView.style.position = "absolute"
  tlView.style.top = "10px"
  tlView.style.right = s"0"
  val teamCpnTMap = Map(teams(0)-> t1, teams(1) -> t2)
  val htmlName = el.timedObjs.map(t =>(t, $t span "|"))
  htmlName.map {
   (t, e )=>

      e.className = "cursor-timeline"
      (t, e )
  }.foreach((t, e ) => teamCpnTMap(t.team).appendChild({
   

    

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
    val (zones,cl,cl2) = if(t.team == teams(0)) then (List(e,s1, s2),"flex","battle-name-l") else (List(e,s2,s1),"flex-reverse","")
    $.div{
      $._class(cl)
      $.childs($.div{
        $._class("battle-name "+cl2)
        $.text(t.simpleName)
      }, 
      $.div{
        $._class("layer")
        $.childs(zones : _ *)
    })}
  
  }))

  def update(e: Iterable[TimedTrait[_]]) =
    htmlName zip e foreach {
      case ((timTrait,element), value) =>
        val nPosCss = if(timTrait.team == teams(0)) then 
          value.pos -3 
        else
          el.params.action - value.pos - 3
        element.style.left = nPosCss.toString + "px"
    }


  

        
  
  def doEvent(): Int =

   
    el.uiUpdate = update
    import withUI.given
    lazy val gameLoop: Int = window.setInterval(()=>{
      if el.pause == 0 then {
       val res : Future[StepResult] = el.doStep()
       res.foreach{
           case StepResult.GameOver(winner,looser) => 
            window.clearInterval(gameLoop)
            end(winner,looser)
           case _ =>
       }
      }else{
        window.clearInterval(gameLoop)
      }

    },25)
    gameLoop



  el.resume = doEvent

