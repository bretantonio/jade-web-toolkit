package com.abreqadhabra.freelec.java.workshop.bookflights.dao;

import com.abreqadhabra.freelec.java.workshop.bookflights.dao.derby.DerbyDAOFactoryImpl;

//Abstract class DAO Factory
public abstract class DAOFactory {

	// List of DAO types supported by the factory
	public static final int DERBY = 1;
	public static final int ORACLE = 2;

	// There will be a method for each DAO that can be
	// created. The concrete factories will have to
	// implement these methods.

	public abstract BookFlightsDAO getBookFlightsDAO() ;
	
	public static DAOFactory getDAOFactory(int whichFactory) {

		switch (whichFactory) {
		case DERBY:
			return new DerbyDAOFactoryImpl();
		case ORACLE:
			return null;
		default:
			return null;
		}
	}

	public abstract void shutdown() ;

}
	