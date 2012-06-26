package com.abreqadhabra.freelec.java.demo.addressbook.mvc.model;

import java.beans.*;
import java.io.*;
/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/15/00 5:55:14 PM)
 * @author: Scott Stanchfield
 */
public class SimpleAddressData implements Serializable, AddressDataModel {
	private java.lang.String fieldName = new String();
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private java.lang.String fieldAddress = new String();
	private java.lang.String fieldCity = new String();
	private java.lang.String fieldState = new String();
	private java.lang.String fieldCountry = new String();
	private java.lang.String fieldPostalCode = new String();
	private java.lang.String fieldBusinessPhone = new String();
	private java.lang.String fieldHomePhone = new String();

	/**
	 * SimpleAddressData constructor comment.
	 */
	public SimpleAddressData() {
		super();
	}

	/**
	 * Accessor for the propertyChange field.
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Gets the address property (java.lang.String) value.
	 * @return The address property value.
	 * @see #setAddress
	 */
	public java.lang.String getAddress() {
		return fieldAddress;
	}

	/**
	 * Gets the businessPhone property (java.lang.String) value.
	 * @return The businessPhone property value.
	 * @see #setBusinessPhone
	 */
	public java.lang.String getBusinessPhone() {
		return fieldBusinessPhone;
	}

	/**
	 * Gets the city property (java.lang.String) value.
	 * @return The city property value.
	 * @see #setCity
	 */
	public java.lang.String getCity() {
		return fieldCity;
	}

	/**
	 * Gets the country property (java.lang.String) value.
	 * @return The country property value.
	 * @see #setCountry
	 */
	public java.lang.String getCountry() {
		return fieldCountry;
	}

	/**
	 * Gets the homePhone property (java.lang.String) value.
	 * @return The homePhone property value.
	 * @see #setHomePhone
	 */
	public java.lang.String getHomePhone() {
		return fieldHomePhone;
	}

	/**
	 * Gets the name property (java.lang.String) value.
	 * @return The name property value.
	 * @see #setName
	 */
	public java.lang.String getName() {
		return fieldName;
	}

	/**
	 * Gets the postalCode property (java.lang.String) value.
	 * @return The postalCode property value.
	 * @see #setPostalCode
	 */
	public java.lang.String getPostalCode() {
		return fieldPostalCode;
	}

	/**
	 * Gets the state property (java.lang.String) value.
	 * @return The state property value.
	 * @see #setState
	 */
	public java.lang.String getState() {
		return fieldState;
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * Sets the address property (java.lang.String) value.
	 * @param address The new value for the property.
	 * @see #getAddress
	 */
	public void setAddress(java.lang.String address) {
		String oldValue = fieldAddress;
		fieldAddress = address;
		firePropertyChange("address", oldValue, address);
	}

	/**
	 * Sets the businessPhone property (java.lang.String) value.
	 * @param businessPhone The new value for the property.
	 * @see #getBusinessPhone
	 */
	public void setBusinessPhone(java.lang.String businessPhone) {
		String oldValue = fieldBusinessPhone;
		fieldBusinessPhone = businessPhone;
		firePropertyChange("businessPhone", oldValue, businessPhone);
	}

	/**
	 * Sets the city property (java.lang.String) value.
	 * @param city The new value for the property.
	 * @see #getCity
	 */
	public void setCity(java.lang.String city) {
		String oldValue = fieldCity;
		fieldCity = city;
		firePropertyChange("city", oldValue, city);
	}

	/**
	 * Sets the country property (java.lang.String) value.
	 * @param country The new value for the property.
	 * @see #getCountry
	 */
	public void setCountry(java.lang.String country) {
		String oldValue = fieldCountry;
		fieldCountry = country;
		firePropertyChange("country", oldValue, country);
	}

	/**
	 * Sets the homePhone property (java.lang.String) value.
	 * @param homePhone The new value for the property.
	 * @see #getHomePhone
	 */
	public void setHomePhone(java.lang.String homePhone) {
		String oldValue = fieldHomePhone;
		fieldHomePhone = homePhone;
		firePropertyChange("homePhone", oldValue, homePhone);
	}

	/**
	 * Sets the name property (java.lang.String) value.
	 * @param name The new value for the property.
	 * @see #getName
	 */
	public void setName(java.lang.String name) {
		String oldValue = fieldName;
		fieldName = name;
		firePropertyChange("name", oldValue, name);
	}

	/**
	 * Sets the postalCode property (java.lang.String) value.
	 * @param postalCode The new value for the property.
	 * @see #getPostalCode
	 */
	public void setPostalCode(java.lang.String postalCode) {
		String oldValue = fieldPostalCode;
		fieldPostalCode = postalCode;
		firePropertyChange("postalCode", oldValue, postalCode);
	}

	/**
	 * Sets the state property (java.lang.String) value.
	 * @param state The new value for the property.
	 * @see #getState
	 */
	public void setState(java.lang.String state) {
		String oldValue = fieldState;
		fieldState = state;
		firePropertyChange("state", oldValue, state);
	}
}
