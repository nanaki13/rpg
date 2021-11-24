package bon.jo.rpg
import bon.jo.rpg.BattleTimeLine.UpdateGameElement
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.BattleTimeLine._
import scala.reflect.ClassTag
import bon.jo.rpg.resolve.FormuleType
import bon.jo.dao.Dao
import bon.jo.rpg.resolve.Formule
import bon.jo.common.ec.Exec

object AffectResolver:

  def read(formuleType : FormuleType)(using a :  Affect,f:  Map[Formule.ID,Formule]):Option[Formule] = f.get((a,formuleType))
  trait Resolver[A,B,C <: Affect]:
    def resolveAffect(a: A, b: B) : PlayerUI.UI[B]
  trait AffectFormuleResolver:
    type R[A] =   Dao[Formule,(Affect,FormuleType)] ?=> Exec[A]
    def formulesID(using c: Affect) : Iterable[(Affect,FormuleType)] =  c.formuleTypes.map(e => c -> e) 
    def formules(using c: Dao[Formule,(Affect,FormuleType)]) : R[Iterable[Formule]] = 
       c.readAll()
    def readAll(id : Iterable[(Affect,FormuleType)])(using c: Dao[Formule,(Affect,FormuleType)]) : Exec[Iterable[Formule]]=
      c.readAll(id)
    def formulesMap : R[Map[(Affect,FormuleType),Formule]] = formules.map(a => a.map(e => (e.affect,e.formuleType) -> e).toMap )

    

trait AffectResolver[A , B <: TPA ]:
  def resolveAffect[C <: Affect](a: A,  b: Iterable[B])(using r:  AffectResolver.Resolver[A,B,C],ct : C): PlayerUI.UI[Iterable[UpdateGameElement]] =
    b.map( tpa => UpdateGameElement(tpa.id, (futureMe) =>   r.resolveAffect(a,futureMe.cast),ct.name))
      

