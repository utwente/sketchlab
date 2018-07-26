#!/bin/bash -eu
# Try to use maven wrapper, otherwise try to use local installation.
mvn_cmd() {
    mvn_command="$(pwd)/mvnw"
    if [ ! -f "$mvn_command" ]; then
        if [ ! $(hash mvn 2>/dev/null) ]; then
            exit 1
        fi
        mvn_command="mvn"
    fi
    "${mvn_command}" $@
    return $?
}

# Determines the project version, as written in the POM file.
determine_project_version() {
    echo $(mvn_cmd org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }')
    return $? 
}

# Make sure there are no untracked changes.
git_has_changes() {
    [ -n "$(git diff-index --name-only HEAD --)" ]
}

# Update the POM version to a new version, using Maven's versions plugin.
# param 1: Release path name (e.g. release, development etc)
# param 2: The version to set.
update_pom_version() {
    echo "Updating POM version to ${1} version: ${2}"
    mvn_cmd versions:set -DnewVersion="${2}"
}

# Creates a commit for a certain release path.
# param 1: Release path name (e.g. release, development etc)
# param 2: The version name to commit.
create_commit() {
    echo "Creating ${1} commit."
    git commit -a -m "Update POM for ${1} to version ${2}"
}

# Prepare the development (dev) branch for work in a certain release path. Checks out the dev 
# branch, updates the POM version to a new version and creates a new commit for it.
# param 1: Release path name (e.g. release, development etc)
# param 2: The version name to commit.
prepare_dev_branch() {
    echo "Preparing dev branch for ${1} version."
    echo "Checkout to dev branch."
    git checkout dev
    update_pom_version ${1} "${2}"
    create_commit ${1} "${2}"
}

# Merges the dev branch into the master branch.
merge_dev_into_master() {
    echo "Checkout master branch and merge dev"
    git checkout master
    git merge dev
}

# Create a new tag for the given tag name, ask for a tag message.
# param 1: The tag name.
create_git_tag() {
    echo "Creating tag \"${1}\"."
    git tag -a "${1}" 
}

if [ "$#" == "0" ]; then
    echo "Usage: $0 {version,create}"
    exit 0
fi

if [ ! -f "$(pwd)/pom.xml" ]; then
    echo "There is no POM file in the current directory."
    exit 1
fi

case "$1" in
    version)
        echo $(determine_project_version)
    ;;
    create)           
        echo "Creating new release."
        echo "This process may ask for your Git password multiple times."
        
        # Make sure there are no untracked changes in Git.
        if git_has_changes; then
            echo "Untracked changes detected." 
            echo "Please take care of these before trying to create a release."
            exit 1
        fi
        
        # Determine the release version name.
        temp_version=$(determine_project_version)
        echo "POM file currently describes the project as being: ${temp_version}."
        if [[ "${temp_version}" == *"-SNAPSHOT" ]]; then
            echo "> Found \"-SNAPSHOT\" appendix, removing this part."
            temp_version=$(sed 's/-SNAPSHOT//' <<< "${temp_version}")
        fi
        read -p "Do you want to use \"${temp_version}\" as the release version? (y/n) "
        if [[ "$REPLY" =~  [yY] ]]; then
            release_version="${temp_version}"
            git_tag="v${release_version}"
        else
            while : ; do
                read -p "What version should be used as the release version? " temp_version
                read -p "Use \"${temp_version}\" as the release version? (y/n) "
                if [[ "$REPLY" =~ [yY] ]]; then
                    release_version="${temp_version}"
                    git_tag="v${release_version}"
                    break
                fi
            done
        fi
        echo "Using \"${release_version}\" as new release version, using \"${git_tag}\" as Git tag."
        echo
        
        # Determine the development version name.
        while : ; do
            read -p "To what development version should the POM be updated after the release? " temp_version
            read -p "Use \"${temp_version}\" as the new development version? (y/n) "
            if [[ "$REPLY" =~ [yY] ]]; then
                dev_version="$temp_version"
                break
            fi
        done
        echo "Using ${dev_version} as new development version."
        echo
        
        # Make sure dev is fully up to date.
        echo "Pulling all from remote."
        git pull --all
        
        # Create release path. 
        prepare_dev_branch release "${release_version}"
        merge_dev_into_master
        create_git_tag "${git_tag}"
        
        # Create dev path.
        prepare_dev_branch development "${dev_version}"
        
        # Push everything and make it final.
        echo "Pushing all changes to remote."
        git push -u origin dev master "${git_tag}"
    ;;
    *)
        echo "Unknown operation: ${1}"
        exit 1
    ;;
esac
