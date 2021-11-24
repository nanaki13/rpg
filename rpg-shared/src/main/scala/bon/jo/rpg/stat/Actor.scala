package bon.jo.rpg.stat

import bon.jo.rpg.{Affect,Commande , RandomName}
import bon.jo.rpg.stat.Actor.{ActorBaseStats, WeaponBaseState}
import bon.jo.rpg.stat.BaseState.ImplicitCommon
import bon.jo.rpg.stat.BaseState.ImplicitCommon._
import bon.jo.rpg.stat.raw.IntBaseStat

import scala.reflect.ClassTag
import scala.util.Random

abstract class Actor extends ActorBaseStats with ArmedActor

object Actor:
  case class Impl(
                    name : String,
                    desc : String,
                    lvl:Int,
                    stats : IntBaseStat,
                    leftHandWeapon: Option[Weapon]= None,
                    rightHandWeapon: Option[Weapon] = None,
                    commandes:List[Commande] = Nil,id: Int = Id[Impl]
            ) extends Actor with ArmedActor:

     override def withId[A  <: StatsWithName](id: Int): A = copy(id= id).asInstanceOf[A]

  trait Lvl:
    /**
     * indicateur général - permet d(utiliser les armes déun niveau équivalent.
     *
     * @return
     */
    val lvl: Int

  trait ActorBaseStats extends StatsWithName with Lvl

  val r = new Random()



  def randomActor[A <: Actor](create: AnyRefBaseStat[Int] => A): A =

   
    create((BaseState.`1` * AnyRefBaseStat(randomActorVal _)).to[AnyRefBaseStat[Int]])



  def randomActorVal(): Float = randomAround(50, 10)

  def randomWeaponVal(): Float = randomAround(10, 5)

  def randomAround(center: Int, deltaRandom: Int): Float = (center + deltaRandom * (r.nextGaussian() - 0.5)).toFloat



  def randomWeapon(): Weapon =
    val stat = (BaseState.`1` * AnyRefBaseStat(randomWeaponVal _))

    Weapon(0,RandomName.randomWeaponName(),"La belle arme",1,stat.to[AnyRefBaseStat[Int]], Affect.Attaque :: Nil)

  trait WeaponBaseState extends StatsWithName with Lvl:
    val affects: List[Affect]

  object Id:
    private val cache = scala.collection.mutable.Map[Class[_],Int]()
    def apply[A](implicit ct : ClassTag[A]):Int =
      val id =  cache.get(ct.runtimeClass) match
        case Some(value) => value+1
        case None => 0
      cache(ct.runtimeClass) = id
      id
    def init[A](int: Int)(implicit ct : ClassTag[A]):Unit=
      cache += ct.runtimeClass -> int

  case class Weapon(id : Int ,name : String,desc : String,lvl : Int,stats: AnyRefBaseStat[Int], affects: List[Affect]=Affect.Attaque :: Nil ) extends  WeaponBaseState:

     override def withId[A <: StatsWithName](id: Int): A = copy(id= id).asInstanceOf[A]

