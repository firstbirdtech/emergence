package com.firstbird.emergence.core.vcs.model

import com.firstbird.emergence.core.vcs.model.BuildStatusState.{Failed, Success}

sealed trait BuildStatusState {

  def isSuccess: Boolean = this match {
    case Success => true
    case Failed  => false
  }

  def isFailure: Boolean = !isSuccess

}

object BuildStatusState {
  case object Success extends BuildStatusState
  case object Failed  extends BuildStatusState
}
