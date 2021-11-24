package bon.jo.rpg


import bon.jo.rpg.TimedTrait._
import bon.jo.rpg.ui.PlayerUI

import scala.concurrent.{ExecutionContext, Future}
import bon.jo.rpg.stat.{ GameElement}
import BattleTimeLine.UpdateGameElement
import BattleTimeLine._
import bon.jo.rpg.stat.GameId
import bon.jo.rpg.resolve.PersoResolveContext._

import bon.jo.rpg.Commande
import bon.jo.rpg.stat.Perso
import bon.jo.rpg.stat.Perso.WithUI
object BattleTimeLine:


  type TP[A] = TimedTrait[A]
  type LTP[A] = List[TP[A]]
  type ITP[A] = Iterable[TP[A]]
  type TPA = TP[GameElement]
  type LTPA = LTP[GameElement]
  type ITPA = ITP[GameElement]
  type Res = CommandeResolver.Dispatcher[ TimedTrait[Perso], TimedTrait[GameElement]] 
  opaque type UpdateGameElement = (GameId.ID,TPA=>TPA,String)
  object UpdateGameElement:
    def apply(id : GameId.ID,tr : TPA=>TPA,name : String):UpdateGameElement = (id,tr,name)
  extension (a : UpdateGameElement)
    def idEl = a._1
    def transfrom(b : TPA) = a._2(b)
    def name = a._3
    def andThen(b : UpdateGameElement) : UpdateGameElement=
      UpdateGameElement(a.idEl, a._2.andThen( b._2), s"${a.name} puis ${b.name} " ) 
    def andThenList(b : List[UpdateGameElement])  : UpdateGameElement=
      (a +: b).reduceLeft( _ andThen _)
  enum NextStateResult(change : List[UpdateGameElement]):
    case  NextStateResultFast( fast : ITP[GameElement],change : List[UpdateGameElement]) extends NextStateResult(change)
    case  NextStateResultAsking( fast : ITP[GameElement], ask :  Future[ITP[GameElement]],change : List[UpdateGameElement])  extends NextStateResult(change)


 // def apply(using TimeLineParam) =
  object TimeLineOps{
    class i()(using params :  TimeLineParam,t : Timed[GameElement]) extends TimeLineOps
    def apply()(using params :  TimeLineParam,t :  Timed[GameElement]) : TimeLineOps = i()
  }
  trait TimeLineOps(using val params :  TimeLineParam)(using Timed[GameElement]):

    var pause = 0
    var uiUpdate : ITPA => Unit = _ => ()
    var timedObjs: LTPA = Nil
    var cnt = 0
    def stop(): Unit =
      timedObjs = Nil
      pause = 0


    def update(pf: TPA): TPA =

      pf.withPos(pf.pos + pf.speed)



    def state(pos: Int, spooed: Int): State =
      pos match
        case i if (i < params.chooseAction) => State.BeforeChooseAction
        case i if (i >= params.chooseAction && (i < params.chooseAction + spooed)) => State.ChooseAction
        case i if (i < params.action) => State.BeforeResolveAction
        case i if (i >= params.action) => State.ResolveAction
        case _ => State.NoState

    def updateAll(a :LTPA ): LTPA =
      a.map(update)

    def dochange(all : ITPA,update : List[UpdateGameElement])(using ui :   WithUI): ITPA = 
      val mapById = all.map(e => e.id -> e).toMap
      val mapTrById = (update.groupMapReduce(_._1)(e => e)(_ andThen _ )).values
      val updatedMap = mapTrById.map{ e => 
        ui.playerUI.message(s"application de ${e.name} ",0)
        e.transfrom(mapById(e.idEl))
        
        }.map(e => e.id -> e).toMap
      all.map{
        e =>
          updatedMap.getOrElse(e.id,e)
      }
        
      
  

    def doStep(using   WithUI,ExecutionContext ) = 
      nextState(timedObjs) match 
      case NextStateResult.NextStateResultFast(fast,change) => timedObjs = dochange(fast,change).toList.sorted
      case NextStateResult.NextStateResultAsking(fast, ask,change) => ask foreach{
        askWithResult =>
          timedObjs =  dochange(fast++askWithResult,change).toList.sorted
      }


    def add(a: GameElement): Unit =
      timedObjs = timedObjs :+ a.timed
    





    def state(e : LTPA): Seq[(TPA, State)] = e.map(p => (p, state(p.pos, p.speed)))



    var resume : ()=>Unit = ()=>{}

    def delta: R[Long] = 
      print( s"${System.currentTimeMillis} - ${summon[Long]}")
      System.currentTimeMillis - summon[Long]
    type R[A] = Long ?=> A

    def nextState(timedObjs : LTPA)(using 
 

    ui: WithUI,
     ec: ExecutionContext
 
     ): NextStateResult =
      import ui.persoCtx.given
      import ui.playerUI
      given d : Long= System.currentTimeMillis
      val ret =  
        if pause == 0 then
          val state_ :  Seq[(TPA, State)]=  state(updateAll(timedObjs))
          def m : Map[GameId.ID,TPA] = state_.map(_._1).map(( a:TPA ) => a.id -> a).toMap
          uiUpdate(state_.map(_._1))
          val cpntMap : Option[Map[GameId.ID,TimedTrait[GameElement]]] = if state_.count(_._2 == State.ResolveAction) > 0 then
            Some(m )
          else
            None
          val fast = state_.filter(_._2 != State.ChooseAction).map{
            (pos, state) =>
              val cible = pos.commandeCtx.cible: Iterable[bon.jo.rpg.stat.GameId.ID]
              def trueCible = cible.map(cpntMap.get(_))
              state match
                case State.BeforeChooseAction => (pos,Nil)
                case State.BeforeResolveAction => (pos,Nil)
                case State.ResolveAction =>
                  val cpnts = trueCible

                  val message = s"${pos.simpleName} fait ${pos.commandeCtx.commande.name} ${if pos.commandeCtx.cible.nonEmpty then
                    s"sur ${cpnts.map(_.simpleName).mkString(", ")}" else ""}"

                  ui.playerUI.message(message,5000)
          
                  val updayedCible = resolve.dispatcher.dispacth(pos.cast, trueCible, pos.commandeCtx.commande)
                  println(pos.effetcts)
                  val desc = pos.effetcts.map(e => e.--).toList
                  val toKeep = (desc  filter (_.time!=0)).toSet flatMap { eff =>
                    println(s"eff = ${eff}")
                    pos.self.modifiers.filter(_.cause.name == eff.name)
                  }
                  println(s"modifiers = ${pos.self.modifiers}")
                  println(s"toKeep = $toKeep")
                  (pos.self.copy(_pos = 0,modifiers=toKeep.toList,effetcts =desc.map{
                    e => 
                      ui.playerUI.message(s"l'effet ${e.name.name} sur ${pos.value[GameElement].self.name} dure encore ${e.time} tour",0)
                      e
                  } filter (_.time!=0)),updayedCible)
                case State.NoState =>(pos,Nil)
                case _ => ???
          }
          if  state_.count(_._2 == State.ChooseAction)>0 then
            val toAsk: Seq[() => Future[TPA]] = state_.filter(_._2 == State.ChooseAction).map {
              (pos,_) =>
                () => {
                  val message = ui.playerUI.message(s"selectionner la commande pour ${pos.simpleName}")
                  pause += 1
                  ui.playerUI.ask(pos, timedObjs).map { act =>
                    ui.playerUI.clear(message.asInstanceOf[ui.playerUI.T])

                    pause -= 1
                    val ret = pos.withCommandeCtx(act)
                    if pause == 0 then
                      resume()

                    ret
                  }
                }

            }
            NextStateResult.NextStateResultAsking(fast.map(_._1),PlayerUI.runSeq(toAsk),fast.flatMap(_._2).toList )
          else
            NextStateResult.NextStateResultFast(fast.map(_._1),fast.flatMap(_._2).toList)
        else
          NextStateResult.NextStateResultFast(timedObjs,Nil)
        end if
      println(s" step done in $delta ms")
      ret
     



  case class TimeLineParam(start: Int, chooseAction: Int, action: Int)


