package bon.jo.rpg

sealed trait State extends Product

object State {
  case object BeforeChooseAction extends State

  case object ChooseAction extends State

  case object BeforeResolveAction extends State

  case object ResolveAction extends State

  case object NoState extends State
}