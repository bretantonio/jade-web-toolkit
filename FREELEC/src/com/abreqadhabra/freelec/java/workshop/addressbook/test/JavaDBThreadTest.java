package com.abreqadhabra.freelec.java.workshop.addressbook.test;

import java.io.IOException;
import java.rmi.RemoteException;

import com.abreqadhabra.freelec.java.demo.addressbook.bin.dao.AddressFrame;
import com.abreqadhabra.freelec.java.workshop.addressbook.client.ClientController;
import com.abreqadhabra.freelec.java.workshop.addressbook.client.data.DatabaseModeFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.FlightServiceDAO;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.db.JavaDBServerThread;

public class JavaDBThreadTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread t = new Thread(new JavaDBServerThread());
		        t.start();

		       // testDAOFactory();

		      //  testAddressFrame();
	}

	
	private static void testAddressFrame() {
	    AddressFrame app = new AddressFrame();
		app.setVisible(true);
		
	}


	
	private static void testDAOFactory() {
		// create the required DAO Factory
		DAOFactory derbyFactory =   
		  DAOFactory.getDAOFactory(DAOFactory.DERBY);

		// Create a DAO
		//FlightServiceDAO flightServiceDAO = derbyFactory.getFlightServiceDAO();
		    DatabaseModeFactory factory = DatabaseModeFactory.getDatabaseFactory(DatabaseModeFactory.EMBEDED_MODE);

		FlightServiceDAO flightServiceDAO=null;
	
		    flightServiceDAO = (FlightServiceDAO) factory.getDAOFactory(DatabaseModeFactory.EMBEDED_MODE, DAOFactory.DERBY).getFlightServiceDAO();
	
		 
		flightServiceDAO.getFieldInfos();
		System.out.println(flightServiceDAO.getRecordCount());
		System.out.println(flightServiceDAO.getRecord(1));
		System.out.println(flightServiceDAO.findByFlightNumber("KE1231"));
		System.out.println(flightServiceDAO.findByFlightNumber("OZ8921").getRecordNumber());
/*		List<Address> addressList = addressDAO.getAddressList();
		
		System.out.println(addressList);
		
		
		List<ListEntry> entries = addressDAO.getListEntry();

		
		System.out.println(entries);
		
		Address address = addressDAO.getAddress(1);
		
		System.out.println(address);	  */  
	}

}
