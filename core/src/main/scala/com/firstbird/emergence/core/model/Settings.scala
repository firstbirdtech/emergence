package com.firstbird.emergence.core.model

import scala.sys.process.Process

import cats.Functor
import cats.effect.Sync
import cats.syntax.all._
import com.firstbird.emergence.core.app.CliOptions
import com.firstbird.emergence.core.configuration.RunConfig
import com.firstbird.emergence.core.model.VcsType
import com.firstbird.emergence.core.vcs.VcsSettings

final case class Settings(
    config: RunConfig,
    vcsType: VcsType,
    vcs: VcsSettings
)

object Settings {

  def from[F[_]: Functor](options: CliOptions)(implicit F: Sync[F]): F[Settings] = {
    val vcsSettings = F.delay {
      val secret = Process(options.gitAskPass.toString).!!.trim
      val user   = VcsSettings.VcsUser(options.vcsLogin, secret)
      VcsSettings(options.vcsApiHost, user, options.repoConfigName)
    }

    vcsSettings
      .map(settings => Settings(options.config, options.vcsType, settings))
  }

}
