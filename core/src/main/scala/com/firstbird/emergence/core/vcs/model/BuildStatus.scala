package com.firstbird.emergence.core.vcs.model

import io.circe._

final case class BuildStatus(
    name: BuildStatusName,
    state: BuildStatusState
)
