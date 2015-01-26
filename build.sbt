name := """demo-scala-redis"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

libraryDependencies ++= Seq(
  jdbc,
  "com.etaty.rediscala" %% "rediscala" % "1.3.1",
  anorm,
  cache,
  ws
)
