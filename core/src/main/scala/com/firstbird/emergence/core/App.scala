package com.firstbird.emergence.core

import caseapp.cats.IOCaseApp
import caseapp.core.RemainingArgs
import cats.effect.{ExitCode, IO}
import com.firstbird.emergence.core.app.CliOptions._
import com.firstbird.emergence.core.app.{CliOptions, _}

object App extends IOCaseApp[CliOptions] {

  def run(options: CliOptions, args: RemainingArgs): IO[ExitCode] = {
    EmergenceContext[IO](options).use(_.run)
  }

}
