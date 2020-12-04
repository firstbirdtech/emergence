package com.firstbird.emergence.core

import cats.effect.IO
import cats.effect.ExitCode
import com.firstbird.emergence.core.app._
import caseapp.cats.IOCaseApp
import com.firstbird.emergence.core.app.CliOptions
import com.firstbird.emergence.core.app.CliOptions._
import caseapp.core.RemainingArgs

object App extends IOCaseApp[CliOptions] {

  def run(options: CliOptions, args: RemainingArgs): IO[ExitCode] = {
    EmergenceContext[IO](options).use(_.run)
  }

}
