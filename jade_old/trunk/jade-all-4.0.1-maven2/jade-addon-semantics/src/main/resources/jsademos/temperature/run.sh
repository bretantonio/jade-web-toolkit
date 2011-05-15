#!/bin/sh

export JADE_HOME=$HOME/jade/3.2/jade

export CLASSPATH=$JADE_HOME/lib/jade.jar:$JADE_HOME/lib/JadeTools.jar:$HOME/JSA/classes

java -cp $CLASSPATH jade.Boot -name test -gui -nomtp dfagent:jsademos.temperature.DFAgent bestsensor:jsademos.temperature.SensorAgent\(2 dfagent@test\) sensor:jsademos.temperature.SensorAgent\(0 dfagent@test\) bettersensor:jsademos.temperature.SensorAgent\(1 dfagent@test\) display:jsademos.temperature.DisplayAgent\(dfagent@test\) son:jsademos.temperature.ManAgent\(son.txt display@test mother@test showkb\) mother:jsademos.temperature.DemoAgent\(mother.txt\) daughter:jsademos.temperature.DemoAgent\(daughter.txt\) 
