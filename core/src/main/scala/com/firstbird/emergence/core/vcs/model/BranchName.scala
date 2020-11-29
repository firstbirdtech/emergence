package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder

import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class BranchName(value: String) extends AnyVal {
  override def toString: String = value
}

object BranchName {

  implicit val branchNameDecoder: Decoder[BranchName] = deriveUnwrappedCodec

}
