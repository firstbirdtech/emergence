package com.firstbird.emergence.core.vcs.bitbucketcloud

import cats.syntax.all._
import com.firstbird.emergence.core.vcs.model.MergeStrategy
import io.circe.DecodingFailure
import io.circe.literal._
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class MergeStrategySpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode MergeStrategy successfully") {
    val table = Table(
      "input"        -> "expected",
      "merge-commit" -> MergeStrategy.MergeCommit.asRight,
      "squash"       -> MergeStrategy.Squash.asRight,
      "fast-forward" -> MergeStrategy.FastForward.asRight,
      "invalid"      -> DecodingFailure("Invalid merge strategy: 'invalid'", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = json"$input"
      val result    = jsonInput.as[MergeStrategy]
      result mustBe expected
    }
  }

}
