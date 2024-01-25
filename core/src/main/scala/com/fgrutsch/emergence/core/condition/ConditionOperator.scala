/*
 * Copyright 2024 Emergence contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgrutsch.emergence.core.condition

import cats.data.ValidatedNel
import cats.syntax.all.*

enum ConditionOperator(val sign: String) {
  case Equal extends ConditionOperator("==")
  case RegEx extends ConditionOperator("^$")

  override def toString: String = sign
}

object ConditionOperator {

  def unapply(value: String): Option[ConditionOperator] = ConditionOperator.values.find(_.sign == value)

  def matches(condition: Condition.Matchable, input: String): ValidatedNel[String, Unit] = {
    val operator = condition.operator
    val value    = condition.value

    operator match {
      case Equal if input == value.underlying         => ().validNel
      case RegEx if value.underlying.r.matches(input) => ().validNel
      case _ => s"Input '${input}' doesn't match condition '$value' with operator '$operator'".invalidNel
    }
  }

}
