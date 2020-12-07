package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder

sealed trait MergeStrategy

object MergeStrategy {

  case object MergeCommit extends MergeStrategy
  case object Squash      extends MergeStrategy
  case object FastForward extends MergeStrategy

  implicit val mergeStrategyDecoder: Decoder[MergeStrategy] = Decoder.decodeString.flatMap {
    case "merge-commit" => Decoder.const(MergeCommit)
    case "squash"       => Decoder.const(Squash)
    case "fast-forward" => Decoder.const(FastForward)
    case s              => Decoder.failedWithMessage(s"Invalid merge strategy: '$s'")
  }

}
