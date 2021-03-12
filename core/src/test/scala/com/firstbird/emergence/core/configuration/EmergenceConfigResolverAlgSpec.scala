package com.firstbird.emergence.core.configuration

import cats.syntax.all._
import com.firstbird.emergence.core.condition._
import com.firstbird.emergence.core.vcs.model._
import testutil._

import scala.concurrent.duration._

class EmergenceConfigResolverAlgSpec extends BaseSpec {

  test("loadAndCombine use runConfig if no repo config file exists and no runEmergenceConfig") {
    val initial = TestState()

    val result = configResolver
      .loadAndCombine(Repository("firstbird", "test"), None)
      .runA(initial)
      .unsafeRunSync()

    result mustBe EmergenceConfig(
      List(
        Condition.BuildSuccessAll,
        Condition.Author(ConditionOperator.Equal, ConditionValue("firstbird"))
      ),
      MergeConfig(
        MergeStrategy.MergeCommit.some,
        false.some,
        1.second.some
      ).some
    )
  }

  test("loadAndCombine use runEmergenceConfig with higher priority if no repo config file exists") {
    val initial = TestState()

    val runEmergenceConfig = EmergenceConfig(
      Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")) :: Nil,
      MergeConfig(
        MergeStrategy.FastForward.some,
        true.some,
        2.seconds.some
      ).some
    )

    val result = configResolver
      .loadAndCombine(Repository("firstbird", "test"), runEmergenceConfig.some)
      .runA(initial)
      .unsafeRunSync()

    result mustBe EmergenceConfig(
      List(
        Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")),
        Condition.BuildSuccessAll,
        Condition.Author(ConditionOperator.Equal, ConditionValue("firstbird"))
      ),
      MergeConfig(
        MergeStrategy.FastForward.some,
        true.some,
        2.seconds.some
      ).some
    )
  }

  test("loadAndCombine use reepo config with highest priority") {
    val repoFile = """
    |conditions:
    |  - "source-branch == update/x"
    |merge:
    |  strategy: squash
    |  close_source_branch: true
    |  throttle: 3 seconds
    """.stripMargin

    val initial = TestState(
      repoEmergenceConfigFile = RepoFile(repoFile).some
    )

    val runEmergenceConfig = EmergenceConfig(
      Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")) :: Nil,
      MergeConfig(
        MergeStrategy.FastForward.some,
        false.some,
        2.seconds.some
      ).some
    )

    val result = configResolver
      .loadAndCombine(Repository("firstbird", "test"), runEmergenceConfig.some)
      .runA(initial)
      .unsafeRunSync()

    result mustBe EmergenceConfig(
      List(
        Condition.SourceBranch(ConditionOperator.Equal, ConditionValue("update/x")),
        Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")),
        Condition.BuildSuccessAll,
        Condition.Author(ConditionOperator.Equal, ConditionValue("firstbird"))
      ),
      MergeConfig(
        MergeStrategy.Squash.some,
        true.some,
        3.seconds.some
      ).some
    )
  }

}
