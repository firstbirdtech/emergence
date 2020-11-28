package com.firstbird.emergence.core.vcs.bitbucketcloud

import com.firstbird.emergence.core.vcs.model._
import com.firstbird.emergence.core.vcs.bitbucketcloud.Encoding._
import io.circe.Encoder
import io.circe.Json

private[bitbucketcloud] final case class MergePullRequestRequest(
    closeSourceBranch: Boolean,
    mergeStrategy: MergeStrategy
)

private[bitbucketcloud] object MergePullRequestRequest {

  implicit val mergePullRequestEncoder: Encoder[MergePullRequestRequest] = {
    Encoder.instance { m =>
      Json.obj(
        "close_source_branch" -> Json.fromBoolean(m.closeSourceBranch),
        "merge_strategy"      -> mergeStrategyEncoder(m.mergeStrategy)
      )
    }
  }

}
