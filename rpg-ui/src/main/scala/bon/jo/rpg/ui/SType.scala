package bon.jo.rpg.ui

import bon.jo.rpg.ui.Rpg
import bon.jo.dao.Dao
import bon.jo.html.PopUp
import bon.jo.rpg.stat.StatsWithName
import bon.jo.rpg.ui.edit.EditStatWithName
import org.scalajs.dom.raw.HTMLElement
import bon.jo.html.SimpleView
import scala.collection.mutable
import scala.util.{Failure, Success}
import scalajs.js
import org.scalajs.dom.console
object SType:
  type Param[A <: StatsWithName] = (Rpg, mutable.ListBuffer[EditStatWithName[A]])
  extension[A <: StatsWithName] (e: Option[Param[A]])
    def rpg: Rpg = e.map(_._1).getOrElse(throw new IllegalStateException())

  trait EditStatWithDao[A <: StatsWithName]:
    this: EditStatWithName[A] =>
    val dao: Dao[A, Int]

    override def deleteButton(): Option[HTMLElement => HTMLElement] = option.map(_._1.executionContext) map {
      implicit ec =>
        SimpleView.withClose(_, {
          console.log(read.id)
          dao.delete(read.id) onComplete {
            case Success(value) => PopUp("Suppression OK")
            case Failure(exception) => 
              console.log(exception)
              PopUp("Suppression KO")
          }
        }, "top-right")
    }
