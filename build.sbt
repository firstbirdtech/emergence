lazy val commonSettings = Seq(
  organization := "com.firstbird.emergence",
  startYear := Some(2020),
  homepage := Some(url("https://github.com/firstbirdtech/emergence")),
  licenses := List(("MIT", url("http://opensource.org/licenses/MIT"))),
  scmInfo := Some(
    ScmInfo(homepage.value.get, "scm:git:https://github.com/firstbirdtech/emergence.git")
  ),
  scalaVersion := "2.13.4",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-language:higherKinds",
    "-unchecked",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Wdead-code"
  )
)

addCommandAlias("codeFmt", ";scalafmtAll;scalafmtSbt")

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(skip in publish := true)
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= Dependencies.core,
    addCompilerPlugin(Dependencies.betterMonadicFor)
  )
