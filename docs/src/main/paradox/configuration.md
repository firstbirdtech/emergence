# Configuration

To run eMERGEnce you need at least a [Run Config](#run-config) and optionally also a [Repository Config](#repository-config).

## Run Config

The run config is the main configuration needed to actually run eMERGEnce. This is the one that you have to specify using the `--config` option.
In this file you specify the VCS repositories that you want to run emergence on, and optionally some default conditions/settings as well.

The format of the file looks like this:

```yml
repositories:
  - name: fgrutsch/test-repo
    conditions:
      - "build-success-all"
    merge:
      strategy: fast-forward
      close_source_branch: false
  - name: fgrutsch/test-repo-2

defaults:
  conditions:
    - "author == ci-user"
  merge:
    strategy: squash
    close_source_branch: true
```

@@@ note

* `repositories.[0].conditions` attribute is optional
*  `repositories.[0].merge` attribute as well as its nested attributes are optional
* `defaults` attribute as well as its nested attributes are optional. If specified, this applys defaults for all configured repositories. If missing, defaults to `strategy: squash` and `close_source_branch: true`
* Be aware that you need at least one condition to run eMERGEnce (after all configs are resolved)

@@@

## Repository Config

If you would rather like to configure eMERGEnce settings on a repository level then you can do this by creating an `.emergence.yml` (configurable using the `--repo-config-name` option) in the root of your repository. The format of the file looks like this:

```yaml
conditions:
  - "author == ci-user"
merge:
  strategy: squash
  close_source_branch: true
```

@@@ note

* `conditions` attribute is optional
* `merge` attribute as well as its nested attributes are optional

@@@

## Resolver

The `conditions` and `merge` settings are resolved with a given priority using this strategy (from lowest to highest):

1. `defaults` from the [Run Config](#run-config)
2. `repositories.[i]` from the [Run Config](#run-config)
3. [Repository Config](#repository-config)

For example if your [Run Config](#run-config) looks like this:

```
repositories:
  - name: fgrutsch/my-repo
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

and your [Repository Config](#repository-config) like this:

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

@@@ note

Conditions are combined instead of overwritten!

@@@