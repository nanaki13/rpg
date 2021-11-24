package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat.*
import bon.jo.rpg.*
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.resolve.PersoResolveContext.*
import bon.jo.rpg.BattleTimeLine.TimeLineParam
object CaffeinPerso extends CaffeinResolve with ReolveHasteFamily[Affect](Affect.Caffein)
