package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat.*
import bon.jo.rpg.*
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.resolve.PersoResolveContext.*
object CancelPerso extends  CancelResolve{

    type P = TimedTrait[GameElement]
    val r = Random()
    def resolveAffect(attp:  TimedTrait[Perso],ciblep :P) : PlayerUI.UI[P]=
        ( attp.value[Perso] , ciblep.value[GameElement]) match
            case (att : Perso,cible : Perso) =>
                val randomMagic  : Double = r.nextDouble * 0.15 + 0.85

                val attM = att.stats.mag
                

                val deffM = cible.stats.psy
             

                val recul = (((attM.toFloat/deffM.toFloat) * randomMagic*40)).toFloat.round
                uiProcess(ciblep.withPos(ciblep.pos-recul),recul)

    def uiProcess(perso : P,recul : Int)(using ui : PlayerUI):P=
        perso.value[Perso] match
            case p : Perso =>
                ui.message(s"${p.name} été reculé de ${recul} dans le temps",5000)
                ui.cpntMap(perso.id).update(Some(perso.cast))
                perso



}