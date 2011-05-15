#!/bin/sh

. ./setClasspath.sh
PORT=1099

java -classpath $JADECP jade.Boot -name "WSIGTestPlatform" -port $PORT -gui
