package com.firstbird.emergence.core.vcs.bitbucketcloud

import io.circe._
import cats.syntax.all._
import com.firstbird.emergence.core.vcs.model.PullRequestNumber._
import com.firstbird.emergence.core.vcs.model.PullRequestTitle._
import com.firstbird.emergence.core.vcs.model.BranchName._
import com.firstbird.emergence.core.vcs.model._

private[bitbucketcloud] object Encoding {

  implicit val pullRequestDecoder: Decoder[PullRequest] = Decoder.instance { c =>
    for {
      id         <- c.downField("id").as[PullRequestNumber]
      title      <- c.downField("title").as[PullRequestTitle]
      branchName <- c.downField("source").downField("branch").downField("name").as[BranchName]
    } yield PullRequest(id, title, branchName)
  }

  implicit val buildStatusDecoder: Decoder[BuildStatus] = Decoder.instance { c =>
    for {
      name  <- c.downField("name").as[BuildStatusName]
      state <- c.downField("state").as[BuildStatusState]
    } yield BuildStatus(name, state)
  }

  implicit val buildStatusStateDecoder: Decoder[BuildStatusState] = {
    Decoder[String].emap {
      case "SUCCESSFUL" => Right(BuildStatusState.Success)
      case "FAILED"     => Right(BuildStatusState.Failed)
      case u            => Left(s"Unknown build status state '$u'")
    }
  }

  implicit val mergeStrategyEncoder: Encoder[MergeStrategy] = Encoder.encodeString.contramap[MergeStrategy] {
    case MergeStrategy.MergeCommit => "merge_commit"
    case MergeStrategy.Squash      => "squash"
    case MergeStrategy.FastForward => "fast_forward"
  }

}
