package com.firstbird.emergence.core.condition

final case class ConditionValue(underlying: String) extends AnyVal {
  override def toString: String = underlying
}
