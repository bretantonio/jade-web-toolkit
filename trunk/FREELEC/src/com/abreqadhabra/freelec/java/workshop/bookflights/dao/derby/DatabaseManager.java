package com.abreqadhabra.freelec.java.workshop.bookflights.dao.derby;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.jdbc.EmbeddedDataSource;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.bookflights.dao.DAOFactory;

public class DatabaseManager {

    //http://www.ibm.com/developerworks/kr/library/os-ad-derbymyfaces/index.html
	// 로그 출력을 위한 선언
	static Logger logger = Logger.getLogger(DatabaseManager.class
			.getCanonicalName());
	
    private static EmbeddedDataSource eds;

    // We want to keep the same connection for a given thread
    // as long as we're in the same transaction
    private static ThreadLocal<Connection> tranConnection = new ThreadLocal();
    
	// 데이터베이스 환경 초기화
	static void initDatabaseEnviroments(int derbyDriverType) {
		// DB프로퍼티설정
		Properties dbProperties = getDBProperties();
		String dbUrl = null;
		logger.log(Level.INFO, dbUrl);

		if (derbyDriverType == DAOFactory.DERBY_EMBEDDED_DRIVER) {
			//getDatabaseUrl("memory:");
                       // String userdir = System.getProperty("user.home");
                     //   String dbname = userdir + "/" + "FREELEC";
			initEmbeddedDataSource("memory:FREELEC");
			Connection connection = null;
			try {
			    connection = DatabaseManager.getConnection();
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

		}else if(derbyDriverType == DAOFactory.DERBY_CLIENT_DRIVER){
//			dbConnection = serverControl.getConnection(dbProperties);
//			serverControl.testConnection(dbConnection);
		}


	}
	
	private static Properties getDBProperties() {
		Properties properties = new Properties();
		//데이터베이스 FREELEC에서 사용되는 사용자와 패스워드 설정
		properties.put("user", Constants.DERBY_DATABASE.STRING_DB_USER);
		properties.put("password", Constants.DERBY_DATABASE.STRING_DB_PASSWORD);
		logger.log(Level.INFO, "dbProperties" + properties);
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		return properties;
	}
	
	public static String getDatabaseUrl(String str) {
            String userdir = System.getProperty("user.home");

		String dbUrl = userdir + Constants.DERBY_DATABASE.STRING_PROTOOL + str
				+ Constants.DERBY_DATABASE.STRING_DATABASE_NAME;
		logger.log(Level.INFO, dbUrl);	

		return dbUrl;
	}
	
	public static void initEmbeddedDataSource(String dbUrl) {
		eds = new EmbeddedDataSource();
		eds.setDatabaseName(dbUrl);
		eds.setUser(Constants.DERBY_DATABASE.STRING_DB_USER);
		eds.setPassword(Constants.DERBY_DATABASE.STRING_DB_PASSWORD);
		eds.setConnectionAttributes("create=true");   
		logger.log(Level.INFO, eds.getDatabaseName());	
		
	}

 
    public static synchronized void beginTransaction() throws Exception {
        if ( tranConnection.get() != null ) {
            throw new Exception("This thread is already in a transaction");
        }
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        tranConnection.set(conn);
    }
    
    public static void commitTransaction() throws Exception {
        if ( tranConnection.get() == null ) {
            throw new Exception("Can't commit: this thread isn't currently in a " +
                    "transaction");
        }
        tranConnection.get().commit();
        tranConnection.set(null);
    }
    
    /** get a connection */
    public static Connection getConnection() throws Exception {
        if ( tranConnection.get() != null ) {
            return tranConnection.get();
        } else {
            return eds.getConnection();
        }
    }
    
    public static void rollbackTransaction() throws Exception {
        if ( tranConnection.get() == null ) {
            throw new Exception("Can't rollback: this thread isn't currently in a " +
                    "transaction");
        }
        tranConnection.get().rollback();
        tranConnection.set(null);
    }
    
	public static void testConnection(Connection conn){

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

	    public static void logSql() throws Exception {
	        executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(" +
	            "'derby.language.logStatementText', 'true')");
	    }
	    /**
	     * Helper wrapper around boilerplate JDBC code.  Execute a statement
	     * that doesn't return results using a PreparedStatment, and returns 
	     * the number of rows affected
	     */
	    public static int executeUpdate(String statement) 
	            throws Exception {
	        Connection conn = getConnection();
	        try {
	           PreparedStatement ps = conn.prepareStatement(statement);
	           return ps.executeUpdate();
	        } finally {
	            releaseConnection(conn);
	        }
	    }
	    
	    /**
	     * Helper wrapper around boilerplat JDBC code.  Execute a statement
	     * that returns results using a PreparedStatement that takes no 
	     * parameters (you're on your own if you're binding parameters).
	     *
	     * @return the results from the query
	     */
	    public static ResultSet executeQueryNoParams(Connection conn, 
	            String statement) throws Exception {
	       PreparedStatement ps = conn.prepareStatement(statement);
	       return ps.executeQuery();
	    }
	    public static void releaseConnection(Connection conn) throws Exception {
	        // We don't close the connection while we're in a transaction,
	        // as it needs to be used by others in the same transaction context
	        if ( tranConnection.get() == null ) {
	            conn.close();
	        }
	    }
	    
	    public static void importSQLScript(Connection conn, InputStream in) throws SQLException
	    {
	            Scanner s = new Scanner(in);
	            s.useDelimiter("(;(\r)?\n)|(--\n)");
	            Statement st = null;
	            try
	            {
	                    st = conn.createStatement();
	                    while (s.hasNext())
	                    {
	                            String line = s.next();
	                            if (line.startsWith("/*!") && line.endsWith("*/"))
	                            {
	                                    int i = line.indexOf(' ');
	                                    line = line.substring(i + 1, line.length() - " */".length());
	                            }

	                            if (line.trim().length() > 0)
	                            {
	                                    st.execute(line);
	                            }
	                    }
	            }
	            finally
	            {
	                    if (st != null) st.close();
	            }
	    }
}
