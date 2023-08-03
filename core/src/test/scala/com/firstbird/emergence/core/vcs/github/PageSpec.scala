package com.fgrutsch.emergence.core.vcs.github

import io.circe.parser.*
import testutil.BaseSpec

class PageSpec extends BaseSpec {

  test("decode json successfully") {
    val input = """[
      "test"
    ]"""

    val result = parse(input).value.as[Page[String]]
    result.value mustBe { Page("test" :: Nil) }
  }

}
