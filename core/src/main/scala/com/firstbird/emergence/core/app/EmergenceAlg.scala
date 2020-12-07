package com.firstbird.emergence.core.app

import cats.effect.{Concurrent, ExitCode}
import cats.instances.all._
import cats.syntax.all._
import com.firstbird.emergence.core._
import com.firstbird.emergence.core.actions.merge.MergeAlg
import com.firstbird.emergence.core.configuration.EmergenceConfigResolverAlg
import com.firstbird.emergence.core.configuration.RunConfig.RepositoryConfig
import com.firstbird.emergence.core.model.Settings
import fs2.Stream
import io.chrisdavenport.log4cats.Logger

class EmergenceAlg[F[_]: Concurrent](implicit
    settings: Settings,
    logger: Logger[F],
    configResolverAlg: EmergenceConfigResolverAlg[F],
    mergeAlg: MergeAlg[F],
    F: MonadThrowable[F]) {

  def run: F[ExitCode] = {
    val stream = Stream
      .emits(settings.config.repositories.toList)
      .evalMap(emergence)
      .compile

    for {

      _      <- logger.info("Running eMERGEnce.")
      result <- stream.foldMonoid.map(_.fold(_ => ExitCode.Error, _ => ExitCode.Success))
    } yield result
  }

  private def emergence(repoConfig: RepositoryConfig): F[Either[Throwable, Unit]] = {
    val repo = repoConfig.name

    val r = F.attempt {
      for {
        _               <- logger.info(s"Starting to merge PRs for the following repository: ${repo}")
        emergenceConfig <- configResolverAlg.loadAndMerge(repo, repoConfig.emergenceConfig)
        _               <- mergeAlg.mergePullRequests(repo, emergenceConfig)
      } yield ()
    }

    r.flatTap {
      case Right(v) => F.unit
      case Left(t)  => logger.error(t)("ERROR") // TODO
    }
  }

}
