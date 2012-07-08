package com.abreqadhabra.freelec.java.workshop.addressbook.server.db.etc;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;

public class JavaDsBServerControl {

    Logger logger = Logger.getLogger(this.getClass().getCanonicalName()); 
	private Connection dbConnection = null;
	Properties dbProperties = new Properties(); // connection properties

	public Connection start() {
		if (isDatabaseExists()) {
			logger.log(Level.INFO, "기존 데이터베이스가 존재합니다.");
			dbConnection = createConnection();
		} else {
			logger.log(Level.INFO, "기존 데이터베이스가 존재하지 않습니다.");
			dbConnection = createDatabase();
		}
	//	printConnectionMetaData(dbConnection);
		testConnection(dbConnection);
		return dbConnection;
	}

	
	public boolean isDatabaseExists() {
		boolean bExists = false;
		String dbLocation = getDatabaseLocation();
		
		logger.log(Level.INFO, "dbLocation: " + dbLocation);

		
		File dbFileDir = new File(dbLocation);
		if (dbFileDir.exists()) {
			bExists = true;
		}
		return bExists;
	}
	
	private String getDatabaseLocation() {
		String dbLocation = System.getProperty("derby.system.home") + "/"
				+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME;
		return dbLocation;
	}

	public Connection createConnection() {

		/* load the desired JDBC driver */
		//loadJDBCDriver();
		Properties dbProperties = getDBProperties();
		Connection connection =null;
		/*
		 * By default, the schema APP will be used when no username is provided.
		 * Otherwise, the schema name is the same as the user name (in this case
		 * "user1" or USER1.)
		 * 
		 * Note that user authentication is off by default, meaning that any
		 * user can connect to your database using any password. To enable
		 * authentication, see the Derby Developer's Guide.
		 */

		// String dbName = "jdbcLMSDB"; // the name of the database

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

		String url = getDatabaseUrl();
		logger.log(Level.INFO, "url: " + url);
		
		try {
			connection = DriverManager.getConnection(url, dbProperties);
			logger.log(Level.INFO, "Connected to database "
					+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME);

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			connection.setAutoCommit(false);

		} catch (SQLException se) {
			se.printStackTrace();
		}

		return connection;
	}
	
	private Properties getDBProperties() {
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		dbProperties.put("user", Constants.DERBY_DATABASE.STRING_DB_USER);
		dbProperties.put("password", Constants.DERBY_DATABASE.STRING_DB_PASSWORD);
		logger.log(Level.INFO, "dbProperties" + dbProperties);

		return dbProperties;
	}
	
	public String getDatabaseUrl() {
		String dbUrl = Constants.DERBY_DATABASE.STRING_PROTOOL
				+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME;
		return dbUrl;
	}
	
	public Connection createDatabase() {
		boolean bCreated = false;
		String dbUrl = getDatabaseUrl();
		Connection connection =null;

		Properties dbProperties = getDBProperties();
		dbProperties.put("create", "true");
		try {
			connection = DriverManager.getConnection(dbUrl, dbProperties);
			logger.log(Level.INFO, "Connected to and created database "
					+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME);
			bCreated = createTables(connection);
			if (bCreated) {
				insertDummyDataset(connection);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbProperties.remove("create");
		return connection;
	}

	private boolean createTables(Connection dbConnection) {
		boolean bCreatedTables = false;
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			statement
					.execute(Constants.DERBY_ADDRESS_DAO.STR_SQL_CREATE_ADDRESS_TABLE);
			bCreatedTables = true;
		} catch (SQLException se) {
			se.printStackTrace();
			//printSQLException(se);
		}

		return bCreatedTables;
	}
	
	private void insertDummyDataset(Connection connection) {

		PreparedStatement preStmtSaveAddress = null;

		try {
			preStmtSaveAddress = connection.prepareStatement(
					Constants.DERBY_ADDRESS_DAO.STR_SQL_SAVE_ADDRESS,
					Statement.RETURN_GENERATED_KEYS);

			preStmtSaveAddress.clearParameters();

			preStmtSaveAddress.setString(1, "LastName");
			preStmtSaveAddress.setString(2, "FirstName");
			;
			preStmtSaveAddress.setString(3, "MiddleName");
			preStmtSaveAddress.setString(4, "Phone");
			preStmtSaveAddress.setString(5, "Email");
			preStmtSaveAddress.setString(6, "Address1");
			preStmtSaveAddress.setString(7, "Address2");
			preStmtSaveAddress.setString(8, "City");
			preStmtSaveAddress.setString(9, "State");
			preStmtSaveAddress.setString(10, "PostalCode");
			preStmtSaveAddress.setString(11, "Country");

			int rowCount = preStmtSaveAddress.executeUpdate();

			preStmtSaveAddress.clearParameters();

			preStmtSaveAddress.setString(1, "1");
			preStmtSaveAddress.setString(2, "2");
			;
			preStmtSaveAddress.setString(3, "3");
			preStmtSaveAddress.setString(4, "4");
			preStmtSaveAddress.setString(5, "5");
			preStmtSaveAddress.setString(6, "6");
			preStmtSaveAddress.setString(7, "7");
			preStmtSaveAddress.setString(8, "8");
			preStmtSaveAddress.setString(9, "9");
			preStmtSaveAddress.setString(10, "10");
			preStmtSaveAddress.setString(11, "11");

			rowCount = preStmtSaveAddress.executeUpdate();

			connection.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void printConnectionMetaData(Connection dbConnection) {
		DatabaseMetaData dbmd = null;
		try {

			dbmd = dbConnection.getMetaData();

			logger.log(Level.INFO, "Database Name    = "
					+ dbmd.getDatabaseProductName());
			logger.log(Level.INFO, "Database Version = "
					+ dbmd.getDatabaseProductVersion());
			logger.log(Level.INFO, "Driver Name      = " + dbmd.getDriverName());
			logger.log(Level.INFO, "Driver Version   = " + dbmd.getDriverVersion());
			logger.log(Level.INFO, "Database URL     = " + dbmd.getURL());

		} catch (SQLException se) {
			se.printStackTrace();
			//printSQLException(se);
		}
	}
	

	public void testConnection(Connection conn){

		Statement stmt = null;
		ResultSet rs = null;
		try {
			// To test our connection, we will try to do a select from the
			// system catalog tables
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from sys.systables");
			while (rs.next())
				logger.log(Level.INFO, "number of rows in sys.systables = "
						+ rs.getInt(1));

		} catch (SQLException sqle) {
			System.out
					.println("SQLException when querying on the database connection; "
							+ sqle);
			try {
				throw sqle;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}
	public void shutdown() {
		if (dbConnection != null) {
			String dbUrl = getDatabaseUrl();
			dbProperties.put("shutdown", "true");
			try {
				dbConnection.commit();
				dbConnection.close();
				DriverManager.getConnection(dbUrl, dbProperties);
				System.out.println("Disconnected to database "
						+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME);

			} catch (SQLException ex) {
			}
		}
	}


}
