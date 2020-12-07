package com.firstbird.emergence.core

import cats.data.ValidatedNel

package object condition {

  type MatchResult = ValidatedNel[String, Unit]

}
