package com.firstbird.emergence.core.model

import cats.data.NonEmptyList
import cats.effect.IO
import cats.syntax.all._
import com.firstbird.emergence.core.app.CliOptions
import com.firstbird.emergence.core.condition._
import com.firstbird.emergence.core.configuration.RunConfig.RepositoryConfig
import com.firstbird.emergence.core.configuration.{RunConfig, _}
import com.firstbird.emergence.core.model.{Settings, VcsType}
import com.firstbird.emergence.core.vcs.model._
import sttp.model.Uri._
import testutil.BaseSpec

import java.nio.file.Paths

class SettingsSpec extends BaseSpec {

  test("from CliOptions to Settings") {
    val cliOptions = CliOptions(
      RunConfig(
        NonEmptyList.one(
          RepositoryConfig(Repository("firstbird", "test"), none)
        ),
        EmergenceConfig(
          List(
            Condition.BuildSuccessAll,
            Condition.Author(ConditionOperator.Equal, ConditionValue("firstbird"))
          ),
          MergeConfig(
            MergeStrategy.MergeCommit.some,
            false.some
          ).some
        ).some
      ),
      VcsType.BitbucketCloud,
      uri"http://localhost",
      "demo",
      Paths.get("core", "src", "test", "resources", "test-gitAsk.sh"),
      ".emergence.yml"
    )

    Settings.from[IO](cliOptions).unsafeRunSync() mustBe settings
  }

}
