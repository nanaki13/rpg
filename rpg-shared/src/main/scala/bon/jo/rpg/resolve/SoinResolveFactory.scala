package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat._
import bon.jo.rpg._
import bon.jo.rpg.Affect.Soin
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.StatsWithName
import bon.jo.rpg.util.Script.*
import bon.jo.rpg.stat.raw.IntBaseStat
object SoinResolveFactory:
    
    type ResolveSoin = Resolver[TimedTrait[Perso],TimedTrait[GameElement],Soin.type]
    def resolve( using  ResolveSoin) : ResolveSoin = summon

    given  ( using Map[Formule.ID, Formule] ) : ResolveSoin   with 
        import Formule.given
        import bon.jo.common.give.given
        given Affect = Affect.Soin
        def formuleSoin = AffectResolver.read(FormuleType.Factor).getOrElse(throw new RuntimeException(s"no formules Soin ${FormuleType.Factor}"))
        val formuleFunction = formuleSoin.formule.toFunction[(IntBaseStat, IntBaseStat)]()
        type P = TimedTrait[Perso]
        
        def resolveAffect(attp: P,ciblep :TimedTrait[GameElement]): PlayerUI.UI[TimedTrait[GameElement]]=
            val att  :Perso = attp.value
            val cible:GameElement = ciblep.value
        
           
            ( att , cible) match
                case (e : Perso,b : Perso) =>
                    val soint = formuleFunction(e.stats,b.stats).round
                    uiProcess(ciblep.withValue(b.copy(hpVar = b.hpVar + soint)).asInstanceOf[P],soint)
                case z => ciblep


        

        def uiProcess(perso : P,soint : Int)(using ui : PlayerUI):TimedTrait[GameElement]=
            perso.value[Perso] match
                case e : Perso =>
                    ui.message(s"${e.name}  a été soigné de ${soint} pv,il a maintenant  ${e.hpVar} pv",5000)
                    ui.cpntMap(perso.id).update(Some(perso.cast))
                    perso.asInstanceOf[TimedTrait[GameElement]]   
            

