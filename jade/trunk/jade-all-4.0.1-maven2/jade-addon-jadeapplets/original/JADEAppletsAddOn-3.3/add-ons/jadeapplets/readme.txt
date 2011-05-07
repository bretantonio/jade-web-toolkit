How to use JADE within Applets example

Author:	Claudiu Anghel

Date:	23.04.2003

For running the example the following steps should be followed:

	1. You should have a hierarchy like jade/add-ons/jadeapplets. 
	2. Run the target copy-jade-lib from build.xml using ant tool - this will copy the jade.jar, Base64.jar, iiop.jar, jadeTools.jar, http.jar, sax2.jar from the JADE distribution to the lib folder. 
	3. Run the target jade-lib-sign - this will sign the JADE jar files 
	4. Run the target compile to compile everything in classes folder 
	5. Run the target applet-jar-sign to generate and sign the clientapplet.jar file 
	6. Run startServer.bat - this will start the JADE platform on the given port (the first parameter) and create the server container (with the HTTP MTP using the port given by the second parameter) and the server agent 
	7. Edit clientJadeApplet.html and fill in properly the host name of the jade platform, the port of the jade platform, and the applet container port. 
	8. Start clientJadeApplet.html

For more details please read the tutorial from the JADE distribution.


