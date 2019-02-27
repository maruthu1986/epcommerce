#!/bin/bash

CURRENT_DIRECTORY=`cd "$(dirname "${BASH_SOURCE[0]}")" && pwd`
chmod u+x "$CURRENT_DIRECTORY"/verifyjava.sh
. "$CURRENT_DIRECTORY"/verifyjava.sh
cd "$CURRENT_DIRECTORY"
WEBAPPS_DIRECTORY="$CURRENT_DIRECTORY"/webapps
TOMCAT_HOME="$WEBAPPS_DIRECTORY"/apache-tomcat
CATALINA_HOME="$TOMCAT_HOME"
unset JAVA_OPTS

chmod u+x "$CATALINA_HOME"/bin/*.sh

for webapp in ep-cortex ep-search ep-integration ep-cm activemq; do
	echo "Stopping $webapp..."
	WEBAPP_BASE="$WEBAPPS_DIRECTORY"/$webapp-webapp-base
	CATALINA_HOME="$CATALINA_HOME" CATALINA_BASE="$WEBAPP_BASE" CATALINA_PID="$WEBAPP_BASE"/${webapp}-pid.txt "$CATALINA_HOME"/bin/catalina.sh stop 5 -force
    sleep 2
done
