// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x

import java.lang
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.sys.process._

import Utils.git

val mainVersion = "1.1.0-SNAPSHOT"
enablePlugins(ScalaJSPlugin)
enablePlugins(JavaAppPackaging)
javacOptions += "-Xmx2G"
val sharedSettings = Seq(version := mainVersion,
  organization := "bon.jo",
  scalaVersion := "3.1.0",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  scalacOptions ++= Seq("-deprecation", "-feature"
    // ,"-source:3.0-migration"
    // ,"-rewrite"
    //   ,"-new-syntax"

  )
)
name := "rpg"
// or any other Scala version >= 2.11.12

lazy val `rpg-shared` =
// select supported platforms
  crossProject(JSPlatform, JVMPlatform)

    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(sharedSettings)
    .settings(
      libraryDependencies += "bon.jo" %%% "phy-shared" % "1.0.0-SNAPSHOT",

    )

//    .jvmSettings(libraryDependencies += "org.scala-js" %%% "scalajs-stubs" % "1.0.0" % "provided")
// configure Scala-Native settings
// .nativeSettings(/* ... */) // defined in sbt-scala-native


def from213(e: ModuleID) = e.cross(CrossVersion.for3Use2_13)
def from3(e: ModuleID) = e.cross(CrossVersion.for2_13Use3)






val stagePath = "I:\\work\\github-io\\rpg"
val snapPath = "I:\\work\\github-io\\rpg\\snapshot"
lazy val `rpg-ui` =
// select supported platforms
  crossProject(JSPlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(sharedSettings)
    .settings(libraryDependencies ++= Seq(from213("org.scala-js" %%% "scalajs-dom" % "1.1.0"), "org.scala-lang.modules" %%% "scala-xml" % "2.0.0"
      , "bon.jo" %%% "html-app" % "1.0.0-SNAPSHOT"

    )

    )

    .settings(
      scalaJSUseMainModuleInitializer := true,
      toGitHubIO := {

       toGitHup(stagePath,baseDirectory.value,sLog.value,(Compile / fullOptJS).value.data)
      },
      toGitHubSnapIO := {
      
       toGitHup(snapPath,baseDirectory.value,sLog.value,(Compile / fullOptJS).value.data)
      }

    ).dependsOn(`rpg-shared`) // defined in sbt-scalajs-crossproject

def toGitHup(targetGitReppo : String,projectDir : File,logg : Logger,jsFile : File)={
      val target = file(targetGitReppo)
    

       val css= projectDir.getParentFile().toPath().resolve("assets/css/index.css").toFile
       logg.info(s"cp ${jsFile} to $target")
       io.IO.copyFile(jsFile, target.toPath.resolve(jsFile.getName).toFile)
       logg.info(s"cp ${css} to $target")
       io.IO.copyFile(css, target.toPath.resolve(css.getName).toFile)
       git commitAndPush snapPath
       logg.info("push to git OK !") 
}
val toGitHubIO = taskKey[Unit]("send to gitub.io")
val toGitHubSnapIO = taskKey[Unit]("send to gitub.io snap")
toGitHubIO := {
  (Compile / fullOptJS).value
}
toGitHubSnapIO := {
  (Compile / fullOptJS).value
}