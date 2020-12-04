package com.firstbird.emergence.core.app

import java.nio.file.{Path, Paths}

import scala.sys.process.Process
import scala.util.Try

import caseapp.core.Error.MalformedValue
import caseapp.core.argparser.{ArgParser, SimpleArgParser}
import caseapp.{AppName, AppVersion, ProgName}
import cats.effect.Sync
import cats.syntax.all._
import com.firstbird.emergence.BuildInfo
import com.firstbird.emergence.core.model._
import com.firstbird.emergence.core.vcs.VcsSettings
import com.typesafe.config.ConfigFactory
import sttp.model.Uri

@AppName(BuildInfo.appName)
@AppVersion(BuildInfo.version)
@ProgName(BuildInfo.cliName)
final case class CliOptions(
    configuration: Configuration,
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

  implicit val configParser: ArgParser[Configuration] = SimpleArgParser.from[Configuration]("path") { s =>
    val file = Paths.get(s).toFile
    Try(ConfigFactory.parseFile(file)).toEither
      .flatMap(Configuration.from(_))
      .leftMap(t => MalformedValue("com.typesafe.config", s"Invalid config file: ${t.getMessage}"))
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

}
