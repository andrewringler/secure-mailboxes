import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "secure-mailboxes"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.twilio.sdk" % "twilio-java-sdk" % "3.3.15")

  val main = play.Project(appName, appVersion, appDependencies).settings( // Add your own project settings here      
  )

}
