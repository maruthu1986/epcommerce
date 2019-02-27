@echo off

SETLOCAL enabledelayedexpansion

SET SCRIPT_PATH=%~dp0
SET BIN_PATH=%SCRIPT_PATH%\bin
SET ROUTE_TO_DATA_POPULATION_HOME=.

:: Include the header file
CALL %BIN_PATH%\header.bat %*
:: If it exited with an error then exit with an error also, and do not proceed to invoke the CLI program below
IF ERRORLEVEL 1 exit /b 1

:: Invoke the Data Population CLI Tool
"%JAVA_CMD%" -cp "%DP_CLASSPATH%" %JAVA_DEBUG_PARMS% ^
    com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication %*