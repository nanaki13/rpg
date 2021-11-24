package bon.jo.rpg.ui


import bon.jo.rpg.TimedTrait

import bon.jo.rpg.CommandeCtx
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.BattleTimeLine.TPA
import bon.jo.ui.UpdatableCpnt

import scala.concurrent.Future
import bon.jo.rpg.stat.Perso
import bon.jo.rpg.stat.GameId

object PlayerUIStdIn:
  object Value extends PlayerUI:
    type T = MessagePlayer
    def register(id: Int, g: bon.jo.rpg.stat.GameElement): Unit=
      ()
    override def ask(d: TimedTrait[GameElement], cible: List[TimedTrait[GameElement]]): Future[CommandeCtx] = CommandeCtx.fromStdIn(d, cible)

    override def message(str: String, timeToDisplay: Int): Unit = println(str)

    override def message(str: String): MessagePlayer = new MessagePlayer {}

    override def clear(str: MessagePlayer): Unit = {

    }

 

    override def cpntMap: GameId.ID => UpdatableCpnt[TPA] = e => new UpdatableCpnt[TPA] {
      override def update(value: Option[TPA]): Unit =
        println(value)
    }

  implicit val value: PlayerUI = Value
