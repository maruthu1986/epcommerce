#!/usr/bin/env bash

set -e

function usage() {
  cat << EOF
Set the version for Elastic Path Commerce projects.

Usage: $(basename $0) [OPTIONS] PLATFORM_VERSION EXTENSION_VERSION

OPTIONS:
    -b, --build                  Build projects upon completing the versioning.
                                 The 'skipAllTests' property is set to speed up build.
    -s, --maven-settings FILE    Alternative path for Maven settings.xml to use.
    -h, --help                   Print this help.


EXAMPLES:
    $(basename $0) 700.0.0-SNAPSHOT 0.0.0-SNAPSHOT
        This will be the most common usage.

    $(basename $0) -s extensions/maven/settings.xml 700.0.0-SNAPSHOT 0.0.0-SNAPSHOT
        This approach lets you use a different Maven settings.xml than the default one picked up by Maven.

    $(basename $0) -s C:/Users/ep-user/code/extensions/maven/ep-settings.xml 700.0.0-SNAPSHOT 0.0.0-SNAPSHOT
        This is the syntax to use for a Windows user when using absolute paths.

    $(basename $0) -b 700.0.0-SNAPSHOT 0.0.0-SNAPSHOT
        Specify the -b option will build the projects after setting their versions to confirm the projects still builds.
EOF
}

# Log message to stderr as red text
function log_error() {
  local message="$@"

  local red_color="\033[01;31m"
  local end_color="\033[00m"

  >&2 echo -e "${red_color}${message}${end_color}"
}

# Wrap sed to work on Windows because "sed -i" destroys file permissions
function exec_sed() {
  local sed_args="$1"
  local file="$2"
  local tmp_file="${file}.bak"

  sed "${sed_args}" "${file}" > "${tmp_file}"
  mv -f "${tmp_file}" "${file}"
}

# Set a Maven POM property value
function set_property_version() {
  local property="$1"
  local value="$2"
  local file="$3"

  exec_sed "s|\(.*<${property}>\).*\(</${property}>.*\)|\1${value}\2|" "${file}"
}

# Set parent version number in Maven POM
function set_parent_version() {
  local version="$1"
  local pom_file="$2"

  exec_sed "\#<parent>#,\#</parent># s|<version>.*</version>|<version>${version}</version>|" "${pom_file}"
}

# Install bill-of-materials POM which Maven would otherwise complain about not existing in a clean build environment
function install_bom_pom() {
  local project_dir="$1"
  local maven_settings="$2"

  mvn ${maven_settings} clean install -N -f "${project_dir}/bill-of-materials/pom.xml"
}

function set_bom_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local platform_version="$3"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/bill-of-materials/pom.xml" \
    -DnewVersion="${platform_version}" \
     --non-recursive
}

function set_commerce_parent_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local platform_version="$3"

  # SET DOWNSTREAM REFERENCES

  # commerce-data
  set_parent_version "${platform_version}" "${project_dir}/commerce-data/pom.xml"

  # commerce-engine
  set_parent_version "${platform_version}" "${project_dir}/commerce-engine/pom.xml"

  # commerce-manager
  set_parent_version "${platform_version}" "${project_dir}/commerce-manager/pom.xml"
  set_parent_version "${platform_version}" "${project_dir}/commerce-manager/cm-libs/pom.xml"

  # cortex-resources
  set_parent_version "${platform_version}" "${project_dir}/cortex-resources/pom.xml"

  # extensions
  set_parent_version "${platform_version}" "${project_dir}/extensions/pom.xml"

  # devops
  set_parent_version "${platform_version}" "${project_dir}/devops/pom.xml"

  # health-monitoring
  set_parent_version "${platform_version}" "${project_dir}/health-monitoring/pom.xml"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/pom.xml" \
    -DnewVersion="${platform_version}" \
     --non-recursive
}

function set_commerce_engine_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local platform_version="$3"

  # SET DOWNSTREAM REFERENCES

  # extensions
  set_property_version "dce.version" "${platform_version}" "${project_dir}/extensions/pom.xml"

  # SET BOM REFERENCES

  set_property_version "dce.version" "${platform_version}" "${project_dir}/bill-of-materials/pom.xml"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/commerce-engine/pom.xml" \
    -DnewVersion="${platform_version}"
}

function set_commerce_manager_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local platform_version="$3"

  # SET DOWNSTREAM REFERENCES

  # extensions
  set_property_version "cmclient.platform.feature.version" "${platform_version}" "${project_dir}/extensions/pom.xml"

  # SET REFERENCES IN BOM AND PARENT

  set_property_version "cmclient.version"                  "${platform_version}" "${project_dir}/pom.xml"
  set_property_version "cmclient.version"                  "${platform_version}" "${project_dir}/bill-of-materials/pom.xml"
  set_property_version "cmclient.platform.feature.version" "${platform_version}" "${project_dir}/bill-of-materials/pom.xml"

  # SET PROJECT VERSION

  # Update cm-libs POM version
  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/commerce-manager/cm-libs/pom.xml" \
    -DnewVersion="${platform_version}"

  # Update com.elasticpath.cmclient.docs POM version
  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/commerce-manager/com.elasticpath.cmclient.docs/pom.xml" \
    -DnewVersion="${platform_version}"

  # Update top-level POM version
  mvn ${maven_settings} org.eclipse.tycho:tycho-versions-plugin:0.17.0:set-version \
    -f "${project_dir}/commerce-manager/pom.xml" \
    -Dtycho.mode=maven \
    -DnewVersion="${platform_version}"

  # Update non-Tycho dependencies version
  mvn ${maven_settings} org.eclipse.tycho:ep-tycho-versions-plugin:set-version \
    -f "${project_dir}/commerce-manager/cm-modules/pom.xml" \
    -Dtycho.mode=maven \
    -DnewVersion="${platform_version}" \
    -Dartifacts=com.elasticpath.cmclient:com.elasticpath.cmclient.libs,com.elasticpath.cmclient:com.elasticpath.cmclient.testlibs
}

function set_cortex_resources_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local extensions_version="$3"

  # SET REFERENCES IN BOM AND PARENT

  set_property_version "cortex.ep.integration.version" "${extensions_version}" "${project_dir}/pom.xml"
  set_property_version "cortex.ep.integration.version" "${extensions_version}" "${project_dir}/bill-of-materials/pom.xml"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/cortex-resources/pom.xml" \
    -DnewVersion="${extensions_version}"
}

function set_commerce_data_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local platform_version="$3"

  # SET DOWNSTREAM REFERENCES

  # commerce-data
  set_property_version "ep.commerce.data.version" "${platform_version}" "${project_dir}/pom.xml"

  # data-population
  set_property_version "ep.data.population.version" "${platform_version}" "${project_dir}/pom.xml"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/commerce-data/pom.xml" \
    -DnewVersion="${platform_version}"
}

function set_health_monitoring_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local platform_version="$3"

  # SET DOWNSTREAM REFERENCES

  # Set reference in bill-of-materials
  set_property_version "ep.health.monitoring.version" "${platform_version}" "${project_dir}/bill-of-materials/pom.xml"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/health-monitoring/pom.xml" \
    -DnewVersion="${platform_version}"
}

function set_extensions_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local extensions_version="$3"

  # SET DOWNSTREAM REFERENCES

  # devops
  set_parent_version "${extensions_version}" "${project_dir}/devops/pom.xml"

  # SET BOM REFERENCES

  set_property_version "commerce.extensions.version" "${extensions_version}" "${project_dir}/bill-of-materials/pom.xml"

  # SET PROJECT VERSION

  # Set project's CM version (separated due to Tycho reactor)
  # NOTE: Need to set CM project versions before rest of extensions or else the
  # tycho-versions-plugin will not properly update the MANIFEST.MF files.
  mvn ${maven_settings} org.eclipse.tycho:tycho-versions-plugin:0.17.0:set-version \
    -f "${project_dir}/extensions/cm/pom.xml" \
    -Dtycho.mode=maven \
    -DnewVersion="${extensions_version}"

  mvn ${maven_settings} org.eclipse.tycho:tycho-versions-plugin:0.17.0:set-version \
    -f "${project_dir}/extensions/cm/ext-cm-modules/pom.xml" \
    -Dtycho.mode=maven \
    -DnewVersion="${extensions_version}"

  # Update parent versions of modules not in main reactor
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cm/ext-cm-modules/system-tests/pom.xml"
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cm/ext-cm-modules/system-tests/selenium/pom.xml"
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cm/ext-cm-modules/ext-cm-webapp-runner/pom.xml"
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cm/ext-cm-modules/ext-system-tests/pom.xml"
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cm/ext-cm-modules/ext-system-tests/selenium/pom.xml"

  # Set project version
  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/extensions/pom.xml" \
    -DnewVersion="${extensions_version}"

  # Update parent versions of modules not in main reactor
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cortex/ext-system-tests/pom.xml"
  set_parent_version "${extensions_version}" "${project_dir}/extensions/cortex/ext-system-tests/cucumber/pom.xml"
}

function set_devops_version() {
  local project_dir="$1"
  local maven_settings="$2"
  local extensions_version="$3"

  # SET PROJECT VERSION

  mvn ${maven_settings} org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -f "${project_dir}/devops/pom.xml" \
    -DnewVersion="${extensions_version}"
}

function build_project() {
  local project_dir="$1"
  local maven_settings="$2"

  mvn ${maven_settings} clean install -f "${project_dir}/pom.xml" -DskipAllTests
}

function main() {
  local build=false
  local maven_settings=''

  local project_base_dir="$( dirname "${BASH_SOURCE[0]}" )/../.."

  # Process command line arguments
  local unnamed_param_number=1
  while [ -n "$1" ]; do
    param="$1"
    if [ "${param:0:1}" == "-" ]; then
      case "${param}" in
        -b|--build)
          build=true
        ;;
        -s|--maven-settings)
          shift
          maven_settings="-s $1"
        ;;
        -h|--help)
          usage
          exit 0
        ;;
        *)
          log_error "Unknown argument: ${param}"
          exit 1
        ;;
      esac
    else
      case "${unnamed_param_number}" in
        1) PLATFORM_VERSION="$1" ;;
        2) EXTENSION_VERSION="$1" ;;
      esac
      unnamed_param_number=$[unnamed_param_number + 1]
    fi
    shift
  done

  if [ "${unnamed_param_number}" -lt 3 ]; then
    log_error 'Not enough parameters passed in.'
    exit 1
  fi

  # Set project versions
  # Start setting versions from leaf nodes of a dependency tree to avoid having
  # to build projects in between set version steps.
  install_bom_pom "${project_base_dir}" "${maven_settings}"

  set_devops_version "${project_base_dir}" "${maven_settings}" "${EXTENSION_VERSION}"

  set_extensions_version "${project_base_dir}" "${maven_settings}" "${EXTENSION_VERSION}"

  set_commerce_data_version "${project_base_dir}" "${maven_settings}" "${PLATFORM_VERSION}"

  set_cortex_resources_version "${project_base_dir}" "${maven_settings}" "${EXTENSION_VERSION}"

  set_commerce_manager_version "${project_base_dir}" "${maven_settings}" "${PLATFORM_VERSION}"

  set_commerce_engine_version "${project_base_dir}" "${maven_settings}" "${PLATFORM_VERSION}"

  set_health_monitoring_version "${project_base_dir}" "${maven_settings}" "${PLATFORM_VERSION}"

  set_bom_version "${project_base_dir}" "${maven_settings}" "${PLATFORM_VERSION}"

  install_bom_pom "${project_base_dir}" "${maven_settings}"

  set_commerce_parent_version "${project_base_dir}" "${maven_settings}" "${PLATFORM_VERSION}"

  # Build projects with new versions
  if [ "${build}" = true ]; then
    build_project "${project_base_dir}" "${maven_settings}"
  fi

  echo "Project version updates complete!"
}

main "$@"
