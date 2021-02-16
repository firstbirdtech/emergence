package testutil

import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.must.Matchers

trait BaseSpec extends AnyFunSuiteLike with Matchers with EitherValues with Context
