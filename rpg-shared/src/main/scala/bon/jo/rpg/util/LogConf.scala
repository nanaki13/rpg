package bon.jo.rpg.util

import scala.reflect.ClassTag

object LogConf {
  given Map[ClassTag[_],Boolean] = Map(summon[ClassTag[bon.jo.rpg.util.Script.type]]-> true)
}
