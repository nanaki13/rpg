package bon.jo.rpg

import bon.jo.rpg
import bon.jo.rpg.stat.Perso
object raw:
  type Affect = rpg.Affect
  val Affect: rpg.Affect.type = rpg.Affect
  type AffectResolver[A, B <: BattleTimeLine.TPA] = rpg.AffectResolver[A, B]
  val BattleTimeLine: rpg.BattleTimeLine.type = rpg.BattleTimeLine

  //type DoActionTrait[A] = rpg.DoActionTrait[A]
 // val DoActionTrait: rpg.DoActionTrait.type = rpg.DoActionTrait
  val State: rpg.State.type = rpg.State
  type State = rpg.State

  type Timed[A] = rpg.Timed[A]
  type TimedTrait[A] = rpg.TimedTrait[A]
  val TimedTrait: rpg.TimedTrait.type = rpg.TimedTrait


