package com.firstbird.emergence.core.vcs.bitbucketcloud

import io.circe.Decoder

private[bitbucketcloud] final case class Page[A](items: List[A])

private[bitbucketcloud] object Page {

  implicit def pageDecoder[A: Decoder]: Decoder[Page[A]] = {
    Decoder.instance { c =>
      c.downField("values").as[List[A]].map(Page(_))
    }
  }

}
