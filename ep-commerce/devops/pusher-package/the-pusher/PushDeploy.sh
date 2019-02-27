#!/bin/bash -e

# Log levels (must be integers)
declare -A logLevels
logLevels['none']=0
logLevels['error']=5
logLevels['detail']=10
logLevels['debug']=25

# Default log level
logLevel=detail

# Path to the extensions and templates
currentWorkingDirectory=$(dirname "$0")
libDirectory="$currentWorkingDirectory/lib"
extensionsDirectory="$currentWorkingDirectory/extensions"
templatesDirectory="$currentWorkingDirectory/templates"

# Source any helper libraries
source $libDirectory/*.sh

# Read in extension points/scripts
log detail "Reading extensions from $extensionsDirectory"
for extension in $extensionsDirectory/*.sh; do
    [ -e "$extension" ] || continue # in case there are none
    log debug " > Loading $extension"
    source "$extension"
done

# Parse the command line arguments
declare -a confFiles
while test "$#" -gt 0; do
    case "$1" in
    -h|--help)
        usage; exit 0 ;;
    -f)
        shift; confFiles+=("$1") ;;
    -p)
        shift; deploymentPackage="$1" ;;
    -d)
        shift; dataPopulationCommand="$1" ;;
    -v|--verbosity)
        shift

        # test if valid log level
        log "$1" >/dev/null || {
            usage
            exit 1
        }
        logLevel="$1"
        log detail "Setting log level to $logLevel"
        ;;
    --)
        shift;  break ;;
    *)
        echo "Invalid option $1" >&2
        usage
        exit 1
        ;;
    esac
    shift
done

if test -z "$deploymentPackage"; then
    echo "Missing required argument -p" >&2
    usage
    exit 1
fi

if test "${#confFiles[@]}" -eq 0; then
    echo "Missing required argument -f" >&2
    usage
    exit 1
fi

if test -z "$dataPopulationCommand"; then
    echo "Missing required argument -d" >&2
    usage
    exit 1
fi

dataPopulationCommandsArray=(none update-db reset-db)
if [[ ! ${dataPopulationCommandsArray[*]} =~ "$dataPopulationCommand" ]]; then
    echo "Invalid data population command $dataPopulationCommand" >&2
    echo "Valid commmands are: ${dataPopulationCommandsArray[*]}"
    usage
    exit 1
fi

# Read in the conf files, skip lines that start with # or *
executionEnvironment=$(java -cp "$currentWorkingDirectory/lib/pusher-support-1.0-SNAPSHOT.jar" com.elasticpath.releng.properties.PropertyResolver --mode sh "${confFiles[@]}")
eval "$executionEnvironment"
echo "$executionEnvironment" | sort

# Get the local deployer environment figured out first
# Check if the baseDirectory exists on the local deployer box and create it if needed
# Create the workspace directory inside the base directory on the deployer
workspaceDirectory="$baseDirectory/workspace"
# Create the log directory inside the base directory
logDirectory="$baseDirectory/logs"
if [ ! -d $baseDirectory ]; then
    log detail "Base directory does not exist. Creating the base directory..."
    mkdir -p $baseDirectory $logDirectory $workspaceDirectory
else
    # Delete the old workspace directory if it still exists
    if [ -d $workspaceDirectory ]; then
        log detail "Old workspace directory found. Removing the old workspace directory..."
        rm -rf $workspaceDirectory
    fi

    log detail "No workspace directory found. Creating new workspace directory..."
    mkdir $workspaceDirectory

    # Check if the log directory exists and create it if needed
    if [ ! -d $logDirectory ]; then
        log detail "No log directory found. Creating the log directory..."
        mkdir $logDirectory
    fi
fi

# Check if the deployment package is where it should be
if [ ! -e $deploymentPackage ]; then
    # Log error and exit if we can't find the deployment package
    log error "Can't find the deployment package...looks like we're done here."
    exit 1
else
    # Copy the deployment package to the workspace and unzip it
    log detail "Copying the deployment package to the workspace directory..."
    cp $deploymentPackage $workspaceDirectory/ && unzip -qo $workspaceDirectory/*.zip -d $workspaceDirectory
fi

# Array of the groups of servers (ie any *Servers variables defined in the conf file)
serverGroups=(`compgen -A variable | grep Servers`)

# hash of IPs to space separated list of server group prefix
# Check for a master/slave search deployment and create the necessary variables
declare -A allAppServers
for serverGroup in "${serverGroups[@]}"; do
    if [ "$serverGroup" == "searchServers" ]; then
        searchServerArray=(${!serverGroup})
        if [[ ${#searchServerArray[@]} -gt 1 ]]; then
            masterSearchServer=${searchServerArray[0]}
            log detail "Master Search Server: $masterSearchServer"
            slaveSearchServers=(${searchServerArray[@]:1})
            log detail "Search Slaves: ${slaveSearchServers[@]}"
        fi
    fi
    for server in ${!serverGroup}; do
        allAppServers[$server]="${allAppServers[$server]} ${serverGroup%Servers}"
    done
done

log detail "Ready to deploy to ${!allAppServers[@]}"

# By now we should be ready to start the deployment
log detail "Deployment starting... "

# Check if we are deploying or updating the database and do the appropriate DB deploy
if [ "none" != "$dataPopulationCommand" ]; then
    ( deployDatabase )
fi

# The good stuff! This is where we actually start to do some work
# The actual work is done in the scripts in the extensions directory
for server in "${!allAppServers[@]}"; do
    # Check for the baseDirectory on each server
    log detail "Checking for base directory $baseDirectory on $server..."
    # Create the base directory if it doesn't exist
    if [ ! `ssh $localUser@$server test -d $baseDirectory && echo exists` ]; then
        log detail "$baseDirectory does not exist on $server..."
        ssh $localUser@$server "mkdir -p $baseDirectory"
    fi

    # Go through the list of applications for this server and do the appropriate deployment
    for application in ${allAppServers[$server]}; do

        # Set the correct context path for this application
        currentContextPath="${application}ContextPath"
        currentContextPath="${!currentContextPath}"

        # set the correct port list for this application
        currentPorts="${application}Ports"
        currentPorts=(${!currentPorts})
        log detail "Processing server $application on $server..."

        if [ ! -z "$server" ]; then
            if [ "$activemqContextPath" == "$currentContextPath" ]; then
                log detail "Deploying ActiveMQ server..."
                ( deployActiveMQ )
            elif [ "$assetsContextPath" == "$currentContextPath" ]; then
                log detail "Deploying $currentContextPath..."
                ( deployAssets )
            elif [ "cmclient" == "$currentContextPath" ]; then
                log detail "Deploying $currentContextPath..."
                ( deployCMC )
            elif [ "$configurationContextPath" == "$currentContextPath" ]; then
                log detail "Deploying $currentContextPath files..."
                ( deployEpConfigAndProperties )
            elif [ "$appServer" == "tomcat" ]; then
                log detail "Deploying $currentContextPath to $appServer on $server..."
                # Check if we are deploying to separate Tomcat instances
                if [[ "$separateAppServerInstances" == "true" && "$currentContextPath" != $studioContextPath ]]; then
                    ( deployTomcatAppSeparateInstance )
                elif [ "$separateAppServerInstances" == "false" ]; then
                    ( deployTomcatApp )
                    if [[ ! ${uniqueTomcatServers[*]} =~ "$server" ]]; then
                        uniqueTomcatServers+=($server)
                    fi
                fi
            fi
        fi
    done

    # Check if we're doing a production deployment
    if [ "$productionDeployment" == "true" ]; then
        # Pause between servers for the specified time
        log detail "Production deployment detected. Pausing for $productionDeploymentPause seconds..."
        sleep $productionDeploymentPause
    fi

done

# Start the app servers
# This only runs if we deployed to a single Tomcat instance on each server
if [ "$appServer" == "tomcat" ]; then
    for uniqueServer in ${uniqueTomcatServers[@]}; do
        log detail "Starting Tomcat on: $uniqueServer"
        ssh $localUser@$uniqueServer "$baseDirectory/$appServer/bin/startup.sh"
    done
fi

cleanup

log detail "Deployment complete. Goodbye."

exit 0
