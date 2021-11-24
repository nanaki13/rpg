package bon.jo.rpg



import bon.jo.rpg.*

import bon.jo.rpg.StdinUtil.fromStdin
import BattleTimeLine.*
import scala.concurrent.Future
import bon.jo.rpg.stat.GameId
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.Actor.Weapon
import CommandeCtx.*
import bon.jo.rpg.stat.Perso
import bon.jo.rpg.resolve.FormuleType


object CommandeCtx:

  def readCibleRec(cible: ITP[GameElement]):List[GameId.ID] =
    def f(t: TP[GameElement]) = t.simpleName
    List(fromStdin(cible, f,_.id))
  def fromStdIn(d: TPA): Commande =
    
    fromStdin(d.value[Perso].commandes)


  
  def fromStdIn(d: TPA, cible: LTPA): Future[CommandeCtx] =
    println(s"choisir commande de ${d.simpleName}")
    Future.successful(fromStdIn(d) match {
      case a @ Commande.Attaque(_,_) =>  a.fromStdIn(cible)
      case Commande.Garde =>Rien
      case Commande.Rien => Rien
      case _ =>Rien
    })


  


  object Rien extends CommandeCtx:
    override def cible = Nil
    val commande = Commande.Rien

  class CommandeCibled(val commande: Commande, val cible: Iterable[GameId.ID]) extends CommandeCtx
  class CommandeWithoutCibled(val commande: Commande) extends CommandeCtx{
    override def cible = Nil
  }

trait CommandeCtx:
  def commande: Commande

  def cible: Iterable[GameId.ID]
trait SystemElement:
  val name : String
  def id : String
enum LR:
  case L 
  case R
  val id = toString
object Commande:
  def staticValues =  List(Garde,Evasion,ChangerDequipement,Voler,Rien)
  def apply(commande : String):Commande =  staticValues.find(_.toString == commande).get
  def apply(commande : String,left : Option[Weapon],right:Option[Weapon]):Commande=
    (left,right) match
      case (None,None) => Commande(commande)
      case (l,r) => 
        commande match
          case LR.L.id if left.isDefined => Commande.Attaque(left.get,LR.L)
          case LR.R.id if right.isDefined => Commande.Attaque(right.get,LR.R)
          case _ => Commande(commande) 
      

enum Commande(val name : String) extends SystemElement:
  case Attaque(val weapon : Weapon,val hand : LR) extends Commande(weapon.name)  
  case Garde extends Commande("garde")  
  case Evasion extends Commande("évasion")  
  case ChangerDequipement extends Commande("changer d'équipement")  
  case Voler extends Commande("voler")  
  case Rien extends Commande("rien")  
  val id = this match 
    case  Attaque( weapon : Weapon, hand : LR) => hand.id
    case _ => toString
  
  def fromStdIn(cible: BattleTimeLine.LTP[GameElement]): CommandeCtx =
    new CommandeCibled(this, readCibleRec(cible))
    

abstract class Effect(val time : Int,val name : Affect):
  def -- : Effect 
case class FactorEffectt(override val time : Int ,factor : Float ,override val name : Affect) extends Effect( time , name ):
  def -- : Effect = copy(time-1)



enum Affect(val name : String,val vivMod:Option[Float] = None) extends SystemElement:


  given Affect.Soin.type = Affect.Soin
  //given Affect.Aoe.type = Affect.Aoe

  given Affect.Hate.type = Affect.Hate
  given Affect.Slow.type = Affect.Slow
  given Affect.Cancel.type = Affect.Cancel


  case Attaque extends Affect("attaque") 
  case Soin extends Affect("soin")  
  case Slow extends Affect("slow",Some(0.75f)) 
  case Hate extends Affect("hate",Some(1.75f)) 
  case Booster extends Affect("booster",Some(1.5f))  
  case Caffein extends Affect("caffein",Some(1.25f))   
  
  case Cancel extends Affect("cancel")  
  val id = toString


  def formuleTypes : Iterable[FormuleType] = this match
    case Affect.Attaque => Some(FormuleType.Degat)
    case _ => Some(FormuleType.ChanceToSuccess)



  def fromStdIn: Affect =
    fromStdin(Affect.values.toList)














