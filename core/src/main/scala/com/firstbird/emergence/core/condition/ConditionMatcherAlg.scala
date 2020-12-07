package com.firstbird.emergence.core.condition

import cats.data.NonEmptyList
import cats.syntax.all._
import com.firstbird.emergence.core.condition.Condition
import com.firstbird.emergence.core.condition.ConditionMatcher.syntax._
import com.firstbird.emergence.core.configuration.EmergenceConfig
import com.firstbird.emergence.core.vcs.model._

class ConditionMatcherAlg[F[_]] {

  def checkConditions(emergenceConfig: EmergenceConfig, input: Input): MatchResult = {

    val checkConditionMatches: Condition => MatchResult = {
      case c: Condition.BuildSuccessAll.type => c.matches(input.buildStatuses)
      case c: Condition.Author               => c.matches(input.pullRequest.author)
      case c: Condition.SourceBranch         => c.matches(input.pullRequest.sourceBranchName)
      case c: Condition.TargetBranch         => c.matches(input.pullRequest.targetBranchName)
      case c: Condition.BuildSuccess         => c.matches(input.buildStatuses)
    }

    NonEmptyList.fromList(emergenceConfig.conditions) match {
      case Some(conditions) =>
        val empty = ().validNel[String]
        conditions.foldLeft(empty) { (acc: MatchResult, a: Condition) =>
          (checkConditionMatches(a), acc).mapN((_, _) => ())
        }

      case None =>
        "No conditions provided. At least one condition needed in order to execute a merge.".invalidNel
    }
  }

  implicit private def buildSuccessAllConditionMatcher
      : ConditionMatcher[Condition.BuildSuccessAll.type, List[BuildStatus]] = {
    ConditionMatcher.of[Condition.BuildSuccessAll.type, List[BuildStatus]] { (condition, input) =>
      input
        .map { bs =>
          if (bs.state.isSuccess) ().validNel
          else s"Build is not succesful: ${bs.name}".invalidNel
        }
        .sequence
        .map(_ => ())
    }
  }

  implicit private def authorConditionMatcher: ConditionMatcher[Condition.Author, Author] = {
    ConditionMatcher.of[Condition.Author, Author] { (condition, input) =>
      ConditionOperator.matches(condition, input.underlying)
    }
  }

  implicit private def sourceBranchNameConditionMatcher: ConditionMatcher[Condition.SourceBranch, BranchName] = {
    ConditionMatcher.of[Condition.SourceBranch, BranchName] { (condition, input) =>
      ConditionOperator.matches(condition, input.underlying)
    }
  }

  implicit private def targetBranchNameConditionMatcher: ConditionMatcher[Condition.TargetBranch, BranchName] = {
    ConditionMatcher.of[Condition.TargetBranch, BranchName] { (condition, input) =>
      ConditionOperator.matches(condition, input.underlying)
    }
  }

  implicit private def buildSuccessConditionMatcher: ConditionMatcher[Condition.BuildSuccess, List[BuildStatus]] = {
    ConditionMatcher.of[Condition.BuildSuccess, List[BuildStatus]] { (condition, input) =>
      input
        .map(bs => ConditionOperator.matches(condition, bs.name.underlying))
        .sequence
        .map(_ => ())
    }
  }

}
