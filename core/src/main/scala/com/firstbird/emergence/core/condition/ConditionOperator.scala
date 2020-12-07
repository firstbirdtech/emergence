package com.firstbird.emergence.core.condition

import cats.syntax.all._

sealed abstract class ConditionOperator(val sign: String) {
  override def toString: String = sign
}

object ConditionOperator {
  case object Equal extends ConditionOperator("==")
  case object RegEx extends ConditionOperator("^$")

  def unapply(value: String): Option[ConditionOperator] = {
    value match {
      case Equal.sign => Equal.some
      case RegEx.sign => RegEx.some
      case _          => none

    }
  }

  def matches(condition: Condition with Condition.Matchable, input: String): MatchResult = {
    val operator = condition.operator
    val value    = condition.value

    condition.operator match {
      case Equal if input == value.underlying         => ().validNel
      case RegEx if value.underlying.r.matches(input) => ().validNel
      case _                                          => s"Input '${input}' doesn't match condition '$value' with operator '$operator'".invalidNel
    }
  }

}
