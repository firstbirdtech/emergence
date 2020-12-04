package com.firstbird.emergence.core.vcs.bitbucketcloud

import cats.syntax.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.model._
import com.firstbird.emergence.core.vcs._
import com.firstbird.emergence.core.vcs.bitbucketcloud.DiffStatResponse._
import com.firstbird.emergence.core.vcs.bitbucketcloud.Encoding._
import com.firstbird.emergence.core.vcs.model._
import sttp.client3._
import sttp.client3.circe._
import sttp.model.HeaderNames.Location
import sttp.model.Uri

object BitbucketCloudVcs {

  private def asIgnoreIsRedirect = ignore
    .mapWithMetadata { (s, m) =>
      if (m.isRedirect) Right(s) else Left(s)
    }

}

final class BitbucketCloudVcs[F[_]](implicit backend: SttpBackend[F, Any], settings: VcsSettings, F: MonadThrowable[F])
    extends Vcs[F] {

  override def listPullRequests(repo: Repository): F[List[PullRequest]] = {
    val uri = settings.apiHost
      .addPath("repositories", repo.owner, repo.name, "pullrequests")
      .addParam("state", "OPEN")

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

  override def mergePullRequest(
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

  override def isMergeable(repo: Repository, number: PullRequestNumber): F[Boolean] = {
    val uri =
      settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "diffstat")

    val redirectResponse = basicRequest
      .get(uri)
      .followRedirects(false)
      .withAuthentication()
      .response(BitbucketCloudVcs.asIgnoreIsRedirect)
      .send(backend)

    for {
      resp1  <- redirectResponse
      newUrl <- F.fromOption(resp1.header(Location), new IllegalStateException((s"Header expected: '${Location}'")))
      uri    <- F.fromEither(Uri.parse(newUrl).leftMap(f => new IllegalArgumentException(s"Not a valid URI: '${newUrl}'")))
      resp2  <- basicRequest.get(uri).withAuthentication().response(asJsonAlways[Page[DiffStatResponse]]).send(backend)
      result <- F.fromEither(resp2.body.leftMap(_.error))
    } yield result.items.forall(_.isMergeable())
  }

  implicit private class RequestOps(request: Request[Either[String, String], Any]) {
    def withAuthentication() = request.auth.basic(settings.user.login, settings.user.secret)
  }

}
