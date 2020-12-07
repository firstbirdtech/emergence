package com.firstbird.emergence.core.condition

import cats.syntax.all._
import io.circe.Decoder

sealed trait Condition

object Condition {

  trait Matchable {
    def operator: ConditionOperator
    def value: ConditionValue
  }

  case object BuildSuccessAll                                                       extends Condition
  final case class Author(operator: ConditionOperator, value: ConditionValue)       extends Condition with Matchable
  final case class SourceBranch(operator: ConditionOperator, value: ConditionValue) extends Condition with Matchable
  final case class TargetBranch(operator: ConditionOperator, value: ConditionValue) extends Condition with Matchable
  final case class BuildSuccess(operator: ConditionOperator, value: ConditionValue) extends Condition with Matchable

  def parse(value: String): Either[String, Condition] = {
    val condition          = parseSimpleCondition(value)
    val matchabelCondition = parseMatchableCondition(value.split(' '))

    condition
      .orElse(matchabelCondition)
      .toRight(s"Not a valid condition: '$value'")
  }

  private def parseMatchableCondition(values: Array[String]): Option[Condition with Matchable] = {
    values match {
      case Array("author", ConditionOperator(operator), v)        => Author(operator, ConditionValue(v)).some
      case Array("source-branch", ConditionOperator(operator), v) => SourceBranch(operator, ConditionValue(v)).some
      case Array("target-branch", ConditionOperator(operator), v) => TargetBranch(operator, ConditionValue(v)).some
      case Array("build-success", ConditionOperator(operator), v) => BuildSuccess(operator, ConditionValue(v)).some
      case _                                                      => none
    }
  }

  private def parseSimpleCondition(value: String): Option[Condition] = {
    value match {
      case "build-success-all" => BuildSuccessAll.some
      case _                   => none
    }
  }

  implicit val conditionDecoder: Decoder[Condition] = Decoder.decodeString.map(parse(_)).flatMap {
    case Left(f)      => Decoder.failedWithMessage(s"Invalid condition format: $f")
    case Right(value) => Decoder.const(value)
  }

}
