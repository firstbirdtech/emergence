package com.firstbird.emergence.core.condition

import com.firstbird.emergence.core.vcs.model.{BuildStatus, PullRequest}

final case class Input(
    pullRequest: PullRequest,
    buildStatuses: List[BuildStatus]
)
