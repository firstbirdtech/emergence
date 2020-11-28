package com.firstbird.emergence.core

import cats.effect.IO
import cats.effect.ExitCode
import com.firstbird.emergence.core.app._
import caseapp.cats.IOCaseApp
import com.firstbird.emergence.core.model.Settings
import com.firstbird.emergence.core.model.Settings._
import caseapp.core.RemainingArgs

object App extends IOCaseApp[Settings] {

  def run(settings: Settings, args: RemainingArgs): IO[ExitCode] = {
    EmergenceContext[IO](settings).use(_.run)
  }

}
