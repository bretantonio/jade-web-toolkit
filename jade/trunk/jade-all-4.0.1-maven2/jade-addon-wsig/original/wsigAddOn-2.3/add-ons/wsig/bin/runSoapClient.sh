#!/bin/sh

CP=../webModule/WEB-INF/classes

java -classpath $CP com.tilab.wsig.soap.SoapClient $1 $2
