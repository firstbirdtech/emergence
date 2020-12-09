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

package com.firstbird.emergence.core.configuration

import java.io.File

import cats.effect.Sync
import cats.kernel.Semigroup
import cats.syntax.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.configuration._
import com.firstbird.emergence.core.vcs.VcsAlg
import com.firstbird.emergence.core.vcs.model.Repository

class EmergenceConfigResolverAlg[F[_]](runConfig: RunConfig)(implicit
    vcsAlg: VcsAlg[F],
    F: Sync[F] with MonadThrowable[F]) {

  def loadAndMerge(repo: Repository, runEmergenceConfig: Option[EmergenceConfig]): F[EmergenceConfig] = {
    for {
      maybeLocalRepoConfig <- vcsAlg.findEmergenceConfigFile(repo)
      localRepoConfig <- maybeLocalRepoConfig match {
        case Some(config) => parseEmergenceConfig(config).map(_.some)
        case None         => F.pure(none[EmergenceConfig])
      }
      localRepoConfig <- F.pure(none[EmergenceConfig])
    } yield {
      val maybeConfig = localRepoConfig |+| runEmergenceConfig |+| runConfig.defaults
      maybeConfig.getOrElse(EmergenceConfig.default)
    }
  }

  private def parseEmergenceConfig(file: File): F[EmergenceConfig] = {
    F
      .delay(configFromYaml(file))
      .flatMap(F.fromTry)
      .map(config => EmergenceConfig.from(config))
      .flatMap(F.fromEither)
  }

  implicit private def emergenceConfigSemigroup: Semigroup[EmergenceConfig] =
    Semigroup.instance[EmergenceConfig]((x, y) => {
      EmergenceConfig(
        conditions = x.conditions |+| y.conditions,
        merge = x.merge |+| y.merge
      )
    })

  implicit private def mergeConfigSemigroup: Semigroup[MergeConfig] =
    Semigroup.instance[MergeConfig] { (x, y) =>
      MergeConfig(
        strategy = x.strategy.orElse(y.strategy),
        closeSourceBranch = x.closeSourceBranch.orElse(y.closeSourceBranch)
      )
    }

}
