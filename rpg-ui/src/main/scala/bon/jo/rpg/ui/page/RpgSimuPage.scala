package bon.jo.rpg.ui.page

import bon.jo.html.HTMLDef.{$c, $ref, $va, HtmlOps}
import bon.jo.html.HtmlRep.HtmlCpnt
import bon.jo.html.{HtmlRep, Selection}
import bon.jo.html.SimpleView
import bon.jo.rpg.stat.raw.Perso
import org.scalajs.dom.document
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{Element, HTMLElement, Node}
import bon.jo.html.ImuutableHtmlCpnt
import bon.jo.html.DomBuilder._
import bon.jo.rpg.ui.HtmlUi.PersoRep
import bon.jo.html.DomShell.ExtendedElement
import org.scalajs.dom.console
import scala.language.dynamics
import bon.jo.rpg.ui.PerCpnt
import bon.jo.rpg.ui.Rpg
import bon.jo.rpg.Team
import bon.jo.rpg.dao.FormuleDao
trait RpgSimuPage:
    self : Rpg =>
      val teams = List(Team("Blue"), Team("Red"))
      def simulation() : Unit =
        FormuleDao.checkFormule.map{
          missingCount => 
            if(missingCount == 0){
              start()
            }
        }
      def start():Unit =
        val enSelButton = SimpleView.bsButton("next team")
        val selected= html.$.div{html.$.row}
       
        val select  = html.$.div{html.$.row}
        selected.style.minHeight = "25em"
        select.style.minHeight = "25em"
        val title : Div  = html.$t.div{
          html.$.row
          html.$t.text[Div](s"Team Blue")
        }
        val cont = html.$.div{
         
          html.$.childs(title, select,selected,enSelButton.wrap(tag.div) )
        }
        cont.classList.add("mt-5")
        cont.classList.add("ml-5")
        root += cont
        //val rep : HtmlRep[Perso,ImuutableHtmlCpnt] = ((z : Perso) => () => Some(z.name.tag(tag.div)))
        persoDao.readAll() flatMap  {
         persos =>
            
           Selection
             .Param[Perso,PerCpnt](Some(enSelButton),selected ++= _.list,PersoRep,PersoRep)
             .selection(persos,select).map(s =>
              (s,teams(0)) 
              ).flatMap{
                s1 =>
                  
                  selected.clear()
                  select.clear()
                  title.textContent ="Team red"
                  enSelButton.textContent = "Start"
                  Selection
                  .Param[Perso,PerCpnt](Some(enSelButton),selected ++= _.list,PersoRep,PersoRep)
                  .selection(persos,select).map(s =>
                  (s1, (s,teams(1)) )
                  )

              }
        } foreach {
         (s1,s2) =>
         
           try
            s1._1.foreach(e => timeLine.add(e,s1._2))
            s2._1.foreach(e => timeLine.add(e,s2._2))
            root.clear()
        
           catch
            case e: Exception => console.log(e)

           startRpg
        }

