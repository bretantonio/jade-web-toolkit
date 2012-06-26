package com.abreqadhabra.freelec.java.demo.addressbook.mvc.model;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/15/00 8:12:21 PM)
 * @author: Scott Stanchfield
 */
import java.io.Serializable;

public interface AddressDataModel extends Serializable {

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) ;

	public java.lang.String getAddress() ;

	public java.lang.String getBusinessPhone() ;

	public java.lang.String getCity() ;

	public java.lang.String getCountry() ;

	public java.lang.String getHomePhone() ;

	public java.lang.String getName() ;

	public java.lang.String getPostalCode() ;

	public java.lang.String getState() ;

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) ;

	public void setAddress(java.lang.String address) ;

	public void setBusinessPhone(java.lang.String businessPhone) ;

	public void setCity(java.lang.String city) ;

	public void setCountry(java.lang.String country) ;

	public void setHomePhone(java.lang.String homePhone) ;

	public void setName(java.lang.String name) ;

	public void setPostalCode(java.lang.String postalCode) ;

	public void setState(java.lang.String state) ;
}
