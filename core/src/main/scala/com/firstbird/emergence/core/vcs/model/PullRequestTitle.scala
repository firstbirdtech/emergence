package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class PullRequestTitle(value: String) extends AnyVal {
  override def toString: String = value
}

object PullRequestTitle {

  implicit val pulRequestTitleDecoder: Decoder[PullRequestTitle] = deriveUnwrappedCodec

}
