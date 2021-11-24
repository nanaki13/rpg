package bon.jo.rpg


trait Timed[-A]:
  def simpleName(value: A): String


  def speed(a: A): Int



  def canChoice(a : A):List[Commande]
