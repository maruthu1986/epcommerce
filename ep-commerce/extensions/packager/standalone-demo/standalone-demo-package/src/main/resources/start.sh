#!/bin/bash

CURRENT_DIRECTORY=`cd "$(dirname "${BASH_SOURCE[0]}")" && pwd`
chmod u+x "$CURRENT_DIRECTORY"/verifyjava.sh
. "$CURRENT_DIRECTORY"/verifyjava.sh
cd "$CURRENT_DIRECTORY"
WEBAPPS_DIRECTORY="$CURRENT_DIRECTORY"/webapps
TOMCAT_HOME="$WEBAPPS_DIRECTORY"/apache-tomcat
CATALINA_HOME="$TOMCAT_HOME"
CATALINA_OPTS="-Dh2.bindAddress=127.0.0.1 -Dactivemq.store.dir=\"$CURRENT_DIRECTORY\"/target/activemq-data -Dcom.sun.management.jmxremote=true -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true"
chmod u+x "$CATALINA_HOME"/bin/*.sh
unset JAVA_OPTS

readonly CORTEX_STUDIO=http://localhost:9080/studio
readonly SEARCH_SERVER="http://localhost:38082/searchserver/product/select?q=*:*&wt=json"
readonly CM=http://localhost:9081/admin
readonly ACTIVE_MQ=http://localhost:39081/jms

# set up apps to run with xmx and start times
# A bit wordy but don't want to use maps as requires bash v4+
webapps=(activemq ep-cortex ep-search ep-integration ep-cm)
webapps_xmx=(512 1024 1536 1280 1536) #mb
webapps_start_time=(10 45 10 10 25) #seconds

count=0
while [ "${webapps[count]}" != "" ];  do
	webapp=${webapps[count]}
	WEBAPP_BASE="$WEBAPPS_DIRECTORY"/${webapp}-webapp-base
	mkdir -p "$WEBAPP_BASE"/logs "$WEBAPP_BASE"/conf "$WEBAPP_BASE"/webapps
	rm -rf "$WEBAPP_BASE"/work
	echo "Starting $webapp..."
	CATALINA_HOME="$CATALINA_HOME" CATALINA_BASE="$WEBAPP_BASE" CATALINA_PID="$WEBAPP_BASE"/${webapp}-pid.txt CATALINA_OPTS="$CATALINA_OPTS -Xmx${webapps_xmx[count]}m" "$CATALINA_HOME"/bin/catalina.sh start
	echo "Waiting for $webapp to start..."
	sleep ${webapps_start_time[count]}
	((count++))
done

echo "Cortex Studio: $CORTEX_STUDIO"
echo "Search Server: $SEARCH_SERVER"
echo "CM: $CM"
echo "ActiveMQ: $ACTIVE_MQ"

if [ "$(uname)" == "Darwin" ]; then
  open $CORTEX_STUDIO
  open $CM
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
  xdg-open $CORTEX_STUDIO
  xdg-open $CM
fi
