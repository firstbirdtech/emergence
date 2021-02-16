# Merge Settings

Below you can see an overview of available merge settings that you can specify in the @ref:[Configuration](configuration.md).

| Name                        | Required | Default | Description                                                                                       |
|-----------------------------|----------|---------|---------------------------------------------------------------------------------------------------|
| `merge.strategy`            | false    | squash  | The merge strategy to use for merging the PR. Allowed: `merge-commit`, `squash` or `fast-forward` |
| `merge.close_source_branch` | false    | true    | Whether to close/delete the source branch or not.                                                 |

