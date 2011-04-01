import sbt._

class FunctionalFinancesProject(info: ProjectInfo) extends DefaultProject(info: ProjectInfo) {
  val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
}
