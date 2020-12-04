package com.firstbird.emergence.core.vcs.model

final case class BuildStatus(
    name: BuildStatusName,
    state: BuildStatusState
)
