name := """demo-scala-redis"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers ++= Seq( 
		"rediscala" at "http://dl.bintray.com/etaty/maven" //Rediscala repo		
	    //,"org.sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"
 )
 
libraryDependencies ++= Seq( 
  //"com.typesafe.akka" %% "akka-contrib" % "2.3.7",
  //"net.debasishg" %% "redisclient" % "2.13",
  "org.webjars" % "bootstrap" % "3.0.0",
  "org.webjars" % "knockout" % "2.3.0",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "org.webjars" % "leaflet" % "0.7.2",
  "org.webjars" % "rjs" % "2.1.11-1-trireme" % "test",
  "com.etaty.rediscala" %% "rediscala" % "1.3.1"
  //,"com.typesafe" %% "play-plugins-redis" % "2.1.1"
)

scalacOptions += "-feature"
