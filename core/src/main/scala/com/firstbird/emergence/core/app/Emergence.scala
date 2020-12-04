package com.firstbird.emergence.core.app

import cats.effect.ExitCode
import cats.instances.all._
import cats.syntax.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.app.CliOptions
import com.firstbird.emergence.core.vcs.Vcs
import io.chrisdavenport.log4cats.Logger

class Emergence[F[_]](options: CliOptions)(implicit logger: Logger[F], vcs: Vcs[F], F: MonadThrowable[F]) {

  def run: F[ExitCode] = {
    for {
      _ <- logger.info("Running eMERGEnce.")
      vcsRepo = options.config.repositories.head.name
      result1 <- vcs.listPullRequests(vcsRepo)
      result3 <- result1.map(pr => vcs.isMergeable(vcsRepo, pr.number)).sequence
      _       <- logger.info(s"is mergable: $result3")
      result2 <- result1.map(pr => vcs.listBuildStatuses(vcsRepo, pr.number).map(s => (pr, s))).sequence
      exitCode <-
        (
          if (result2.head._2.forall(_.state.isSuccess)) {
            println("PR considered: " + result2.head._2)
            println("All Build statuses are succesful.")
            F.pure(ExitCode.Success)
            // vcs.mergePullRequest(
            //   repo,
            //   result1.head.number,
            //   MergeStrategy.Squash,
            //   true)
          } else { F.pure { println("Not all build status are succefful"); ExitCode.Success } }
        )
    } yield exitCode
  }

}
