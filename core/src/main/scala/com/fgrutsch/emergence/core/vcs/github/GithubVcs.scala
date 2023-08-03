/*
 * Copyright 2023 Emergence contributors
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

package com.fgrutsch.emergence.core.vcs.github

import cats.MonadThrow
import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.*
import com.fgrutsch.emergence.core.vcs.github.Encoding.given
import com.fgrutsch.emergence.core.vcs.model.*
import io.circe.JsonObject
import sttp.client3.*
import sttp.client3.circe.*
import sttp.model.HeaderNames.Location
import sttp.model.Uri

object GithubVcs {

  private val asRedirect = {
    ignore
      .mapWithMetadata { (s, m) => Either.cond(m.isRedirect, s, s) }
  }

}

final class GithubVcs[F[_]](using backend: SttpBackend[F, Any], settings: VcsSettings, F: MonadThrow[F])
    extends VcsAlg[F] {

  override def listPullRequests(repo: Repository): F[List[PullRequest]] = {
    val uri = settings.apiHost
      .addPath("repos", repo.owner, repo.name, "pulls")
      .addParam("state", "open")
      .addParam("per_page", "50")

    basicRequest
      .get(uri)
      .header("X-GitHub-Api-Version", "2022-11-28")
      .header("Accept", "application/vnd.github+json")
      .withAuthentication()
      .response(asJson[Page[PullRequest]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.map(_.items)))
  }

  override def listBuildStatuses(repo: Repository, pr: PullRequest): F[List[BuildStatus]] = {

    val uri =
      settings.apiHost.addPath("repos", repo.owner, repo.name, "commits", pr.sourceBranchHead.toString, "status")

    basicRequest
      .get(uri)
      .header("X-GitHub-Api-Version", "2022-11-28")
      .header("Accept", "application/vnd.github+json")
      .withAuthentication()
      .response(asJson[BuildStatus])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.map(List(_))))
  }

  override def mergePullRequest(
      repo: Repository,
      pr: PullRequest,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): F[Unit] = {
    val uri  = settings.apiHost.addPath("repos", repo.owner, repo.name, "pulls", pr.number.toString, "merge")
    val body = MergePullRequestRequest(mergeStrategy, pr.sourceBranchHead)

    basicRequest
      .put(uri)
      .header("X-GitHub-Api-Version", "2022-11-28")
      .header("Accept", "application/vnd.github+json")
      .withAuthentication()
      .body(body)
      .response(asJson[JsonObject]) // Verifies 2xx response
      .send(backend)
      .flatMap(r => F.fromEither(r.body.map(_ => ())))
  }

  override def mergeCheck(repo: Repository, pr: PullRequest): F[MergeCheck] = {
    // Github does not have the concept of a merge check. 
    // Instead, if there is a merge conflict, github will not produce a "success" status check
    MergeCheck.Accept.pure[F]
  }

  override def findEmergenceConfigFile(repo: Repository): F[Option[RepoFile]] = {
    val uri =
      settings.apiHost.addPath("repos", repo.owner, repo.name, "contents", settings.repositoryConfigName)

    basicRequest
      .get(uri)
      .header("X-GitHub-Api-Version", "2022-11-28")
      .header("Accept", "application/vnd.github.raw")
      .withAuthentication()
      .response(asEither(ignore, asStringAlways).map(_.toOption))
      .send(backend)
      .map(_.body.map(RepoFile(_)))
  }

  extension (request: Request[Either[String, String], Any]) {
    def withAuthentication() = request.auth.basic(settings.user.login, settings.user.secret)
  }

}
