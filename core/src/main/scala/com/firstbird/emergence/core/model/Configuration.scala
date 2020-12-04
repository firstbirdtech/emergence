package com.firstbird.emergence.core.model

import com.typesafe.config.Config

import io.circe.generic.auto._
import io.circe.config.syntax._
import io.circe.Error
import io.circe.Decoder

final case class Configuration(
    repositories: List[Configuration.Repository]
)

object Configuration {

  def from(config: Config): Either[Error, Configuration] = config.as[Configuration]

  final case class Repository(owner: String, name: String)

  implicit val repositoryDecoder: Decoder[Repository] = Decoder.decodeString.flatMap { s =>
    s.split('/') match {
      case Array(owner, name) => Decoder.const(Repository(owner, name))
      case _                  => Decoder.failedWithMessage(s"Invalid repository format: '$s'")
    }

  }

}
