package bon.jo.rpg.dao


import bon.jo.dao.LocalJsDao
import bon.jo.dao.LocalJsDao.{MappedDao, MappedDaoImpl}
import bon.jo.rpg.stat.raw.Perso
import bon.jo.rpg.resolve.FormuleType
import bon.jo.rpg.resolve.Formule
import FormuleType.{Degat,ChanceToSuccess}
import bon.jo.util.Mapper

import scala.concurrent.ExecutionContext
import bon.jo.rpg.Affect
import bon.jo.dao.Dao

trait FormuleDao extends Dao[Formule,(Affect,FormuleType)]
trait FormuleJs extends scalajs.js.Object:
  val affect : String
  val formuleType : String
  val formule : String
  

object FormuleDao {

  trait FormuleDaoJs extends LocalJsDao[FormuleJs,(Affect,FormuleType)]:
    val name = "FormuleDao"
    val fId: FormuleJs => (Affect,FormuleType) = e =>(Affect.valueOf(e.affect),FormuleType.valueOf(e.formuleType))


  implicit object FormuleMapper extends Mapper[Formule, FormuleJs]:
    override val map: Formule => FormuleJs = f => (scalajs.js.Dynamic.literal( affect = f.affect.id,formule = f.formule,formuleType = f.formuleType.toString ).asInstanceOf[FormuleJs])
    override val unmap: FormuleJs => Option[Formule] = f => Some(Formule(affect = Affect.valueOf(f.affect),formuleType =FormuleType.valueOf(f.formuleType) ,formule = f.formule))

  def apply(jsDao: FormuleDaoJs)(implicit executionContext: ExecutionContext): MappedDao[FormuleJs, Formule,(Affect,FormuleType)] with FormuleDao =
    new MappedDaoImpl(jsDao) with FormuleDao
}