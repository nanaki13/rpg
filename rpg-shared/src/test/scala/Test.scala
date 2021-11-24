import collection.mutable.Stack
import org.scalatest._
import flatspec._
import matchers._
import bon.jo.memo.Script.*
import bon.jo.common.give.given

class ExampleSpec extends AnyFlatSpec with should.Matchers {

  "A expression" should "be the true" in {

    "1 + 1 ".toExpression.evaluateVal  should be( 2)
    "1 + (5+4)".toExpression.evaluate(using (s) =>0)  should be( 10)
    "2 * (x+4)".toExpression.evaluate(using (s) =>2)  should be( 12)
    "1+2*2 * 4".toExpression.evaluate(using (s) =>2)  should be( 24)
    "1+2*2 * 4".toExpressionWithAsso.evaluate(using (s) =>2)  should be( 17)
    "1+2*(2) + 4".toExpressionWithAsso.evaluate(using (s) =>2)  should be( 9)
    "1+2*(5-3) + 4".toExpressionWithAsso.evaluate(using (s) =>2)  should be( 9)
    "1+2*(5-3) - 1  + 4".toExpressionWithAsso.evaluate(using (s) =>2)  should be( 8)
  
    "1+2*(5-7) - 10/5  + 4".toExpressionWithAsso.evaluate(using (s) =>2)  should be( -1)
 
    "(1+2*(5-7) -1  - 10/5  + 4) - (5*7 )".toExpressionWithAsso.evaluate(using (s) =>2)  should be((1+2*(5-7) -1  - 10/5  + 4) - (5*7 ))
    "1+2*2 * 4".toExpressionWithAsso should be(  "1+(2*2 * 4)".toExpression)
    "1+2/2 * 4".toExpressionWithAsso should be(  "1+(2/2 * 4)".toExpression)
     "(x -1 ) * (x + 1)".toExpressionWithAsso.evaluate(using (s) =>2)  should be( 3)
   
  }

  extension [A <: Product] (p : A)
    def nameToProp : Iterator[(String,Any)] = p.productElementNames zip p.productIterator
  def stringFunction[A <: Product] : String => A => Float = s => a => a.nameToProp.find(_._1 == s).map(_._2).get.asInstanceOf[Float]
  case class P(x : Float,y : Float)
  case class Y(x : Float,y : Float)
  "A expression" should "be convert to function base" in {
    
          given ToFunction[String,P] =  ToFunction(stringFunction[P])

          val calc =  "x+y".toExpression.toFunction(P(1,2))
          calc should be (3)

    }

    "A expression" should "be convert to function" in  {
          given List[String] = List("a","b")

          val calc :Float =  "a.x+b.y".toExpression.toFunction((P(1,2),P(1,2)))
          calc should be (3)

    }

   "A expression" should "be convert to function with gen" in    {
          given List[String] = List("a","b")

      

          val calc =  "(a.x+b.x ) * (a.y + b.y)".toExpression.toFunction((P(1,2),Y(3,4)))
          println( "(a.x+b.x ) * (a.y + b.y)".toExpression)
          calc should be (24)

      
  }

  /* it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    } 
  }*/
}