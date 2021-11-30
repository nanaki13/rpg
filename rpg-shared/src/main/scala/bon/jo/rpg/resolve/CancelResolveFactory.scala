package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.stat._
import bon.jo.rpg._
import bon.jo.rpg.Affect.Cancel
import scala.util.Random
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.StatsWithName
import bon.jo.rpg.util.Script.*
import bon.jo.rpg.stat.raw.IntBaseStat
object CancelResolveFactory:
    
    type ResolveCancel = Resolver[TimedTrait[Perso],TimedTrait[GameElement],Cancel.type]
    def resolve( using  ResolveCancel) : ResolveCancel = summon

    given  ( using Map[Formule.ID, Formule] ) : ResolveCancel   with 
        import Formule.given
        import bon.jo.common.give.given
        given Affect = Affect.Cancel
        def formuleCancel = AffectResolver.read(FormuleType.Factor).getOrElse(throw new RuntimeException(s"no formules Cancel ${FormuleType.Factor}"))
        val formuleFunction = formuleCancel.formule.toFunction[(IntBaseStat, IntBaseStat)]()
        type P = TimedTrait[Perso]
        
        def resolveAffect(attp: P,ciblep :TimedTrait[GameElement]): PlayerUI.UI[TimedTrait[GameElement]]=
            val att  :Perso = attp.value
            val cible:GameElement = ciblep.value
        
           
            ( att , cible) match
                case (e : Perso,b : Perso) =>
                    val recul = formuleFunction(e.stats,b.stats).round
                    uiProcess(ciblep.withPos(ciblep.pos-recul),recul)
                case z => ciblep


        

        def uiProcess(perso : P,recul : Int)(using ui : PlayerUI):TimedTrait[GameElement]=
            perso.value[Perso] match
                case e : Perso =>
                    ui.message(s"${e.name} été reculé de ${recul} dans le temps",5000)
                    ui.cpntMap(perso.id).update(Some(perso.cast))
                    perso.asInstanceOf[TimedTrait[GameElement]]   
            

