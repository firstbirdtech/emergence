package com.firstbird.emergence.core.configuration

import cats.data.NonEmptyList
import com.firstbird.emergence.core.configuration.EmergenceConfig._
import com.firstbird.emergence.core.vcs.model.Repository
import com.firstbird.emergence.core.vcs.model.Repository._
import com.typesafe.config.Config
import io.circe.config.syntax._
import io.circe.generic.auto._
import io.circe.{Decoder, Error}

final case class RunConfig(
    repositories: NonEmptyList[RunConfig.RepositoryConfig],
    defaults: Option[EmergenceConfig]
)

object RunConfig {

  final case class RepositoryConfig(
      name: Repository,
      emergenceConfig: Option[EmergenceConfig]
  )

  object RepositoryConfig {
    implicit val repositoryConfigDecoder: Decoder[RepositoryConfig] = Decoder.instance { c =>
      for {
        name            <- c.downField("name").as[Repository]
        emergenceConfig <- c.as[Option[EmergenceConfig]]
      } yield RepositoryConfig(name, emergenceConfig)
    }
  }

  def from(config: Config): Either[Error, RunConfig] = config.as[RunConfig]

  implicit val runConfigDecoder: Decoder[RunConfig] = Decoder.instance { c =>
    for {
      repositories <- c.downField("repositories").as[NonEmptyList[RepositoryConfig]]
      defaults     <- c.downField("defaults").as[Option[EmergenceConfig]]
    } yield RunConfig(repositories, defaults)
  }

}
