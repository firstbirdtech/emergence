package com.fgrutsch.emergence.core.utils

import cats.syntax.all._
import com.fgrutsch.emergence.core.utils.config._
import io.circe.DecodingFailure
import io.circe.literal._
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

import scala.concurrent.duration._

class ConfigSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode BuildStatusState successfully") {
    val table = Table(
      "input"     -> "expected",
      "2 seconds" -> 2.seconds.asRight,
      "1 minute"  -> 1.minute.asRight,
      "Inf"       -> DecodingFailure("Expected a finite duration.", Nil).asLeft,
      "-Inf"      -> DecodingFailure("Expected a finite duration.", Nil).asLeft,
      "invalid"   -> DecodingFailure("Invalid finite duration: format error invalid", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = json"$input"
      val result    = jsonInput.as[FiniteDuration]
      result mustBe expected
    }
  }

}
