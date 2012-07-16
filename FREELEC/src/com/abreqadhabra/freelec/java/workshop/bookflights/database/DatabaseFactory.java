package com.abreqadhabra.freelec.java.workshop.bookflights.database;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.bookflights.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.bookflights.database.local.LocalDatabaseFactoryImpl;

public abstract class DatabaseFactory {
    
	// 로그 출력을 위한 선언
	static Logger logger = Logger.getLogger(DatabaseFactory.class.getCanonicalName());

    // List of DAO types supported by the factory
    public static final int LOCAL_DB_MODE = 0;
    
    public static final int REMOTE_DB_MODE = 1;
    
    /**
     * This method instantiates the DAO class that is used in this
     * applications deployment environment to access the data.
     */
    public abstract DAOFactory getDAOFactory(int whichDatabaseFactory, int whichDAOFactory);
    
	public static DatabaseFactory getDatabaseFactory(int whichDatabaseFactory) {
		switch (whichDatabaseFactory) {
		case LOCAL_DB_MODE:
			logger.log(Level.INFO, "로컬 데이터베이스 모드로 동작합니다.");
			return (DatabaseFactory) new LocalDatabaseFactoryImpl();
		case REMOTE_DB_MODE:
			logger.log(Level.INFO, "원격 데이터베이스 모드로 동작합니다.");
			// return new RemoteDataDAOFactory();
		default:
			return null;
		}
	}
    
}