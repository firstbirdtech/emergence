package com.firstbird.emergence.core.vcs.model

final case class PullRequest(
    number: PullRequestNumber,
    title: PullRequestTitle,
    branchName: BranchName
)
