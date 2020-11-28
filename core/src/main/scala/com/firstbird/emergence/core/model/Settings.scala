package com.firstbird.emergence.core.model

import java.nio.file.Path
import com.firstbird.emergence.core.vcs.Vcs
import com.firstbird.emergence.core.model._
import sttp.model.Uri
import cats.syntax.all._
import caseapp.core.argparser.ArgParser
import caseapp.core.argparser.SimpleArgParser
import caseapp.core.Error.MalformedValue
import java.nio.file.Paths
import com.typesafe.config.ConfigFactory
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import com.typesafe.config.Config
import cats.effect.Sync
import scala.sys.process.Process

final case class Settings(
    configuration: Configuration,
    vcsType: Settings.VcsType,
    vcsApiHost: Uri,
    vcsLogin: String,
    gitAskPass: Path
) {

  def vcsUser[F[_]](implicit F: Sync[F]): F[VcsUser] = {
    F.delay {
      val secret = Process(gitAskPass.toString).!!.trim
      VcsUser(vcsLogin, secret)
    }
  }

}

object Settings {

  sealed abstract class VcsType(val value: String)
  object VcsType {
    case object BitbucketCloud extends VcsType("bitbucket-cloud")
    def values: Set[VcsType] = Set(BitbucketCloud)
  }

  implicit val configParser: ArgParser[Configuration] = SimpleArgParser.from[Configuration]("path") { s =>
    val file = Paths.get(s).toFile
    Try(ConfigFactory.parseFile(file)).toEither
      .flatMap(Configuration.from(_))
      .leftMap(t => MalformedValue("com.typesafe.confog", s"Invalid config file: ${t.getMessage}"))
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
