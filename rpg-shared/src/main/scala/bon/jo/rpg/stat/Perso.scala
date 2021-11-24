package bon.jo.rpg.stat


//import bon.jo.rpg.DoActionTrait.WithAction
import bon.jo.rpg.stat.Actor.{Weapon, WeaponBaseState}
import bon.jo.rpg.{CommandeResolver, AffectResolver}
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.{Affect,Commande, AffectResolver, Timed, TimedTrait}
import bon.jo.rpg.BattleTimeLine._
import scala.collection.mutable
import bon.jo.rpg.AffectResolver.Resolver
import bon.jo.rpg.Affect.Attaque
import bon.jo.rpg.AffectResolver
import bon.jo.rpg.BattleTimeLine
import bon.jo.rpg.resolve.PersoResolveContext._
import bon.jo.rpg.LR
import bon.jo.rpg.resolve.PersoCtx



object Perso:

  type Weapons = Option[(Option[Weapon],Option[Weapon])]
  object ArmePerso:
    def unapply(e : StatsWithName):Weapons=
      e match
        case Perso(_,_,_,_, _, _, _, l, r) => Some(l,r)
        case _ => None

  trait PlayerPersoUI extends PlayerUI:
    type S = Perso
  class WithUI()(using val playerUI : PlayerUI)(using TimeLineParam,ResolveContext):
    
    given WithUI = this
    given persoCtx :  PersoCtx = new PersoCtx{}
    



  object PeroPero extends Timed[Perso]:


    override def speed(a: Perso): Int = (a.asInstanceOf[Perso].stats.viv / 10f).round

    override def simpleName(value: Perso): String = value.asInstanceOf[Perso].name

    override def canChoice(a: Perso): List[Commande] = 
      val perso = a.asInstanceOf[Perso]
      perso.commandes

  //given  Timed[Perso] = PeroPero





case class Perso(  id : Int, name: String,desc : String, stats : AnyRefBaseStat[Int], hpVar: Int ,lvl : Int , commandes : List[Commande] ,
   leftHandWeapon: Option[Weapon],
rightHandWeapon: Option[Weapon] 
                  ) extends Actor with GameElement with StatsWithName:

  def this(  id : Int, name: String,desc : String, stats : AnyRefBaseStat[Int], lvl : Int = 1, commandes : List[Commande] = Nil,
   leftHandWeapon: Option[Weapon]= None,
rightHandWeapon: Option[Weapon] = None
                ) = this(id,name,desc,stats,stats.hp,lvl,commandes,leftHandWeapon,rightHandWeapon)
  def randomWeapon() =
     copy(leftHandWeapon = Some(randomSoin(Actor.randomWeapon())),rightHandWeapon = Some(randomSoin(Actor.randomWeapon())))
  override def withId[A <: StatsWithName](id: Int): A = copy(id= id).asInstanceOf[A]


