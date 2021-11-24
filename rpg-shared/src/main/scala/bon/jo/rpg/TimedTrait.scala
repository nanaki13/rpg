package bon.jo.rpg

import bon.jo.rpg.CommandeCtx
import bon.jo.rpg.stat.raw
import bon.jo.rpg.stat.GameElement
import bon.jo.rpg.stat.GameId
import bon.jo.rpg.stat.GameId.ID.given
import java.util.concurrent.Flow
import bon.jo.rpg.TimedTrait.TimedObject
case class Mod(speedMod : Float = 1,cause : Effect)
object TimedTrait:
  private var id = 0

  
  given Ordering[TimedTrait[_]]  = (a,b) => GameId.ID.safe(a.id).compareTo(GameId.ID.safe(b.id))
  private def getId: GameId.ID =
    id += 1
    GameId.ID(id)
  
  case class TimedObject( _value: GameElement,id : GameId.ID,_pos : Int= 0,_commandeCtx : CommandeCtx,effetcts : List[Effect],modifiers :  List[Mod] = Nil)(using Timed[GameElement]) extends TimedTrait[GameElement]:
    val workerTimed: Timed[GameElement] = summon[Timed[GameElement]]
    override def withPos(i: Int): TimedTrait[GameElement] = copy(_pos = i)
    

    override def withCommandeCtx(i: CommandeCtx): TimedTrait[GameElement] = copy(_commandeCtx = i)
    def withValue[B <: GameElement](a: B): TimedTrait[B]  = copy(_value = a)
    def addEffect(effetct : Effect): TimedTrait[GameElement] = 
      val withMoid =  effetct match
        case FactorEffectt(e,fact,_) => copy(modifiers = modifiers :+ Mod(fact,effetct))
        case _ => this
      withMoid.copy(effetcts = effetcts :+ effetct)
  extension (value: GameElement)(using Timed[GameElement]) 
    def timed =  TimedObject(value,id   = getId,0,CommandeCtx.Rien,Nil)

trait TimedTrait[-A] {
  this : TimedObject =>
  val self = this
  val id : GameId.ID

  def stats : raw.IntBaseStat = _value.self.stats
  val effetcts : List[Effect]

  def withPos(i: Int): TimedTrait[A]
  def withValue[B <: A](a: B): TimedTrait[B]
  def addEffect(effetct : Effect): TimedTrait[A]
  
  def value[B <: GameElement] = _value.asInstanceOf[B]

  def cast[T ] = this.asInstanceOf[T]

  def speed: Int = (workerTimed.speed(value) * modifiers.map(_.speedMod).fold(1f)(_ * _)).round

  def canChoice: List[Commande]= workerTimed.canChoice(value)
  def withCommandeCtx(i: CommandeCtx): TimedTrait[A]


  def commandeCtx: CommandeCtx = _commandeCtx


  def pos: Int = _pos


  def simpleName: String = workerTimed.simpleName(value)

  override def toString: String = value.toString
}