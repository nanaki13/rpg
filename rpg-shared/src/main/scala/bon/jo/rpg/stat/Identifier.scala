package bon.jo.rpg.stat

object Identifier:
    trait IntId extends Identifier[Int]
   

trait Identifier[A]:
    opaque type ID = A
    object ID:
        def apply(a : A):ID = a
        def safe(id : ID):A = id