package com.abreqadhabra.freelec.java.workshop.addressbook.dao.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.FlightServiceDAO;

public class DerbyDAOFactoryImpl extends DAOFactory {

	private Connection dbConnection = null;

	public DerbyDAOFactoryImpl(){
		dbConnection = getConnection();
	}
	 /**
	  *	gets a database connection
	  * If the dbUrl is trying to connect to the Derby NetNsSampleWork server using JCC
	  * then the jcc driver must be already loaded before calling this method,
	  * else there will be an error
	  * return jcc connection if no error, else null
	  */
	 public Connection getConnection() {
		 Connection conn = null;
		
			//conn = DriverManager.getConnection(dbUrl, properties);
			try {
				Class.forName(Constants.DERBY_DATABASE.STRING_DERBY_CLIENT_DRIVER_NAME)
				.newInstance();
				conn = DriverManager.getConnection("jdbc:derby://127.0.0.1:1527/"+Constants.DERBY_DATABASE.STRING_DATABASE_NAME);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  return conn;
	 }


	@Override
	public FlightServiceDAO getFlightServiceDAO() {
		// CloudscapeCustomerDAO implements CustomerDAO
		return new DerbyFlightServiceDAOImpl(dbConnection);
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}



}
