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

import com.fgrutsch.emergence.core.vcs.model.*

final private[github] case class GithubPullRequest(
    number: PullRequestNumber,
    title: PullRequestTitle,
    sourceBranchName: BranchName,
    sourceBranchHead: Commit,
    targetBranchName: BranchName,
    author: Author,
    draft: Boolean,
    mergeable: Option[Boolean] // Only present as response to PR query
) {
  def toPullRequest(): PullRequest =
    PullRequest(
      number,
      title,
      sourceBranchName,
      sourceBranchHead,
      targetBranchName,
      author
    )
}
