import java.time.LocalDate

ThisBuild / dynverSeparator            := "-" // Default uses '+' which is not valid for docker tags
ThisBuild / scalaVersion               := "3.3.7"
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("11"), JavaSpec.temurin("17"))

addCommandAlias("codeFmt", ";headerCreate;scalafmtAll;scalafmtSbt;scalafixAll")
addCommandAlias("codeVerify", ";scalafmtCheckAll;scalafmtSbtCheck;scalafixAll --check;headerCheck")

lazy val commonSettings = Seq(
  organization        := "com.firstbird.emergence",
  sonatypeProfileName := "com.firstbird",
  startYear           := Some(2020),
  homepage            := Some(url("https://github.com/firstbirdtech/emergence")),
  licenses            := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(
    ScmInfo(homepage.value.get, "scm:git:https://github.com/firstbirdtech/emergence.git")
  ),
  developers += Developer(
    "contributors",
    "Contributors",
    "",
    url("https://github.com/firstbirdtech/emergence/graphs/contributors")
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explain-types",
    "-feature",
    "-language:higherKinds",
    "-unchecked",
    "-Ysafe-init",
    "-Xfatal-warnings"
  ),
  headerLicense     := Some(HeaderLicense.ALv2(LocalDate.now.getYear.toString, "Emergence contributors")),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(publish / skip := true)
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= Dependencies.core
  )
  .settings(
    Compile / sourceGenerators += Def.task {
      val directory = "com/fgrutsch/emergence"
      val pkg       = "com.fgrutsch.emergence"
      val file      = (Compile / sourceManaged).value / directory / "BuildInfo.scala"

      IO.write(
        file,
        s"""
        |package $pkg
        |
        |object BuildInfo {
        |  val Version: String = "${version.value}"
        |}""".stripMargin
      )
      Seq(file)
    }.taskValue
  )
  .settings(
    dockerBaseImage      := "eclipse-temurin:17",
    Docker / packageName := "firstbird/emergence",
    dockerUpdateLatest   := true
  )

lazy val docs = project
  .in(file("docs"))
  .settings(commonSettings)
  .settings(
    name                         := "docs",
    githubWorkflowArtifactUpload := false
  )
  .dependsOn(core)
  .enablePlugins(ParadoxPlugin)
