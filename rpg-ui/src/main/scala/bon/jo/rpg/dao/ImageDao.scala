package bon.jo.rpg.dao

import bon.jo.dao.LocalJsDao
import bon.jo.dao.LocalJsDao.{MappedDao, MappedDaoImpl}
import bon.jo.rpg.dao.ImageJs
import bon.jo.rpg.ui.Image
import bon.jo.util.Mapper
import bon.jo.rpg.dao.IntMappedDao
import scala.concurrent.ExecutionContext

trait ImageDao  extends MappedDao[ImageJs, Image,String] 

object ImageDao {

  trait ImageDaoJs extends LocalJsDao[ImageJs,String]:
    val name = "ImageDao"
    val fId: ImageJs => String = _.path


  implicit object ImageMapper extends Mapper[Image, ImageJs]:
    override val map: Image => ImageJs = i => ImageJs.apply(i.path)
    override val unmap: ImageJs => Option[Image] = ImageJs.unapply

  def apply(jsDao: ImageDaoJs)(implicit executionContext: ExecutionContext): MappedDao[ImageJs, Image,String] with ImageDao =
    new MappedDaoImpl(jsDao) with ImageDao
}