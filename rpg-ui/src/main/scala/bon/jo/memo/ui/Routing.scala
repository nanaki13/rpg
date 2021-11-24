package bon.jo.memo.ui

import org.scalajs.dom.window

import scala.util.{Failure, Success, Try}

object Routing:

  type PF[A] = PartialFunction[Routing.Path, A]


  def create[A](partialFunction: PartialFunction[Routing.Path, A])(implicit _404: A): Routing.Path => A =
    partialFunction orElse { case _ => _404 }

  case class Path(str: List[String]):
    def matches[R](list: PathMatchList[R]): Option[R] =
      if str.size != list.str.size then
        None
      else
        list.tr(str.zip(list.str).filter(e => e._2.variable && e._2.matches(e._1)).map(e => e._2.extract(e._1)))

    def /(s: String): Path = copy(str = str :+ s)

    def /[A](s: PathMatcher[_])(implicit tp: List[Any] => Option[A]): PathMatchList[A] = PathMatchList(str.map(StrictMatch.apply) :+ s)(tp)

  case class PathMatchList[R](str: List[PathMatcher[_]])(tp: List[Any] => Option[R]):
    def tr(value: List[Any]): Option[R] = tp(value)


  trait PathMatcher[A]:
    def matches(str: String): Boolean

    def extract(str: String): A

    def variable: Boolean

  trait PathMatcherString extends PathMatcher[String]:
    override def extract(str: String): String = str

  case class StrictMatch(strRef: String) extends PathMatcherString:
    override def matches(str: String): Boolean = strRef == str

    override def variable: Boolean = false

  object IntPath extends PathMatcher[Int]:
    override def matches(str: String): Boolean =
      Try(str.toInt) match
        case Failure(_) => false
        case Success(_) => true


    override def variable: Boolean = true

    override def extract(str: String): Int = str.toInt

  implicit class PFromStr(str: String):
    def /(o: String): Path = Path(List(str, o))
    def  p =  Path(str :: Nil)

  object Path:
    def apply(str: String): Path = Path(str.split("/").toList.filter(_.nonEmpty))


  def urlPath: Routing.Path = Path(scalajs.js.URIUtils.decodeURI(window.location.pathname))
