package com.firstbird.emergence.core.vcs.model

sealed trait MergeStrategy

object MergeStrategy {

  case object MergeCommit extends MergeStrategy
  case object Squash      extends MergeStrategy
  case object FastForward extends MergeStrategy

}
