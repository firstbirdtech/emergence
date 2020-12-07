package com.firstbird.emergence.core.vcs

import java.io.File

import com.firstbird.emergence.core.vcs.model._

trait VcsAlg[F[_]] {

  def listPullRequests(repo: Repository): F[List[PullRequest]]

  def listBuildStatuses(repo: Repository, number: PullRequestNumber): F[List[BuildStatus]]

  def mergePullRequest(
      repo: Repository,
      number: PullRequestNumber,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): F[Unit]

  def isMergeable(repo: Repository, number: PullRequestNumber): F[Mergable]

  def findEmergenceConfigFile(repo: Repository): F[Option[File]]

}
