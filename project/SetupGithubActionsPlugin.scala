import sbt._
import sbtghactions.GenerativePlugin
import sbtghactions.GenerativePlugin.autoImport._
import sbtghactions.WorkflowStep._

object SetupGithubActionsPlugin extends AutoPlugin {

  override def requires: Plugins              = GenerativePlugin
  override def trigger                        = allRequirements
  override def buildSettings: Seq[Setting[_]] = Seq(
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowJavaVersions += JavaSpec.temurin("17"),
    githubWorkflowBuild   := Seq(WorkflowStep.Sbt(List("codeVerify", "test"))),
    githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release"))),
    githubWorkflowPublishTargetBranches += RefPredicate.StartsWith(Ref.Tag("v")),
    githubWorkflowPublish := Seq(
      WorkflowStep.Sbt(
        List("ci-release"),
        env = Map(
          "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE_NEW }}",
          "PGP_SECRET"        -> "${{ secrets.PGP_SECRET_NEW }}",
          "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
          "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
        )
      )
    ),
    githubWorkflowPublishPostamble ++= List(
      WorkflowStep.Use(
        UseRef.Public("docker", "login-action", "v2"),
        name = Some("Login to Docker Hub"),
        params = Map("username" -> "${{ secrets.DOCKER_USERNAME }}", "password" -> "${{ secrets.DOCKER_TOKEN }}")
      ),
      WorkflowStep.Run(List("sbt core/docker:publish"), name = Some("Publish docker image")),
      WorkflowStep.Run(
        List("sbt docs/paradox"),
        name = Some("Generate documentation"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')")
      ),
      WorkflowStep.Use(
        UseRef.Public("JamesIves", "github-pages-deploy-action", "v4"),
        name = Some("Publish gh-pages"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')"),
        params = Map("folder" -> "docs/target/paradox/site/main")
      )
    )
  )

}
