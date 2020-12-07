package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class Author(underlying: String) extends AnyVal {
  override def toString: String = underlying
}

object Author {

  implicit val authorDecoder: Decoder[Author] = deriveUnwrappedCodec

}
