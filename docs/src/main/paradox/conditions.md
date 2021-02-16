# Conditions

Below you can see an overview of available condition attributes and operators which together form a `condition`.

A sample condition that you can specify in your configuration might look this: `"author == ci-user:`

## Attributes

The current supported pull request attributes are the following:

| Attribute Name      | Operators   | Description                                                                                               |
|---------------------|-------------|-----------------------------------------------------------------------------------------------------------|
| `build-success-all` |             | All build results for the pull requests must be successful. Note: At least 1 PR build status is required. |
| `author`            | `== OR ^$`  | The name of the author that created the pull request.                                                     |
| `source-branch`     | `== OR ^$`  | The name of the source branch.                                                                            |
| `target-branch`     | `== OR ^$`  | The name of the target branch.                                                                            |


## Operators

Operators are used to conditionally match on an attribute's value. Please be aware that not all operators are available for all attributes!

| Operator Name   | Description         |
|-----------------|---------------------|
| `==`            | Exact equals.       |
| `^$`            | A valid Java regex. |