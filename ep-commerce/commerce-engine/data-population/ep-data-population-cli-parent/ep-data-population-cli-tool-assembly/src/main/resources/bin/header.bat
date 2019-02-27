:: Path Variables ::

SET DATA_POPULATION_HOME=%SCRIPT_PATH%\%ROUTE_TO_DATA_POPULATION_HOME%
:: Resolve the DATA_POPULATION_HOME after ROUTE_TO_DATA_POPULATION_HOME has been appended to SCRIPT_PATH
pushd %DATA_POPULATION_HOME%
SET DATA_POPULATION_HOME=%cd%
popd

SET DATA_POPULATION_WORKING_DIRECTORY=%DATA_POPULATION_HOME%\tmp

:: JAVA OPTIONS ::

:: Use $JAVA_HOME if set, otherwise just rely on the java application on the $PATH
SET JAVA_CMD=java
if EXIST "%JAVA_HOME%" (
    SET JAVA_CMD=%JAVA_HOME%\bin\java
)

:: Setup Java debug parms if the environment variable is set
SET JAVA_DEBUG_PARMS=
if "%DATA_POPULATION_DEBUG%" == "true" (
    REM We HAVE to escape parentheses inside conditional blocks because the parser sucks
    REM and treats the parentheses in the ECHO below as end blocks otherwise.
    REM Also have to use REM statements here rather than :: as wouldn't work otherwise

    echo Running in Java debug mode ^(port 5005^)
    SET JAVA_DEBUG_PARMS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005
)

:: This classpath is updated below once the configuration directory has been determined
SET DP_CLASSPATH=%DATA_POPULATION_HOME%\lib\*

:: CLASSPATH OPTIONS ::

:: Now update the classpath to put a static and dynamic configuration directory on the classpath.
:: The static classpath directory will configure logging and be supplied by the tool at build time.
:: The dynamic classpath directory is used so that an importexporttool.config (based on the configured database connection) can be generated at runtime
:: and put on the classpath. Versions of Import/Export before 6.9 requires it to be on the classpath. Versions after can just specify a file location.

SET STATIC_CONFIGURATION_CLASSPATH_DIRECTORY=%DATA_POPULATION_HOME%\config
SET DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY=%DATA_POPULATION_WORKING_DIRECTORY%\config\on-classpath

:: If the dynamic classpath directory exists and is not empty clear the directory as otherwise it can lead to classpath problems
:: Don't delete the directory itself as it needs to be there before we start the JVM, but clear the contents
if EXIST %DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY% (
    DEL %DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY% /q
) else (
    :: Make sure the directory exists before we launch as this can also lead to classpath issues
    MKDIR %DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY%
)

SET DP_CLASSPATH=%DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY%;%STATIC_CONFIGURATION_CLASSPATH_DIRECTORY%;%DP_CLASSPATH%