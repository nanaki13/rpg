package bon.jo.memo.ui

import org.scalajs.dom.raw

class IOHtml[A <: raw.Element, E](xmlf: => A, extractF: A => E) extends HtmlExtract[E, A]:
  val html: A = xmlf

  override def extract(htmlp: A): E = extractF(htmlp)

  def toValue: E = extract(html)
