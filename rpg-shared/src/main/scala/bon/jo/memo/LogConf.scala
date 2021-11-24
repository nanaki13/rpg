package bon.jo.memo

import scala.reflect.ClassTag

object LogConf {
  given Map[ClassTag[_],Boolean] = Map(summon[ClassTag[bon.jo.memo.Script.type]]-> true)
}
