package com.fgrutsch.emergence.core.vcs.github

import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.github.Encoding.given
import com.fgrutsch.emergence.core.vcs.model.{MergeStrategy, *}
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.{DecodingFailure, Json}
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class EncodingSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode PullRequest successfully") {
    val input = """{
        "id": 1461185956,
        "number": 1,
        "state": "open",
        "title": "Removed not working trigger and debug logging.",
        "user": {
            "login": "fgrutsch"
        },
        "head": {
            "label": "radancy-referrals:update-automerge-workflow",
            "ref": "update-automerge-workflow",
            "sha": "1234"
        },
        "base": {
            "label": "radancy-referrals:main",
            "ref": "main",
            "sha": "f26170f221907b98b1dffff40da416e5e84f3962"
        },
        "author_association": "CONTRIBUTOR",
        "auto_merge": null,
        "active_lock_reason": null
    }"""

    val result = parse(input).value.as[PullRequest]
    result.value mustBe {
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Removed not working trigger and debug logging."),
        BranchName("update-automerge-workflow"),
        Ref("1234"),
        BranchName("main"),
        Author("fgrutsch")
      )
    }
  }

  test("decode BuildStatus successfully") {
    val input = """{
        "state": "success"
    }"""

    val result = parse(input).value.as[BuildStatus]
    result.value mustBe { BuildStatus(BuildStatusName("success"), BuildStatusState.Success) }
  }

  test("decode BuildStatusState successfully") {
    val table = Table(
      "input"      -> "expected",
      "success" -> BuildStatusState.Success.asRight,
      "pending" -> BuildStatusState.InProgress.asRight,
      "failure"     -> BuildStatusState.Failed.asRight,
      "invalid"    -> DecodingFailure("Unknown build status state: 'invalid'", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = Json.fromString(input)
      val result    = jsonInput.as[BuildStatusState]
      result mustBe { expected }
    }
  }

  test("encode MergeStrategy successfully") {
    val table = Table[MergeStrategy, String](
      "input"                   -> "expepcted",
      MergeStrategy.MergeCommit -> "merge",
      MergeStrategy.Squash      -> "squash",
      MergeStrategy.FastForward -> "rebase"
    )

    forAll(table) { case (input, expected) =>
      val result = input.asJson
      result mustBe { Json.fromString(expected) }
    }
  }

}
