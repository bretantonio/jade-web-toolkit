package com.javadude.articles.vaddmvc1;

import java.beans.*;
import java.io.*;
import java.util.*;
/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/15/00 6:31:59 PM)
 * @author: Scott Stanchfield
 */
public class HashAddressBook implements Serializable, AddressBookModel {
	private Hashtable records = new Hashtable();
	private int count = 0;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private com.javadude.articles.vaddmvc1.AddressDataModel[] fieldAddresses = null;
	private AddressDataFactoryModel fieldAddressDataFactory = null;

	/**
	 * HashAddressBook constructor comment.
	 */
	public HashAddressBook() {
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
	 * Perform the add method.
	 * @param address com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	public void add(AddressDataModel address) {
		if (records.get(address.getName()) == null)
			count++;
		AddressDataModel copy =
			getAddressDataFactory().create(address);
		records.put(copy.getName(),copy);
		firePropertyChange("addresses",null, getAddresses());
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * Perform the find method.
	 * @return com.javadude.articles.vaddmvc1.AddressDataModel
	 * @param name java.lang.String[]
	 */
	public AddressDataModel find(String name) {
		AddressDataModel data = (AddressDataModel)records.get(name);
		if (data == null)
			throw new RuntimeException(name + " not found");
		return getAddressDataFactory().create(data);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Gets the addressDataFactory property (com.javadude.articles.vaddmvc1.AddressDataFactoryModel) value.
	 * @return The addressDataFactory property value.
	 * @see #setAddressDataFactory
	 */
	public AddressDataFactoryModel getAddressDataFactory() {
		return fieldAddressDataFactory;
	}

	/**
	 * Gets the addresses property (com.javadude.articles.vaddmvc1.AddressDataModel[]) value.
	 * @return The addresses property value.
	 */
	public com.javadude.articles.vaddmvc1.AddressDataModel[] getAddresses() {
		// convert the contents of the Hashtable into an array
		Enumeration e = records.elements();
		AddressDataModel[] result = new AddressDataModel[count];
		int i = 0;
		while (e.hasMoreElements())
			result[i++] = (AddressDataModel) e.nextElement();
		return result;
	}

	/**
	 * Gets the addresses index property (com.javadude.articles.vaddmvc1.AddressDataModel) value.
	 * @return The addresses property value.
	 * @param index The index value into the property array.
	 */
	public AddressDataModel getAddresses(int index) {
		return getAddresses()[index];
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * Sets the addressDataFactory property (com.javadude.articles.vaddmvc1.AddressDataFactoryModel) value.
	 * @param addressDataFactory The new value for the property.
	 * @see #getAddressDataFactory
	 */
	public void setAddressDataFactory(AddressDataFactoryModel addressDataFactory) {
		AddressDataFactoryModel oldValue = fieldAddressDataFactory;
		fieldAddressDataFactory = addressDataFactory;
		firePropertyChange("addressDataFactory", oldValue, addressDataFactory);
	}
}
