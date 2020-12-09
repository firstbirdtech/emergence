/*
 * Copyright 2020 Emergence contributors
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
