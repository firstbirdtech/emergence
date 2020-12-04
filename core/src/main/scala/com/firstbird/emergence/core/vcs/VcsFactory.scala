package com.firstbird.emergence.core.vcs

import com.firstbird.emergence.core._
import com.firstbird.emergence.core.vcs.bitbucketcloud._
import com.firstbird.emergence.core.model._
import com.firstbird.emergence.core.app._
import sttp.client3.SttpBackend

final class VcsFactory[F[_]: MonadThrowable](implicit sttpBackend: SttpBackend[F, Any], settings: VcsSettings) {

  private def bitbucketCloud(): Vcs[F] = {
    new BitbucketCloudVcs()
  }

  def getVcs(options: CliOptions): Vcs[F] = options.vcsType match {
    case CliOptions.VcsType.BitbucketCloud => bitbucketCloud()
  }

}
