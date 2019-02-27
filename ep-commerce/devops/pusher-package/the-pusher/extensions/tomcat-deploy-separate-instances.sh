deployTomcatAppSeparateInstance() {

    # Map the application ports to the generic port names in the server.xml template & create the variables for the filter
    portVariables=( $portVariableNames )
    for (( i=0; i<${#portVariables[@]}; i++ )); do
        printf -v ${portVariables[$i]} "${currentPorts[$i]}"
    done

    # Create a new instance of Tomcat if none exists (one Tomcat instance per app)
    if [ ! `ssh $localUser@$server "cd $baseDirectory && ls -t | grep $appServer-$currentContextPath | head -n 1"` ]; then
        log detail "No Tomcat directory for $currentContextPath found on $server..."
        if [ ! `ssh $localUser@$server "test -e $baseDirectory/$appServerPackage && echo exists"` ]; then
            log detail "Copying $appServerPackage to $server..."
            scp -q $workspaceDirectory/webapps/$appServerPackage $localUser@$server:$baseDirectory
        fi

        log detail "Creating new $appServer instance for $currentContextPath..."
        ssh $localUser@$server "unzip -qo $baseDirectory/$appServerPackage -d $baseDirectory &&
            DATE=\$(date +%D-%H%M%S|sed -e 's/\//-/g') &&
            cp -R $baseDirectory/apache-$appServer-$appServerVersion $baseDirectory/$appServer-$currentContextPath-$appServerVersion-\$DATE &&
            ln -s $baseDirectory/$appServer-$currentContextPath-$appServerVersion-\$DATE $baseDirectory/$currentContextPath &&
            chmod +x '$baseDirectory/$currentContextPath/bin/'*.sh"

        # setup the variables that need to be changed in the setenv.sh script
        # This needs to be changed if you change the default port variable names
        jmxPort=$(($httpPort+10))
        debugPort=$(($httpPort+20))
        shutdownPort=$(($httpPort-10))

        # Set the jvmRoute variable to the IP address of this server
        # This will get used in the filter to set the correct jvmRoute
        jvmRoute=$server

        log detail "Configuring server.xml for $currentContextPath on $server..."
        cat "$templatesDirectory/tomcat-$appServerVersion-server.xml" | env_filter | ssh $localUser@$server "cat > '$baseDirectory/$currentContextPath/conf/server.xml'"

        # We need to change the default CATALINA_HOME variable in the setenv.sh script before we can start Tomcat
        ssh $localUser@$server "sed -i 's|"\$HOME\/ep\/tomcat"|"$baseDirectory\/$currentContextPath"|' $baseDirectory/$currentContextPath/bin/setenv.sh"

        # Set the JMX port in the setenv.sh script
        ssh $localUser@$server "sed -i -r 's|(.*)(jmxremote.port=)([0-9]+)(.*)|\1\2$jmxPort\4|' $baseDirectory/$currentContextPath/bin/setenv.sh"

        # Set the RMI remote port in the setenv.sh script (for JMX through firewalls)
        ssh $localUser@$server "sed -i -r 's|(.*)(rmi.port=)([0-9]+)(.*)|\1\2$jmxPort\4|' $baseDirectory/$currentContextPath/bin/setenv.sh"

        # Set the debug port in the setenv.sh script
        ssh $localUser@$server "sed -i -r 's|(.*)(,address=)([0-9]+)(.*)|\1\2$debugPort\4|' $baseDirectory/$currentContextPath/bin/setenv.sh"

    else
        # Stop Tomcat if it's running
        log detail "Stopping $currentContextPath on $server..."
        ssh $localUser@$server "source $baseDirectory/$currentContextPath/bin/setenv.sh && if test -e \"\$CATALINA_PID\" && ps -p \"\$(cat \"\$CATALINA_PID\")\" > /dev/null; then
                $baseDirectory/$currentContextPath/bin/shutdown.sh -force
            fi"

        # Delete the old Tomcat logs before we copy the deployment
        # You might want to comment this out for production deployments
        ssh $localUser@$server "rm -rf $baseDirectory/$currentContextPath/logs/*"

        # Check the number of existing copies of the Tomcat directory and delete any over the max
        oldAppServerDeployments=`ssh $localUser@$server "ls $baseDirectory | grep $appServer-$currentContextPath | wc -l"`
        if [[ $oldAppServerDeployments -gt $maxOldAppServerDeployments ]]; then
            ssh $localUser@$server "ls -dt $baseDirectory/*/ | grep $appServer-$currentContextPath-$appServerVersion | awk 'NR>max' max=$maxOldAppServerDeployments | xargs rm -rf"
        fi

        # Check if we need to backup the indexes (if we are deploying search and solrHome is still inside the search app)
        if [[ ( -z $solrHome && $updateDb == 0 && $deployDb == 0 && ${allAppServers[$server]} =~ $searchContextPath && ! -z $searchServers ) ]]; then
            if [ ! `ssh $localUser@$server test -d $baseDirectory/$currentContextPath/webapps/$searchContextPath/WEB-INF/solrHome && echo exists` ]; then
                log detail "No indexes to backup..."
            else
                log detail "Creating backup copy of the Solr indexes..."
                ssh $localUser@$server "cp -R $baseDirectory/$currentContextPath/webapps/$searchContextPath/WEB-INF/solrHome /tmp"
            fi
        fi

        log detail "Making a copy of the Tomcat directory and switching symlink on $server..."
        ssh $localUser@$server "rm -f $baseDirectory/$currentContextPath &&
            DATE=\$(date +%D-%H%M%S|sed -e 's/\//-/g') &&
            PREVIOUSTOMCAT=\$(ls -t $baseDirectory | grep '$appServer-$currentContextPath-$appServerVersion-[0-9]\+-' | head -n 1 ) && 
            cp -R $baseDirectory/\$PREVIOUSTOMCAT $baseDirectory/$appServer-$currentContextPath-$appServerVersion-\$DATE &&
            ln -s $baseDirectory/$appServer-$currentContextPath-$appServerVersion-\$DATE $baseDirectory/$currentContextPath &&
            rm -rf $baseDirectory/$currentContextPath/webapps/* &&
            rm -f $baseDirectory/$currentContextPath/conf/Catalina/localhost/*"
    fi

    # Create a new context file with the DB connection info from the conf file
    log detail "Creating a new database configuration file on $server..."
    cat "$templatesDirectory/$appServer-$dbVendor-context.xml" | env_filter | ssh $localUser@$server "mkdir -p '$baseDirectory/$currentContextPath/conf/Catalina/localhost' && cat > '$baseDirectory/$currentContextPath/conf/Catalina/localhost/$currentContextPath.xml'"

    # Delete the existing tomcat/lib jars and copy them over again to make sure we have the latest
    log detail "Refreshing jars in Tomcat lib directory..."
    ssh $localUser@$server "rm -f $baseDirectory/$currentContextPath/lib/* && cp $baseDirectory/apache-$appServer-$appServerVersion/lib/* $baseDirectory/$currentContextPath/lib"

    # Copy dependencies needed to get ActiveMQ working into Tomcat lib/
    log detail "Adding ActiveMQ dependencies on $server..."
    scp -q $workspaceDirectory/tools/activemq/lib/*.jar $localUser@$server:$baseDirectory/$currentContextPath/lib/

    # Create the directory for the webapp
    if [ ! `ssh $localUser@$server test -d $baseDirectory/$currentContextPath/webapps/$currentContextPath  && echo exists` ]; then
        log detail "Creating directory for $currentContextPath on $server..."
        ssh $localUser@$server "mkdir -p $baseDirectory/$currentContextPath/webapps/$currentContextPath"
    fi

    # Deploy the WAR file
    log detail "Copying $currentContextPath war file to $server..."
    # The '-' after currentContextPath during scp is needed so contexts that are substrings of another context won't get incorrectly copied
    scp -q $workspaceDirectory/webapps/*$currentContextPath-*.war $localUser@$server:$baseDirectory/$currentContextPath/webapps/$currentContextPath/
    log detail "Extracting $currentContextPath war file on $server..."
    ssh $localUser@$server "unzip -qo $baseDirectory/$currentContextPath/webapps/$currentContextPath/*.war -d $baseDirectory/$currentContextPath/webapps/$currentContextPath/"

    # Check if we are deploying Cortex and deploy Studio to the same Tomcat instance
    if [[ ( ! -z "$studioServers" && $currentContextPath == $cortexContextPath ) ]]; then
        log detail "Copying $studioContextPath war file to $server..."
        ssh $localUser@$server "mkdir -p $baseDirectory/$currentContextPath/webapps/$studioContextPath"
        scp -q $workspaceDirectory/webapps/*$studioContextPath*.war $localUser@$server:$baseDirectory/$currentContextPath/webapps/$studioContextPath/
        log detail "Extracting $studioContextPath war file on $server..."
        ssh $localUser@$server "unzip -qo $baseDirectory/$currentContextPath/webapps/$studioContextPath/*.war -d $baseDirectory/$currentContextPath/webapps/$studioContextPath/"
    fi

    # Check if we are deploying the Search indexes outside of the Search webapp
    # We assume Master/Slave Search deployments are done this way
    if [[ ( ! -z $solrHome && $currentContextPath == $searchContextPath ) ]]; then

         # Check that the solrHome directory exists and create it if not
        if [ -n $solrHome ]; then
            if [ ! `ssh $localUser@$server "test -d $solrHome"` ]; then
                log detail "Solr home directory does not exist...Creating solrHome"
                ssh $localUser@$server "mkdir -p $solrHome"
                # Copy the default Solr config files to the new solrHome directory
                ssh $localUser@$server "cp -R $baseDirectory/$currentContextPath/webapps/$currentContextPath/WEB-INF/solrHome/* $solrHome"
            fi
        fi

        # Add the solrHome location property to the setenv.sh script if it's not there already
        if [ ! `ssh $localUser@$server "grep -r solr.solr.home=$solrHome $baseDirectory/$currentContextPath/bin/setenv.sh &> /dev/null && echo true"` ]; then
            log detail "Adding solrHome property to JAVA_OPTS in $baseDirectory/$currentContextPath/bin/setenv.sh..."
            ssh $localUser@$server "printf 'export JAVA_OPTS=\" \$JAVA_OPTS -Dsolr.solr.home=$solrHome \"' >> $baseDirectory/$currentContextPath/bin/setenv.sh"
        fi

        # For each Solr index update the path to the include file to whatever we have set for $solrHome
        for solrCore in $solrCores; do
            log detail "Updating path to Solr replication config file for $solrCore on $server..."
            ssh $localUser@$server "sed -i 's;file:\/etc\/ep\/$solrCore.solr.replication.config.xml;file:$solrHome\/conf\/$solrCore.solr.replication.config.xml;' $solrHome/conf/$solrCore.config.xml"
        done

        # Check if we're deploying a master/slave setup for the search servers and deploy the correct wars
        if [[ ( -n $masterSearchServer && $masterSearchServer == $server && $currentContextPath == $searchContextPath ) ]]; then
            log detail "Master Search Match Found!"
            log detail "Creating $currentContextPath master configuration on $server..."

            # Check if we've already updated ep.properties with the master configuration and add it if not
            if [ ! `ssh $localUser@$server "grep -r ep.search.requires.master=true $epPropertiesFile &> /dev/null && echo true"` ]; then
                log detail "configuring $epPropertiesFile on master search server..."
                ssh $localUser@$server "printf '# Set as search master\nep.search.requires.master=true' >> $epPropertiesFile"
            fi

            # Create the master Solr configuration file
            for solrCore in $solrCores; do
                log detail "Creating Solr configuration file for $solrCore index on master server $server..."
                ssh $localUser@$server "printf '<requestHandler name=\"/replication\" class=\"solr.ReplicationHandler\">\n<lst name=\"master\">\n<str name=\"replicateAfter\">startup</str>\n<str name=\"replicateAfter\">commit</str>\n<str name=\"confFiles\">schema.xml,stopwords.txt,elevate.xml</str>\n<str name=\"commitReserveDuration\">00:00:10</str>\n</lst>\n</requestHandler>' > $solrHome/conf/$solrCore.solr.replication.config.xml"
            done

        # Check if we are deploying a search slave and do it
        elif [[ ( -n "${slaveSearchServers[0]}" && $currentContextPath == $searchContextPath ) ]]; then
            if [[ ${slaveSearchServers[*]} =~ "$server" ]]; then
                log detail "Found a matching search slave!"
                log detail "Creating $currentContextPath slave configuration file on $server..."

                # Check if the Quartz jobs are already disabled in ep.properties, disable them if they aren't
                if [ ! `ssh $localUser@$server "grep -r ep.search.triggers=disabled $epPropertiesFile &> /dev/null && echo true"` ]; then
                    log detail "Disabling quartz jobs on slave search server..."
                    ssh $localUser@$server "printf '# Disable quartz jobs on search slave\nep.search.triggers=disabled' >> $epPropertiesFile"
                fi

                for solrCore in $solrCores; do
                    # Write out the slave search server replication configuration files
                    log detail "Creating Solr configuration file for $solrCore index on slave server $server..."
                    ssh $localUser@$server "printf '<requestHandler name=\"/replication\" class=\"solr.ReplicationHandler\">\n<lst name=\"slave\">\n<str name=\"enable\">true</str><str name=\"masterUrl\">http://$masterSearchServer:$httpPort/search/$solrCore</str>\n<str name=\"pollInterval\">00:01:00</str>\n<str name=\"compression\">internal</str>\n<str name=\"httpConnTimeout\">5000</str>\n<str name=\"httpReadTimeout\">10000</str>\n</lst>\n</requestHandler>' > $solrHome/conf/$solrCore.solr.replication.config.xml"
                done
            fi
        fi
    fi

    # Check if we need to restore the search indexes
    if [[ ( -z $solrHome && $updateDb == 0 && $deployDb == 0 && $currentContextPath == $searchContextPath && ! -z $searchServers ) ]]; then
        if [ ! `ssh $localUser@$server test -d /tmp/solrHome && echo exists` ]; then
            log detail "No indexes to restore..."
        else
            log detail "Restoring the Solr indexes..."
            ssh $localUser@$server "cp -R /tmp/solrHome $baseDirectory/$currentContextPath/webapps/$searchContextPath/WEB-INF/"
        fi
    fi

    # Since each application is in it's own Tomcat instance we can start it now since we know we're done at this point
    ssh $localUser@$server "$baseDirectory/$currentContextPath/bin/startup.sh"

}
