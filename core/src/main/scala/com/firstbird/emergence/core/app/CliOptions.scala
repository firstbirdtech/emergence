package com.firstbird.emergence.core.app

import java.io.File
import java.nio.file.{Path, Paths}

import scala.sys.process.Process
import scala.util.Try

import caseapp.core.Error
import caseapp.core.Error.MalformedValue
import caseapp.core.argparser.{ArgParser, SimpleArgParser}
import caseapp.{AppName, AppVersion, ProgName}
import cats.effect.Sync
import cats.syntax.all._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.firstbird.emergence.BuildInfo
import com.firstbird.emergence.core.configuration._
import com.firstbird.emergence.core.vcs.VcsSettings
import com.typesafe.config.ConfigFactory
import sttp.model.Uri

@AppName(BuildInfo.appName)
@AppVersion(BuildInfo.version)
@ProgName(BuildInfo.cliName)
final case class CliOptions(
    config: EmergenceConfig,
    vcsType: CliOptions.VcsType,
    vcsApiHost: Uri,
    vcsLogin: String,
    gitAskPass: Path
) {

  def vcsSettings[F[_]](implicit F: Sync[F]): F[VcsSettings] = {
    F.delay {
      val secret = Process(gitAskPass.toString).!!.trim
      val user   = VcsSettings.VcsUser(vcsLogin, secret)
      VcsSettings(vcsApiHost, user)
    }
  }

}

object CliOptions {

  sealed abstract class VcsType(val value: String)
  object VcsType {
    case object BitbucketCloud extends VcsType("bitbucket-cloud")
    def values: Set[VcsType] = Set(BitbucketCloud)
  }

  implicit val pathParser: ArgParser[Path] = SimpleArgParser.from[Path]("path") { s =>
    val path   = Paths.get(s)
    val isFile = path.toFile().isFile()
    Either.cond(isFile, path, MalformedValue("java.nio.file.Path", "Not a valid file path."))
  }

  implicit val vcsTypeParser: ArgParser[VcsType] = SimpleArgParser.from[VcsType]("vcs-type") { s =>
    VcsType.values
      .find(_.value.toLowerCase == s)
      .toRight(MalformedValue("VcsType", s"Value must be one of: ${VcsType.values.map(_.value).mkString(" or ")}"))
  }

  implicit val uriParser: ArgParser[Uri] = SimpleArgParser.from[Uri]("uri") { s =>
    Uri
      .parse(s)
      .leftMap(MalformedValue("Uri", _))
  }

  implicit val configParser: ArgParser[EmergenceConfig] = SimpleArgParser.from[EmergenceConfig]("path") { s =>
    val file = Paths.get(s).toFile

    for {
      yml  <- parseYaml(file)
      json <- readJsonFromYamll(yml)
      config <- Try(ConfigFactory.parseString(json)).toEither
        .flatMap(EmergenceConfig.from(_))
        .leftMap(t => MalformedValue("com.typesafe.config", s"Invalid config file: ${t.getMessage}"))
    } yield config
  }

  private def parseYaml(file: File): Either[Error, Object] = {
    val yamllReader = new ObjectMapper(new YAMLFactory)
    Try(yamllReader.readValue(file, classOf[Object])).toEither
      .leftMap(t => MalformedValue("yml", s"Unable to read config as yml: ${t.getMessage}"))
  }

  private def readJsonFromYamll(yml: Object): Either[Error, String] = {
    val jsonWriter = new ObjectMapper
    Try(jsonWriter.writeValueAsString(yml)).toEither.leftMap(t =>
      MalformedValue("json", s"Unable to write yml as json: ${t.getMessage}"))
  }

}
