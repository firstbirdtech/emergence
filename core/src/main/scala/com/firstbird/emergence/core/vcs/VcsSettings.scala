package com.firstbird.emergence.core.vcs

import sttp.model.Uri

final case class VcsSettings(apiHost: Uri, user: VcsSettings.VcsUser, repositoryConfigName: String)

object VcsSettings {

  final case class VcsUser(login: String, secret: String)

}
