name := "NTP Service"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.3.4"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test"

libraryDependencies +=
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

    