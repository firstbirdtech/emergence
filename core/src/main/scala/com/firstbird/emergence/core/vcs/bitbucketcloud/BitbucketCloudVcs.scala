package com.firstbird.emergence.core.vcs.bitbucketcloud

import sttp.client3._
import sttp.client3.circe._
import cats.syntax.all._
import com.firstbird.emergence.core.vcs.model._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.vcs.bitbucketcloud.Encoding._
import com.firstbird.emergence.core.vcs._
import sttp.model.Uri
import cats.Monad
import com.firstbird.emergence.core.model.VcsUser

final class BitbucketCloudVcs[F[_]](baseUri: Uri)(implicit
    backend: SttpBackend[F, Any],
    vcsUser: VcsUser,
    F: MonadThrowable[F])
    extends Vcs[F] {

  override def listPullRequests(repo: Repository): F[List[PullRequest]] = {
    val uri = baseUri.addPath("repositories", repo.owner, repo.name, "pullrequests")

    basicRequest
      .get(uri)
      .withAuthentication()
      .response(asJsonAlways[Page[PullRequest]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.bimap(_.error, _.items)))
  }

  override def listBuildStatuses(repo: Repository, number: PullRequestNumber): F[List[BuildStatus]] = {
    val uri = baseUri.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "statuses")

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
    val uri  = baseUri.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "merge")
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
    def withAuthentication() = request.auth.basic(vcsUser.login, vcsUser.secret)
  }

}
