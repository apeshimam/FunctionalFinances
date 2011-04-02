import sbt._

class FunctionalFinancesProject(info: ProjectInfo) extends DefaultProject(info: ProjectInfo) {
  val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
  val scalatime = "org.scala-tools.time" % "time_2.8.1" % "0.3" 
}
