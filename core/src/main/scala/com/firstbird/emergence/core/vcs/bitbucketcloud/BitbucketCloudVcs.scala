package com.firstbird.emergence.core.vcs.bitbucketcloud

import cats.syntax.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.vcs._
import com.firstbird.emergence.core.vcs.bitbucketcloud.Encoding._
import com.firstbird.emergence.core.vcs.model._
import sttp.client3._
import sttp.client3.circe._

final class BitbucketCloudVcs[F[_]](implicit backend: SttpBackend[F, Any], settings: VcsSettings, F: MonadThrowable[F])
    extends Vcs[F] {

  override def listPullRequests(repo: Repository): F[List[PullRequest]] = {
    val uri = settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests")

    basicRequest
      .get(uri)
      .withAuthentication()
      .response(asJsonAlways[Page[PullRequest]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.bimap(_.error, _.items)))
  }

  override def listBuildStatuses(repo: Repository, number: PullRequestNumber): F[List[BuildStatus]] = {
    val uri =
      settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "statuses")

    basicRequest
      .get(uri)
      .withAuthentication()
      .response(asJsonAlways[Page[BuildStatus]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.bimap(_.error, _.items)))
  }

  def mergePullRequest(
      repo: Repository,
      number: PullRequestNumber,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): F[Unit] = {
    val uri  = settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "merge")
    val body = MergePullRequestRequest(closeSourceBranch, mergeStrategy)

    basicRequest
      .post(uri)
      .withAuthentication()
      .body(body)
      .response(asJsonAlways[PullRequest])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.bimap(_.error, _ => ())))
  }

  implicit private class RequestOps(request: Request[Either[String, String], Any]) {
    def withAuthentication() = request.auth.basic(settings.user.login, settings.user.secret)
  }

}
