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

@echo Using Java exe %JAVA_EXE%
FOR /F tokens^=2-3^ delims^=.-_^" %%j in ('%JAVA_EXE% -fullversion 2^>^&1') DO SET "JAVA_VERSION=%%j.%%k"

if not "%JAVA_VERSION%" == "1.8" (
	@echo Java 1.8 is required, but you have a different version ^(%JAVA_VERSION%^). Please switch to 1.8
	PING 1.1.1.1 -n 1 -w 20000 >NUL
	exit /b 103
)

endlocal & set JAVA_HOME=%_JavaHome%
@echo off

set CURRENT_DIRECTORY=%~dp0
cd "%CURRENT_DIRECTORY%"
set WEBAPPS_DIRECTORY=%CURRENT_DIRECTORY%webapps
set TOMCAT_HOME=%WEBAPPS_DIRECTORY%\apache-tomcat
set CATALINA_HOME=%TOMCAT_HOME%

set CATALINA_OPTS=-Dh2.bindAddress=127.0.0.1 -Dcom.sun.management.jmxremote=true -Xmx1536m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true

set CORTEX_STUDIO="http://localhost:9080/studio"
set SEARCH_SERVER="http://localhost:38082/searchserver/product/select?q=*:*&wt=json"
set CM="http://localhost:9081/admin"
set ACTIVE_MQ="http://localhost:39081/jms"

for /d %%W in ("%WEBAPPS_DIRECTORY%\*-webapp-base") do (
	echo Starting "%%W" ...
	mkdir "%%W\logs" 2>NUL
	mkdir "%%W\conf" 2>NUL
	mkdir "%%W\webapps" 2>NUL
	mkdir "%%W\temp" 2>NUL
    rmdir /s /q "%%W\work"
	set CATALINA_BASE=%%W
	ECHO.%%W| FIND /I "activemq">Nul && (
		set CATALINA_OPTS=-Dh2.bindAddress=127.0.0.1 -Dcom.sun.management.jmxremote=true -Xmx512m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true
		set TITLE=activemq
		START CMD /C CALL "%CATALINA_HOME%\bin\catalina.bat" start
		PING 1.1.1.1 -n 1 -w 10000 >NUL
	)
	ECHO.%%W| FIND /I "ep-cortex">Nul && (
		set CATALINA_OPTS=-Dh2.bindAddress=127.0.0.1 -Dcom.sun.management.jmxremote=true -Xmx1024m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true
		set TITLE=ep-cortex
		START CMD /C CALL "%CATALINA_HOME%\bin\catalina.bat" start
		PING 1.1.1.1 -n 1 -w 45000 >NUL
		start "" %CORTEX_STUDIO%
	)
	ECHO.%%W| FIND /I "ep-search">Nul && (
		set CATALINA_OPTS=-Dh2.bindAddress=127.0.0.1 -Dcom.sun.management.jmxremote=true -Xmx1536m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true
		set TITLE=ep-search
		START CMD /C CALL "%CATALINA_HOME%\bin\catalina.bat" start
		PING 1.1.1.1 -n 1 -w 10000 >NUL
	)
	ECHO.%%W| FIND /I "ep-integration">Nul && (
		set CATALINA_OPTS=-Dh2.bindAddress=127.0.0.1 -Dcom.sun.management.jmxremote=true -Xmx1280m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true
		set TITLE=ep-integration
		START CMD /C CALL "%CATALINA_HOME%\bin\catalina.bat" start
		PING 1.1.1.1 -n 1 -w 10000 >NUL
    )
    ECHO.%%W| FIND /I "ep-cm">Nul && (
        set CATALINA_OPTS=-Dh2.bindAddress=127.0.0.1 -Dcom.sun.management.jmxremote=true -Xmx1280m -Dsun.lang.ClassLoader.allowArraySyntax=true -XX:CompileCommand=exclude,org/apache/velocity/runtime/directive/Foreach,render -Dfile.encoding=UTF-8 -Dapple.awt.UIElement=true -Djava.awt.headless=true
        set TITLE=ep-cm
        START CMD /C CALL "%CATALINA_HOME%\bin\catalina.bat" start
        PING 1.1.1.1 -n 1 -w 10000 >NUL
        	start "" %CM%
    )
)

echo "Cortex Studio: %CORTEX_STUDIO%"
echo "Search Server: %SEARCH_SERVER%"
echo "CM: %CM%"
echo "ActiveMQ: %ACTIVE_MQ%"
