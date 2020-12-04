package com.firstbird.emergence.core.condition

import cats.syntax.all._

sealed trait ConditionField

object ConditionField {
  case object Author       extends ConditionField
  case object SourceBranch extends ConditionField
  case object TargetBranch extends ConditionField
  case object BuildSuccess extends ConditionField

  def unapply(value: String): Option[ConditionField] = {
    value match {
      case "author"        => Author.some
      case "source-branch" => SourceBranch.some
      case "target-branch" => TargetBranch.some
      case "build-success" => BuildSuccess.some
      case _               => none
    }
  }
}
