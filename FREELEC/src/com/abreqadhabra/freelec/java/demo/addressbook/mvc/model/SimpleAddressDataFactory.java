package com.abreqadhabra.freelec.java.demo.addressbook.mvc.model;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/16/00 9:04:46 PM)
 * @author: Scott Stanchfield
 */
public class SimpleAddressDataFactory implements AddressDataFactoryModel {

	/**
	 * SimpleAddressDataFactory constructor comment.
	 */
	public SimpleAddressDataFactory() {
		super();
	}

	/**
	 * create method comment.
	 */
	public AddressDataModel create() {
		return new SimpleAddressData();
	}

	/**
	 * create method comment.
	 */
	public AddressDataModel create(AddressDataModel address) {
		SimpleAddressData copy = new SimpleAddressData();
		copy.setName(address.getName());
		copy.setAddress(address.getAddress());
		copy.setCity(address.getCity());
		copy.setState(address.getState());
		copy.setCountry(address.getCountry());
		copy.setPostalCode(address.getPostalCode());
		copy.setHomePhone(address.getHomePhone());
		copy.setBusinessPhone(address.getBusinessPhone());
		return copy;
	}
}
