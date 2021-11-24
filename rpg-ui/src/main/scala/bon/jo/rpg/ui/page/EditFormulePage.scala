package  bon.jo.rpg.ui.page

import org.scalajs.dom.raw.HTMLElement
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HTMLDef.$c
import org.scalajs.dom.html.{Span, Button}
import bon.jo.html.DomBuilder.html.$t
import bon.jo.html.DomBuilder.html.$
import $t.*
import bon.jo.memo.Script._
import scala.language.dynamics
import org.scalajs.dom.raw.HTMLInputElement
import bon.jo.html.SimpleView
import bon.jo.html.HTMLDef.HtmlOps
import bon.jo.html.DomBuilder
trait EditFormulePage {

    object char :
        var value : Char = 'a'
    type P = List[( HTMLInputElement,HTMLInputElement)] 

    var parValue :P  = List(
            $t.input[HTMLInputElement](initializeParam) ->
            $t.input[HTMLInputElement](initializeValue)

        )
    val formule : HTMLElement  = $.span{
            $.->( _.contentEditable = "true")
            $.->(m =>m.$keyup{ 
                    _ => 
                        runExp
                })
             $.css(_.height = "7em")
             $ text "a"
            }
    val result : Span  = $c.span[Span]
    def p[A](a : A):A = 
        println(a)
        a
    given ToFunction[String,P] = s =>  u =>
   
        u.find(_._1.value == s).map(_._2.value).map(p).getOrElse(throw new IllegalStateException(s"pas de variable $s")).toFloat


    def runExp:Unit = 
            import bon.jo.common.give.given
            println("phrase = "+formule.textContent.toPhrase)
            println("node = "+formule.textContent.toNode)
            println("expression = "+formule.textContent.toExpression)
            val f = formule.textContent.toFunction[P]()
            result.textContent = f(parValue).toString


    def initializeEventInmut:DomBuilder.html.HtmlBuilder[HTMLInputElement]={
        val input = summon[HTMLInputElement]
        input.$keyup (_ => runExp)    
        input
    }
    

    def initializeParam:DomBuilder.html.HtmlBuilder[HTMLInputElement]={
        initializeEventInmut
        $t.doOnMe(e => {
           val c =  char.value

           e.value = s"$c"

           char.value = (c + 1.toChar).toChar
 
        })  
    }
    def initializeValue:DomBuilder.html.HtmlBuilder[HTMLInputElement]={
        initializeEventInmut
        $t.doOnMe(_.value="1")    
    }
    def editPage(using   c : org.scalajs.dom.raw.HTMLElement ) : () => Unit= () =>
       
        val addVar : Button  = SimpleView.bsButton("+")
        val paramCont = $.div($.childs(parValue.map((par,v)=> $.div($.childs(par,v))) : _ * ))    
        addVar.$click{ _ =>
            parValue = parValue :+  $t.input[HTMLInputElement](initializeParam) -> $t.input[HTMLInputElement](initializeValue)
            val (p,v) = parValue.last
            paramCont += $ div ($ childs (p,v))
        }



      
    
        parValue.foreach{ 
            (a,b) =>
            a.$keyup (_ => runExp)
            b.$keyup (_ => runExp)
        }
        
        
       
          
        $.childs(formule,$.span($.childs($.span(text("=")),result)),
                paramCont    
        
      , addVar )
      runExp

      
}



