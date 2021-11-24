//package bon.jo.memo.ui
//
//import org.scalajs.dom.raw.{Element, HTMLCollection, HTMLElement, MouseEvent, Node, Text}
//import org.scalajs.dom.{DOMList, document}
//
//import scala.language.dynamics
//import scala.scalajs.js
//
//object HTMLDef {
//  type Ev = Iterable[(Element, js.Function1[MouseEvent, _])]
//
//  implicit class DomlistOps[T](domList: DOMList[T]) extends Iterable[T] {
//
//    def cp: List[T] = {
//      _iterator.toList
//    }
//
//    def _iterator: Iterator[T] = new Iterator[T] {
//
//      val l = domList.length
//      var _index = 0
//
//      override def hasNext: Boolean = _index < l
//
//      override def next(): T = {
//        val ret = domList.item(_index)
//        _index += 1
//        ret
//      }
//    }
//
//    override def iterator: Iterator[T] = cp.iterator
//  }
//
//  implicit class HtmlOps[T <: Element](t: T) {
//
//    object $classSelect extends scala.Dynamic {
//
//      def apply(clSel: String): HTMLCollection = t.getElementsByClassName(clSel)
//
//      def selectDynamic(clSel: String): HTMLCollection = apply(clSel)
//    }
//
//    def _class: String = t.classList.mkString(" ")
//
//    def _class_=(s: String): Unit = {
//
//      s.split(" ").foreach(t.classList.add)
//    }
//
//    def $to[A <: HTMLElement]: A = t.asInstanceOf[A]
//
//    def $list(htmlList: Iterable[HTMLElement]): T = {
//      htmlList.foreach(t appendChild _)
//      t
//    }
//
//    def safeRm(): Option[Node] = {
//      if (t.parentNode != null && !js.isUndefined(t.parentNode)) {
//        Some(t.parentNode.removeChild(t))
//      } else {
//        None
//      }
//    }
//
//    def $attr(keyValue: (Any, Any)*): T = {
//      keyValue.foreach(e => {
//        t.setAttribute(e._1.toString, e._2.toString)
//
//      })
//      t
//    }
//
//    def +=(childRen: Node): T = {
//      t.appendChild(childRen).asInstanceOf[HTMLElement]
//      t
//    }
//
//    def $textContent(str: String): T = {
//      t.textContent = str
//      t
//    }
//
//    def ++=(childRens: Node*): T = {
//      childRens foreach +=
//      t
//    }
//
//    def ++=(childRens: List[Node]): T = {
//      childRens foreach +=
//      t
//    }
//
//    def :=(toMe: T => Unit): T = {
//      toMe(t)
//      t
//    }
//
//
//  }
//
//  object $c extends scala.Dynamic {
//    def selectDynamic[T <: HTMLElement](tagName: String): T = {
//      document.createElement(tagName).asInstanceOf[T]
//    }
//  }
//
//  object $ref extends scala.Dynamic {
//    def applyDynamic(tagName: String)(d: HTMLElement => Unit): HTMLElement = {
//      val ret = $c.selectDynamic[HTMLElement](tagName)
//      d(ret)
//      ret
//    }
//
//    object t extends scala.Dynamic {
//      def applyDynamic[T <: HTMLElement](tagName: String)(d: T => Unit): T = {
//        val ret = $c.selectDynamic[T](tagName)
//        d(ret)
//        ret
//      }
//    }
//  }
//
//  object $refns extends scala.Dynamic {
//    def applyDynamic(tagName: String)(ns: String, d: Element => Unit): Element = {
//      val ret = document.createElementNS(ns, tagName)
//      d(ret)
//      ret
//    }
//
//    object t extends scala.Dynamic {
//      def applyDynamic[T <: Element](tagName: String)(ns: String, d: T => Unit): T = {
//        val ret = document.createElementNS(ns, tagName).asInstanceOf[T]
//        d(ret)
//        ret
//      }
//    }
//  }
//
//  object $attrns extends scala.Dynamic {
//    def applyDynamic(tagName: String)(ns: String, htmlL: (Any, Any)*): Element = {
//      document.createElementNS(ns, tagName).$attr(htmlL: _ *)
//    }
//  }
//
//  object $attr extends scala.Dynamic {
//    def applyDynamic(tagName: String)(htmlL: (Any, Any)*): HTMLElement = {
//      $c.selectDynamic[HTMLElement](tagName).$attr(htmlL: _ *)
//    }
//
//    object t extends scala.Dynamic {
//      def applyDynamic[T <: HTMLElement](tagName: String)(htmlL: (Any, Any)*): T = {
//        $c.selectDynamic[T](tagName).$attr(htmlL: _ *)
//      }
//    }
//  }
//
//  object $t extends scala.Dynamic {
//    def apply(str: String): Text = document.createTextNode(str)
//
//    def applyDynamic(tagName: String)(textContent: String): HTMLElement = {
//      val ret = $c.selectDynamic[HTMLElement](tagName)
//      ret.textContent = textContent
//      ret
//    }
//  }
//
//  object $va extends scala.Dynamic {
//    def applyDynamic(tagName: String)(htmlL: Node*): HTMLElement = {
//      val ret = $c.selectDynamic[HTMLElement](tagName)
//      htmlL.foreach(ret.appendChild)
//      ret
//    }
//
//    object t extends scala.Dynamic {
//      def applyDynamic[T <: HTMLElement](tagName: String)(htmlL: Node*): T = {
//        val ret = $c.selectDynamic[T](tagName)
//        htmlL.foreach(ret.appendChild)
//        ret
//      }
//
//    }
//
//  }
//
//  object $l extends scala.Dynamic {
//    def applyDynamic(tagName: String)(htmlL: Iterable[Node]): HTMLElement = {
//      val ret = $c.selectDynamic[HTMLElement](tagName)
//      htmlL.foreach(ret.appendChild)
//      ret
//    }
//
//    object t extends scala.Dynamic {
//      def applyDynamic[T <: HTMLElement](tagName: String)(htmlL: Iterable[Node]): T = {
//        val ret = $c.selectDynamic[T](tagName)
//        htmlL.foreach(ret.appendChild)
//        ret
//      }
//    }
//  }
//
//
//}
