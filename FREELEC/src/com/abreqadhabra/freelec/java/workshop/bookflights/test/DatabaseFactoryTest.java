package com.abreqadhabra.freelec.java.workshop.bookflights.test;

import java.sql.Connection;
import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.bookflights.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.bookflights.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.bookflights.dao.derby.DatabaseManager;
import com.abreqadhabra.freelec.java.workshop.bookflights.database.DatabaseFactory;

public class DatabaseFactoryTest {

	// 로그 출력을 위한 선언
	static Logger logger = Logger.getLogger(DatabaseFactoryTest.class
			.getCanonicalName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			
			DatabaseFactory databaseFactory = DatabaseFactory
					.getDatabaseFactory(DatabaseFactory.LOCAL_DB_MODE);
			// 로컬모드
			databaseFactory.getDAOFactory(DatabaseFactory.LOCAL_DB_MODE,
					DAOFactory.DERBY_EMBEDDED_DRIVER);
			// 리모트모드
//			databaseFactory.getDAOFactory(DatabaseFactory.LOCAL_DB_MODE,
//					DAOFactory.DERBY_CLIENT_DRIVER);

			//DatabaseManager.logSql();
			Connection connection = DatabaseManager.getConnection();
			
			try {
			    
				DatabaseManager.beginTransaction();
				DatabaseManager.testConnection(connection);
				DatabaseManager.commitTransaction();
			} catch (Exception e) {
				DatabaseManager.rollbackTransaction();
				throw e;
			}
			
			
			
		} catch (Exception e) {
		}
	}
}
