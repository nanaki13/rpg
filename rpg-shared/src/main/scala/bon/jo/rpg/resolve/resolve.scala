package bon.jo.rpg
import bon.jo.rpg.resolve.PersoResolveContext._
import bon.jo.rpg.BattleTimeLine.{TP,TPA}
import bon.jo.rpg.stat.Perso
import bon.jo.rpg.BattleTimeLine.UpdateGameElement
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.Actor.WeaponBaseState
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.BattleTimeLine.TimeLineParam
package resolve {
  
  object Formule:
    given List[String] = List("att","deff")
    type ID = (Affect,FormuleType)
  case class Formule(affect : Affect,formuleType : FormuleType ,formule : String)
  class DefaultResolveContext extends ResolveContext{
       def attaqueResolve = PersoAttaqueResolve
       def soinResolve = SoinPerso
       def cancelResolve = CancelPerso
       def slowResolve = SlowPerso
       def hateResolve = HatePerso
       def caffeinResolve = CaffeinPerso
       def boosterResolve = BoostPerso
      // def gardeResolve = ResolveContext.unknwon[A.Garde.type]()

  }
  object dispatcher extends  CommandeResolver.Dispatcher[TP[Perso],TPA] 
  given CommandeResolver.Dispatcher[TP[Perso],TPA] = dispatcher
  trait PersoCtx(using PlayerUI,TimeLineParam,ResolveContext):
    val resolveAffectCtx = summon[ResolveContext]
    given CommandeResolver.CommandeResolveCtx[TP[Perso],TPA] = new  CommandeResolver.CommandeResolveCtx[TP[Perso],TPA]:     
      override def attaque = 
        new CommandeResolver.Resolver[TP[Perso],TPA, bon.jo.rpg.Commande.Attaque]:
          def resolveCommand(a: TP[Perso],  b: Iterable[TPA])(using att : Commande.Attaque): Iterable[UpdateGameElement] = 
            att.hand match
              case LR.L => extractResolveArme(a,b)(a.value[Perso].leftHandWeapon.get)
              case LR.R => extractResolveArme(a,b)(a.value[Perso].rightHandWeapon.get)
        

      override def  rien =  
        new CommandeResolver.Resolver[TP[Perso],TPA, bon.jo.rpg.Commande.Rien.type]:
          def resolveCommand(a: TP[Perso],  b: Iterable[TPA])(using att : Commande.Rien.type): Iterable[UpdateGameElement] = 
          scala.collection.Iterable.empty
    object resolveAffect extends AffectResolver[TP[Perso],TPA]
    def extractResolveArme(a: TimedTrait[Perso], b: Iterable[TimedTrait[GameElement]])(wl : WeaponBaseState):scala.collection.Iterable[UpdateGameElement]=
        import resolveAffectCtx.given
        (for ac <- wl.affects yield
                ac match
                  case given Affect.Soin.type=>   
                    resolveAffect.resolveAffect[Affect.Soin.type](a,b)
                  case given Affect.Attaque.type=>   
                    resolveAffect.resolveAffect[Affect.Attaque.type](a,b)
                  case given Affect.Cancel.type=>   
                    resolveAffect.resolveAffect[Affect.Cancel.type](a,b)
                  case given Affect.Slow.type=>   
                    resolveAffect.resolveAffect[Affect.Slow.type](a,b)
                  case given Affect.Hate.type=>   
                    resolveAffect.resolveAffect[Affect.Hate.type](a,b)
                  case given Affect.Booster.type=>   
                    resolveAffect.resolveAffect[Affect.Booster.type](a,b)
                  case given Affect.Caffein.type=>   
                    resolveAffect.resolveAffect[Affect.Caffein.type](a,b)
                  case _ => 
                    summon[PlayerUI].message("Mais sa fait encore rien",0)
                    Nil
        ).flatten
}
