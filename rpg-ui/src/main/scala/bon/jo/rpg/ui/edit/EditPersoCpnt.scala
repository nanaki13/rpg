package bon.jo.rpg.ui.edit

import bon.jo.html.DomBuilder.*
import bon.jo.html.DomBuilder.html.*
import bon.jo.html.DomBuilder.html.$.*
import bon.jo.rpg.ui.Rpg
import bon.jo.rpg.ui.SType
import bon.jo.rpg.ui.SType.*
import bon.jo.rpg.ui.edit.Types.Pram
import bon.jo.rpg.ui.edit.EditStatWithName
import bon.jo.rpg.ui.edit.EditStat
import bon.jo.dao.Dao
import bon.jo.html.DomShell.ExtendedElement
import bon.jo.html.HTMLDef.*
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep
import bon.jo.html.HtmlRep.HtmlRepParam
import bon.jo.html.SimpleView.bsButton
import bon.jo.rpg.{Affect, RandomName}
import bon.jo.rpg.stat.raw.{Actor, IntBaseStat, Perso, Weapon}
import org.scalajs.dom.html.{Button, TextArea}
import org.scalajs.dom.raw.{HTMLElement, HTMLLIElement, HTMLUListElement}

import scala.language.dynamics
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import bon.jo.rpg.Affect
import bon.jo.rpg.SystemElement
import bon.jo.rpg.Commande
import bon.jo.rpg.ui.Rpg
           
object Types:
  type Pram = SType.Param[Perso]

object EditPersoCpnt extends HtmlRepParam[Perso, Pram, EditStatWithName[Perso]]:


  override def html(memo: Perso, option: Option[Pram]): EditPersoCpnt =
    new EditPersoCpnt(memo, option)(EditStat)

  given HtmlRepParam[Perso, Pram, EditStatWithName[Perso]] = this



class EditPersoCpnt(initial: Perso, option: Option[(Rpg, mutable.ListBuffer[EditStatWithName[Perso]])])(repStat: HtmlRep[IntBaseStat, EditStat])
extends EditStatWithName[Perso]  (
  initial, option)(repStat) with SType.EditStatWithDao[Perso]:
  override implicit val rep: HtmlRepParam[Perso, Pram, EditStatWithName[Perso]] = EditPersoCpnt

  override def randomValue: Perso = Actor.randomActor(e => new Perso(initial.id, RandomName(),"Le plus beau des héros", e))
  override val  dao: Dao[Perso, Int] = option.rpg.persoDao
  val equipRight: Button = bsButton("+")
  val equipLeft: Button = bsButton("+")
  private var varRightHand: Option[Weapon] = initial.rightHandWeapon
  private var varLeftHand: Option[Weapon] = initial.leftHandWeapon


  def getAction(str: String): Option[bon.jo.rpg.SystemElement] = 
    Some(Commande(str,varLeftHand,varRightHand))
  def readAction(p: Perso): Iterable[bon.jo.rpg.SystemElement] = p.commandes
  def equipAction(addButton: Button, updateTitle: HTMLElement)(optionF: Option[Weapon] => Unit) =
    addButton.$click { _ =>
      option foreach {
        (rpg, value) =>
          given ExecutionContext = rpg.executionContext
          val ul = $c.ul[HTMLUListElement]
          rpg.weaponDao.readAll().map {
            e =>
           
              e.map(w => w -> $.li{
                text(w.name)
                _class("list-group-item btn")
                click( {
                   val s = Some(w)
                      optionF(s)
                      updateTitle.textContent = txt(s)
                      ul.removeFromDom()
                })
              })

          } foreach {
            ws =>
              val sel = ws.toList.map(_._2).foldLeft(ul)(_ += _)
              sel.style.height = "5em"
              sel.style.overflowY = "scroll"
              sel._class = "list-group"
              beforeState(sel)
          }

      }
    }

  def initialAction(initial: Perso):Iterable[SystemElement] =
    Commande.staticValues ++ initial.armesCommandes()


  def txt(optionW: Option[Weapon]): String = optionW.map(_.name).getOrElse("-")

  def spanArm(optionW: Option[Weapon]): HTMLElement = 
    $ span {
      col
      _class("black-on-white")
      text(txt(optionW))
    }
      


  private val leftArm = spanArm(initial.leftHandWeapon)
  private val rightArm = spanArm(initial.rightHandWeapon)
  private val handsCont = $va div List(leftArm, equipLeft, rightArm, equipRight)

  override def create(id: Int, name: String,desc : String, intBaseStat: IntBaseStat, action: List[SystemElement]): Perso =
    new Perso(id, name,desc, intBaseStat, lvl = 1, action.asInstanceOf[List[Commande]], leftHandWeapon = varLeftHand, rightHandWeapon = varRightHand)

  override def beforeStatOption: Option[HTMLElement] = Some( $va div List(handsCont))


  equipAction(equipRight, rightArm) {
    r =>
      varRightHand = r
      varRightHand foreach (_ => updateAction(read))
  }
  equipAction(equipLeft, leftArm){
    r =>
      varLeftHand = r
      varLeftHand foreach  ( _ => updateAction(read))
  }
