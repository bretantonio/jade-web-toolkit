@set target=%1

@if ""%1"" == """" @set target=j2se

@echo Running the JSA demo in %target% environment

java -cp "../../../bin;../../../../../leap/%target%/lib/JadeLeap.jar" jade.Boot -nomtp -gui -name test dfagent:jsademos.temperature.DFAgent;sensor:jsademos.temperature.SensorAgent(0,dfagent@test);bettersensor:jsademos.temperature.SensorAgent(1,dfagent@test);bestsensor:jsademos.temperature.SensorAgent(2,dfagent@test);display:jsademos.temperature.DisplayAgent(dfagent@test);son:jsademos.temperature.ManAgent(son.txt,display@test,mother@test,showkb);mother:jsademos.temperature.DemoAgent(mother.txt);daughter:jsademos.temperature.DemoAgent(daughter.txt)
