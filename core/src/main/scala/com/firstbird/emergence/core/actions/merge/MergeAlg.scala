package com.firstbird.emergence.core.actions.merge

import cats.effect.Concurrent
import cats.syntax.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.condition.{ConditionMatcherAlg, Input}
import com.firstbird.emergence.core.configuration.EmergenceConfig
import com.firstbird.emergence.core.vcs.VcsAlg
import com.firstbird.emergence.core.vcs.model.{Mergable, MergeStrategy, Repository}
import fs2.Stream
import io.chrisdavenport.log4cats.Logger

class MergeAlg[F[_]: Concurrent](implicit
    logger: Logger[F],
    vcsAlg: VcsAlg[F],
    conditionMatcherAlg: ConditionMatcherAlg[F],
    F: MonadThrowable[F]) {

  def mergePullRequests(repo: Repository, emergenceConfig: EmergenceConfig): F[Unit] = {
    Stream
      .evals(vcsAlg.listPullRequests(repo))
      .evalFilter { pr =>
        for {
          buildStatuses <- vcsAlg.listBuildStatuses(repo, pr.number)
          input         <- Input(pr, buildStatuses).pure[F]
          matchResult   <- conditionMatcherAlg.checkConditions(emergenceConfig, input).pure[F]
          // TODO log reason
        } yield matchResult.isValid
      }
      .evalFilter { pr =>
        vcsAlg
          .isMergeable(repo, pr.number)
          .flatMap {
            case Mergable.Yes => F.pure(true)
            case Mergable.No(reason) =>
              logger
                .info(s"Skipping PR #${pr.number} as its not in a mergable state. Reason: $reason")
                .map(_ => false)
          }
      }
      // TODO call actual merge
      .mapAsync(1) { pr =>
        vcsAlg.mergePullRequest(repo, pr.number, MergeStrategy.Squash, true) // TODO read settings
      }
      .compile
      .drain
  }

}
