package com.fgrutsch.emergence.core.vcs.github

import com.fgrutsch.emergence.core.vcs.model.*

final private[github] case class GithubPullRequest(
    number: PullRequestNumber,
    title: PullRequestTitle,
    sourceBranchName: BranchName,
    sourceBranchHead: Commit,
    targetBranchName: BranchName,
    author: Author,
    draft: Boolean,
    mergeable: Option[Boolean] // Only present as response to PR query
) {
  def toPullRequest(): PullRequest =
    PullRequest(
      number,
      title,
      sourceBranchName,
      sourceBranchHead,
      targetBranchName,
      author
    )
}
