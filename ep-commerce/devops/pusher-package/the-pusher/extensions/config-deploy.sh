deployEpConfigAndProperties () {

    configPath=$baseDirectory

    # We could likely remove the existence check - we would just always deploy
    # And the dir might be a variable instead of hardcoded
    # Don't check for existence, just deploy them.
    log detail "Deploying EP properties and config files to $configPath ..."
    scp -qr $pusherConfigDirectory/files/* $localUser@$server:$configPath/

    filterConfigFiles $configPath

    return 0
}

filterConfigFiles () {

    configPath=$1
    log detail "Filtering config files for $appServer ..."
    return 0
}

