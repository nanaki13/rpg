package bon.jo.rpg.ui.edit


import bon.jo.rpg.RandomName
import bon.jo.rpg.stat.Actor.Id
import bon.jo.rpg.stat.raw.{Actor, AnyRefBaseStat, IntBaseStat, Weapon}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import bon.jo.rpg.ui.SType.*
import bon.jo.dao.Dao
import bon.jo.html.HtmlRep
import bon.jo.html.HtmlRep.HtmlRepParam

import scala.language.dynamics
import bon.jo.rpg.SystemElement
import bon.jo.rpg.Affect
import bon.jo.rpg.ui.SType
import bon.jo.rpg.ui.edit.{EditStatWithName, EditStat}
object EditWeaponCpnt extends HtmlRepParam[Weapon, SType.Param[Weapon], EditStatWithName[Weapon]]:

  override def html(memo: Weapon, option: Option[SType.Param[Weapon]]): EditWeaponCpnt =
    new EditWeaponCpnt(memo, option)(EditStat)


  object Implicit:
    type Hrep = HtmlRepParam[Weapon, SType.Param[Weapon], EditStatWithName[Weapon]]
    implicit val value: Hrep = EditWeaponCpnt



class EditWeaponCpnt(initial: Weapon, option: Option[SType.Param[Weapon]])(repStat: HtmlRep[IntBaseStat, EditStat]) extends EditStatWithName[Weapon](initial,option)(repStat)
with SType.EditStatWithDao[Weapon]:
  override implicit val rep: HtmlRepParam[Weapon, SType.Param[Weapon], EditStatWithName[Weapon]] = EditWeaponCpnt

  override def randomValue: Weapon = new Weapon(initial.id,RandomName.randomWeaponName(),"La belle arme",1,AnyRefBaseStat[Float](Actor.randomWeaponVal _).map(_.round))

  override def create(id : Int,name: String,desc : String, intBaseStat: IntBaseStat, action: List[SystemElement]): Weapon = new Weapon(id,name,desc,1,intBaseStat,action.asInstanceOf[List[Affect]])

  override val dao: Dao[Weapon, Int] = option.rpg.weaponDao
  def initialAction(p: Weapon):Iterable[SystemElement] = Affect.values
  def readAction(p: Weapon): Iterable[bon.jo.rpg.SystemElement]  = p.affects
  def getAction(str: String): Option[SystemElement] = Some(Affect.valueOf(str))

