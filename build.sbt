name := """akka-http-multipart-formdata"""

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.10"
  val scalaTestV = "3.0.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  )
}

