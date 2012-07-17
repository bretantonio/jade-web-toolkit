package com.abreqadhabra.freelec.java.workshop.bookflights.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.bookflights.dao.derby.DerbyDAOFactoryImpl;

//Abstract class DAO Factory
public abstract class DAOFactory {

	// 로그 출력을 위한 선언
	static Logger logger = Logger.getLogger(DAOFactory.class.getCanonicalName());

	// List of DAO types supported by the factory
	public static final int DERBY_EMBEDDED_DRIVER = 0;
	public static final int DERBY_CLIENT_DRIVER = 1;
	public static final int ORACLE = 2;

	// There will be a method for each DAO that can be
	// created. The concrete factories will have to
	// implement these methods.

	
	public static DAOFactory getDAOFactory(int whichFactory) {

		switch (whichFactory) {
		case DERBY_EMBEDDED_DRIVER:
			logger.log(Level.INFO, "["+DERBY_EMBEDDED_DRIVER+"] Derby Embedded Server로 접속합니다.");
			return new DerbyDAOFactoryImpl(DERBY_EMBEDDED_DRIVER);
		case DERBY_CLIENT_DRIVER:
			logger.log(Level.INFO, "["+DERBY_CLIENT_DRIVER+"] Derby Network Server로 접속합니다.");	
			return new DerbyDAOFactoryImpl(DERBY_CLIENT_DRIVER);
		case ORACLE:
			logger.log(Level.INFO, "["+ORACLE+"] 오라클 데이터베이스로 접속합니다.");			
			return null;
		default:
			return null;
		}
	}


	public abstract BookFlightsDAO getBookFlightsDAO() ;
	public abstract void shutdown() ;
}
	