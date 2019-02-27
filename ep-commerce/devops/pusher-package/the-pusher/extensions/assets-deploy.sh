
deployAssets() {

    # Deploy the assets
    # Check for the number of old assets deployments and delete old ones if necessary
    if [[ `ssh $localUser@$server "ls $baseDirectory | grep assets- | wc -l"` -gt $maxOldAssetsDeployments ]]; then
        ssh $localUser@$server "cd $baseDirectory && ls -t | grep assets- | awk 'NR>max' max=$maxOldAssetsDeployments | xargs rm -rf"
    fi
    assetsDirectory="assets-`date +%D-%H%M%S|sed -e 's/\//-/g'`"
    log detail "Creating assets directory and deploying assets to $server..."
    # Delete the old symlink, create the new assets directory and copy / unzip / symlink
    ssh $localUser@$server "rm -f $baseDirectory/$assetsLocation && cd $baseDirectory && mkdir $assetsDirectory"
    scp -q $workspaceDirectory/webapps/*$currentContextPath*.zip $localUser@$server:$baseDirectory/$assetsDirectory
    ssh $localUser@$server "unzip -qo $baseDirectory/$assetsDirectory/*$currentContextPath*.zip -d $baseDirectory/$assetsDirectory && ln -s $baseDirectory/$assetsDirectory $baseDirectory/$assetsLocation"

}
