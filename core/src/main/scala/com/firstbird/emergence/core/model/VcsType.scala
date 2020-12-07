package com.firstbird.emergence.core.model

sealed abstract class VcsType(val underlying: String)

object VcsType {
  case object BitbucketCloud extends VcsType("bitbucket-cloud")
  def values: Set[VcsType] = Set(BitbucketCloud)
}
