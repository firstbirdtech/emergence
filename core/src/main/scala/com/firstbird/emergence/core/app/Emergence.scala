package com.firstbird.emergence.core.app

import com.firstbird.emergence.core.vcs.Vcs
import cats.effect.ExitCode
import cats.syntax.all._
import cats.instances.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.vcs.model.Repository
import cats.Monad
import com.firstbird.emergence.core.vcs.model.{MergeStrategy, Repository => VcsRepo}
import com.firstbird.emergence.core.model.Settings
import cats.effect.IO
import io.chrisdavenport.log4cats.Logger

class Emergence[F[_]](settings: Settings)(implicit logger: Logger[F], vcs: Vcs[F], F: MonadThrowable[F]) {

  def run: F[ExitCode] = {
    for {
      _       <- logger.info("Running emergence.")
      vcsRepo = VcsRepo(settings.configuration.repositories.head.owner, settings.configuration.repositories.head.name)
      result1 <- vcs.listPullRequests(vcsRepo)
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
