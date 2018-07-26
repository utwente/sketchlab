# Sketchlab development tools
This directory represent tools used for developing Sketchlab. As of this moment, Sketchlab uses the following external tools: 
* [Release tools](#release-tools)(`release_tools.sh`)

## Release tools
This script is used to create a new version release, this script uses a combination of Maven and Git commands to make releasing a new version as smooth as possible. Note that this script has to be called from the root of the project, where the `pom.xml` file is present.
```
Usage: <project root>/tools/release_tools.sh {version,create}
``` 
### Operations
Release tools currently supports two operations:
* Determine project version (`version`)
* Create a new release (`create`)
These operations are passed to the script as a parameter.
#### `version` operation
This will print the current project version, as described in the POM file.
#### `create` operation
This operation will create a release using the following steps:
* Check if there are untracked changes, fail if so.
* Query user for new version to be released. This will also determine the Git tag name, e.g. if the release should be version "0.1", the Git tag will become "v0.1"  
* Query user for new version to which the `dev` branch should be set after release.
* Pull all changes from remote, using `git pull --all`
* Prepare `dev` branch for release, this comprises of the following steps:
    * Switch/checkout the `dev` branch.
    * Update POM version to the new to-be-released version, using the Maven versions plugin (`mvn versions:set -DnewVersion=<new version>`)
    * Create commit stating the version was updated.
* Merge `dev` into `master`:
    * Checkout `master`.
    * Merge `dev` into `master` using `git merge dev`
* Create a Git tag for the current state of the `master` branch, using the aforementioned Git tag version. Note that the script will prompt for a release message.
* Prepare `dev` branch for new development work, using the following steps:
    * Switch/checkout the `dev` branch.
    * Update POM version to the new development version, using the Maven versions plugin (`mvn versions:set -DnewVersion=<new version>`)
    * Create commit stating the version was updated.
* Push all changes to remote.
