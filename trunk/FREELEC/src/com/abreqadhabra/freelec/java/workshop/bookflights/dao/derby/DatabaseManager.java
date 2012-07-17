package com.abreqadhabra.freelec.java.workshop.bookflights.dao.derby;

import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.jdbc.EmbeddedDataSource;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.bookflights.dao.DAOFactory;

public class DatabaseManager {

	// 로그 출력을 위한 선언
	static Logger logger = Logger.getLogger(DatabaseManager.class
			.getCanonicalName());
	
    private static EmbeddedDataSource eds;

    // We want to keep the same connection for a given thread
    // as long as we're in the same transaction
    private static ThreadLocal<Connection> tranConnection = new ThreadLocal();
    
	// 데이터베이스 환경 초기화
	void initDatabaseEnviroments(int derbyDriverType) {
		// DB프로퍼티설정
		Properties dbProperties = getDBProperties();
		String dbUrl = getDatabaseUrl();

		if (derbyDriverType == DAOFactory.DERBY_EMBEDDED_DRIVER) {
			initEmbeddedDataSource(dbUrl, dbProperties);
		}else if(derbyDriverType == DAOFactory.DERBY_CLIENT_DRIVER){
//			dbConnection = serverControl.getConnection(dbProperties);
//			serverControl.testConnection(dbConnection);
		}

	}
	
	private Properties getDBProperties() {
		Properties properties = new Properties();
		//데이터베이스 FREELEC에서 사용되는 사용자와 패스워드 설정
		properties.put("user", Constants.DERBY_DATABASE.STRING_DB_USER);
		properties.put("password", Constants.DERBY_DATABASE.STRING_DB_PASSWORD);
		logger.log(Level.INFO, "dbProperties" + properties);
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		return properties;
	}
	
	public String getDatabaseUrl() {
		String dbUrl = Constants.DERBY_DATABASE.STRING_PROTOOL
				+ Constants.DERBY_DATABASE.STRING_DATABASE_NAME;
		return dbUrl;
	}
	
	public static void initEmbeddedDataSource(String dbUrl, Properties dbProperties) {
		eds = new EmbeddedDataSource();
		eds.setDatabaseName(Constants.DERBY_DATABASE.STRING_DATABASE_NAME);
		eds.setUser(Constants.DERBY_DATABASE.STRING_DB_USER);
		eds.setPassword(Constants.DERBY_DATABASE.STRING_DB_PASSWORD);
		eds.setCreateDatabase("create");   
		logger.log(Level.INFO, eds.getDatabaseName());
		eds.
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
}
