# Releasing

To release a new version of emergence follow the following steps:

1. Make sure all changes are commited to the master branch and that the builds successfully passed 
2. Use git to tag the lastest master commit with the new version: `git tag -a vx.x.x -m "vx.x.x"`
3. Reload SBT so the `sbt-dynver` plugin uses the previously tagged version
4. Use `sbt show version` to check if the project version is correct
5. Use `sbt publish` to publish the artifact to [https://bintray.com/firstbird/maven/emergence](https://bintray.com/firstbird/maven/emergence)
6. Use `sbt core/docker:publish` to publish a new docker version to [https://hub.docker.com/r/firstbird/emergence](https://hub.docker.com/r/firstbird/emergence)
7. Push local git tag to remote using `git push --follow-tags`
8. Run `sbt docs/mdoc` to generate documenation
9. Commit documentation generation change and push it to master
10. Create a new release in [GitHub](https://github.com/firstbirdtech/emergence/releases)
11. Sync new version to maven central via [bintray](https://bintray.com/firstbird/maven/emergence)