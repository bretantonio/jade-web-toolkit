package com.abreqadhabra.freelec.java.workshop.addressbook.server.db;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.drda.NetworkServerControl;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;

public class JavaDBServerControl {

	// 로그 출력을 위한 선언
	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	
	// Server instance for testing connection
	NetworkServerControl networkServerControl = null;
	
	PrintWriter pw = new PrintWriter(System.out,true);	// to print messages

	
public JavaDBServerControl(){
	
}

public JavaDBServerControl(int port) {
	try {
		networkServerControl = new
			  NetworkServerControl(InetAddress.getByName("localhost"), port);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	logger.log(Level.INFO, "Derby Network Server 가 생성되었습니다.");
}

	/**
	 * Start Derby Network server
	 * @throws Exception 
	 * 
	 */
	public void start() throws Exception {
		
			networkServerControl.start(pw);
			logger.log(Level.INFO, networkServerControl.getSysinfo());

		logger.log(Level.INFO, "Derby Network Server 가 시작되었습니다.");
	}
	
    /**
     * Shutdown the NetworkServer
     * @throws Exception 
     */
    public void shutdown() throws Exception {
        	networkServerControl.shutdown();

    }
  
	/**
	 * trace utility of server
	 * @throws Exception 
	 */
	public void logConnection(boolean onoff) throws Exception {
			networkServerControl.logConnections(onoff);
	}

	
	/**
	 * trace utility of server
	 * @throws Exception 
	 */
	public void trace(boolean onoff) throws Exception {
			networkServerControl.trace(onoff);

	}

	/**
	 * Try to test for a connection Throws exception if unable to get a
	 * connection
	 * @throws Exception 
	 */
	public void testForConnection() throws Exception  {

			networkServerControl.ping();

	}
	
	public Connection getConnection(Properties dbProperties) {

		Connection connection =null;

		if (isDatabaseExists()) {
			System.out.println("기존 데이터베이스가 존재합니다.");
			connection = createConnection(dbProperties);
		} else {
			System.out.println("기존 데이터베이스가 존재하지 않습니다.");
			connection = checkAndCreateDatabase(dbProperties);
		}
		return connection;
		
	}
	

	private boolean isDatabaseExists() {
		boolean isExists = false;
		String dbLocation = getDatabaseLocation();		
		File dbFileDir = new File(dbLocation);
		if (dbFileDir.exists()) {
			isExists = true;
		}else{
			dbFileDir.mkdir();
			logger.log(Level.INFO,
					"Derby Network Server가 사용하는 시스템 디렉토리가 존재하지 않아 새롭게 생성하였습니다.");
		}
		return isExists;
	}
	
	private String getDatabaseLocation() {
		String dbLocation = System.getProperty("derby.system.home") + "/"
				+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME;
		return dbLocation;
	}
	
	
	// DriverManager API를 사용하여 데이터베이스 컨넥션 취득
	public Connection createConnection(Properties properties) {

		/* load the desired JDBC driver */
		//loadJDBCDriver();
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
		System.out.println("url: " + url);
		
		try {
			connection = DriverManager.getConnection(url, properties);
			System.out.println("Connected to database "
					+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME);

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			connection.setAutoCommit(false);

		} catch (SQLException se) {
			se.printStackTrace();
		}

		return connection;
	}
	
	
	public String getDatabaseUrl() {
		String dbUrl = Constants.DERBY_DATABASE.STRING_PROTOOL
				+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME;
		return dbUrl;
	}
	
	
	private Connection checkAndCreateDatabase(Properties dbProperties) {
		boolean isTableCreated = false;
		String dbUrl = getDatabaseUrl();
		Connection connection =null;

		dbProperties.put("create", "true");
		try {
			connection = DriverManager.getConnection(dbUrl, dbProperties);
			System.out.println("Connected to and created database "
					+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME);
			isTableCreated = createTables(connection);
			if (isTableCreated) {
				insertDummyDataset(connection);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		//dbProperties.remove("create");
		return connection;
	}
	private boolean createTables(Connection dbConnection) {
		boolean isTableCreated = false;
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			//statement.execute(Constants.DERBY_ADDRESS_DAO.STR_SQL_CREATE_ADDRESS_TABLE);
			statement.execute(Constants.DERBY_FLIGHTS_DAO.STR_SQL_CREATE_FLIGHTS_TABLE);
			isTableCreated = true;
		} catch (SQLException se) {
			se.printStackTrace();
			//printSQLException(se);
		}

		return isTableCreated;
	}
	
	private void insertDummyDataset(Connection connection) {

		PreparedStatement preStmtSaveAddress = null;

		try {
//			preStmtSaveAddress = connection.prepareStatement(
//					Constants.DERBY_ADDRESS_DAO.STR_SQL_SAVE_ADDRESS,
//					Statement.RETURN_GENERATED_KEYS);
		    preStmtSaveAddress = connection.prepareStatement(
				Constants.DERBY_FLIGHTS_DAO.STR_SQL_INSERT_DUMMY_DATA);
		    
			int rowCount = preStmtSaveAddress.executeUpdate();


/*			preStmtSaveAddress.clearParameters();

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
*/
			connection.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			while (rs.next()){
				System.out.println("number of rows in sys.systables = "
						+ rs.getInt(1));
			}

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

	void waitForConnection() {
		// Server instance for testing connection
	//			NetworkServerControl server = null;

				// Use NetworkServerControl.ping() to wait for
				// NetworkServer to come up. We could have used
				// NetworkServerControl to start the server but the property is
				// easier.
				try{
		//		server = new NetworkServerControl();
					
					networkServerControl.ping();
					networkServerControl.logConnections(true);
					System.out.println(networkServerControl.getRuntimeInfo());

					Thread.currentThread().sleep(5000);

					System.out.println("Derby Network Server now running");
//					
//				System.out.println(networkServerControl.getSysinfo());
//				System.out.println("Testing if Network Server is up and running!");
		
				} catch (Exception e) {

						e.printStackTrace();

				}
	}

}
