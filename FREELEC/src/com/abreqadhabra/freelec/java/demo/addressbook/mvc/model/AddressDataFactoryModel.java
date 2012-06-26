package com.abreqadhabra.freelec.java.demo.addressbook.mvc.model;

import java.io.Serializable;
/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/16/00 8:55:31 PM)
 * @author: Scott Stanchfield
 */
public interface AddressDataFactoryModel extends Serializable {

	public AddressDataModel create();

	public AddressDataModel create(AddressDataModel address);
}
