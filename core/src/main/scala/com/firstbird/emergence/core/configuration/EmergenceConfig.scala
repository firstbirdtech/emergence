package com.firstbird.emergence.core.configuration

import cats.syntax.all._
import com.firstbird.emergence.core.condition.Condition
import com.typesafe.config.Config
import io.circe.config.syntax._
import io.circe.generic.auto._
import io.circe.{Decoder, Error}

final case class EmergenceConfig(
    conditions: List[Condition],
    merge: Option[MergeConfig]
)

object EmergenceConfig {

  val default: EmergenceConfig = EmergenceConfig(Nil, none)

  def from(config: Config): Either[Error, EmergenceConfig] = config.as[EmergenceConfig]

  implicit val emergenceConfigDecoder: Decoder[EmergenceConfig] = Decoder.instance { c =>
    for {
      conditions <- c.downField("conditions").as[Option[List[Condition]]]
      merge      <- c.downField(("merge")).as[Option[MergeConfig]]
    } yield EmergenceConfig(conditions.getOrElse(Nil), merge)

  }

}
