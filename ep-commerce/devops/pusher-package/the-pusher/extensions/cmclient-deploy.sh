deployCMC() {

    ssh $localUser@$server "mkdir -p $cmcHttpdDocumentDir"
    # Deploy the Commerce Manager Client packages
    # Check for the number of old cmclient deployments and delete old ones if necessary
    if [[ `ssh $localUser@$server "ls $cmcHttpdDocumentDir | grep cmclient- | wc -l"` -gt $maxOldCmcDeployments ]]; then
        ssh $localUser@$server "cd $cmcHttpdDocumentDir && ls -t | grep cmclient- | awk 'NR>max' max=$maxOldCmcDeployments | xargs rm -rf"
    fi
    cmcDirectory="cmclient-`date +%D-%H%M%S|sed -e 's/\//-/g'`"
    log detail "Creating cmclient directory and deploying cmclient packages to $server..."
    # Delete the old symlink, create the new cmclient directory and copy / unzip / symlink
    ssh $localUser@$server "rm -f $cmcHttpdDocumentDir/$cmClientContextPath && cd $cmcHttpdDocumentDir && mkdir $cmcDirectory"
    scp $workspaceDirectory/webapps/*commercemanager*.zip $localUser@$server:$cmcHttpdDocumentDir/$cmcDirectory
    ssh $localUser@$server "ln -s $cmcHttpdDocumentDir/$cmcDirectory $cmcHttpdDocumentDir/$cmClientContextPath"

}
