package bon.jo.rpg.dao

import bon.jo.rpg.ui.Export.WeaponJS
import bon.jo.dao.LocalJsDao
import bon.jo.rpg.dao.IntMappedDao
import bon.jo.dao.LocalJsDao.{MappedDao, MappedDaoImpl}
import bon.jo.rpg.stat.raw.Weapon
import bon.jo.util.Mapper

import scala.concurrent.ExecutionContext

trait WeaponDao extends IntMappedDao[WeaponJS, Weapon]:
  self: MappedDao[WeaponJS, Weapon,Int] =>

object WeaponDao {

  trait WeaponDaoJs extends LocalJsDao[WeaponJS,Int]:
    val name = "WeaponDao"
    val fId: WeaponJS => Int = _.id


  implicit object WeaponMapper extends Mapper[Weapon, WeaponJS]:
    override val map: Weapon => WeaponJS = WeaponJS.apply
    override val unmap: WeaponJS => Option[Weapon] = WeaponJS.unapply

  def apply(jsDao: WeaponDaoJs)(implicit executionContext: ExecutionContext): MappedDao[WeaponJS, Weapon,Int] with WeaponDao =
    new MappedDaoImpl(jsDao) with WeaponDao  with IntMappedDao[WeaponJS, Weapon]
}