package bon.jo.rpg.dao

import bon.jo.dao.LocalJsDao.MappedDao
import scala.scalajs.js
import scala.reflect.ClassTag
import bon.jo.rpg.stat.Actor.Id
import scala.concurrent.Future

type IntMappedDaoType[A <: js.Object, B] = MappedDao[A, B, Int] with IntMappedDao[A,B] 
trait IntMappedDao[A <: js.Object, B]:
    this : MappedDao[A , B, Int]=>
      
    def createOrUpdate[AC <: B](a: AC)(implicit classTag: ClassTag[AC],setId : (el : AC,id : Int) => AC): FO =
      daoJs.fId(mapper.map(a)) match
        case 0 => {
          val id= Id[AC]

          create( setId(a,id))
        }
        case _ => update(a)
    def initId(implicit classTag: ClassTag[B]): Future[Unit] =
      readIds().map {
        case Nil => List(0)
        case e => e
      }.map(_.max).map(Id.init[B](_))
