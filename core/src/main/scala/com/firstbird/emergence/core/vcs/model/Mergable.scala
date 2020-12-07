package com.firstbird.emergence.core.vcs.model

sealed trait Mergable {
  def isSuccess: Boolean = this match {
    case Mergable.Yes   => true
    case Mergable.No(_) => false
  }

  def isFailure: Boolean = !isSuccess
}

object Mergable {

  case object Yes                     extends Mergable
  final case class No(reason: String) extends Mergable

  def cond(b: Boolean, noReason: => String): Mergable = if (b) Yes else No(noReason)

}
