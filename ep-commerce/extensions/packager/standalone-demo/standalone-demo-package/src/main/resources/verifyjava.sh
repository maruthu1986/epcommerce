#!/usr/bin/env bash
echo "Verifying Java is installed correctly..."
if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
  _java="$JAVA_HOME/bin/java"
elif type -p java; then
  _java=java
  if [ "$(uname)" == "Darwin" ]; then
    if [[ -x "/usr/libexec/java_home" ]]; then
      export JAVA_HOME=`/usr/libexec/java_home -v 1.8 -f`
      echo "Exporting JAVA_HOME as $JAVA_HOME"
    else
      echo "Could not set JAVA_HOME. Please ensure Java is installed and JAVA_HOME is set accordingly"
      exit 103
    fi
  elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))
  	echo "Exporting JAVA_HOME as $JAVA_HOME"
  fi
else
  echo "Java was not found. Please ensure java is installed and JAVA_HOME is set"
  exit 101
fi

exactversion=`expr "$($_java -version 2>&1)" : '.*java version "\(.*\)"'`
versionarray=(${exactversion//./ })
version=${versionarray[0]}${versionarray[1]}
if [[ "$version" != "18" ]]; then
  echo "Java 1.8 is required, but you have a different version ($exactversion). Please switch to 1.8"
  exit 102
fi