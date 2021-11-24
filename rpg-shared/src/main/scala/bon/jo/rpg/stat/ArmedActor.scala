package bon.jo.rpg.stat

import bon.jo.rpg.{Commande,LR,Affect}
import bon.jo.rpg.CommandeCtx
import bon.jo.rpg.stat.Actor.{ActorBaseStats, Weapon, WeaponBaseState}
import bon.jo.rpg.stat.AnyRefBaseStat.r
import bon.jo.rpg.stat.BaseState.ImplicitCommon._
import bon.jo.rpg.stat.raw.IntBaseStat

trait ArmedActor:
  self: ActorBaseStats =>
  val leftHandWeapon: Option[Weapon]
  val rightHandWeapon: Option[Weapon]

  def leftHand: Option[IntBaseStat] = leftHandWeapon map (_.stats)
  def rightHand: Option[IntBaseStat] = rightHandWeapon map (_.stats)
  def twoHand: Boolean = leftHand == rightHand

  def leftArmedStat(): AnyRefBaseStat[Float] =
    stats growPercent leftHand.getOrElse(BaseState.`0`)

  def rightArmedStat(): AnyRefBaseStat[Float] =
    stats growPercent rightHand.getOrElse(BaseState.`0`)

  def twoAndStat(): AnyRefBaseStat[Float] = stats growPercent (rightHand.getOrElse(BaseState.`0`) + leftHand.getOrElse(BaseState.`0`))
  def armesCommandes():List[Commande]=
    
    val  l= leftHandWeapon map (e => Commande.Attaque(e,LR.L))
    val r = rightHandWeapon map (e => Commande.Attaque(e,LR.R))
    l.toList ++ r.toList
  
  def randomSoin(weapon: Actor.Weapon): Weapon =
    if r.nextDouble()>0.5 then
      weapon.copy(affects = weapon.affects :+ Affect.Soin)
    else
      weapon





 // def leftHandAction = leftHandWeapon.map(e=>e.action.map(mapAttaque(Action.Attaque.MainGauche))).getOrElse(Nil)
  //def rightHandAction = rightHandWeapon.map(e=>e.action.map(mapAttaque(Action.Attaque.MainDroite))).getOrElse(Nil)


  //var actionCtx: Option[ActionCtx] = None


