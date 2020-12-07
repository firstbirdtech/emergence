package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class BranchName(underlying: String) extends AnyVal {
  override def toString: String = underlying
}

object BranchName {

  implicit val branchNameDecoder: Decoder[BranchName] = deriveUnwrappedCodec

}
