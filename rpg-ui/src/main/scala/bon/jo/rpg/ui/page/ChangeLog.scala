package bon.jo.rpg.ui.page

import bon.jo.html.HtmlRep.HtmlCpnt

import org.scalajs.dom.raw.HTMLElement
import scala.language.dynamics

import bon.jo.common.Tree
import scala.annotation.tailrec

import bon.jo.html.HtmlRep
import bon.jo.common.Add
import Tree.*
import Tree.given
import Tree as t
import bon.jo.html.HtmlRep.PrXmlId
import bon.jo.html.DomBuilder.*
import bon.jo.html.DomBuilder.HtmlDsl
import bon.jo.html.DomBuilder.html.*
import bon.jo.html.DomBuilder.html.$.*
case class ChangeLog(version : String,date : String,data : Tree[String])
object ChangeLog extends HtmlCpnt:
 
    def textTree(str : String) : Tree[String] = Tree.Value(str)
   

  

   
 


    def valueTohtml(e : String)=  $.li(text(e))
    

    given add : Add[HTMLElement] with
        def add(a : HTMLElement,b : HTMLElement)= 
            a.appendChild(b)
        
        def monoid = $.ul{me}
   
    def htmlTree(t : Tree[String]):HTMLElement=
        t.map(valueTohtml).reduce


    val data = List(ChangeLog(
        "Version 1.0.0"
        ,"06-2021" 
        , / (t.childs(
            / {
                t.value("Séparation des actions en Affect et commande")
                t.childs(
                textTree("Affect : Sont sur les armes"),
                textTree("Commande : Sur les preso")
                )},
            / 
            {t.value("Ajout des éffets hâte,slow, caféine, booster, cancel") 
            t.childs(
                textTree("Les affects sont résolut avec des jets"),
                textTree("Détail des jets dans la popup")
               
                )
            },
            / 
            {t.value("Ebauchde de l'édition de fromule") 
      
            } 
       
            )
    )
    ))
    given HtmlRep[ChangeLog,HtmlCpnt] with 
       def  html(c : ChangeLog) = HtmlCpnt(()=> Some( $.div{
                row
                $(
                    $ div{
                      addClass("col-lg")
                      $($ h3 {
                      
                        text(c.version)  
                      })  
                    },
                    $ div{
                        addClass("col-lg")
                        $($ h4 {
                           
                            text(c.date) 
                        })
                    },
                    $ div{
                        addClass(s"col-lg")
                        $($ ul{
                            $(htmlTree(c.data))
                      
                       
                        }) 
                    }
                )
    }))

    override val get:  IterableOnce[HTMLElement] = create()

    def changeLogHtml() =  data.map(_.html)

    def create():Some[HTMLElement]=
   
    Some( $.div{
            addClass("mt-5 bg-change-log container rounded mx-auto")
            $.childs(changeLogHtml().flatMap(_.get) :_ * )
        })
    
    
    

end ChangeLog