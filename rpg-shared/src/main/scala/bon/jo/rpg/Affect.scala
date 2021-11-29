package bon.jo.rpg

import bon.jo.rpg.SystemElement
import bon.jo.rpg.resolve.FormuleType
import bon.jo.rpg.StdinUtil.fromStdin
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
    case Affect.Slow => FormuleType.withoutDegat()
    case Affect.Caffein => FormuleType.withoutDegat()
    case Affect.Hate => FormuleType.withoutDegat()
    case Affect.Soin =>  Some(FormuleType.Factor) 
    case _ => Some(FormuleType.ChanceToSuccess)



  def fromStdIn: Affect =
    fromStdin(Affect.values.toList)
