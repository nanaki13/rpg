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
trait RpgSimuPage:
    self : Rpg =>
      def simulation() : Unit =
        val enSelButton = SimpleView.bsButton("Start")
        val selected : Div = $c.div
        val select : Div = $c.div

        val cont = $c.div[Div].$row( select,selected,enSelButton.wrap(tag.div) )
        cont.classList.add("mt-5")
        cont.classList.add("ml-5")
        root += cont
        val rep : HtmlRep[Perso,ImuutableHtmlCpnt] = ((z : Perso) => () => Some(z.name.tag(tag.div)))
        persoDao.readAll() flatMap  {
         persos =>
            
           Selection
             .Param[Perso,PerCpnt](Some(enSelButton),selected ++= _.list,PersoRep,PersoRep)
             .selection(persos,select)
        } foreach {
         selection =>
         
           try
            selection.foreach(e => timeLine.add(e))
            root.clear()
        
           catch
            case e: Exception => console.log(e)

           startRpg
        }

