#!/bin/bash

# The specific application to look for
APPLICATION=$1

# Thread dump interval
SLEEP_TIME=30

# Check if were given an application to look for and get the Tomcat PID
if [ ! -z "$APPLICATION" ]; then
    # Get the PID of the specific Tomcat application server
    JAVA_PID=`ps -ef|grep java|grep $APPLICATION|grep -v grep|awk '{ print $2 }'`
    # Create a file in /tmp to watch, we stop the thread dumps if the file is deleted
    echo "$JAVA_PID" > /tmp/dumpThreads$APPLICATION
    # Create thread dumps until the file in /tmp is deleted
    while [ -e /tmp/dumpThreads$APPLICATION ]; do
        kill -3 $JAVA_PID
        sleep $SLEEP_TIME
    done
else
    # There's only one Tomcat server running so get that PID
    JAVA_PID=`ps -ef|grep org.apache.catalina|grep -v grep|awk '{ print $2 }'`
    echo "$JAVA_PID" > /tmp/dumpThreads
    # Create thread dumps until the file in /tmp is deleted
    while [ -e /tmp/dumpThreads ]; do
        kill -3 $JAVA_PID
        sleep $SLEEP_TIME
    done
fi
