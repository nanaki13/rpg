package bon.jo.rpg.stat

import bon.jo.rpg.stat.raw.IntBaseStat

trait StatsWithName:
  // self : IntBaseStat =>

  val name: String
  val id: Int
  val desc : String
  
  val stats: IntBaseStat

  def withId[A <: StatsWithName](id: Int): A

