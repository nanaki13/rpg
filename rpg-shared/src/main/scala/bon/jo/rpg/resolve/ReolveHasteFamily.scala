package bon.jo.rpg.resolve
import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat.*
import bon.jo.rpg.*
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.resolve.PersoResolveContext.*
import bon.jo.rpg.BattleTimeLine.TimeLineParam

trait ReolveHasteFamily[T <: Affect ](val e : Affect ) :

    type P = TimedTrait[GameElement]
    val r = Random()
    def resolveAffect(attp:  TimedTrait[Perso],ciblep :P) : PlayerUI.UI[P]=
        ( attp.value[Perso] , ciblep.value[GameElement]) match
            case (att : Perso,cible : Perso) =>
                val chanceToHit = 0.4 + (att.stats.mag)/100d
                PlayerUI(s"Chance de succés : ${(chanceToHit*100).round.toInt} %")
                val rr = r.nextDouble
                PlayerUI(s"Lancer : ${(rr*100).round.toInt} %")
                if(rr < chanceToHit) then
                    PlayerUI(s"Réussite!")
                     val factor = calculFactor(attp,ciblep)
                     val eff : FactorEffectt =  FactorEffectt(3,factor,e)
                     uiProcess(ciblep.addEffect(eff),eff)
                else
                    PlayerUI(s"Echec!")
                    ciblep
              

    def calculFactor(att : TimedTrait[Perso],defe : P): PlayerUI.UI[Float]=
        e.vivMod.get

    def uiProcess(perso : P,factor : FactorEffectt)(using ui : PlayerUI):P=
        perso.value[Perso] match
            case p : Perso =>
                ui.message(s"${p.name} est boosté de ${factor.factor} par ${e.name} pedans ${factor.time} tour",5000)
                ui.cpntMap(perso.id).update(Some(perso.cast))
                perso

