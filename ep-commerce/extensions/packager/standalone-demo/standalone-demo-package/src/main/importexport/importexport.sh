#!/bin/sh

if [ -z "$JAVA_HOME" ] ; then
	echo "JAVA_HOME environment variable must be set"
	exit 1;
fi

export LIB=lib

for x in $LIB/*.jar; do
    IE_CLASSPATH=$IE_CLASSPATH:$x
done
export IE_CLASSPATH=.:$IE_CLASSPATH

DEBUG_PARMS=""
if [ "$1" = "debug" ] || [ "$IE_DEBUG" = "true" ]; then
  echo "Running in debug mode, please attach a Java debugger..."
  DEBUG_PARMS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8889"

  if [ "$1" = "debug" ]; then
  	shift
  fi
fi

"$JAVA_HOME"/bin/java -cp "$IE_CLASSPATH" -Xmx512m $DEBUG_PARMS com.elasticpath.importexport.client.Index $@
