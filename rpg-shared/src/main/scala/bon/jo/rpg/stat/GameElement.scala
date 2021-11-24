package bon.jo.rpg.stat
import bon.jo.rpg.stat.StatsWithName
trait GameElement:
  this : StatsWithName =>
  val self = this