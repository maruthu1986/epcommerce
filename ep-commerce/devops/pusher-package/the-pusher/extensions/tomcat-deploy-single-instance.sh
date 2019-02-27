
deployTomcatApp() {

    if [[ ! ${uniqueTomcatServers[*]} =~ "$server" ]]; then
        # Copy the newest tomcat directory or scp -q a default package and unzip it
        if [ ! `ssh $localUser@$server "cd $baseDirectory && ls -t | grep $appServer | head -n 1"` ]; then
            log detail "No Tomcat directory found on $server... deploying default Tomcat package."
            scp -q $workspaceDirectory/webapps/$appServerPackage $localUser@$server:$baseDirectory
            ssh $localUser@$server "unzip -qo $baseDirectory/$appServerPackage -d $baseDirectory &&
                DATE=\$(date +%D-%H%M%S|sed -e 's/\//-/g') &&
                cp -R $baseDirectory/apache-$appServer-$appServerVersion $baseDirectory/$appServer-$appServerVersion-\$DATE &&
                ln -s $baseDirectory/$appServer-$appServerVersion-\$DATE $baseDirectory/$appServer &&
                chmod +x '$baseDirectory/$appServer/bin/'*.sh"

                # Set the default values for the Tomcat server.xml file
                shutdownPort=8005
                httpPort=8080
                httpsPort=8443
                ajpPort=8009
                ajpSecurePort=8010
                jvmRoute=$server

                # Configure SSL/HTTPS in server.xml
                log detail "Configuring server.xml on $server..."
                cat "$templatesDirectory/tomcat-$appServerVersion-server.xml" | env_filter | ssh $localUser@$server "cat > '$baseDirectory/tomcat/conf/server.xml'"

                # We need to change the default CATALINA_HOME variable in the setenv.sh script before we can start Tomcat
                ssh $localUser@$server "sed -i 's|"\$HOME\/ep\/tomcat"|"$baseDirectory\/$appServer"|' $baseDirectory/$appServer/bin/setenv.sh"

        else
            # Stop Tomcat if it's running
            log detail "Stopping Tomcat on $server..."
            ssh $localUser@$server "source $baseDirectory/$appServer/bin/setenv.sh && if test -e \"\$CATALINA_PID\" && ps -p \"\$(cat \"\$CATALINA_PID\")\" > /dev/null; then
                    $baseDirectory/$appServer/bin/shutdown.sh -force
                fi"

            # Delete the old Tomcat logs before we copy the deployment
            ssh $localUser@$server "rm -rf $baseDirectory/$appServer/logs/*"

            # Check the number of existing copies of the Tomcat directory and delete any over the max
            oldAppServerDeployments=`ssh $localUser@$server "ls $baseDirectory | grep $appServer | wc -l"`
            if [[ $oldAppServerDeployments -gt $maxOldAppServerDeployments ]]; then
				ssh $localUser@$server "ls -dt $baseDirectory/*/ | grep $appServer-$appServerVersion | awk 'NR>max' max=$maxOldAppServerDeployments | xargs rm -rf"
            fi

            # Check if we need to backup the indexes (if we are deploying search and solrHome is still inside the search app)
            if [[ ( "$updateDb" == 0 && "$deployDb" == 0 ) || ( "$updateDb" == 1 && "$deployDb" == 0 ) && ${allAppServers[$server]} =~ $searchContextPath && ! -z $searchServers ]]; then
                if [ ! `ssh $localUser@$server test -d $baseDirectory/$appServer/webapps/$searchContextPath/WEB-INF/solrHome && echo exists` ]; then
                    log detail "No indexes to backup..."
                else
                    log detail "Creating backup copy of the Solr indexes..."
                    ssh $localUser@$server "cp -R $baseDirectory/$appServer/webapps/$searchContextPath/WEB-INF/solrHome /tmp"
                fi
            fi

	        log detail "Making a copy of the Tomcat directory and switching symlink on $server..."
            ssh $localUser@$server "rm -f $baseDirectory/$appServer &&
                DATE=\$(date +%D-%H%M%S|sed -e 's/\//-/g') &&
                PREVIOUSTOMCAT=\$(ls -t $baseDirectory | grep '$appServer-$appServerVersion-[0-9]\+-' | head -n 1 ) && 
                cp -R $baseDirectory/\$PREVIOUSTOMCAT $baseDirectory/$appServer-$appServerVersion-\$DATE &&
                ln -s $baseDirectory/$appServer-$appServerVersion-\$DATE $baseDirectory/$appServer &&
                rm -rf $baseDirectory/$appServer/webapps/* &&
                rm -f $baseDirectory/$appServer/conf/Catalina/localhost/*"

        fi
    fi

    # Create a new context file with the DB connection info from the conf file
    log detail "Creating a new database configuration file on $server..."
    cat "$templatesDirectory/$appServer-$dbVendor-context.xml" | env_filter | ssh $localUser@$server "mkdir -p '$baseDirectory/$appServer/conf/Catalina/localhost' &&
        cat > '$baseDirectory/$appServer/conf/Catalina/localhost/$currentContextPath.xml'"

    # Delete the existing tomcat/lib jars and copy them over again to make sure we have the latest
    log detail "Refreshing jars in Tomcat lib directory..."
    ssh $localUser@$server "rm -f $baseDirectory/$appServer/lib/* && unzip -qo $baseDirectory/$appServerPackage -d $baseDirectory && cp $baseDirectory/apache-$appServer-$appServerVersion/lib/* $baseDirectory/$appServer/lib && rm -rf $baseDirectory/apache-$appServer-$appServerVersion"

    # Copy dependencies needed to get ActiveMQ working into Tomcat lib/
    log detail "Adding ActiveMQ dependencies on $server..."
    scp -q $workspaceDirectory/tools/activemq/lib/*.jar $localUser@$server:$baseDirectory/$appServer/lib/

    if [ ! `ssh $localUser@$server test -d $baseDirectory/$appServer/webapps/$currentContextPath  && echo exists` ]; then
        log detail "Creating directory for $currentContextPath on $server..."
        ssh $localUser@$server "mkdir -p $baseDirectory/$appServer/webapps/$currentContextPath"
    fi

    log detail "Copying $currentContextPath war file to $server..."
    # The '-' after currentContextPath during scp is needed so contexts that are substrings of another context won't get incorrectly copied
    scp -q $workspaceDirectory/webapps/*$currentContextPath-*.war $localUser@$server:$baseDirectory/$appServer/webapps/$currentContextPath/
    log detail "Extracting $currentContextPath war file on $server..."
    ssh $localUser@$server "unzip -qo $baseDirectory/$appServer/webapps/$currentContextPath/*.war -d $baseDirectory/$appServer/webapps/$currentContextPath/"

    # Check if we need to restore the search indexes
    if [[ ( $updateDb == 0 && $deployDb == 0 && $currentContextPath == $searchContextPath && ! -z $searchServers ) || ( "$updateDb" == 1 && "$deployDb" == 0 && $currentContextPath == $searchContextPath && ! -z $searchServers ) ]]; then
        if [ ! `ssh $localUser@$server test -d /tmp/solrHome && echo exists` ]; then
            log detail "No indexes to restore..."
        else
            log detail "Restoring the Solr indexes..."
            ssh $localUser@$server "cp -R /tmp/solrHome $baseDirectory/$appServer/webapps/$searchContextPath/WEB-INF/"
        fi
    fi

    if [[ ( $externalSolr == "true" ) && ( $currentContextPath == $searchContextPath && ! -z $searchServers) ]]; then
        # Check that the solrHome directory exists and create it if not
        log detail "Check for external solr home at $solrHome ..."
        if [ -n $solrHome ]; then
            if [ ! -d $solrHome ]; then
                log detail "Solr home directory does not exist... Creating solrHome $solrHome"
                ssh $localUser@$server "mkdir -p $solrHome"
            fi
				
            if [[( $copySolrConfig == "true" ) || (! -d $solrHome/conf)]]; then
            # Copy the default Solr config files to the new solrHome directory
                log detail "Copy the default Solr config files to the external solrHome directory $solrHome ..."
                ssh $localUser@$server "cp -R $baseDirectory/$appServer/webapps/$searchContextPath/WEB-INF/solrHome/* $solrHome"
            fi
        fi

        # Add the solrHome location property to the setenv.sh script if it's not there already
        if [ ! `ssh $localUser@$server "grep -r solr.solr.home=$solrHome $baseDirectory/$appServer/bin/setenv.sh &> /dev/null && echo true"` ]; then
            log detail "Adding solrHome property to JAVA_OPTS in $baseDirectory/$appServer/bin/setenv.sh..."
            ssh $localUser@$server "printf 'export JAVA_OPTS=\" \$JAVA_OPTS -Dsolr.solr.home=$solrHome \"' >> $baseDirectory/$appServer/bin/setenv.sh"
        fi
    fi
}
