package com.fgrutsch.emergence.core.vcs

import cats.data.StateT
import com.fgrutsch.emergence.core.vcs.model._
import testutil._

class MockVcsAlg extends VcsAlg[Eff] {

  override def listPullRequests(repo: Repository): Eff[List[PullRequest]] = {
    val pr1 = PullRequest(
      PullRequestNumber(1),
      PullRequestTitle("Test"),
      BranchName("update/a"),
      BranchName("master"),
      Author("fgrutsch")
    )

    val pr2 = PullRequest(
      PullRequestNumber(2),
      PullRequestTitle("Test2"),
      BranchName("update/b"),
      BranchName("master"),
      Author("fgrutsch")
    )

    val pr3 = PullRequest(
      PullRequestNumber(3),
      PullRequestTitle("Test3"),
      BranchName("update/c"),
      BranchName("master"),
      Author("fgrutsch")
    )

    StateT.pure(pr1 :: pr2 :: pr3 :: Nil)
  }

  override def listBuildStatuses(repo: Repository, number: PullRequestNumber): Eff[List[BuildStatus]] = {
    number match {
      case PullRequestNumber(1) =>
        val bs = BuildStatus(BuildStatusName("Build and Test"), BuildStatusState.Success)
        StateT.pure(bs :: Nil)
      case PullRequestNumber(2) =>
        val bs = BuildStatus(BuildStatusName("Build and Test"), BuildStatusState.Failed)
        StateT.pure(bs :: Nil)
      case PullRequestNumber(3) =>
        val bs = BuildStatus(BuildStatusName("Build and Test"), BuildStatusState.Success)
        StateT.pure(bs :: Nil)
      case _ =>
        throw new IllegalArgumentException(s"listBuildStatuses for PR #${number} not mocked!!!")
    }
  }

  override def mergePullRequest(
      repo: Repository,
      number: PullRequestNumber,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): Eff[Unit] = {
    val pr = TestState.MergedPr(number, mergeStrategy, closeSourceBranch)
    StateT.modify(_.addMergedPr(pr))
  }

  override def mergeCheck(repo: Repository, number: PullRequestNumber): Eff[MergeCheck] = {
    number match {
      case PullRequestNumber(1) | PullRequestNumber(2) => StateT.pure(MergeCheck.Accept)
      case PullRequestNumber(3)                        => StateT.pure(MergeCheck.Decline("failed"))
      case _                                           => throw new IllegalArgumentException(s"mergeCheck for PR #${number} not mocked!!!")
    }
  }

  override def findEmergenceConfigFile(repo: Repository): Eff[Option[RepoFile]] = {
    StateT.inspect(_.repoEmergenceConfigFile)
  }

}
