/**

  자바 데이터베이스 서버 샘플 프로그램은 Derby Network Server와 상호작용 하는 간단한 JDBC 애플리케이션입니다.
  
 이 프로그램은 다음과 같은 동작을 합니다.

 1.	Derby Network Server를 시작합니다.
 2.	클라이언트용 JDBC 드라이버를 적재합니다.
 3. creates the database if not already created
 4. checks to see if the schema is already created, and if not,
 5. creates the schema which includes the table SAMPLETBL and corresponding indexes.
 6. connects to the database
 7. loads the schema by inserting data
 8. starts client threads to perform database related operations
 9. has each of the clients perform DML operations (select, insert, delete, update) using JDBC calls,
    i)	 one client opens an embedded connection to perform database operations
         You can open an embedded connection in the same JVM that starts the Derby Network
         Server.
    ii)  one client opens a client connection to the Derby Network Server to perform database operations.
 10.waits for the client threads to finish the tasks
 11.shuts down the Derby Network Server at the end of the demo

 <P>
 Usage: java nserverdemo.NsSample
 <P>
 Please note, a file derby.log is created in the directory you run this program.
 This file contains the logging of connections made with the derby network server
 */

package com.abreqadhabra.freelec.java.workshop.addressbook.server.db.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.drda.NetworkServerControl;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.db.JavaDBServerControl;

public class SimpleNetworkServer {

	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	public SimpleNetworkServer() {
		Handler handler = null;
		try {
			handler = new FileHandler("OutFile.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.getLogger("").addHandler(handler);

		startWithProperty();
		setDerbySystemDirectory();
		loadDerbyDriver();
	}

	private void startWithProperty() {
		logger.log(Level.INFO, "Starting Network Server");
		System.setProperty("derby.drda.startNetworkServer", "true");
	}

	// decide on the db system directory
	// create the db system directory

	public void setDerbySystemDirectory() {
		// decide on the db system directory
		String userHomeDir = System.getProperty("user.home", ".");
		String systemDir = userHomeDir + "/."
				+ Constants.DERBY_DATABASE.STRING_DATABASE_NAME;
		System.setProperty("derby.system.home", systemDir);

		logger.log(Level.INFO, "systemDir: " + systemDir);

		// create the db system directory
		File fileSystemDir = new File(systemDir);
		fileSystemDir.mkdir();
	}

	public void loadDerbyDriver() {
		// Booting derby

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

	public static void main(String[] args) {

		SimpleNetworkServer dbServer = new SimpleNetworkServer();
		dbServer.start();

	}

	public void start() {

		Connection connection = null;
		JavaDBServerControl serverControl = new JavaDBServerControl();

		try {
			waitForStart();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			connection = serverControl.start();
			waitForExit();
		}/*
		 * catch (SQLException sqle) {
		 * System.out.println("Failure making connection: " + sqle);
		 * sqle.printStackTrace(); }
		 */finally {

			if (connection != null) {
				serverControl.shutdown();

			}

		}
	}

	/**
	 * Tries to check if the Network Server is up and running by calling ping If
	 * successful, then it returns else tries for 50 seconds before giving up
	 * and throwing an exception.
	 * 
	 * @throws Exception
	 *             when there is a problem with testing if the Network Server is
	 *             up and running
	 */
	private static void waitForStart() throws Exception {

		// Server instance for testing connection
		NetworkServerControl server = null;

		// Use NetworkServerControl.ping() to wait for
		// NetworkServer to come up. We could have used
		// NetworkServerControl to start the server but the property is
		// easier.
		server = new NetworkServerControl();
		System.out.println(server.getSysinfo());
		System.out.println("Testing if Network Server is up and running!");
		int i = 0;
		// for (int i = 0; i < 10; i++) {
		while (true) {
			try {

				server.ping();
				server.logConnections(true);
				System.out.println(server.getRuntimeInfo());

				Thread.currentThread().sleep(5000);

				System.out.println("Derby Network Server now running");

			} catch (Exception e) {
				System.out.println("Try #" + i + " " + e.toString());
				if (i == 9) {
					System.out
							.println("Giving up trying to connect to Network Server!");
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * This method waits until the user hits enter to stop the server and
	 * eventually exit this program Allows clients to continue to connect using
	 * client connections from other jvms to Derby Network Server that was
	 * started in this program
	 */
	private static void waitForExit() {
		System.out.println(ijUsage());
		System.out.println("Clients can continue to connect: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Press [Enter] to stop Server");
		try {
			in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns a string with information as to how to connect to Derby Network
	 * Server
	 */
	private static String ijUsage() {

		String ijUsage = "\nWhile my app is busy with embedded work, ";
		ijUsage += "ij might connect like this:\n\n";
		ijUsage += "\tjava -Dij.user="
				+ Constants.DERBY_DATABASE.STRING_DB_USER
				+ " -Dij.password="
				+ Constants.DERBY_DATABASE.STRING_DB_PASSWORD
				+ " -Dij.protocol=jdbc:derby://localhost:1527/ org.apache.derby.tools.ij\n";
		ijUsage += "\tij> connect '" + Constants.DERBY_DATABASE.STRING_DATABASE_NAME
				+ "';\n\n";

		return ijUsage;
	}

	public void printSQLException(SQLException se) {
		while (se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());

			se = se.getNextException();
		}
	}

	void printSQLWarning(SQLWarning sw) {
		while (sw != null) {

			System.out.print("SQLWarning: State=" + sw.getSQLState());
			System.out.println(", Severity = " + sw.getErrorCode());
			System.out.println(sw.getMessage());

			sw = sw.getNextWarning();
		}
	}

}
