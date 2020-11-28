package com.firstbird.emergence.core.vcs.model

import io.circe.Decoder
import com.firstbird.emergence.core.vcs.model.BuildStatusState.Success
import com.firstbird.emergence.core.vcs.model.BuildStatusState.Failed

sealed trait BuildStatusState {

  def isSuccess: Boolean = this match {
    case Success => true
    case Failed => false
  }

  def isFailure: Boolean = !isSuccess

}

object BuildStatusState {
  case object Success extends BuildStatusState
  case object Failed  extends BuildStatusState
}
