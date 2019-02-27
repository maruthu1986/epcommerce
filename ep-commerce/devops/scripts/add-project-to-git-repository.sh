#!/bin/bash
# This script adds a new project to GitHub or a similar git hosting service.
#   1. Create a new GitHub repository
#   2. Copy the HTTPS URL of the repository
#   3. Run this script from the root directory of the project to be added to GitHub, with the HTTPS URL as a parameter.
#   4. Open the GitHub repository in a browser, click on Settings, and set the default branch to "develop"

if (( "$#" != 1 )) 
then
	echo "Usage: ./$0 repositoryUrl"
	echo "  Parameters:"
	echo "  * repositoryUrl: The GitHub repository HTTPS URL. This must be a newly created empty repository." 
	exit 1
fi

if ! git config --global -l | grep -E -q 'core.autocrlf=input'  
then
	echo "Git not configured to convert CRLF to LF when adding files to a repository."
	echo "To correct, execute 'git config --global core.autocrlf input'"
	exit 1
fi

if ! git config --global -l | grep -E -q 'user.name='  
then
	echo "Your Git user name is not configured."
	echo "To correct, execute 'git config --global user.name=<your name>'"
	exit 1
fi

if ! git config --global -l | grep -E -q 'user.email='  
then
	echo "Your Git user email address is not configured."
	echo "To correct, execute 'git config --global user.email=<your email address>'"
	exit 1
fi

printf "Adding project to GIT repository $1"
echo " "
set -v

git init
git add --all
git commit -m "Initial commit"
git remote add origin $1
git push -u origin master
git checkout -b develop
git push -u origin develop

