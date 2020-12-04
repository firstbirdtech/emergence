package com.firstbird.emergence.core.configuration

import cats.data.NonEmptyList
import com.firstbird.emergence.core.condition.Condition
import com.firstbird.emergence.core.condition.Condition._
import com.typesafe.config.Config
import io.circe.Error
import io.circe.config.syntax._
import io.circe.generic.auto._

final case class EmergenceConfig(
    repositories: List[RepositoryConfig],
    defaults: Option[EmergenceConfig.Defaults]
)

object EmergenceConfig {

  final case class Defaults(
      merge: Option[MergeConfig],
      conditions: Option[NonEmptyList[Condition]]
  )

  def from(config: Config): Either[Error, EmergenceConfig] = config.as[EmergenceConfig]

}
