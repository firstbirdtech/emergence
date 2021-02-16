package com.firstbird.emergence.core.vcs.bitbucketcloud

import com.firstbird.emergence.core.vcs.bitbucketcloud.MergePullRequestRequest._
import com.firstbird.emergence.core.vcs.model.MergeStrategy
import io.circe.literal._
import io.circe.syntax._
import testutil.BaseSpec

class MergePullRequestRequestSpec extends BaseSpec {

  test("encode json successfully") {
    val input = MergePullRequestRequest(true, MergeStrategy.Squash)

    val result = input.asJson

    result mustBe json"""{
        "close_source_branch": true,
        "merge_strategy": "squash"
    }"""
  }

}
