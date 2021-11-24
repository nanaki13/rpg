package bon.jo.rpg.dao

import bon.jo.rpg.ui.Export.PersoJS
import bon.jo.dao.LocalJsDao
import bon.jo.dao.LocalJsDao.{MappedDao, MappedDaoImpl}
import bon.jo.rpg.stat.raw.Perso
import bon.jo.util.Mapper
import bon.jo.rpg.dao.IntMappedDao
import scala.concurrent.ExecutionContext

trait PersoDao  extends IntMappedDao[PersoJS, Perso]:
  self: MappedDao[PersoJS, Perso,Int] =>

object PersoDao {

  trait PersoDaoJs extends LocalJsDao[PersoJS,Int]:
    val name = "PersoDao"
    val fId: PersoJS => Int = _.id


  implicit object PersoMapper extends Mapper[Perso, PersoJS]:
    override val map: Perso => PersoJS = PersoJS.apply
    override val unmap: PersoJS => Option[Perso] = PersoJS.unapply

  def apply(jsDao: PersoDaoJs)(implicit executionContext: ExecutionContext): MappedDao[PersoJS, Perso,Int] with PersoDao =
    new MappedDaoImpl(jsDao) with PersoDao
}