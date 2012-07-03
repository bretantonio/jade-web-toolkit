package com.abreqadhabra.freelec.java.workshop.addressbook.test;

import java.util.List;

import com.abreqadhabra.freelec.java.demo.addressbook.bin.dao.AddressFrame;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.AddressDAO;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.Address;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.ListEntry;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.javadb.JavaDBServerThread;

public class JavaDBThreadTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread t = new Thread(new JavaDBServerThread());
		        t.start();

		        testDAOFactory();
		        
		        testAddressFrame();
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
		AddressDAO addressDAO = 
				derbyFactory.getAddressDAO();
		
		List<Address> addressList = addressDAO.getAddressList();
		
		System.out.println(addressList);
		
		
		List<ListEntry> entries = addressDAO.getListEntry();

		
		System.out.println(entries);
		
		Address address = addressDAO.getAddress(1);
		
		System.out.println(address);	    
	}

}
