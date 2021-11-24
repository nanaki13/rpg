package bon.jo.rpg.ui

trait PlayerMessage:
  type T <: MessagePlayer

  def message(str: String, timeToDisplay: Int): Unit

  def message(str: String): MessagePlayer

  def clear(str: T): Unit
