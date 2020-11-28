package com.firstbird.emergence.core.vcs

import com.firstbird.emergence.core.vcs.model._

trait Vcs[F[_]] {

  def listPullRequests(repo: Repository): F[List[PullRequest]]

  def listBuildStatuses(repo: Repository, number: PullRequestNumber): F[List[BuildStatus]]

  def mergePullRequest(repo: Repository, number: PullRequestNumber, mergeStrategy: MergeStrategy, closeSourceBranch: Boolean): F[Unit]

}
