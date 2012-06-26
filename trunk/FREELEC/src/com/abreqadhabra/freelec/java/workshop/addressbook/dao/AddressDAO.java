package com.abreqadhabra.freelec.java.workshop.addressbook.dao;

import java.util.List;

import com.abreqadhabra.freelec.java.workshop.addressbook.domain.Address;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.ListEntry;

//Interface that all AddressDAO must support
public interface AddressDAO {

	public Address getAddress(int index);

	public int saveAddress(Address address);

	public List<ListEntry> getListEntry();

	public boolean editAddress(Address addres);

	public boolean removeAddress(int index);

	public List<Address> getAddressList();
	
	

	
}
