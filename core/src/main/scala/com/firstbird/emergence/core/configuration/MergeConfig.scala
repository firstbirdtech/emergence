package com.firstbird.emergence.core.configuration

import com.firstbird.emergence.core.model.MergeStrategy
import io.circe.Decoder

final case class MergeConfig(
    strategy: Option[MergeStrategy],
    closeSourceBranch: Option[Boolean]
)

object MergeConfig {

  implicit val mergeConfigDecoder: Decoder[MergeConfig] = Decoder.instance { c =>
    for {
      strategy          <- c.downField("strategy").as[Option[MergeStrategy]]
      closeSourceBranch <- c.downField("close_source_branch").as[Option[Boolean]]
    } yield MergeConfig(strategy, closeSourceBranch)
  }

}
