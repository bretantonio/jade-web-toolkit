package com.abreqadhabra.freelec.jbpf.examples.lms.bin.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class EmbeddedJavaDB {

	/* the default framework is embedded */
	private String framework = "embedded";

	// Derby Driver
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	// Derby Connect URL
	private static String protocol = "jdbc:derby:";

	// Derby Connect URL
	// private static String dbURL = "jdbc:derby:sampleDB;create=true";

	// jdbc Connection
	private static Connection conn = null;

	private static Statement stmt = null;

	public EmbeddedJavaDB() {

		System.out.println("EmbeddedJavaDB starting in " + framework + " mode");
		conn = createConnection();
		createTables();

	}

	private void createTables() {

		Statement statement = null;
        PreparedStatement psInsert = null;

		/*
		 * Creating a statement object that we can use for running various SQL
		 * statements commands against the database.
		 */
		try {
			statement = conn.createStatement();
			// We create a table...
			System.out.println(strCreateUserTable);
			statement.execute(strCreateUserTable);
			System.out.println("Created table LMS_USER");
			
            // and add a few rows...

            /* It is recommended to use PreparedStatements when you are
             * repeating execution of an SQL statement. PreparedStatements also
             * allows you to parameterize variables. By using PreparedStatements
             * you may increase performance (because the Derby engine does not
             * have to recompile the SQL statement each time it is executed) and
             * improve security (because of Java type checking).
             */
            // parameter 1 is num (int), parameter 2 is addr (varchar)
            psInsert = conn.prepareStatement(
                        "insert into LMS_USER values (?, ?, ?, ?)");

            psInsert.setString(1, "1234567890123");
            psInsert.setString(2, "(주)프리렉 ");
            psInsert.setString(3, "29");
            psInsert.setString(4, "경기도 부천시 원미구 상동 532-12 나루빌딩 401호 (420-030)");
            psInsert.executeUpdate();
            System.out.println("Inserted (주)프리렉 ");	
			
			System.out.println(strCreateStudentTable);
			statement.execute(strCreateStudentTable);
			System.out.println("Created table LMS_STUDENT");
			
			System.out.println(strCreateProfessorTable);
			statement.execute(strCreateProfessorTable);
			System.out.println("Created table LMS_PROFESSOR");			
			
			System.out.println(strCreateEmployeeTable);
			statement.execute(strCreateEmployeeTable);
			System.out.println("Created table LMS_EMPLOYEE");			
			
			conn.commit();
			
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}

	}

	/**
	 * Loads the appropriate JDBC driver for this environment/framework. For
	 * example, if we are in an embedded environment, we load Derby's embedded
	 * Driver, <code>org.apache.derby.jdbc.EmbeddedDriver</code>.
	 */
	private static void loadDriver() {

		try {
			Class.forName(driver).newInstance();
			// Get a connection
		} catch (ClassNotFoundException cnfe) {
			System.err.println("\nUnable to load the JDBC driver " + driver);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err.println("\nUnable to instantiate the JDBC driver "
					+ driver);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err.println("\nNot allowed to access the JDBC driver "
					+ driver);
			iae.printStackTrace(System.err);
		}

	}

	public static  Connection createConnection() {
		
		/* load the desired JDBC driver */
		loadDriver();
		
		Properties props = new Properties(); // connection properties
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		props.put("user", "freelec");
		props.put("password", "freelec");

		/*
		 * By default, the schema APP will be used when no username is provided.
		 * Otherwise, the schema name is the same as the user name (in this case
		 * "user1" or USER1.)
		 * 
		 * Note that user authentication is off by default, meaning that any
		 * user can connect to your database using any password. To enable
		 * authentication, see the Derby Developer's Guide.
		 */

		String dbName = "jdbcLMSDB"; // the name of the database

		/*
		 * This connection specifies create=true in the connection URL to cause
		 * the database to be created when connecting for the first time. To
		 * remove the database, remove the directory derbyDB (the same as the
		 * database name) and its contents.
		 * 
		 * The directory derbyDB will be created under the directory that the
		 * system property derby.system.home points to, or the current directory
		 * (user.dir) if derby.system.home is not set.
		 */
		try {
			conn = DriverManager.getConnection(protocol + dbName
					+ ";create=true", props);
			System.out.println("Connected to and created database " + dbName);

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			conn.setAutoCommit(false);

		} catch (SQLException sqle) {
			printSQLException(sqle);
		}

		return conn;
	}
	
	public void disconnect() {
		try {
			// the shutdown=true attribute shuts down Derby
			DriverManager.getConnection("jdbc:derby:;shutdown=true");

			// To shut down a specific database only, but keep the
			// engine running (for example for connecting to other
			// databases), specify a database in the connection URL:
			// DriverManager.getConnection("jdbc:derby:" + dbName +
			// ";shutdown=true");
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}
	}

	/**
	 * Prints details of an SQLException chain to <code>System.err</code>.
	 * Details included are SQL State, Error code, Exception message.
	 * 
	 * @param e
	 *            the SQLException from which to print details.
	 */
	public static void printSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}

	private static final String strCreateUserTable = 
			"CREATE TABLE LMS_USER (" + 
			"ssn VARCHAR(13) NOT NULL PRIMARY KEY, " +
			"name VARCHAR(30), " + 
			"age VARCHAR(30), " +
			"adress VARCHAR(200)" + 
			")";
	
	private static final String strCreateStudentTable = 
			"CREATE TABLE LMS_STUDENT (" + 
			"userid VARCHAR(30), " +
			"ssn VARCHAR(13), " + 
			"studentid VARCHAR(30)" + 
			")";
	
	private static final String strCreateProfessorTable = 
			"CREATE TABLE LMS_PROFESSOR (" + 
			"userid VARCHAR(30), " +
			"ssn VARCHAR(13), " + 
			"course VARCHAR(30)" + 
			")";	

	private static final String strCreateEmployeeTable = 
			"CREATE TABLE LMS_EMPLOYEE (" + 
			"userid VARCHAR(30), " +
			"ssn VARCHAR(13), " + 
			"department VARCHAR(30)" + 
			")";	

}
