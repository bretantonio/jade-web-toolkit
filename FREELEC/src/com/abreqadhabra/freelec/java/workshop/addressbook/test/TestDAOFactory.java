package com.abreqadhabra.freelec.java.workshop.addressbook.test;

import java.util.List;

import com.abreqadhabra.freelec.java.workshop.addressbook.dao.AddressDAO;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.Address;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.ListEntry;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.javadb.SimpleNetworkServer;

public class TestDAOFactory {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
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

//		// create a new customer
//		int newCustNo = addressDAO.insertCustomer(...);
//
//		// Find a customer object. Get the Transfer Object.
//		Customer cust = custDAO.findCustomer(...);
//
//		// modify the values in the Transfer Object.
//		cust.setAddress(...);
//		cust.setEmail(...);
//		// update the customer object using the DAO
//		custDAO.updateCustomer(cust);
//
//		// delete a customer object
//		custDAO.deleteCustomer(...);
//		// select all customers in the same city 
//		Customer criteria=new Customer();
//		criteria.setCity("New York");
//		Collection customersList = 
//		  custDAO.selectCustomersTO(criteria);
//		// returns customersList - collection of Customer
//		// Transfer Objects. iterate through this collection to
//		// get values.
	}

}
