package bon.jo.rpg


trait Timed[A]:
  type B <: A
  def simpleName(value: B ): String


  def speed(a: B ): Float



  def canChoice(a : B ):List[Commande]
