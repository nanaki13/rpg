package bon.jo.rpg.ui

import bon.jo.rpg.ui.Export.{PersoJS, WeaponJS}
import bon.jo.dao.LocalJsDao.MappedDao
import bon.jo.html.DomShell.{ExtendedElement, ExtendedHTMLCollection}
import bon.jo.html.HTMLDef.{$c, $ref, HtmlOps}
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep
import bon.jo.html.HtmlRep.PrXmlId
import bon.jo.rpg.BattleTimeLine.{TimeLineParam,TimeLineOps}
import bon.jo.rpg.dao.{PersoDao, WeaponDao}

import bon.jo.rpg.stat.Perso.WithUI
import bon.jo.rpg.stat.Perso.given
import bon.jo.rpg.stat._
import bon.jo.rpg.resolve.PersoResolveContext._
import bon.jo.rpg.stat.raw.{Perso, Weapon}
import bon.jo.util.Ec
import org.scalajs.dom.document
import org.scalajs.dom.html.{Button, Div}

import scala.collection.mutable.ListBuffer
import bon.jo.rpg.stat.GameId
import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg._
import bon.jo.rpg.ui.page.ArmesPage
import bon.jo.rpg.resolve.given
import bon.jo.rpg.dao.FormuleJs
import bon.jo.rpg.resolve.FormuleType
import bon.jo.rpg.resolve.Formule
import bon.jo.rpg.resolve.PersoAttaqueResolve
import bon.jo.rpg.resolve.PersoSlowPersoFactory
import bon.jo.rpg.dao.FormuleDao
import org.scalajs.dom.raw.HTMLElement
import bon.jo.rpg.AffectResolver.AffectFormuleResolver
import bon.jo.dao.Dao
import bon.jo.rpg.dao.IntMappedDaoType
import bon.jo.rpg.ui.GameParams.given
import bon.jo.rpg.ui.TimeLineCpnt
import bon.jo.rpg.ui.PerCpnt
import bon.jo.rpg.ui.HtmlUi
import bon.jo.rpg.ui.page.RpgSimuPage






trait Rpg extends Ec with ArmesPage with RpgSimuPage with AffectFormuleResolver:
  def createButton(addRandomButton: Button): Unit
  def init(): HTMLElement

  val onChangePage = ListBuffer[()=>Unit]()
  val weaponDao: MappedDao[WeaponJS, Weapon,Int] with WeaponDao
  val persoDao: MappedDao[PersoJS, Perso,Int] with PersoDao
  val formuleDao: MappedDao[FormuleJs, Formule,(Affect,FormuleType)] with FormuleDao 

  val deckCreation: Div =
 
    $c.div[Div]




  given Timed[GameElement] =bon.jo.rpg.stat.Perso.PeroPero.asInstanceOf[Timed[GameElement]]
  given timeLine: TimeLineOps = TimeLineOps()
  val root = $ref div {
    d =>
      d._class = "container-fluid pt-5"


  }
  document.getElementsByTagName("app-rpg").foreach { e =>
    e.innerHTML = ""
    e += root
  }
  
  
  var cpntMap: Map[GameId.ID,  PerCpnt] = _
  def clearUI(using ui : HtmlUi)=
    ui.choice.clear()
    ui.messageDiv.clear()
    root.appendChild(ui.choice)
    root.appendChild(ui.messageDiv)
  def startRpg =
    given Rpg = this
    given HtmlUi = new HtmlUi{}
    given HtmlRep[Perso, PerCpnt] = HtmlUi.PersoRep
    
   

    given Dao[bon.jo.rpg.resolve.Formule,(bon.jo.rpg.Affect, bon.jo.rpg.resolve.FormuleType)] =( formuleDao:  Dao[Formule,(Affect,FormuleType)] ) 

 

   

    formulesMap.map{
      ( f  :  Map[Formule.ID,Formule] )=>
        given Map[Formule.ID,Formule] =f
        given ResolveContext = new resolve.DefaultResolveContext{
          override def attaqueResolve:AttaqueResolve = (new PersoAttaqueResolve{}).createResolve
          override def slowResolve:SlowResolve = (new PersoSlowPersoFactory{}).createResolve
        }
        go

    }
 //   given Resolver[Perso, Perso,Action.Attaque.type] = CalculsPersoPerso
    def go(using ResolveContext):Unit=
  
      val linkedUI = new WithUI()
      import linkedUI.given
      
      cpntMap = timeLine.timedObjs.map{ v =>
        val htmlCpnt = v.value.asInstanceOf[Perso].html
        (v.id,htmlCpnt)
      }.toMap
      

      root.style.maxWidth = "80%"
      cpntMap.flatMap(_._2.get).foreach(e => root += e)

      clearUI
      val cpntTimeLine = new TimeLineCpnt( linkedUI)
      root.appendChild(cpntTimeLine.tlView)
      cpntTimeLine.tlView.$userCanDrag()
      cpntTimeLine.doEvent()
      onChangePage += (() => timeLine.stop())





  /*initChoiXperso.$click { _ =>
    persosForGame.map(_.read).foreach(e => {
      e.randomWeapon()
      yl.add(e)
    })
    root.clear()
    startRpg
  }*/

