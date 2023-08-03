package com.fgrutsch.emergence.core.vcs.github

import com.fgrutsch.emergence.core.vcs.github.MergePullRequestRequest.*
import com.fgrutsch.emergence.core.vcs.model.{MergeStrategy}
import com.fgrutsch.emergence.core.vcs.model.{Commit => Commit}
import io.circe.parser.*
import io.circe.syntax.*
import testutil.BaseSpec

class MergePullRequestRequestSpec extends BaseSpec {

  test("encode json successfully") {
    val input = MergePullRequestRequest(MergeStrategy.Squash, Commit("1234"))

    val result = input.asJson

    result mustBe {
      parse("""{
        "merge_method": "squash",
        "sha": "1234"
    }""").value
    }
  }

}
