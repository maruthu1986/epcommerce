deployActiveMQ() {

    # We are going to kill the running instance of ActiveMQ and do a fresh deployment every time
    log detail "Stopping existing ActiveMQ instance..."
    if [[ $(ssh $localUser@$server "ps -ef | grep activemq | grep -v grep && echo true") ]]; then
        ssh $localUser@$server "ps -ef | grep activemq | grep -v grep | awk '{ print \$2 }' | xargs kill -9 "
    fi

    # Check if ActiveMQ is already installed and running and install and start if not
    if [ $(ssh $localUser@$server "[ -d $baseDirectory/apache-activemq ] && echo true") ]; then
        log detail "Removing existing ActiveMQ instance..."
        ssh $localUser@$server "rm -rf $baseDirectory/apache-activemq*"
    fi
    
    log detail "Installing ActiveMQ..."
    activemqPackage=$(ls $workspaceDirectory/webapps/ | grep apache-activemq)
    activemqDirectory=$(echo $activemqPackage | sed 's/-bin.zip//g')
    ssh $localUser@$server "rm -rf $baseDirectory/apache-activemq*"
    scp -q $workspaceDirectory/webapps/$activemqPackage $localUser@$server:$baseDirectory
    ssh $localUser@$server "unzip -qo $baseDirectory/$activemqPackage -d $baseDirectory && ln -s $baseDirectory/$activemqDirectory $baseDirectory/apache-activemq && chmod +x $baseDirectory/$activemqDirectory/bin/activemq"

    log detail "Starting ActiveMQ..."
    ssh $localUser@$server "nohup $baseDirectory/apache-activemq/bin/activemq start > $baseDirectory/apache-activemq/data/nohup.out < /dev/null &"

}
