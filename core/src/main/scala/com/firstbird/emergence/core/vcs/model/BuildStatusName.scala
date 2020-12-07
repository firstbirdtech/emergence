package com.firstbird.emergence.core.vcs.model

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class BuildStatusName(underlying: String) extends AnyVal {
  override def toString: String = underlying
}

object BuildStatusName {

  implicit val buildStatusCodec: Codec[BuildStatusName] = deriveUnwrappedCodec

}
