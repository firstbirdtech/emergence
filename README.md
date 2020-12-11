# eMERGEnce

[![Maven Central](https://img.shields.io/maven-central/v/com.firstbird.emergence/core_2.13.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.firstbird.emergence%22%20AND%20a:%22core_2.13%22)
[![Github Actions CI Workflow](https://github.com/firstbirdtech/emergence/workflows/CI/badge.svg)](https://github.com/firstbirdtech/emergence/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/firstbirdtech/emergence/branch/master/graph/badge.svg?token=mTUZsPVuXK)](https://codecov.io/gh/firstbirdtech/emergence)
[![Docker Pulls](https://img.shields.io/docker/pulls/firstbird/emergence.svg)](https://img.shields.io/docker/pulls/firstbird/emergence.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

eMERGEnce is a bot that helps you to get rid of all your emerged pull requests automatically on configurable conditions.

## Getting Started

The current supported Version Control System are:

* [BitBucket Cloud](https://bitbucket.org/product)

We appreciate contributions for other VCS implementations as well, just show us your PR!

### Running eMERGEnce

You can run eMERGEnce with [Docker](https://www.docker.com/).

To get a list of available options run: `docker run firstbird/emergence:latest --help` and you should get the following output:

```
eMERGEnce 0.1.0
Usage: emergence [options]
  --usage  <bool>
        Print usage and exit
  --help | -h  <bool>
        Print help message and exit
  --config  <path>
        The path to the eMERGEnce run config file.
  --vcs-type  <BitbucketCloud>
        The type of VCS you want to run eMERGEnce.
  --vcs-api-host  <uri>
        The base URI for VCS api calls. e.g. https://api.bitbucket.org/2.0
  --vcs-login  <string>
        The username for authenticating VCS API calls.
  --git-ask-pass  <path>
        The path to the executable script file that returns your VCS secret for authenticating VCS API calls.
  --repo-config-name  <string>
        The name/path of the eMERGEnce config file inside the repository. Default: .emergence.yml
```

An example `docker run` command might look like this (depending on the selected VCS type):

```
docker run -v $HOST_DIR:/opt/emergence -it firstbird/emergence:latest \
    --config "/opt/emergence/run-config.yml" \
    --vcs-type "bitbucket-cloud" \
    --vcs-api-host "https://api.bitbucket.org/2.0" \
    --vcs-login ${BITBUCKET_USERNAME} \ 
    --git-ask-pass "opt/emergence/git-ask-pass.sh"
```

Please check [Configuring eMERGEnce](#configuring-emergence) for the format of the file passed via the `--config` option.

The [--git-ask-pass](https://git-scm.com/docs/gitcredentials) option must be the path to an executable that returns to stdout. For example:

```
#!/bin/sh
echo "my-git-secret"
```

### Configuring eMERGEnce

Configuration for eMERGEence can be configured using the `--config` option as well as with a repository local configuration file.

#### Operators

| Operator Name | Description         |
|---------------|---------------------|
| ==            | Exact equals.       |
| ^$            | A valid Java regex. |

#### Conditions

| Condition Name    | Operators | Description                                                 |
|-------------------|-----------|-------------------------------------------------------------|
| build-success-all |           | All build results for the pull requests must be successful. |
| author            | == OR ^$  | The name of the author that created the pull request.       |
| source-branch     | == OR ^$  | The name of the source branch.                              |
| target-branch     | == OR ^$  | The name of the target branch.                              |
| build-success     | == OR ^$  | The name of a specific build result                         |

#### Merge Settings

| Name                      | Required | Default | Description                                                                                 |
|---------------------------|----------|---------|---------------------------------------------------------------------------------------------|
| merge.strategy            | false    | squash  | The merge strategy to use for merging the PR. Allowed: merge-commit, squash or fast-forward |
| merge.close_source_branch | false    | true    | Whether to close/delete the source branch or not.                                           |


#### Config Resolver

The `conditions` and `merge` settings are resolved with a given priority using this strategy (from lowest to highest):

1. `defaults` object from the run config specified via the `--config` option
2. `repositories.[i]` config per array element from the run config specified via the `--config` option
3. The configuration provided in the repository directly, specified via the `--repo-config-name` option

For example if your run config looks like this (`--config` option):

```
repositories:
  - name: firstbirdtech/my-repo
    conditions:
      - "build-success-all"
    merge:
      strategy: fast-forward
      close_source_branch: true

defaults:
  merge:
    strategy: merge-commit
    close_source_branch: false
  conditions:
    - "author == emergence"
```

and in the repository `firstbirdtech/my-repo` you have the file `.emergence.yml`:

```
conditions:
  - "source-branch ^$ ^update\/.+$"
  - "target-branch == master"
merge:
  strategy: squash
  close_source_branch: false
```

then eMERGEnce will use the follwing settings:

* Conditions:
    *  "build-success-all"
    *  "author == emergence"
    *  "target-branch == master"
* Merge
    * Strategy: squash
    * Close Source Branch: false

Please note that conditions are combined instead of overwritten!

## Credits

The code of this project is heavily inspired and based on [fthomas's](https://github.com/fthomas) awesome [scala-steward](https://github.com/scala-steward-org/scala-steward) project. Go check it out!

## Contributors

* [Fabian Grutsch](https://github.com/fgrutsch)

## License

This code is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.txt).