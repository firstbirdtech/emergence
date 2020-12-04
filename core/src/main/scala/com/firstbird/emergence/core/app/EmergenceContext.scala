package com.firstbird.emergence.core.app

import cats.effect.{Resource, _}
import com.firstbird.emergence.core.vcs._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import sttp.client3._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

object EmergenceContext {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](options: CliOptions): Resource[F, Emergence[F]] = {
    for {
      implicit0(logger: Logger[F])                <- Resource.liftF(Slf4jLogger.create[F])
      implicit0(sttpBackend: SttpBackend[F, Any]) <- AsyncHttpClientCatsBackend.resource[F]()
      implicit0(vcsSettings: VcsSettings)         <- Resource.liftF(options.vcsSettings[F])
    } yield {
      implicit val vcsFactory = new VcsFactory
      implicit val vcs        = vcsFactory.getVcs(options)
      new Emergence(options)
    }
  }

}
