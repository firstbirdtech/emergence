package com.firstbird.emergence.core.app

import caseapp.core.Error._
import caseapp.core.app.CaseApp
import cats.data.NonEmptyList
import cats.syntax.all._
import com.firstbird.emergence.core.app.CliOptions._
import com.firstbird.emergence.core.condition._
import com.firstbird.emergence.core.configuration.RunConfig.RepositoryConfig
import com.firstbird.emergence.core.configuration.{EmergenceConfig, MergeConfig, RunConfig}
import com.firstbird.emergence.core.model.VcsType
import com.firstbird.emergence.core.vcs.model.{MergeStrategy, Repository}
import sttp.model.Uri._
import testutil.BaseSpec

import java.nio.file.Paths
import scala.concurrent.duration._

class CliOptionsSpec extends BaseSpec {

  test("parse CliOptions") {
    val result = CaseApp.parse[CliOptions](
      List(
        List("--config", "core/src/test/resources/test-runConfig.yml"),
        List("--vcs-type", "bitbucket-cloud"),
        List("--vcs-api-host", "http://localhost"),
        List("--vcs-login", "demo"),
        List("--git-ask-pass", "core/src/test/resources/test-gitAsk.sh")
      ).flatten
    )

    val (r, _) = result.value

    r mustBe CliOptions(
      RunConfig(
        NonEmptyList.one(
          RepositoryConfig(
            Repository("firstbird", "test"),
            EmergenceConfig.default.some
          )
        ),
        EmergenceConfig(
          Condition.BuildSuccessAll :: Nil,
          MergeConfig(MergeStrategy.Squash.some, true.some, 1.second.some).some
        ).some
      ),
      VcsType.BitbucketCloud,
      uri"http://localhost",
      "demo",
      Paths.get("core", "src", "test", "resources", "test-gitAsk.sh"),
      ".emergence.yml"
    )
  }

  test("parse fails on invalid --config option") {
    val result = CaseApp.parse[CliOptions](
      List(
        List("--config", "core/src/test/resources/non-existing.yml"),
        List("--vcs-type", "bitbucket-cloud"),
        List("--vcs-api-host", "http://localhost"),
        List("--vcs-login", "demo"),
        List("--git-ask-pass", "core/src/test/resources/test-gitAsk.sh")
      ).flatten
    )

    result.left.value match {
      case e: SeveralErrors =>
        e.message must startWith(
          "Argument --config: Malformed RunConfig: Unable to read run config: core/src/test/resources/non-existing.yml"
        )
      case _ => fail("Unexpected error")
    }
  }

  test("parse fails on invalid --vcs-type option") {
    val result = CaseApp.parse[CliOptions](
      List(
        List("--config", "core/src/test/resources/test-runConfig.yml"),
        List("--vcs-type", "bitbucket-clouda"),
        List("--vcs-api-host", "http://localhost"),
        List("--vcs-login", "demo"),
        List("--git-ask-pass", "core/src/test/resources/test-gitAsk.sh")
      ).flatten
    )

    result.left.value match {
      case e: SeveralErrors =>
        e.message must startWith("Argument --vcs-type: Malformed VcsType: Value must be one of: bitbucket-cloud")
      case _ =>
        fail("Unexpected error")
    }
  }

  test("parse fails on invalid --git-ask-pass option") {
    val result = CaseApp.parse[CliOptions](
      List(
        List("--config", "core/src/test/resources/test-runConfig.yml"),
        List("--vcs-type", "bitbucket-cloud"),
        List("--vcs-api-host", "://"),
        List("--vcs-login", "demo"),
        List("--git-ask-pass", "core/src/test/resources/non-existing.sh")
      ).flatten
    )

    result.left.value match {
      case e: SeveralErrors =>
        e.message must startWith("Argument --git-ask-pass: Malformed Path: Not a valid file.")
      case _ =>
        fail("Unexpected error")
    }
  }

}
