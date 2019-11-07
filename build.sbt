name := "yode"

version := "0.1.0"

scalaVersion := "2.11.12"

nativeLinkStubs := true

nativeMode := "debug"

nativeLinkingOptions := Seq( s"-L/${baseDirectory.value}/native-lib" )

scalacOptions ++= Seq( "-deprecation", "-feature", "-unchecked", "-language:postfixOps", "-language:implicitConversions", "-language:existentials" )

organization := "xyz.hyperreal"

mainClass in (Compile, run) := Some( "xyz.hyperreal." + name.value.replace('-', '_') + ".Main" )

licenses := Seq("ISC" -> url("https://opensource.org/licenses/isc"))

homepage := Some(url("https://github.com/edadma/" + name.value))

enablePlugins(ScalaNativePlugin)

libraryDependencies += "com.lihaoyi" %%% "utest" % "0.7.1" % "test"

testFrameworks += new TestFramework( "utest.runner.Framework" )

libraryDependencies ++= Seq(
  "com.github.scopt" %%% "scopt" % "3.7.0",
  "com.lihaoyi" %%% "fastparse" % "1.0.0"
)

libraryDependencies ++= Seq(
  "xyz.hyperreal" %%% "yola" % "0.1a.0"
)
