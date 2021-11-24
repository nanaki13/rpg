import sbt.{File, file}

import java.lang
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.scalanative.sbtplugin.process.Process.builderToProcess
import scala.language.dynamics
object Utils {
    object git extends GitOps
    object StrBuild extends scala.Dynamic{
      def from(file: sbt.File):ListStr = {
        ListStr(Nil,file)
      }


      case class ListStr(str : List[String],dir: sbt.File)extends scala.Dynamic{

        def toProcess(dir : File) = {
          val p =  new lang.ProcessBuilder()
          p.directory(dir)
          p.command(str.toIndexedSeq : _ *)
        }
        def $ = {
          val p =  new lang.ProcessBuilder()
          p.directory(dir)
          p.command(str.toIndexedSeq : _ *)
        }
        def selectDynamic(string: String):ListStr={
          copy(str = str :+ string)
        }
      }
    }
    trait GitOps{
      def commitAndPush(dit : String):Unit={
        val git =  StrBuild.from(file(dit)).git

        git.add.`-u`.$.!
        git.commit.`-m`.selectDynamic(s"deploy : ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}").$.!
        git.push.$.!

      }
    }
}
