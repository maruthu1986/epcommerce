@echo off

setlocal ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
set KEY_NAME=HKLM\SOFTWARE\JavaSoft\Java Runtime Environment
set VALUE_NAME=CurrentVersion
FOR /F "usebackq skip=2 tokens=3" %%A IN (`REG QUERY "%KEY_NAME%" /v %VALUE_NAME% 2^>nul`) DO (
	set CurrentJavaRuntime=%%A
)
if not defined CurrentJavaRuntime (
	@echo Java was not found. Please ensure java is installed and JAVA_HOME is set
	PING 1.1.1.1 -n 1 -w 20000 >NUL
	exit /b 101
)

if "%JRE_HOME%" == "" (
	if "%JAVA_HOME%" == "" (
		set JAVA_CURRENT=%KEY_NAME%\%CurrentJavaRuntime%
		FOR /F "usebackq skip=2 tokens=3*" %%A IN (`REG QUERY "!JAVA_CURRENT!" /v JavaHome 2^>nul`) DO (
			set _JavaHome=%%A %%B
		)
	) else (
	    set _JavaHome=%JAVA_HOME%
	)
) ELSE (
	set _JavaHome=%JRE_HOME%
)

set JAVA_EXE="%_JavaHome%\bin\java"

set CURRENT_DIRECTORY=%~dp0
cd "%CURRENT_DIRECTORY%"
set WEBAPPS_DIRECTORY=%CURRENT_DIRECTORY%webapps
set TOMCAT_HOME=%WEBAPPS_DIRECTORY%\apache-tomcat
set CATALINA_HOME=%TOMCAT_HOME%

set CATALINA_OPTS=-Dcom.sun.management.jmxremote=true -Xmx1536m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true

for /d %%W in ("%WEBAPPS_DIRECTORY%\*-webapp-base") do (
	echo Stopping "%%W" ...
	set CATALINA_BASE=%%W
	START CMD /C CALL "%CATALINA_HOME%\bin\catalina.bat" stop
)