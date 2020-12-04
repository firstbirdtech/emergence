package com.firstbird.emergence.core.condition

final case class Value(value: String) extends AnyVal {
  override def toString: String = value
}
