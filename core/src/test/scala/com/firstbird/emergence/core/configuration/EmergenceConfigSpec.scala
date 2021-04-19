package com.firstbird.emergence.core.vcs.bitbucketcloud

import cats.syntax.all._
import com.firstbird.emergence.core.condition._
import com.firstbird.emergence.core.configuration.{EmergenceConfig, MergeConfig}
import com.firstbird.emergence.core.vcs.model.MergeStrategy
import com.typesafe.config.ConfigFactory
import testutil.BaseSpec

import scala.concurrent.duration._

class EmergenceConfigSpec extends BaseSpec {

  test("default returns empty EmergenceConfig") {
    EmergenceConfig.default mustBe EmergenceConfig(Nil, none)
  }

  test("from reads settings from typesafe Config") {
    val config = ConfigFactory.parseString("""
    |conditions = [
    |   "build-success-all",
    |   "author == test"
    |]
    |merge {
    |   "strategy" = "squash"
    |   "close_source_branch" = true
    |   "throttle" = "1 second"
    |}
    """.stripMargin)

    val result = EmergenceConfig.from(config)
    result mustBe EmergenceConfig(
      Condition.BuildSuccessAll :: Condition.Author(ConditionOperator.Equal, ConditionValue("test")) :: Nil,
      MergeConfig(MergeStrategy.Squash.some, true.some, 1.second.some).some
    ).asRight
  }

}
