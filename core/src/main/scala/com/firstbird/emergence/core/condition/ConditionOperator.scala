package com.firstbird.emergence.core.condition

import cats.syntax.all._

sealed trait ConditionOperator

object ConditionOperator {
  case object Equal extends ConditionOperator
  case object RegEx extends ConditionOperator

  def unapply(value: String): Option[ConditionOperator] = {
    value match {
      case "==" => Equal.some
      case "$$" => RegEx.some
      case _    => none

    }
  }
}
