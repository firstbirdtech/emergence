package com.fgrutsch.emergence.core.vcs.github

import cats.effect.IO
import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.model.*
import io.circe.parser.*
import sttp.client3.Response
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.testing.SttpBackendStub
import sttp.model.{Header, Method, StatusCode}
import testutil.BaseSpec

class GithubVcsSpec extends BaseSpec {

  private given SttpBackendStub[IO, Any] = AsyncHttpClientCatsBackend
    .stub[IO]
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repos" :: "owner" :: "name" :: "pulls" :: Nil) &&
      r.method == Method.GET &&
      r.uri.paramsMap == Map("state" -> "open", "per_page" -> "50")
    }
    .thenRespond(
      parse("""
        [
    {
        "id": 1461185956,
        "number": 1,
        "state": "open",
        "title": "Test",
        "user": {
            "login": "fgrutsch"
        },
        "head": {
            "label": "radancy-referrals:update-automerge-workflow",
            "ref": "update/abc",
            "sha": "1234"
        },
        "base": {
            "label": "radancy-referrals:main",
            "ref": "master",
            "sha": "f26170f221907b98b1dffff40da416e5e84f3962"
        },
        "author_association": "CONTRIBUTOR",
        "auto_merge": null,
        "active_lock_reason": null
    }
    ]
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repos" :: "owner" :: "name" :: "commits" :: "1234" :: "status" :: Nil) &&
      r.method == Method.GET
    }
    .thenRespond(
      parse("""
        {
          "state": "success"
        }
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repos" :: "owner" :: "name" :: "pulls" :: "1" :: "merge" :: Nil) &&
      r.method == Method.PUT
    }
    .thenRespond(
      parse("""
        {
          "sha": "1234",
          "merged": true,
          "message": "Pull Request successfully merged"
        }
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repos" :: "owner" :: "name" :: "contents" :: ".emergence.yml" :: Nil) &&
      r.method == Method.GET && 
      r.headers.contains(Header("Accept", "application/vnd.github.raw"))
    }
    .thenRespond(".emergence.yml-file-content")

  private val githubVcs = new GithubVcs[IO]

  private val dummyPR = PullRequest(
          PullRequestNumber(1),
          PullRequestTitle("Test"),
          BranchName("update/abc"),
          Ref("1234"),
          BranchName("master"),
          Author("fgrutsch")
        )
  test("listPullRequests") {
    val result = githubVcs.listPullRequests(Repository("owner", "name")).unsafeRunSync()
    result mustBe {
      List(
        dummyPR
      )
    }
  }

  test("listBuildStatuses") {
    val result = githubVcs.listBuildStatuses(Repository("owner", "name"), dummyPR).unsafeRunSync()
    result mustBe { List(BuildStatus(BuildStatusName("success"), BuildStatusState.Success)) }
  }

  test("mergePullRequest") {
    val result = githubVcs
      .mergePullRequest(Repository("owner", "name"), dummyPR, MergeStrategy.Squash, true)
      .unsafeRunSync()
    result mustBe { () }
  }

  test("mergeCheck") {
    val result = githubVcs.mergeCheck(Repository("owner", "name"), dummyPR).unsafeRunSync()
    result mustBe { MergeCheck.Accept }
  }

  test("findEmergenceConfig") {
    val result = githubVcs.findEmergenceConfigFile(Repository("owner", "name")).unsafeRunSync()
    result mustBe { RepoFile(".emergence.yml-file-content").some }
  }

}
