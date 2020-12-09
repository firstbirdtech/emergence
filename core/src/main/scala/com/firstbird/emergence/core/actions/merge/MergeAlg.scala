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
