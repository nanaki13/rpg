package bon.jo.rpg

import bon.jo.rpg.BattleTimeLine.TimeLineParam
import bon.jo.rpg.resolve.PersoResolveContext._
import bon.jo.rpg.stat.Perso
import bon.jo.rpg.stat._
import bon.jo.rpg.stat.{AnyRefBaseStat, Perso}
import bon.jo.rpg.TimedTrait._
import bon.jo.rpg.ui.{PlayerUI, PlayerUIStdIn}
import bon.jo.rpg.resolve.PersoAttaqueResolve
import scala.concurrent.ExecutionContext.Implicits.global
import bon.jo.rpg.BattleTimeLine.TimeLineOps
import Perso.given
import bon.jo.rpg.resolve.given
import bon.jo.rpg.resolve.PersoCtx
import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.Commande.Garde


object TestRPG extends App:
  var id = 0

  def getid() =
    id += 1
    id

    
  given TimeLineParam =  TimeLineParam(0, 50, 70)
  given Timed[GameElement] = summon[Timed[bon.jo.rpg.stat.Perso]]
  given yl :  TimeLineOps = TimeLineOps()
  given PlayerUI = PlayerUIStdIn.value


  given ResolveContext = new resolve.DefaultResolveContext

  

  val ui : Perso.WithUI =  Perso.WithUI()


  val p1 = new Perso(1, "Bob", "Un bon gars", AnyRefBaseStat.randomInt(50, 10))
  val p2 = new Perso(2, "Bill", "Un bon gars", AnyRefBaseStat.randomInt(50, 10))


  import ui.given

  val ctw = summon[PersoCtx]
  import ctw.given
  yl.add(p1)
  yl.add(p2)


  for _ <- 1 to 100 do


    yl.nextState(yl.timedObjs)












