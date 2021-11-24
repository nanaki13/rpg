package bon.jo.memo.ui

import scala.scalajs.js

import scala.scalajs.js.JSConverters._
object MemoLists:
  class MemoList( val elements : js.Array[ListElementJS])  extends MemoListJS :
    def pure():MemoListJS = js.Dynamic.literal(elements = elements.map(_.pure()).toJSArray ).asInstanceOf[MemoListJS]



  trait MemoListJS extends js.Object:
    val elements : js.Array[ListElementJS]
  trait ListElementJS extends js.Object:
    val content : String
    val checked : Boolean
    def pure():ListElementJS
  class ListElement(val content : String,val checked : Boolean) extends ListElementJS:
    def pure():ListElementJS = js.Dynamic.literal(content = content,checked = checked).asInstanceOf[ListElementJS]
    def this(a : ListElementJS)=
      this(a.content,a.checked)

