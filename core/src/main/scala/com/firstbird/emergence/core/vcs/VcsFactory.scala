package com.firstbird.emergence.core.vcs

import com.firstbird.emergence.core._
import com.firstbird.emergence.core.vcs.bitbucketcloud._
import com.firstbird.emergence.core.model._
import sttp.client3.SttpBackend

final class VcsFactory[F[_]: MonadThrowable](implicit sttpBackend: SttpBackend[F, Any], vcsUser: VcsUser) {

  private def bitbucketCloud(settings: Settings): Vcs[F] = {
    new BitbucketCloudVcs(settings.vcsApiHost)
  }

  def getVcs(settings: Settings): Vcs[F] = settings.vcsType match {
    case Settings.VcsType.BitbucketCloud => bitbucketCloud(settings)
  }

}
