package com.abreqadhabra.freelec.java.workshop.addressbook.server.javadb.common;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;

public class JavaDBUtil {

	public static void loadJDBCDriver() {
		// load Derby driver
		try {
			Class.forName(
					Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME)
					.newInstance();
			// Get a connection
		} catch (ClassNotFoundException cnfe) {
			System.err
					.println("\nUnable to load the JDBC driver "
							+ Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err
					.println("\nUnable to instantiate the JDBC driver "
							+ Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err
					.println("\nNot allowed to access the JDBC driver "
							+ Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME);
			iae.printStackTrace(System.err);
		}
	}
}
