package bon.jo.rpg
import bon.jo.rpg.BattleTimeLine.UpdateGameElement
import bon.jo.rpg.Commande.Rien
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.BattleTimeLine._
import scala.reflect.ClassTag


object CommandeResolver:

     

  trait Resolver[A , B<: TPA,C  ]:
      def resolveCommand(a: A,  b: Iterable[B])(using C): Iterable[UpdateGameElement]


  trait CommandeResolveCtx[A , B<: TPA]:
    given rien: Resolver[A , B,Rien.type]
    given attaque: Resolver[A , B,Commande.Attaque]
  trait Dispatcher[A,B <: TPA] :
    def resolveCommand[C ](a: A,  b: Iterable[B])(using C)(using r : Resolver[A , B,C] ) = r.resolveCommand(a,b)
    def dispacth(a: A,  b: Iterable[B],cc : Commande)(using c : CommandeResolveCtx[A,B],p : PlayerUI ) :   Iterable[UpdateGameElement] = 
      import c.given
      cc match 
        case given Commande.Rien.type => resolveCommand[Commande.Rien.type](a,b)
        case given Commande.Attaque => resolveCommand[Commande.Attaque](a,b)
        case a => 
          p.message(s"Pas encore fait : ${a}",0)
          Nil
      
      
      

