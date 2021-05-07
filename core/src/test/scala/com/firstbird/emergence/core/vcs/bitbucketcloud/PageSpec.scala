package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import io.circe.literal._
import testutil.BaseSpec

class PageSpec extends BaseSpec {

  test("decode json successfully") {
    val input = json"""{
        "values": ["test"]
    }"""

    val result = input.as[Page[String]]
    result.value mustBe Page("test" :: Nil)
  }

}
