package bon.jo.rpg.dao
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import bon.jo.rpg.ui.Image
object ImageJs:
  def apply(path: String): ImageJs =
    js.Dynamic.literal(
      path = path,
      ).asInstanceOf[ImageJs]

  def unapply(value: ImageJs): Option[Image] =
    Some(Image(value.path))

trait ImageJs extends js.Object:
    val path: String