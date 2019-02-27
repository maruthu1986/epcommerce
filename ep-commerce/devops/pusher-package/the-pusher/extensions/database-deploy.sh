deployDatabase() {

    # If we are deploying a new database we might as well stop the app servers now
    if [ "$deployDB" == "true" ]; then
        for server in "${!allAppServers[@]}"; do
            if [[ "$separateAppServerInstances" == "true" ]]; then
                for application in ${allAppServers[$server]}; do
                    if [  `ssh $localUser@$server "test -e $baseDirectory/$application/bin/shutdown.sh && echo exists"` ]; then
                        log detail "Stopping $application on $server"
                        ssh $localUser@$server "$baseDirectory/$application/bin/shutdown.sh -force"
                    fi
                done
            else
                if [ "$appServer" == "tomcat" ]; then
                    ssh $localUser@$server "if test -e \"\$CATALINA_PID\" && ps -p \"\$(cat \"\$CATALINA_PID\")\" > /dev/null; then
                        $baseDirectory/$appServer/bin/shutdown.sh -force
                    fi"
                fi
            fi
        done

        # Wait for the servers to shut down
        sleep 30
    fi

    # Name of the data import directory
    importDirectory="data-import-directory"
    # Create the import directory
    mkdir $workspaceDirectory/database/$importDirectory
    # Open the jar into the correct directory name
    unzip -qo $workspaceDirectory/database/*data*.jar -d $workspaceDirectory/database/$importDirectory
    unzip -qo $workspaceDirectory/tools/*data-population*.zip -d $workspaceDirectory/tools

    cp $workspaceDirectory/database/jdbc/*.jar $workspaceDirectory/tools/*data-population-cli*/lib

    $workspaceDirectory/tools/*data-population-cli*/data-population.sh \
        --dataDirectory $workspaceDirectory/database/$importDirectory \
        --configDirectory $pusherConfigDirectory/ \
        $dataPopulationCommand

    log detail "Data population operation complete."
}

