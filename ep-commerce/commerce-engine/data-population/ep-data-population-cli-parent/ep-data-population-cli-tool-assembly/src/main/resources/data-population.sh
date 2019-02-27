#!/bin/bash

SCRIPT_PATH=$(cd $(dirname "$0"); pwd)
BIN_PATH="$SCRIPT_PATH/bin"
ROUTE_TO_DATA_POPULATION_HOME="."

# Include the header file
. "$BIN_PATH/header.sh"

# Invoke the Data Population CLI Tool
"$JAVA_CMD" "$JAVA_MEMORY_HEAPSIZE" "$JAVA_MEMORY_MAXPERMSIZE" -cp "$DP_CLASSPATH" $JAVA_DEBUG_PARMS  \
    com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication "$@"