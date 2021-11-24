package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat._
import bon.jo.rpg._
import bon.jo.rpg.Affect.Soin
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.StatsWithName
object SoinPerso extends  Resolver[TimedTrait[Perso],TimedTrait[GameElement],Soin.type]{

    type P = TimedTrait[Perso]
    val r = Random()
    def resolveAffect(attp: P,ciblep :TimedTrait[GameElement]): PlayerUI.UI[TimedTrait[GameElement]]=
        val att  :Perso = attp.value
        val cible:GameElement = ciblep.value
       
        val randomMagic  : Double = r.nextDouble * 0.15 + 0.85
        ( att , cible) match
            case (e : Perso,b : Perso) =>
                val soint = ( e.stats.mag * randomMagic).round.toInt
                uiProcess(ciblep.withValue(b.copy(hpVar = b.hpVar + soint)).asInstanceOf[P],soint)
            case z => ciblep


       

    def uiProcess(perso : P,soint : Int)(using ui : PlayerUI):TimedTrait[GameElement]=
        perso.value[Perso] match
            case e : Perso =>
                ui.message(s"${e.name}  a été soigné de ${soint} pv,il a maintenant  ${e.hpVar} pv",5000)
                ui.cpntMap(perso.id).update(Some(perso.cast))
                perso.asInstanceOf[TimedTrait[GameElement]]



}
