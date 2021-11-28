package bon.jo.rpg.ui

import bon.jo.rpg.ui.Export.{PersoJS, WeaponJS}
import bon.jo.dao.LocalJsDao.MappedDao
import bon.jo.html.DomShell.{ExtendedElement, ExtendedHTMLCollection}
import bon.jo.html.HTMLDef.{$c, $ref, HtmlOps}
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep
import bon.jo.html.HtmlRep.PrXmlId
import bon.jo.html.DomBuilder.html
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
import bon.jo.rpg.resolve.PersoHateResolveFactory
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
import scala.concurrent.Future
import bon.jo.html.PopUp
import bon.jo.html.DomBuilder.html.$
import  bon.jo.rpg.resolve.ResolveFactory
import bon.jo.rpg.dao.ImageJs
import bon.jo.rpg.dao.ImageDao




trait Rpg extends Ec with ArmesPage with RpgSimuPage with AffectFormuleResolver:
  def createButton(addRandomButton: Button): Unit
  def init(): HTMLElement

  val onChangePage = ListBuffer[()=>Unit]()
  val weaponDao: MappedDao[WeaponJS, Weapon,Int] with WeaponDao
  val persoDao: MappedDao[PersoJS, Perso,Int] with PersoDao
  val formuleDao: MappedDao[FormuleJs, Formule,(Affect,FormuleType)] with FormuleDao 
  val iamgeDao: MappedDao[ImageJs, Image,String] with ImageDao
  given Dao[bon.jo.rpg.resolve.Formule,(bon.jo.rpg.Affect, bon.jo.rpg.resolve.FormuleType)] =( formuleDao:  Dao[Formule,(Affect,FormuleType)] ) 
  val deckCreation: Div =
 
    $c.div[Div]




  import bon.jo.rpg.stat.Perso.given
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
    
   

    


    FormuleDao.checkFormule.map{
      missingCount => 
        if(missingCount == 0){
          
          formulesMap.map{
          ( f  :  Map[Formule.ID,Formule] )=>
          given Map[Formule.ID,Formule] =f
          given ResolveContext = new resolve.DefaultResolveContext{
            override def attaqueResolve:AttaqueResolve = (new PersoAttaqueResolve{}).createResolve
            override def slowResolve:SlowResolve = (new PersoSlowPersoFactory{}).createResolve

            override def caffeinResolve:CaffeinResolve = 
              new ResolveFactory(Affect.Caffein){}
              .createResolve.asInstanceOf[CaffeinResolve]
            override def hateResolve:HateResolve = (new PersoHateResolveFactory{}).createResolve
          }
          go

    }

        }
    }

    def end(winner : Team,losser : List[Team]): Unit = 
      root.clear()
      import html.$
      val win = $.div{
        $.text(s"Winner : ${winner.name}")
      }
      root.appendChild(win)
        

 //   given Resolver[Perso, Perso,Action.Attaque.type] = CalculsPersoPerso
    def go(using ResolveContext):Unit=
  
      val linkedUI = new WithUI()
      import linkedUI.given
     
      def nomansLand = $ div {$ _class "nomans"}
     
      val area = $ div {$ _class "area"}
      cpntMap = timeLine.timedObjs.map{ v =>
        val htmlCpnt = v.value.asInstanceOf[Perso].html
        (v.id,htmlCpnt)
      }.toMap
      val teamMap = timeLine.timedObjs.map{v =>
        (v.id,v.team)
      }.toMap
      val teamCpntMap = teamMap.values.toSet.map(t => t -> ($ div {
        $ _class "team-cont" 
        $.attr("id" -> t.name) 
      })).toMap
      val teamCpnt = teamCpntMap.values.iterator.to(List)
      area += teamCpnt(0)
      teamCpnt.drop(1).foreach{
        team =>
        area += nomansLand
        area += team
      
      }
      cpntMap.map{
        (k,v) =>
          val teamCpnt = teamCpntMap(teamMap(k))
          teamCpnt ++= v.get.iterator.to(List)
      }
      root += area
      val cpntTimeLine = new TimeLineCpnt( linkedUI,end)
      root.appendChild(cpntTimeLine.tlView)
      root.style.maxWidth = "80%"
      //cpntMap.flatMap(_._2.get).foreach(e => root += e)

      clearUI

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

