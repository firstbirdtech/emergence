package com.firstbird.emergence.core.vcs

import com.firstbird.emergence.core._
import com.firstbird.emergence.core.model.{Settings, VcsType}
import com.firstbird.emergence.core.vcs.bitbucketcloud._
import sttp.client3.SttpBackend

final class VcsFactory[F[_]: MonadThrowable](implicit sttpBackend: SttpBackend[F, Any]) {

  def getVcs(settings: Settings): VcsAlg[F] = settings.vcsType match {
    case VcsType.BitbucketCloud => bitbucketCloud(settings.vcs)
  }

  private def bitbucketCloud(implicit settings: VcsSettings): VcsAlg[F] = {
    new BitbucketCloudVcs()
  }

}
