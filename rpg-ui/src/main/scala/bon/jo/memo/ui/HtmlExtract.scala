package bon.jo.memo.ui

import org.scalajs.dom.raw

object HtmlExtract:
  trait HtmlExtractAny[A] extends HtmlExtract[A, raw.Element]

  implicit class HtmlValue[A <: raw.Element, B: HtmlExtractAny](a: A):
    def toValue: B = implicitly[HtmlExtractAny[B]].extract(a)

trait HtmlExtract[A, B <: raw.Element] {
  def extract(html: B): A
}