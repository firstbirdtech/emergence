/*
 * Copyright 2021 Emergence contributors
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

package com.firstbird.emergence.core.app

import cats.effect.ExitCode
import cats.instances.all._
import cats.syntax.all._
import com.firstbird.emergence.BuildInfo
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.configuration.EmergenceConfigResolverAlg
import com.firstbird.emergence.core.configuration.RunConfig.RepositoryConfig
import com.firstbird.emergence.core.merge.MergeAlg
import com.firstbird.emergence.core.model.Settings
import com.firstbird.emergence.core.utils.logging._
import fs2.Stream
import io.chrisdavenport.log4cats.Logger

class EmergenceAlg[F[_]](implicit
    settings: Settings,
    logger: Logger[F],
    configResolverAlg: EmergenceConfigResolverAlg[F],
    mergeAlg: MergeAlg[F],
    streamCompiler: Stream.Compiler[F, F],
    F: MonadThrowable[F]) {

  private val banner = {
    """
      |     ___  ___ ___________ _____  _____               
      |     |  \/  ||  ___| ___ \  __ \|  ___|              
      |  ___| .  . || |__ | |_/ / |  \/| |__ _ __   ___ ___ 
      | / _ \ |\/| ||  __||    /| | __ |  __| '_ \ / __/ _ \
      ||  __/ |  | || |___| |\ \| |_\ \| |__| | | | (_|  __/
      | \___\_|  |_/\____/\_| \_|\____/\____/_| |_|\___\___|
    """.stripMargin
  }

  def run: F[ExitCode] = {
    val stream = Stream
      .emits(settings.config.repositories.toList)
      .evalMap(emergence)
      .compile

    for {

      _      <- logger.info(s"$banner")
      _      <- logger.info(s"Running eMERGEnce with version: ${BuildInfo.version}")
      _      <- printConfiguredRepos()
      result <- stream.foldMonoid.map(_.fold(_ => ExitCode.Error, _ => ExitCode.Success))
    } yield result
  }

  private def emergence(repoConfig: RepositoryConfig): F[Either[Throwable, Unit]] = {
    val repo = repoConfig.name

    val result = F.attempt {
      for {
        _               <- logger.info(s"Starting to merge PRs for repository: ${repo}")
        emergenceConfig <- configResolverAlg.loadAndMerge(repo, repoConfig.emergenceConfig)
        _               <- mergeAlg.mergePullRequests(repo, emergenceConfig)
        _               <- logger.info(sectionSeperator)
      } yield ()
    }

    result.flatTap {
      case Right(_) => F.unit
      case Left(t)  => logger.error(t)(s"Failure on running emergence for repostiory: $repo")
    }
  }

  private def printConfiguredRepos() = {
    val s = bulletPointed(settings.config.repositories.map(_.name).toList)
    logger.info(s"The following repositories are configured:$s")
  }

}
