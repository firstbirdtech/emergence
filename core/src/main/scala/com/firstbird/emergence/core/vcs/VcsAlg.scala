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

package com.firstbird.emergence.core.vcs

import java.io.File

import com.firstbird.emergence.core.vcs.model._

trait VcsAlg[F[_]] {

  def listPullRequests(repo: Repository): F[List[PullRequest]]

  def listBuildStatuses(repo: Repository, number: PullRequestNumber): F[List[BuildStatus]]

  def mergePullRequest(
      repo: Repository,
      number: PullRequestNumber,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): F[Unit]

  def isMergeable(repo: Repository, number: PullRequestNumber): F[Mergable]

  def findEmergenceConfigFile(repo: Repository): F[Option[File]]

}
