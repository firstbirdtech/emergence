package com.fgrutsch.emergence.core.vcs.model

import io.circe.Decoder

final case class Ref(underlying: String) extends AnyVal {
  override def toString: String = underlying
}

object Ref {

  given Decoder[Ref] = Decoder.decodeString.map(Ref(_))

}