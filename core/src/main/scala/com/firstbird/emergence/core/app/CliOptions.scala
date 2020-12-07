package com.firstbird.emergence.core.app

import java.nio.file.{Path, Paths}

import caseapp.core.Error.MalformedValue
import caseapp.core.argparser.{ArgParser, SimpleArgParser}
import caseapp.{AppName, AppVersion, ProgName}
import cats.syntax.all._
import com.firstbird.emergence.BuildInfo
import com.firstbird.emergence.core.configuration._
import com.firstbird.emergence.core.model.VcsType
import sttp.model.Uri

@AppName(BuildInfo.appName)
@AppVersion(BuildInfo.version)
@ProgName(BuildInfo.cliName)
final case class CliOptions(
    config: RunConfig,
    vcsType: VcsType,
    vcsApiHost: Uri,
    vcsLogin: String,
    gitAskPass: Path,
    repoConfigName: String = ".emergence.yml"
)

object CliOptions {

  implicit val pathParser: ArgParser[Path] = SimpleArgParser.from[Path]("path") { s =>
    val path   = Paths.get(s)
    val isFile = path.toFile().isFile()
    Either.cond(isFile, path, MalformedValue("java.nio.file.Path", "Not a valid file path."))
  }

  implicit val vcsTypeParser: ArgParser[VcsType] = SimpleArgParser.from[VcsType]("vcs-type") { s =>
    VcsType.values
      .find(_.underlying.toLowerCase == s)
      .toRight(MalformedValue("VcsType", s"Value must be one of: ${VcsType.values.map(_.underlying).mkString(" or ")}"))
  }

  implicit val uriParser: ArgParser[Uri] = SimpleArgParser.from[Uri]("uri") { s =>
    Uri
      .parse(s)
      .leftMap(MalformedValue("Uri", _))
  }

  implicit val runConfigParser: ArgParser[RunConfig] = SimpleArgParser.from[RunConfig]("path") { s =>
    val file = Paths.get(s).toFile

    configFromYaml(file).toEither
      .flatMap(RunConfig.from(_))
      .leftMap(t => MalformedValue("com.typesafe.config", s"Invalid config file: ${t.getMessage}"))
  }

}
