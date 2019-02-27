export CATALINA_HOME="$HOME/ep/tomcat"

export CATALINA_PID="${CATALINA_HOME}/bin/catalina.pid"

export CATALINA_OPTS="${CATALINA_OPTS} -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.util.logging.config.file=${CATALINA_HOME}/conf/logging.properties -Dcatalina.home=${CATALINA_HOME} "
export CATALINA_OPTS="${CATALINA_OPTS} -Xms2048m -Xmx4096m -XX:ReservedCodeCacheSize=300m -Xdebug -Xrunjdwp:transport=dt_socket,address=1081,server=y,suspend=n "
export CATALINA_OPTS="${CATALINA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintTenuringDistribution -XX:+PrintHeapAtGC -Xloggc:${CATALINA_HOME}/logs/gc.log "
export CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8888 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=`hostname -i` "

