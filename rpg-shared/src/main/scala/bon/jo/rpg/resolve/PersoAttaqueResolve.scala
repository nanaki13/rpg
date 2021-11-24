package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.common.ec.Exec
import bon.jo.rpg.resolve.PersoResolveContext._
import bon.jo.rpg.stat._
import bon.jo.rpg._
import bon.jo.rpg.stat.raw.IntBaseStat
import bon.jo.rpg.Affect.Attaque
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.dao.Dao
import bon.jo.rpg.AffectResolver.AffectFormuleResolver
import bon.jo.memo.Script.*
object PersoAttaqueResolve extends  AttaqueResolve{

    type P = TimedTrait[GameElement]
    val r = Random()
    
    def resolveAffect(attp:  TimedTrait[Perso],ciblep :P):PlayerUI.UI[P]=
        ( attp.value[Perso] , ciblep.value[Perso]) match
            case (att : Perso,cible :  Perso) =>
               
                val randomMagic  : Double = r.nextDouble * 0.15 + 0.85
                val randomPhy  : Double = r.nextDouble * 0.15 + 0.85
                val attM = att.stats.mag
                val attP = att.stats.str

                val deffM = cible.stats.psy
                val deffP = cible.stats.vit

                val degat = (((attM-deffM)/2 * randomMagic) +  (attP-deffP)/2 * randomPhy).toFloat.round
                uiProcess(ciblep.withValue(cible.copy(hpVar = cible.hpVar - degat)),degat)

    def uiProcess(perso : P,degat : Int)(using ui : PlayerUI):P=
        
                val  p : Perso  =  perso.value
                ui.message(s"${p.name} a perdu ${degat} pv, il lui reste ${p.hpVar} pv",5000)
                ui.cpntMap(perso.id).update(Some(perso.cast))
                perso

}
trait PersoAttaqueResolve(using formulesMap :  Map[Formule.ID,Formule]) :
    import Formule.given
    import bon.jo.common.give.given
    given Affect = Affect.Attaque
  

    inline def degatFormule = AffectResolver.read(FormuleType.Degat)
    val degatF : ((IntBaseStat,IntBaseStat)) => Float = degatFormule.get.formule.toFunction[(IntBaseStat,IntBaseStat)]()
    def createResolve:AttaqueResolve= 
        new AttaqueResolve:
            type P = TimedTrait[GameElement]
            val r = Random()

            def resolveAffect(attp:  TimedTrait[Perso],ciblep :P):PlayerUI.UI[P]=
                ( attp.value[Perso] , ciblep.value[Perso]) match
                    case (att : Perso,cible :  Perso) =>
                    
                        val degat = degatF(att.stats,cible.stats).round
                        uiProcess(ciblep.withValue(cible.copy(hpVar = cible.hpVar - degat)),degat)

            def uiProcess(perso : P,degat : Int)(using ui : PlayerUI):P=
        
                val  p : Perso  =  perso.value
                ui.message(s"${p.name} a perdu ${degat} pv, il lui reste ${p.hpVar} pv",5000)
                ui.cpntMap(perso.id).update(Some(perso.cast))
                perso




    
