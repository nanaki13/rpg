package bon.jo.memo.ui

object Target:

  case object MemoCreation extends Target
  case object FindMemo extends Target

  case class ReadMemo(id: Int) extends Target

  case object KeyWordK extends Target

  case object _404 extends Target


sealed trait Target extends Product