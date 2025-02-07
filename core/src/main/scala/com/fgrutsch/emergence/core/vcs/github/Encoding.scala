/*
 * Copyright 2025 Emergence contributors
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

import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.model.*
import com.fgrutsch.emergence.core.vcs.model.BranchName.*
import com.fgrutsch.emergence.core.vcs.model.PullRequestNumber.*
import com.fgrutsch.emergence.core.vcs.model.PullRequestTitle.*
import io.circe.*

private[github] object Encoding {

  given Decoder[GithubPullRequest] = Decoder.instance { c =>
    for {
      id               <- c.downField("number").as[PullRequestNumber]
      title            <- c.downField("title").as[PullRequestTitle]
      sourceBranchName <- c.downField("head").downField("ref").as[BranchName]
      sourceBranchHead <- c.downField("head").downField("sha").as[Commit]
      targetBranchName <- c.downField("base").downField("ref").as[BranchName]
      author           <- c.downField("user").downField("login").as[Author]
      draft            <- c.downField("draft").as[Boolean]
      mergeable        <- c.downField("mergeable").as[Option[Boolean]]
    } yield GithubPullRequest(id, title, sourceBranchName, sourceBranchHead, targetBranchName, author, draft, mergeable)
  }

  given Decoder[BuildStatusState] = {
    Decoder[String].emap {
      case "success" => BuildStatusState.Success.asRight
      case "pending" => BuildStatusState.InProgress.asRight
      case "failure" => BuildStatusState.Failed.asRight
      case s         => s"Unknown build status state: '$s'".asLeft
    }
  }

  given Decoder[BuildStatus] = Decoder.instance { c =>
    for {
      name  <- c.downField("state").as[BuildStatusName]
      state <- c.downField("state").as[BuildStatusState]
    } yield BuildStatus(name, state)
  }

  given mergeStrategyEncoder: Encoder[MergeStrategy] = Encoder.encodeString.contramap[MergeStrategy] {
    case MergeStrategy.MergeCommit => "merge"
    case MergeStrategy.Squash      => "squash"
    case MergeStrategy.FastForward => "rebase"
  }

}
