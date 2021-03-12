# Merge Settings

Below you can see an overview of available merge settings that you can specify in the @ref:[Configuration](configuration.md).

| Name                        | Required | Default   | Description                                                                                                                                                                                                                                                                |
|-----------------------------|----------|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `merge.strategy`            | false    | squash    | The merge strategy to use for merging the PR. Allowed: `merge-commit`, `squash` or `fast-forward`                                                                                                                                                                          |
| `merge.close_source_branch` | false    | true      | Whether to close/delete the source branch or not.                                                                                                                                                                                                                          |
| `merge.throttle`            | false    | 0 seconds | Throttle time after merging a PR before processing the next one. (This can be helpful if the VCS implementation includes a merge conflict check which would be ignored if all PRs are processed/merged at the same time as there wouldn't be enough time to figure it out. |

