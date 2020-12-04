package com.firstbird.emergence.core.configuration

import cats.data.NonEmptyList
import com.firstbird.emergence.core.condition.Condition
import com.firstbird.emergence.core.model._

final case class RepositoryConfig(
    name: Repository,
    conditions: NonEmptyList[Condition],
    merge: Option[MergeConfig]
)
