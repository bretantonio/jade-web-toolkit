set JADE_HOME=../../../..
set LIB=%JADE_HOME%/add-ons/security/lib
set MYCLPATH=%JADE_HOME%/leap/j2se/lib/JadeLeap.jar;%JADE_HOME%\lib\commons-codec\commons-codec-1.3.jar;%LIB%\jadeSecurity.jar;%LIB%\examples.jar

java -cp %MYCLPATH%  jade.Boot -conf mainLeap.conf 

pause
