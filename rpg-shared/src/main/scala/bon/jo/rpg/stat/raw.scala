package bon.jo.rpg.stat

import bon.jo.rpg.stat

object raw:
  type Actor = stat.Actor
  val Actor: stat.Actor.type = stat.Actor
  type AnyRefBaseStat[+A] = stat.AnyRefBaseStat[A]
  type FloatBaseStat = stat.AnyRefBaseStat[Float]
  type IntBaseStat = stat.AnyRefBaseStat[Int]
  type StringBaseStat = stat.AnyRefBaseStat[String]
  val AnyRefBaseStat: stat.AnyRefBaseStat.type = stat.AnyRefBaseStat
  type ArmedActor = stat.ArmedActor
  val BaseState: stat.BaseState.type = stat.BaseState
  type Perso = stat.Perso
  type Weapon = stat.Actor.Weapon
  val Perso: stat.Perso.type = stat.Perso
