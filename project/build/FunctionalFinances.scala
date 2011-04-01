import sbt._

class HelloWorldProject(info: ProjectInfo) extends DefaultProject(info) 
{
  val scalatools_release = "Scala Tools Releases" at "http://scala-tools.org/repo-releases/"

  val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
  
}
