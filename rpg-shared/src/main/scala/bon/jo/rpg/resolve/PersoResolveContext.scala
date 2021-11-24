package bon.jo.rpg.resolve

import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.Affect
import bon.jo.rpg.TimedTrait
import bon.jo.rpg.stat.Perso
import bon.jo.rpg.stat.GameElement

import bon.jo.rpg.ui.PlayerUI

object PersoResolveContext :
    type AttaqueResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Attaque.type]
    type SoinResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Soin.type]
   // type GardeResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Garde.type]
    type CancelResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Cancel.type]
    type SlowResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Slow.type]
    type HateResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Hate.type]
    type CaffeinResolve =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Caffein.type]
    type BoosterResolver =  Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Booster.type]
    object ResolveContext:
        def unknwon[A <: Affect]():  Resolver[TimedTrait[Perso], TimedTrait[GameElement],A] = 
            new Resolver:
                override def resolveAffect(a: TimedTrait[Perso], b: TimedTrait[GameElement]) : PlayerUI.UI[TimedTrait[GameElement]] =       
                    summon[PlayerUI].message("mais sa fait rien",1000)
                    b
    trait ResolveContext:
        given attaqueResolve: AttaqueResolve
        given soinResolve: SoinResolve
      //  given gardeResolve: GardeResolve
        given cancelResolve: CancelResolve
        given slowResolve: SlowResolve
        given hateResolve: HateResolve
        given caffeinResolve: CaffeinResolve
        given boosterResolve: BoosterResolver
        
    //    given Resolver[TimedTrait[Perso], TimedTrait[GameElement],Affect.Rien.type] = ResolveContext.unknwon[Affect.Rien.type]()



  

