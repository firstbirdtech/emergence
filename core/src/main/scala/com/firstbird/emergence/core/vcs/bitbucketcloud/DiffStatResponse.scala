package com.firstbird.emergence.core.vcs.bitbucketcloud

import io.circe.Decoder

final private[bitbucketcloud] case class DiffStatResponse(status: String) {

  def isMergeable(): Boolean = status match {
    case "modified" => true
    case _          => false
  }

}

private[bitbucketcloud] object DiffStatResponse {

  implicit val diffStatsResponseDecoder: Decoder[DiffStatResponse] = Decoder.instance { c =>
    c.downField("status").as[String].map(DiffStatResponse(_))
  }

}
