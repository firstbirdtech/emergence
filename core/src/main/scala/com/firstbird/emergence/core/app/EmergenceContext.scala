package com.firstbird.emergence.core.app

import cats.effect.{Resource, _}
import com.firstbird.emergence.core.actions.merge.MergeAlg
import com.firstbird.emergence.core.condition.ConditionMatcherAlg
import com.firstbird.emergence.core.configuration.EmergenceConfigResolverAlg
import com.firstbird.emergence.core.model.Settings
import com.firstbird.emergence.core.vcs._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import sttp.client3._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

object EmergenceContext {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](options: CliOptions): Resource[F, EmergenceAlg[F]] = {
    for {
      implicit0(logger: Logger[F])                <- Resource.liftF(Slf4jLogger.create[F])
      implicit0(sttpBackend: SttpBackend[F, Any]) <- AsyncHttpClientCatsBackend.resource[F]()
      implicit0(settings: Settings)               <- Resource.liftF(Settings.from[F](options))
    } yield {
      implicit val vcsFactory          = new VcsFactory
      implicit val vcsAlg              = vcsFactory.getVcs(settings)
      implicit val configResolverAlg   = new EmergenceConfigResolverAlg[F](settings.config)
      implicit val conditionMatcherAlg = new ConditionMatcherAlg[F]
      implicit val mergeAlg            = new MergeAlg[F]
      new EmergenceAlg
    }
  }

}
