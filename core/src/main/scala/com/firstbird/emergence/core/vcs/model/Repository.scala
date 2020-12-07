package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder

final case class Repository(owner: String, name: String) {
  override def toString: String = s"$owner/$name"
}

object Repository {

  implicit val repositoryDecoder: Decoder[Repository] = Decoder.decodeString.flatMap { s =>
    s.split('/') match {
      case Array(owner, name) => Decoder.const(Repository(owner, name))
      case _                  => Decoder.failedWithMessage(s"Invalid repository format: '$s'")
    }
  }

}
