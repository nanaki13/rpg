package bon.jo.rpg.ui.page

import bon.jo.rpg.ui.Export.{PersoJS, WeaponJS}
import bon.jo.rpg.ui.Rpg
import bon.jo.dao.IndexedDB.DBExeception

import bon.jo.html.DomShell.ExtendedElement
import bon.jo.html.HTMLDef.HtmlOps
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep.HtmlRepParam
import bon.jo.html.HtmlRep.*
import bon.jo.html.SimpleView
import bon.jo.html.PopUp
import bon.jo.rpg.RandomName
import bon.jo.rpg.dao.PersoDao
import bon.jo.rpg.stat.Actor.Id
import bon.jo.rpg.stat.StatsWithName
import bon.jo.rpg.stat.raw.{Actor, Perso, Weapon}
import bon.jo.rpg.ui.{  SType}
import bon.jo.rpg.ui.edit.{EditPersoCpnt, EditStatWithName, EditWeaponCpnt}
import bon.jo.util.Ec
import org.scalajs.dom.console
import org.scalajs.dom.html.Button

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.util.{Failure, Success}
import bon.jo.rpg.ui.Rpg
import bon.jo.rpg.dao.IntMappedDaoType
import bon.jo.rpg.ui.Image
trait EditPage[A <: StatsWithName, B <: scalajs.js.Object] extends Ec:
  type Hrep = HtmlRepParam[A, SType.Param[A], EditStatWithName[A]]
  implicit val v: Hrep

  val cpnts: mutable.ListBuffer[EditStatWithName[A]] = mutable.ListBuffer.empty[EditStatWithName[A]]

  val rpg: Rpg
  val dao: IntMappedDaoType[B,A]
  val addRandomButton: Button = SimpleView.bsButton("+")
  addRandomButton.$click{
    _ => addRandom()
  }
  def random(): A

  def addRandom(): Unit =
    val p = random()
    val persoCpnt: EditStatWithName[A] = p.htmlp(rpg -> cpnts)
    cpnts += persoCpnt
    rpg.deckCreation ++= persoCpnt.list
    persoCpnt.head.asInstanceOf[js.Dynamic].scrollIntoView(js.Dynamic.literal(
      behavior= "smooth"
    ))

  def init(implicit ct: ClassTag[A]): Unit =
    cpnts.clear()
    rpg.deckCreation.clear()
    rpg.root += rpg.deckCreation

    dao.initId.map {
      _ =>

        dao.readAll().onComplete {

          case Failure(exception) => console.log(exception)
          case Success(value) => {

            value.foreach((w: A) => {
              val htmlCpnt = w.htmlp(rpg -> cpnts)
              cpnts += htmlCpnt
              rpg.deckCreation ++= htmlCpnt.list
            })


          }
        }

        val saveB = SimpleView.bsButton("save")

        saveB.$click { _ =>
          val ops: mutable.Seq[Future[Unit]] = cpnts.map(e => (e, e.read)).map {
            case (view, v) =>
              if v.id == 0 then
                (view, v.withId[A](id = Id[A]), dao.create _)
              else {
                (view, v, dao.update(_, None))
              }: (EditStatWithName[A], A, A => dao.FO)
          }.map { case (view, w, fw) => (view, fw(w)) }.map {
            case (view, e) =>
              e.map { value =>
                view.update(value)
              }
          }
          Future.sequence(ops) onComplete {
            case Success(_) => PopUp("Sauvegarde OK")
            case Failure(exception) =>
              PopUp("Sauvegarde KO")
              exception.printStackTrace()
              exception match
                case DBExeception(e) => console.log(e)
                case _ =>
          }
        }


        rpg.root += saveB


    } onComplete {
      case Failure(exception) => {
        scalajs.js.special.debugger()
        console.log(exception)
      }
      case Success(value) =>
    }
    rpg.createButton(addRandomButton)


trait ArmesPage:
  self: Rpg =>


  def initChoixArme(): Unit =
    val page = new EditPage[Weapon, WeaponJS] {
      override implicit val v: Hrep = EditWeaponCpnt.Implicit.value

      override val rpg: Rpg = self

      override val dao:IntMappedDaoType[WeaponJS,Weapon] = rpg.weaponDao

      override def random(): Weapon = Actor.randomWeapon()

      override implicit val executionContext: ExecutionContext = self.executionContext
    }
    page.init

  def initChoixPerso(): Unit =
    val page = new EditPage[Perso, PersoJS] {
      override implicit val v: Hrep = EditPersoCpnt


      override val rpg: Rpg = self
      override val dao: IntMappedDaoType[PersoJS, Perso] with PersoDao = rpg.persoDao

      override def random(): Perso = Actor.randomActor(e => new Perso(0, RandomName(),"Le plus beau des héros", e,Image("")))

      override implicit val executionContext: ExecutionContext = self.executionContext
    }
    page.init
