package com.firstbird.emergence.core.vcs.bitbucketcloud

import io.circe.literal._
import testutil.BaseSpec

class DiffStatResponseSpec extends BaseSpec {

  test("isMergeable returns true if status contains 'modified'") {
    val response = DiffStatResponse("modified")
    response.isMergeable() mustBe true
  }

  test("isMergeable returns false if status does not contain 'modified'") {
    val response = DiffStatResponse("removed")
    response.isMergeable() mustBe false
  }

  test("decode json successfully") {
    val input = json"""{
        "status": "modified"
        }"""

    val result = input.as[DiffStatResponse]
    result.value mustBe DiffStatResponse("modified")
  }

}
