@echo off
rem Windows XP Syntax
set LIB=lib
"%JAVA_HOME%\bin\java" -cp ".;%LIB%\*" -Xmx512m com.elasticpath.importexport.client.Index %*
@echo on
