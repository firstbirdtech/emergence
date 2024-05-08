import sbt._

object Dependencies {

  private val circeVersion      = "0.14.7"
  private val fs2Version        = "3.10.2"
  private val sttpClientVersion = "3.9.6"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                   % "logback-classic"                % "1.5.6",
    "co.fs2"                          %% "fs2-io"                         % fs2Version,
    "co.fs2"                          %% "fs2-core"                       % fs2Version,
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml"        % "2.16.2",
    "com.monovore"                    %% "decline-effect"                 % "2.4.1",
    "com.softwaremill.sttp.client3"   %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "async-http-client-backend-cats" % sttpClientVersion,
    "com.typesafe"                     % "config"                         % "1.4.3",
    "io.circe"                        %% "circe-core"                     % circeVersion,
    "io.circe"                        %% "circe-parser"                   % circeVersion,
    "io.circe"                        %% "circe-yaml"                     % "0.15.1",
    "org.scalatest"                   %% "scalatest"                      % "3.2.18" % Test,
    "org.typelevel"                   %% "cats-effect"                    % "3.5.4",
    "org.typelevel"                   %% "log4cats-slf4j"                 % "2.7.0"
  )
}
