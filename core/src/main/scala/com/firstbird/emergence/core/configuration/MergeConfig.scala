package com.firstbird.emergence.core.configuration

import cats.syntax.all._
import com.firstbird.emergence.core.vcs.model.MergeStrategy
import io.circe.Decoder

final case class MergeConfig(
    strategy: Option[MergeStrategy],
    closeSourceBranch: Option[Boolean]
)

object MergeConfig {

  val default: MergeConfig = MergeConfig(none, none)

  implicit val mergeConfigDecoder: Decoder[MergeConfig] = Decoder.instance { c =>
    for {
      strategy          <- c.downField("strategy").as[Option[MergeStrategy]]
      closeSourceBranch <- c.downField("close_source_branch").as[Option[Boolean]]
    } yield MergeConfig(strategy, closeSourceBranch)
  }

}
