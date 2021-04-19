package testutil

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.all._
import com.firstbird.emergence.core.app._
import com.firstbird.emergence.core.condition.{ConditionOperator, ConditionValue, _}
import com.firstbird.emergence.core.configuration.RunConfig.RepositoryConfig
import com.firstbird.emergence.core.configuration.{EmergenceConfig, MergeConfig, _}
import com.firstbird.emergence.core.merge.MergeAlg
import com.firstbird.emergence.core.model.{Settings, VcsType}
import com.firstbird.emergence.core.vcs.VcsSettings.VcsUser
import com.firstbird.emergence.core.vcs.model.{MergeStrategy, Repository}
import com.firstbird.emergence.core.vcs.{VcsSettings, _}
import sttp.model.Uri._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

private[testutil] trait Context {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO]               = IO.timer(ExecutionContext.global)

  implicit val settings = Settings(
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
          false.some,
          1.second.some
        ).some
      ).some
    ),
    VcsType.BitbucketCloud,
    VcsSettings(
      uri"http://localhost",
      VcsUser("demo", "secret"),
      ".emergence.yml"
    )
  )

  implicit val vcsSettings = settings.vcs

  implicit val mockLogger          = new MockLogger
  implicit val mockVcsAlg          = new MockVcsAlg
  implicit val configResolver      = new EmergenceConfigResolverAlg[Eff](settings.config)
  implicit val conditionMatcherAlg = new ConditionMatcherAlg[Eff]
  implicit val mergeAlg            = new MergeAlg[Eff]
  implicit val emergenceAlg        = new EmergenceAlg[Eff]

}
