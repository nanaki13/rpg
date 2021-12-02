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
   

  

   
 


    def valueTohtml(e : String)=  $.li(doOnMe{
        _.innerHTML = e
    })
    

    given add : Add[HTMLElement] with
        def add(a : HTMLElement,b : HTMLElement)= 
            a.appendChild(b)
        
        def monoid = $.ul{me}
   
    def htmlTree(t : Tree[String]):HTMLElement=
        t.map(valueTohtml).reduce


    val data = List(ChangeLog(
        "Version 1.1.0-SNAPSHOT"
        ,"12-2021" 
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
            {t.value("Edition de formule complété") 
             t.childs(
                textTree("Correction de bug d'arrondi"),
                textTree("Pour le moment, il est nécessaire de rentrer toute les formule")
               
                )
            },
            / 
            {t.value("Edition perso") 
             t.childs(
                textTree("On peut ajouter des image!")
               
                )
            },
            / 
            {t.value("Timeline") 
             t.childs(
                textTree("La vitesse des persos est pondéré par la vitesse max des persos,( effets des affects inclus)")
               
                )
            },
            / 
            {t.value("Navigation dirext") 
             t.childs(
                textTree("""On peut aller directos a une page : <a href="./index.html?page=Simulation" >index.html?page=Simulation</a>""")
               
                )
            } 
       
            )
    )
    ))
    given HtmlRep[ChangeLog,HtmlCpnt] with 
       def  html(c : ChangeLog) = HtmlCpnt(()=> Some( $.div{
                row
                $(
                    $ div{
                      addClass("col-6-lg")
                      $($ h3 {
                      
                        text(c.version)   
                      },
                      $ h2 {
                      
                        text(c.date)   
                      }
                      )  
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