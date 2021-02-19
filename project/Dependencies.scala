import sbt._

object Dependencies {

  private val circeVersion      = "0.13.0"
  private val fs2Version        = "2.5.2"
  private val sttpClientVersion = "3.1.3"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                 % "logback-classic"                % "1.2.3",
    "com.github.alexarchambault"    %% "case-app-cats"                  % "2.0.4",
    "com.softwaremill.sttp.client3" %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3" %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % sttpClientVersion,
    "co.fs2"                        %% "fs2-core"                       % fs2Version,
    "co.fs2"                        %% "fs2-io"                         % fs2Version,
    "io.circe"                      %% "circe-config"                   % "0.8.0",
    "io.circe"                      %% "circe-yaml"                     % "0.13.1",
    "io.circe"                      %% "circe-literal"                  % circeVersion,
    "io.circe"                      %% "circe-generic-extras"           % circeVersion,
    "io.chrisdavenport"             %% "log4cats-slf4j"                 % "1.1.1",
    "org.scalatest"                 %% "scalatest"                      % "3.2.4" % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy" %% "better-monadic-for" % "0.3.1"

}
