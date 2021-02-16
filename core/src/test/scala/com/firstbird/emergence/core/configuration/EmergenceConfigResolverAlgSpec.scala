package com.firstbird.emergence.core.configuration

import cats.syntax.all._
import com.firstbird.emergence.core.condition._
import com.firstbird.emergence.core.vcs.model._
import testutil._

class EmergenceConfigResolverAlgSpec extends BaseSpec {

  test("loadAndMerge use runConfig if no repo config file exists and no runEmergenceConfig") {
    val initial = TestState()

    val result = configResolver
      .loadAndMerge(Repository("firstbird", "test"), None)
      .runA(initial)
      .unsafeRunSync()

    result mustBe EmergenceConfig(
      List(
        Condition.BuildSuccessAll,
        Condition.Author(ConditionOperator.Equal, ConditionValue("firstbird"))
      ),
      MergeConfig(
        MergeStrategy.MergeCommit.some,
        false.some
      ).some
    )
  }

  test("loadAndMerge use runEmergenceConfig with higher priority no repo config file exists") {
    val initial = TestState()

    val runEmergenceConfig = EmergenceConfig(
      Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")) :: Nil,
      MergeConfig(
        MergeStrategy.FastForward.some,
        true.some
      ).some
    )

    val result = configResolver
      .loadAndMerge(Repository("firstbird", "test"), runEmergenceConfig.some)
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
        true.some
      ).some
    )
  }

  test("loadAndMerge use reepo config with highest priority") {
    val repoFile = """
    |conditions:
    |  - "source-branch == update/x"
    |merge:
    |  strategy: squash
    |  close_source_branch: true
    """.stripMargin

    val initial = TestState(
      repoEmergenceConfigFile = RepoFile(repoFile).some
    )

    val runEmergenceConfig = EmergenceConfig(
      Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")) :: Nil,
      MergeConfig(
        MergeStrategy.FastForward.some,
        false.some
      ).some
    )

    val result = configResolver
      .loadAndMerge(Repository("firstbird", "test"), runEmergenceConfig.some)
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
        true.some
      ).some
    )
  }

}
