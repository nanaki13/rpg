package bon.jo.rpg.util
import PhraseElementBuildOps.canAdd
import PhraseElementBuildOps.add
trait ForFun

enum PhraseElement:
  case Symbol(c : Char)
  case Word(s : String)
  case White(s : String)

enum TreePhrase:
  case End()


def testParse() = println(PhraseElement("bo'('zdz)dd(   (   gd) )z" +
  "es gens!"))

object PhraseElement:
  def apply(c : Char) : PhraseElement =
    if c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' then 
        PhraseElement.Word(c.toString)
    else if c == ' ' || c == '\t' || c == '\r'  || c == '\n' then
      PhraseElement.White(c.toString)
    else PhraseElement.Symbol(c) 
  def add(org : List[PhraseElement],c : Char) : List[PhraseElement] = 
    if org.nonEmpty && org.head.canAdd(c) then org.head.add(c)+:org.tail else PhraseElement(c)+:org

  def apply(s : String) : List[PhraseElement] = s.foldLeft(List[PhraseElement]())(add(_,_)).reverse


object PhraseElementBuildOps:

  
  extension(e : PhraseElement)

    def canAdd(c : Char): Boolean = 
      e match
        case _:  PhraseElement.Symbol => false
        case  _: PhraseElement.Word => c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
        case  _: PhraseElement.White => c == ' ' || c == '\t' || c == '\r'  || c == '\n'  
    def add(c : Char): PhraseElement = 
      e match  
        case PhraseElement.Word(v) =>  PhraseElement.Word(v+c)
        case  PhraseElement.White(v) =>PhraseElement.White(v+c)
        case _:PhraseElement.Symbol => throw IllegalStateException()
