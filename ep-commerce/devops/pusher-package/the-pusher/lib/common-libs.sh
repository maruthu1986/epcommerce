#!/bin/bash -e

# Trap stop signals
trap stop SIGKILL SIGINT SIGTERM ERR

## ----------------------------------------------
## Functions

# Clean up the workspace directory when the script exits
cleanup() {
    log detail "Removing the workspace directory before exiting..."
    rm -rf $workspaceDirectory
}


# Cut the script off and kill all child processes if we detect a ctrl-c
stop() {
    log_err error "Caught a kill signal...stopping deployment script and all child processes."
    cleanup
    exit 1
}

## _log_levels
## ------
## Outputs all log levels in the format:
## number name
_log_levels() {
    for level in "${!logLevels[@]}"; do
        echo "${__logLevel[$level]} $level"
    done | sort -rn
}

## usage
## ------
## Outputs the usage of the program
usage() {
    echo "Usage: $0 [OPTIONS] -f <config> [-f <config> ...] -p <package> -d <data-population-command>"
    echo
    echo "Required arguments:"
    echo "  -f                 Deployment configuration file. All configuration files will be"
    echo "                        merged with the last property defined taking precedence."
    echo "  -p                 Deployment Package File (ie The name of the zip file containing the"
    echo "                        WARs, including path if necessary)."
    echo "  -d                 Data population command. Valid values are:"
    echo "                         none - do not run data population"
    echo "                         reset-db - drop, re-create and populate the database"
    echo "                         update-db - update the database"
    echo
    echo "Optional arguments:"
    echo "  -h, --help         Show this help menu"
    echo "  -v, --verbosity=[$(_log_levels | sort -rn | cut -d' ' -f2 | tr [:space:] '|' | sed -e 's/|$//')]"
    echo "                     Sets the verbosity level of the script. Default value: $logLevel"
    echo "  -q                 Alias for verbosity=$(_log_levels | sort -rn | head -n1 | cut -d' ' -f2)"
    echo
    echo "Example: $0 -f conf/NightlyEnvironmentTomcatOracleDeploy.conf -p DeploymentPackage-SNAPSHOT-1.0-Rev-123431.zip"
}

## log_err <level> <message> [<message> ...]
## ------
## A wrapper around log() which redirects output to stderr
log_err() {
    log "$@" >&2
}

## log <level> <message> [<message> ...]
## ------
## Logs a message to stdout if the current $logLevel of the message allows it
log() {
    level="$1"
    shift # get rid of level so we can pass message directly

    if test -z "${logLevels[$level]}"; then
        echo "Invalid log level: $level" >&2
        return 1
    fi

    if test "${logLevels[$logLevel]}" -ge "${logLevels[$level]}"; then
        echo " ] " "$@"
    fi
}

## env_filter
## ------
## Reads from stdin line-by-line and outputs the result of filtering any variables
## in the form ${variable} or $variable with the value which has been set in the
## environment.
## For variables with the form ${variable.something.something} the periods must be
## replaced with underscores for the variable placeholders to be translated properly
env_filter() {
    DONE=false
    until $DONE; do
        IFS='$' read -a array || DONE=true
        echo -n "${array[0]}"
        for ((i=1; i<${#array[@]}; ++i)); do
            if [ "${array[$i]:0:1}" == "{" ] && echo "${array[$i]}" | grep '^\{[A-Za-z0-9_]\+\}' >/dev/null; then
                # value has $ and looks like a {} variable
                __key="$(echo "${array[$i]}" | grep -o '^\{[A-Za-z0-9_]\+\}')"
                __key="${__key#?}"
                __key="${__key%?}"
                echo -n "${!__key}${array[$i]:(( ${#__key} + 2 ))}"
            elif echo "${array[$i]}" | grep '^[A-Za-z0-9_]\+' >/dev/null; then
                # value has $ and looks like a non-{} variable
                __key="$(echo "${array[$i]}" | grep -o '^[A-Za-z0-9_]\+' || true)"
                echo -n "${!__key}${array[$i]:${#__key}}"
            else
                # value has $, but isn't really a variable
                echo -n "\$${array[$i]}"
            fi
        done
        $DONE || echo # catch no eol at end of file
    done
}


####################################################
# GateKeeper V2 URL Validation
# If you call this method, you need to set up gk_checkN variables either locally
# or via the env pusher config.
# Variable syntax is:
# gk_check<num>=<url> <httpcode> <success_string>
# For example:
# gk_check1=http://10.10.2.171:8080/storefront/?storeCode=SNAPITUP 200 Binoculars
# gk_check2=http://10.10.2.171:8080/search/status/ 200 OK
# Notes:
# - Embed user:pass in URL if basic auth needed
# - success_string is case insensitive and optional
####################################################
gatekeeper () {

  if [ -z "${gk_tries}" ]; then
    gk_tries=30
  fi

  if [ -z "${gk_duration}" ]; then
    gk_duration=30
  fi

  i=1
  while true; do

    checkname="gk_check$i";
    eval check=\( \${${checkname}[@]} \)
    url=${check[0]}
    expected_code=${check[1]}
    expected_string=${check[2]}
    gk_try=1
    passed=0

    if [ ! -z ${url} ]; then

      while [  "${gk_try}" -le "${gk_tries}" ] && [ "${passed}" -eq "0" ];
      do
        echo -n "Checking URL ($url) for response code ($expected_code) and expected string value ($expected_string) ... "
        # set invalid back to zero
        invalid=0
        # HTTP response code check
        response_code=$(curl -s -o /dev/null -w "%{http_code}" "$url") ||
        if [ "$response_code" != "$expected_code" ]; then
          invalid=1
        fi

        # String parse check
        if ! curl -s "$url" | grep -i "$expected_string" > /dev/null; then
          invalid=2
        fi

        # Validate any issues and message out
        if [ $invalid -eq 0 ]; then
          echo "Validation passed."
          passed=1
        elif [ ${gk_try} -ne ${gk_tries} ]; then
          echo "INFO: Did not receive expected response code or string. Trying again in ${gk_duration} seconds (try ${gk_try} of ${gk_tries})."
          sleep ${gk_duration}
        else
          if [ $invalid -eq 1 ]; then
            echo && echo "ERROR: Validation failed on expected response code.  Expected $expected_code but got $response_code."
          elif [ $invalid -eq 2 ]; then
            echo && echo "ERROR: Expected string ($expected_string) not found."
          else
            echo && echo "ERROR: Validation failed for unknown reason."
          fi
          exit $invalid
        fi
        ((gk_try++))
      done
    else

      echo "No more validation checks."
      break

    fi

    ((i++))

    sleep 2

  done
}
