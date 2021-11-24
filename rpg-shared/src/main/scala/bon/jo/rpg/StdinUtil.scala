package bon.jo.rpg

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object StdinUtil:
  def defStr[A](a: A) = a.toString

  def fromStdin[A](v: List[A]): A =
    def f = defStr[A] _

    fromStdin[A,A](v, f,a =>a)
  @tailrec
  def fromStdin[A,B](v: scala.collection.Iterable[A], str: A => String, map: A => B): B =
    v.zipWithIndex.foreach {
      case (a, i) => println(s"$i -> ${str(a)}")
    }
    Try {
      v.toList(StdIn.readLine().toInt)
    } match
      case Success(value) => map(value)
      case Failure(exception) => println("invalid"); fromStdin(v, str,map)
