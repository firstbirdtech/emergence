package com.firstbird.emergence.core.condition

import cats.syntax.all._
import io.circe.Decoder

sealed trait Condition

object Condition {

  final case class DynamicCondition(field: ConditionField, operator: ConditionOperator, value: Value) extends Condition

  sealed trait StaticCondition extends Condition
  object StaticCondition {
    case object BuildSuccessAll extends StaticCondition

    def unapply(value: String): Option[StaticCondition] = {
      value match {
        case "build-success-all" => BuildSuccessAll.some
        case _                   => none
      }
    }
  }

  def parse(value: String): Either[String, Condition] = {
    val staticCondition = StaticCondition.unapply(value)

    val dynamicCondition = value.split(' ') match {
      case Array(ConditionField(f), ConditionOperator(o), v) => DynamicCondition(f, o, Value(v)).some
      case _                                                 => none
    }

    staticCondition
      .orElse(dynamicCondition)
      .toRight(s"Not a valid condition: '$value'")
  }

  implicit val conditionDecoder: Decoder[Condition] = Decoder.decodeString.map(parse(_)).flatMap {
    case Left(f)      => Decoder.failedWithMessage(s"Invalid condition format: $f")
    case Right(value) => Decoder.const(value)
  }

}
