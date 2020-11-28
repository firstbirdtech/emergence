lazy val commonSettings = Seq(
  organization := "com.firstbird.emergence",
  scalaVersion := "2.13.4"
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= Dependencies.core,
    addCompilerPlugin(Dependencies.betterMonadicFor)
  )
