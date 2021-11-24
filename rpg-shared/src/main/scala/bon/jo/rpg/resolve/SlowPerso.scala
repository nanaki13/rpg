package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat.*
import bon.jo.rpg.*
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.resolve.PersoResolveContext.*
import bon.jo.rpg.stat.raw.IntBaseStat
import bon.jo.rpg.BattleTimeLine.TimeLineParam
import bon.jo.memo.Script.*
import bon.jo.rpg.draw.draw
object SlowPerso extends  SlowResolve :

    type P = TimedTrait[GameElement]
    val r = Random()
    def resolveAffect(attp:  TimedTrait[Perso],ciblep :P) : PlayerUI.UI[P]=
        ( attp.value[Perso] , ciblep.value[GameElement]) match
            case (att : Perso,cible : Perso) =>
                val chanceToHit = 0.7 + (att.stats.res - ciblep.stats.res)/100d
                PlayerUI(s"Chance de succés : ${(chanceToHit*100).round.toInt} %")
                val rr = r.nextDouble
                PlayerUI(s"Lancer : ${(rr*100).round.toInt} %")
                if(rr < chanceToHit) then
                    PlayerUI(s"Réussite!")
                    val factor = calculFactor(attp,ciblep)
                    val eff : FactorEffectt =  FactorEffectt(3,factor,Affect.Slow)
                    uiProcess(ciblep.addEffect(eff),eff)
                else
                    PlayerUI(s"Echec!")
                    ciblep

    def calculFactor(att : TimedTrait[Perso],defe : P):Float=
        Affect.Slow.vivMod.get
    

    def uiProcess(perso : P,factor : FactorEffectt)(using ui : PlayerUI):P=
        perso.value[Perso] match
            case p : Perso =>
                ui.message(s"${p.name} est ralenti de ${factor.factor} dans le temps pedans ${factor.time} tour",5000)
                ui.cpntMap(perso.id).update(Some(perso.cast))
                perso


