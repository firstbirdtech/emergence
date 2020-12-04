import sbt._

object Dependencies {

  private val sttpClientVersion = "3.0.0-RC10"

  val core: Seq[ModuleID] = Seq(
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml"        % "2.12.0",
    "com.github.alexarchambault"      %% "case-app-cats"                  % "2.0.4",
    "com.softwaremill.sttp.client3"   %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "async-http-client-backend-cats" % sttpClientVersion,
    "io.circe"                        %% "circe-config"                   % "0.8.0",
    "io.circe"                        %% "circe-generic-extras"           % "0.13.0",
    "io.chrisdavenport"               %% "log4cats-slf4j"                 % "1.1.1"
  )

  val betterMonadicFor: ModuleID = "com.olegpy" %% "better-monadic-for" % "0.3.1"

}
