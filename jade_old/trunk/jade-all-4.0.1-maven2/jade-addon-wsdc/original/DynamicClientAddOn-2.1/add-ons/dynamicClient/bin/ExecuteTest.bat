@ECHO OFF
SETLOCAL
@echo off

set WSDC_HOME=..
set JADE_HOME=..\..\..

set WSDC_LIB=%JADE_HOME%\lib\jade.jar;%WSDC_HOME%\lib\wsdc.jar;%WSDC_HOME%\lib\axisPlus.jar;%WSDC_HOME%\lib\commons-discovery-0.2.jar;%WSDC_HOME%\lib\commons-logging-1.1.1.jar;%WSDC_HOME%\lib\jaxrpc-1.1.jar;%WSDC_HOME%\lib\log4j-1.2.8.jar;%WSDC_HOME%\lib\saaj-1.2.jar;%WSDC_HOME%\lib\wsdl4j-1.5.1.jar;%WSDC_HOME%\lib\jadeMisc.jar;%WSDC_HOME%\lib\wss4j-1.5.1.jar;%WSDC_HOME%\lib\xmlsec-1.3.0.jar;.

SET SAVECLASSPATH=%CLASSPATH%
SET CLASSPATH=%WSDC_LIB%

"%JAVA_HOME%\bin\javac" Test.java
"%JAVA_HOME%\bin\java" Test

:end
SET CLASSPATH=%SAVECLASSPATH%
SET SAVECLASSPATH=
ENDLOCAL
