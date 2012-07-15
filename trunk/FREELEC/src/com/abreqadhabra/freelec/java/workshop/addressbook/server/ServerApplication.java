package com.abreqadhabra.freelec.java.workshop.addressbook.server;


/**
 * This class implements the RMI-Based server application.
 * <p>
 * 
 * @see ServerController
 * @see com.abreqadhabra.freelec.java.workshop.addressbook.server.util.ValidationUtil
 * @author Dongsup Kim
 * @version 0.8 19-Dec-2006
 */
public class ServerApplication {
    /**
         * Main to start the <code> ServerApplication </code> and creates the
         * Instance of <code> ServerController </code>. Create a RMI server
         * with the specified port(1024-65535).
         * 
         * @param args
         *                The port of naming service like rmi registry.
         */
    public static void main(String[] args) {
	boolean isValid = false;
	int port = 0;
	if (args != null && args.length == 1) {
	    isValid = ServerController.portValidation(args[0]);
	    if (isValid) {
		port = Integer.parseInt(args[0]);
	    }
	}
	if (Boolean.TRUE.equals(Boolean.valueOf(isValid))) {
	    new ServerController(port);
	} else {
	    System.err.println("Usage: java -jar ServerApplication.jar "
		    + "<port>");
	    System.exit(-1);
	}
    }
}
