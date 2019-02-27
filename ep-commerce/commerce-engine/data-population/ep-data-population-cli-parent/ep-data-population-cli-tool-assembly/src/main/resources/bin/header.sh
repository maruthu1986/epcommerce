#!/bin/bash

### Functions ###

function getDirectoryPath() {
	echo $(cd "$1"; pwd)
}

### Path Variables ###

# Export the installation and working directories so they're available to the CLI tool
export DATA_POPULATION_HOME=$(getDirectoryPath "$SCRIPT_PATH/$ROUTE_TO_DATA_POPULATION_HOME")
export DATA_POPULATION_WORKING_DIRECTORY="$DATA_POPULATION_HOME/tmp"

### JAVA OPTIONS ###

# Use $JAVA_HOME if set, otherwise just rely on the java application on the $PATH
JAVA_CMD="java"
if [ -n "$JAVA_HOME" ] ; then
	JAVA_CMD="$JAVA_HOME/bin/java"
fi

# Setup Java debug parms if the environment variable is set
JAVA_DEBUG_PARMS=""
if [ "$DATA_POPULATION_DEBUG" = "true" ]; then
  echo "Running in Java debug mode (port 5005)"
  JAVA_DEBUG_PARMS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
fi

# Setup Java memory parms
JAVA_MEMORY_HEAPSIZE="-Xmx1536m"
JAVA_MEMORY_MAXPERMSIZE="-XX:MaxPermSize=786m"

# This classpath is updated below once the configuration directory has been determined
DP_CLASSPATH="$DATA_POPULATION_HOME/lib/*"

### CLASSPATH OPTIONS ###

# Now update the classpath to put a static and dynamic configuration directory on the classpath.
# The static classpath directory will configure logging and be supplied by the tool at build time.
# The dynamic classpath directory is used so that an importexporttool.config (based on the configured database connection) can be generated at runtime
# and put on the classpath. Versions of Import/Export before 6.9 requires it to be on the classpath. Versions after can just specify a file location.

export STATIC_CONFIGURATION_CLASSPATH_DIRECTORY="$DATA_POPULATION_HOME/config"
export DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY="$DATA_POPULATION_WORKING_DIRECTORY/config/on-classpath"

# If the dynamic classpath directory exists and is not empty clear the directory as otherwise it can lead to classpath problems
# Don't delete the directory itself as it needs to be there before we start the JVM, but clear the contents
if [ -e "$DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY" ]; then
	if [ $(ls -A "$DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY") ]; then
        rm "$DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY/"*
    fi
else
	# Make sure the directory exists before we launch as this can also lead to classpath issues
    mkdir -p "$DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY"
fi

DP_CLASSPATH="$DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY:$STATIC_CONFIGURATION_CLASSPATH_DIRECTORY:$DP_CLASSPATH"