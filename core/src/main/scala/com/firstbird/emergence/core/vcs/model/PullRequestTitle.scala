package com.firstbird.emergence.core.vcs.model

import io.circe.generic.extras.semiauto.deriveUnwrappedCodec
import io.circe.Decoder

final case class PullRequestTitle(value: String) extends AnyVal {
  override def toString: String = value
}

object PullRequestTitle {

  implicit val pulRequestTitleDecoder: Decoder[PullRequestTitle] = deriveUnwrappedCodec

}
