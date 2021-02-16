package com.firstbird.emergence.core.vcs

import cats.effect._
import com.firstbird.emergence.core.vcs.bitbucketcloud.BitbucketCloudVcs
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import testutil._

class VcsFactorySpec extends BaseSpec {

  implicit private val sttpBackend = AsyncHttpClientCatsBackend.stub[IO]
  private val vcsFactory           = new VcsFactory[IO]

  test("getVcs returns a BitbucketCloudVcs instance") {
    val result = vcsFactory.getVcs(settings)
    result match {
      case _: BitbucketCloudVcs[IO] => succeed
      case _                        => fail("wrong VcsAlg")
    }

  }

}
