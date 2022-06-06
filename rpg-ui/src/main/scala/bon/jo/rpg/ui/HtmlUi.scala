package bon.jo.rpg.ui

import bon.jo.rpg.ui.HtmlUi.given
import bon.jo.html.DomShell.ExtendedElement
import bon.jo.html.HTMLDef.$c
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep
import bon.jo.html.HtmlRep.PrXmlId
import bon.jo.html.SimpleView


import bon.jo.rpg.raw._
import bon.jo.rpg.stat.raw.Perso.PlayerPersoUI
import bon.jo.rpg.stat.raw._
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.ui.PerCpnt
import bon.jo.ui.UpdatableCpnt
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}
import bon.jo.html.HTMLDef.HtmlOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.util.Success
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.GameId
import bon.jo.rpg.SystemElement
import bon.jo.rpg.CommandeCtx
import bon.jo.rpg.Commande
import bon.jo.rpg.CommandeCtx.CommandeCibled
import bon.jo.rpg.CommandeCtx.CommandeWithoutCibled
import bon.jo.html.ImuutableHtmlCpnt
import bon.jo.rpg.ui.Rpg
import bon.jo.rpg.ui.SimpleMessage
object HtmlUi:
  object ActionRep extends HtmlRep[SystemElement, ImuutableHtmlCpnt]:
    override def html(memo: SystemElement): ImuutableHtmlCpnt = () => Some(SimpleView.bsButton(s"${memo.name}"))

  given HtmlRep[SystemElement, ImuutableHtmlCpnt] = ActionRep




  implicit object PersoRep extends HtmlRep[Perso, PerCpnt]:
    override def html(memo: Perso): PerCpnt = new PerCpnt(memo)



trait HtmlUi( using rpg : Rpg) extends PlayerPersoUI with SimpleMessage:

 
  val choice: Div = $c.div

  override def ask(asker: TimedTrait[GameElement], cible: List[TimedTrait[GameElement]]): Future[CommandeCtx] =

  
    choice.clear()
    val p: Promise[Commande] = Promise[Commande]()
    asker.canChoice.map(a => a -> a.html).foreach {
      case (action, cpnt) =>
        lazy val evL: Seq[(js.Function1[MouseEvent, _], HTMLElement)] = cpnt.list.map {
          e =>
            val ev = e.$click { _ =>
              if !p.isCompleted then
                evL.foreach {
                  case (value, element) => element.removeEventListener("click", value)
                }
                p.success(action)



              choice.clear()
            }
            choice.appendChild(e)
            (ev, e)

        }
        evL

    }
    val ret = Promise[CommandeCtx]()
    p.future.foreach {
      case action@(c : Commande.Combo.type ) => askCombo(action,asker , cible, ret)
      case action@(c : Commande.Attaque ) =>
        askAttaque(action,asker , cible, ret)
      case action: Commande => ret.tryComplete(Success(new CommandeWithoutCibled(action)))

    }
    ret.future
  inline def askCombo(action : Commande,asker: TimedTrait[GameElement], cible: List[TimedTrait[GameElement]],ret : Promise[CommandeCtx]) :Unit = 
    asker.team
    val sameTeam = cible.filter(cb => cb.team == asker.team && cb.canChoice.contains(Commande.Combo))
    val messagep = message("cliquer sur les participants et valider")
    choice.clear()
    val ok = SimpleView.bsButton(s"ok")
    choice += ok
    ok.$click{ _ => 
      clear(messagep)
      ret.tryComplete(Success(new CommandeWithoutCibled(action)))
    }
    

  inline def askAttaque(action : Commande, asker: TimedTrait[GameElement], cible: List[TimedTrait[GameElement]],ret : Promise[CommandeCtx]) :Unit = 
    val pp = Promise[TimedTrait[GameElement]]()
    val messagep = message("cliquer sur un cible")
    lazy val allEvent: Seq[(HTMLElement, js.Function1[MouseEvent, _])] = asker.value[GameElement] match
      case p: Perso => cible.flatten { v => {    
        v.value[GameElement] match
          case b: Perso => {

            val eAndView = rpg.cpntMap(v.id)
          
            lazy val hAndEvent: Seq[(HTMLElement, js.Function1[MouseEvent, _])] = eAndView.list.map {
              h =>

                //    h._class += " btn btn-primary"
                h.style.cursor = "pointer"
                h -> h.$click { _ =>
                  if !pp.isCompleted then
                    allEvent.foreach {
                      case (element, value) =>
                        element.removeEventListener("click", value)
                        h.style.cursor = ""
                      //                              element.classList.remove("btn")
                      //                              element.classList.remove("btn-primary")
                    }
                    clear(messagep)
                    pp.success(v.asInstanceOf[TimedTrait[GameElement]])


                  // h.removeEventListener("click", c)

                }
            }


            pp.future.foreach(sel => if !ret.isCompleted then {
              ret.success(new CommandeCibled(action, List(sel.id)))
            })
            hAndEvent
          }
      }
      }
      case _ => Nil
    allEvent

  override def cpntMap: GameId.ID => UpdatableCpnt[BattleTimeLine.TPA] = a =>{
      o => {
        o map (v =>  rpg.cpntMap(v.id).update(Some(v.value.asInstanceOf[Perso])))
      
      }
  }
     
  


