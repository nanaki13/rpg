package bon.jo.rpg.ui.edit


import bon.jo.html.HtmlRep.HtmlCpnt
import bon.jo.html.{HtmlRep, Selection}
import bon.jo.html.SimpleView

import bon.jo.rpg.stat.raw.Perso
import org.scalajs.dom.document
import org.scalajs.dom.html.{Div, Span}
import org.scalajs.dom.raw.{Element, HTMLElement, Node}
import bon.jo.html.ImuutableHtmlCpnt
import bon.jo.html.DomBuilder.*
import bon.jo.rpg.ui.Rpg
import bon.jo.rpg.ui.HtmlUi.PersoRep
import bon.jo.html.DomShell.ExtendedElement
import org.scalajs.dom.console
import bon.jo.rpg.ui.edit.EditPersoCpnt
import bon.jo.rpg.ui.edit.EditPersoCpnt.given
import bon.jo.rpg.ui.edit.EditStatWithName
import scala.language.dynamics
import bon.jo.html.DomBuilder.html.$
import bon.jo.html.DomBuilder.html.$t
import bon.jo.html.DomBuilder.html.$t.*
import bon.jo.rpg.Affect
import org.scalajs.dom.raw.HTMLSelectElement
import bon.jo.rpg.stat.Actor
import bon.jo.rpg.stat.AnyRefBaseStat
import bon.jo.rpg.RandomName
import bon.jo.html.HtmlRep.PrXmlId

import scala.collection.mutable.ListBuffer
import bon.jo.rpg.util.Script.*
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.rpg.stat.AnyRefBaseStat
import bon.jo.rpg.resolve.FormuleType
import org.scalajs.dom.raw.HTMLOptionElement
import bon.jo.rpg.resolve.Formule

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import bon.jo.html.PopUp
import bon.jo.rpg.ui.edit.EditStatWithName
import bon.jo.rpg.ui.Rpg
import bon.jo.rpg.ui.Image
object EditFormauleAffect:
      extension (affect : Affect)


        def optionHtml : HTMLOptionElement =  $t.option[HTMLOptionElement]{
          text(affect.name)
          value(affect)
        }
      def simulation(using rpg : Rpg,ec : ExecutionContext) : Unit =
        
        given HTMLElement = rpg.root
        val dao = rpg.formuleDao
        val result : Div =  $t.div[Div](_class("border-b black-s-on-white"))
      
        var formuleObj  : Formule = Formule(Affect.values.head,FormuleType.Degat,"att.str")
        val formule : Div = $t div {
          _class("border-b black-on-white")
          doOnMe(_.contentEditable="true")
         
        }

        val att: Perso = Actor.randomActor(e => new Perso(1, RandomName(),"J'attaque", e,Image("")))

        val deff: Perso = Actor.randomActor(e => new Perso(2, RandomName(),"Je défend", e,Image("")))
        val buff = ListBuffer.empty[EditStatWithName[Perso]]

        val List(attCpnt,deffCpnt) =  List(att,deff).map(_.htmlp((summon[Rpg],buff)))
        def runExp:Unit = 
            import bon.jo.common.give.given
            import bon.jo.rpg.resolve.Formule.given
            import bon.jo.rpg.stat.BaseState.ImplicitCommon.given
            println("phrase = "+formule.textContent.toPhrase)
            println("node = "+formule.textContent.toNode)
            println("expression = "+formule.textContent.toExpression)
            
           
            
            val f =  formule.textContent.toFunction[(AnyRefBaseStat.Impl[Int],AnyRefBaseStat.Impl[Int])]()
            result.textContent = f((attCpnt.read.stats.to[AnyRefBaseStat.Impl[Int]],deffCpnt.read.stats.to[AnyRefBaseStat.Impl[Int]])).toString

        def readDb = dao.read((formuleObj.affect,formuleObj.formuleType)).map{
          oFor => 
            formuleObj = oFor.getOrElse({
              saveDB
              formuleObj
            })
            formule.innerText = formuleObj.formule
            runExp
            
        }.run
        extension (f : Future[_])
            def run = f.onComplete{
              case Success(e) =>  e
              case Failure(f) =>  f.printStackTrace; console.log(f)
            }
            def runAnd(success:  => Unit,fail:  => Unit) = f.onComplete{
              case Success(e) =>  success
              case Failure(f) =>  f.printStackTrace; console.log(f);fail
            }
        def saveDB = dao.create(formuleObj).run
        def updateDB = dao.update(formuleObj).runAnd(PopUp("Formule sauvegardé"), PopUp("Formule non sauvegardé"))
        def optionFormuleType(formuleType : FormuleType) : HTMLOptionElement =  $t.option[HTMLOptionElement]{
          text(formuleType.toString)
          value(formuleType)
        }
        def saveButton() = $t.button[org.scalajs.dom.html.Button] {
          btnActive
          text("Save")
          click({
            formuleObj = formuleObj.copy(formule = formule.innerText)
            updateDB
          })
        }

        def playButton() = $t.button[org.scalajs.dom.html.Button] {
          btnActive
          text("▶️")
          click({
            runExp
          })
        }
        def newFormuleTypeSelected(ftSelect : HTMLSelectElement):Unit = 
          val formuleType =FormuleType.valueOf(ftSelect.value)
          formuleObj = formuleObj.copy(formuleType =  formuleType)
          readDb
          //dao.
        val selectFormuleTypeSelect = $t.select[HTMLSelectElement]{
          childs(Affect.values.head.formuleTypes.map(optionFormuleType).toSeq: _ * )
          onchange[HTMLSelectElement](newFormuleTypeSelected)
          }
          

       
        
        def newAffectSelected(affectSelect : HTMLSelectElement):Unit = 
          selectFormuleTypeSelect.clear()
          val affect = Affect.valueOf(affectSelect.value)
          val formuleType = affect.formuleTypes.head
          formuleObj = formuleObj.copy(affect,formuleType)
          Affect.valueOf(affectSelect.value).formuleTypes.map(optionFormuleType).foreach(selectFormuleTypeSelect.appendChild)
          readDb



        val selectAffectSelect : HTMLSelectElement = $t.select[HTMLSelectElement]{
          childs(Affect.values.map(_.optionHtml): _ * )
          onchange[HTMLSelectElement](newAffectSelected)

        }




        def formuleCont = 
          def row1 = $.div{
            row
            cols(selectAffectSelect,selectFormuleTypeSelect)
          }
          def row2 = $.div{
            row
            cols(formule)
          }
           def row3 = $.div{
            row
            cols(playButton(),result,saveButton())
          }
          $.div{
            col
            childs(row1,row2,row3)
          }
        val chil = attCpnt.list ++ deffCpnt.list :+ formuleCont
        childs(
          $.div{
             row
             cols(chil : _ *)
          },
           $.div{
             row
             cols($.div(childs( saveButton())))
          }
        )
        
       
        formule.$keyup{
           kv => 
            if(kv.keyCode == 13){
              console.log( org.scalajs.dom.window.getSelection.focusNode.previousSibling.appendChild($.tNode("\n")))
            }
            runExp
        }



        readDb
      end simulation



        

