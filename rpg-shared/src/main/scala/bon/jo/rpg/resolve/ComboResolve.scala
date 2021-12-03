package bon.jo.rpg.resolve

import bon.jo.rpg.TimedTrait
import bon.jo.rpg.BattleTimeLine.LTP
import bon.jo.rpg.BattleTimeLine.TP
import bon.jo.rpg.stat.Perso
object ComboResolve:
  case class ComboMembers(lead : TP[Perso],members : LTP[Perso] )
