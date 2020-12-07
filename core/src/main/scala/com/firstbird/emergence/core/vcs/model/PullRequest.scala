package com.firstbird.emergence.core.vcs.model

final case class PullRequest(
    number: PullRequestNumber,
    title: PullRequestTitle,
    sourceBranchName: BranchName,
    targetBranchName: BranchName,
    author: Author
)
