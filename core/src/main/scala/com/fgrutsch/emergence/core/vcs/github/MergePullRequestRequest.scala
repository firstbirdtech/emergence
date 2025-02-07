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

import com.fgrutsch.emergence.core.vcs.github.Encoding.{mergeStrategyEncoder, *}
import com.fgrutsch.emergence.core.vcs.model.*
import io.circe.{Encoder, Json}

final private[github] case class MergePullRequestRequest(
    mergeStrategy: MergeStrategy,
    sha: Commit
)

private[github] object MergePullRequestRequest {

  given Encoder[MergePullRequestRequest] = {
    Encoder.instance { m =>
      Json.obj(
        "merge_method" -> mergeStrategyEncoder(m.mergeStrategy),
        "sha"          -> Json.fromString(m.sha.toString)
      )
    }
  }

}
