package com.firstbird.emergence.core.vcs.bitbucketcloud

import cats.syntax.all._
import com.firstbird.emergence.core.vcs.bitbucketcloud.Encoding._
import com.firstbird.emergence.core.vcs.model.{MergeStrategy, _}
import io.circe.DecodingFailure
import io.circe.literal._
import io.circe.syntax._
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class EncodingSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode PullRequest successfully") {
    val input = json"""{
        "id": 1,
        "title": "Test",
        "source": {
            "branch": {
                "name": "update/abc"
            }
        },
        "destination": {
            "branch": {
                "name": "master"
            }
        },
        "author": {
            "nickname": "firstbird"
        }
    }"""

    val result = input.as[PullRequest]
    result.value mustBe PullRequest(
      PullRequestNumber(1),
      PullRequestTitle("Test"),
      BranchName("update/abc"),
      BranchName("master"),
      Author("firstbird")
    )
  }

  test("decode BuildStatus successfully") {
    val input = json"""{
        "name": "Build and Test",
        "state": "SUCCESSFUL"
    }"""

    val result = input.as[BuildStatus]
    result.value mustBe BuildStatus(BuildStatusName("Build and Test"), BuildStatusState.Success)
  }

  test("decode BuildStatusState successfully") {
    val table = Table(
      "input"      -> "expected",
      "SUCCESSFUL" -> BuildStatusState.Success.asRight,
      "INPROGRESS" -> BuildStatusState.InProgress.asRight,
      "FAILED"     -> BuildStatusState.Failed.asRight,
      "STOPPED"    -> BuildStatusState.Stopped.asRight,
      "invalid"    -> DecodingFailure("Unknown build status state: 'invalid'", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = json"$input"
      val result    = jsonInput.as[BuildStatusState]
      result mustBe expected
    }
  }

  test("encode MergeStrategy successfully") {
    val table = Table[MergeStrategy, String](
      "input"                   -> "expepcted",
      MergeStrategy.MergeCommit -> "merge_commit",
      MergeStrategy.Squash      -> "squash",
      MergeStrategy.FastForward -> "fast_forward"
    )

    forAll(table) { case (input, expected) =>
      val result = input.asJson
      result mustBe json"$expected"
    }
  }

}
