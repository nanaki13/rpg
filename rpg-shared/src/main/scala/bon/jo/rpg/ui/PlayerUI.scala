package bon.jo.rpg.ui


import bon.jo.rpg.CommandeCtx
import bon.jo.rpg.TimedTrait
import bon.jo.rpg.BattleTimeLine._
import bon.jo.rpg.stat.GameElement
import bon.jo.ui.UpdatableCpnt

import scala.concurrent.{ExecutionContext, Future}
import bon.jo.rpg.stat.GameId


trait PlayerUI extends PlayerMessage:


  def cpntMap: GameId.ID => UpdatableCpnt[TPA]


 

  def ask(d: TimedTrait[GameElement], cible: List[TimedTrait[GameElement]]): Future[CommandeCtx]


object PlayerUI {
  type UI[A] = (PlayerUI,TimeLineParam) ?=> A
  def runSeq[A](toAsk: Seq[() => Future[A]],res : List[A] = Nil)(implicit ec: ExecutionContext): Future[ List[A]] =

    if toAsk.isEmpty then
      Future.successful((res))
    else
      toAsk.head().flatMap {
        e => runSeq(toAsk.tail,res :+ e)
      }
  def apply(str : String): PlayerUI.UI[Unit] = summon[PlayerUI].message(str,0)
}