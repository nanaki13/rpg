package bon.jo.rpg.ui

import bon.jo.rpg.ui.Export.StatJS

import bon.jo.rpg.stat.Actor.{Lvl, Weapon, WeaponBaseState}
import bon.jo.rpg.stat.StatsWithName
import bon.jo.rpg.stat.raw.{AnyRefBaseStat, BaseState, IntBaseStat, Perso, Weapon}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import bon.jo.rpg.Commande
import bon.jo.common.Affects
import bon.jo.rpg.Affect

object Export:

  def apply(e: StatsWithName with Lvl): js.Object with js.Dynamic =
    js.Dynamic.literal(
      id = e.id,
      name = e.name,
      desc = e.desc,
      lvl = e.lvl,
      stats = StatJS(e.stats)
    )

  object PersoJS:
    // type R = PersoJS

    def apply(t: Perso): PersoJS =
      val dPerso = Export.apply(t)
      dPerso.leftHandWeapon = t.leftHandWeapon.map(WeaponJS(_)).getOrElse(js.undefined)
      dPerso.rightHandWeapon = t.rightHandWeapon.map(WeaponJS(_)).getOrElse(js.undefined)
      dPerso.commandes = t.commandes.map{
        case Commande.Attaque(a,b)=> b.id
        case a : Commande => a.toString 
      }.toJSArray
      dPerso.asInstanceOf[PersoJS]

    def unapply(value: PersoJS): Option[Perso] =
      value.stats match
        case StatJS(stat) =>
          val left = value.leftHandWeapon.toOption.flatMap(WeaponJS.unapply)
          val right = value.rightHandWeapon.toOption.flatMap(WeaponJS.unapply)
          val commandes = value.commandes map( Commande(_ ,left,right))
         
          Some(new Perso(value.id, value.name, value.desc, stat, value.lvl, commandes.toList, left, right))
        case _ => None


  object WeaponJS:


    def unapply(weaponJS: WeaponJS): Option[Weapon] =
      weaponJS.stats match
        case StatJS(stat) =>
          val action = weaponJS.affects.toList map Affect.valueOf
          Some(Weapon(weaponJS.id, weaponJS.name,weaponJS.desc, weaponJS.lvl, stat, action))
        case _ => None


    def apply(weapon: Weapon): WeaponJS =
      val ret = Export.apply(weapon: StatsWithName with Lvl)
      ret.affects = weapon.affects.map(_.toString).toJSArray
      ret.asInstanceOf[WeaponJS]

  trait NameIdStat extends js.Object:
    val lvl: Int
    val name: String
    val id: Int
    val desc: String
    val stats: StatJS
   

  trait JSCompanion[T]:
    type R

    def unapply(t: R): Option[T]

    def apply(R: T): R

  object StatJS:


    def apply(stats: IntBaseStat): StatJS =
      js.Dynamic.literal(
        hp = stats.hp,
        sp = stats.sp,
        viv = stats.viv,
        vit = stats.vit,
        chc = stats.chc,
        mag = stats.mag,
        res = stats.res,
        psy = stats.psy,
        str = stats.str,
      ).asInstanceOf[StatJS]

    def unapply(value: StatJS): Option[IntBaseStat] =
      Some(AnyRefBaseStat[Int](value.hp,
        value.sp,
        value.viv,
        value.str,
        value.mag,
        value.vit,
        value.psy,
        value.res,
        value.chc))

  trait StatJS extends js.Object:
    val hp: Int
    val sp: Int
    val viv: Int
    val str: Int
    val mag: Int
    val vit: Int
    val psy: Int
    val res: Int
    val chc: Int


  trait PersoJS extends NameIdStat:

    val leftHandWeapon: js.UndefOr[WeaponJS]
    val rightHandWeapon: js.UndefOr[WeaponJS]
    val commandes : js.Array[String]

  trait WeaponJS extends NameIdStat:
    val affects : js.Array[String]
