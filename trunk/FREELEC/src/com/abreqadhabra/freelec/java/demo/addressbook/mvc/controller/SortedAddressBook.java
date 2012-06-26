package com.abreqadhabra.freelec.java.demo.addressbook.mvc.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.abreqadhabra.freelec.java.demo.addressbook.mvc.model.AddressBookModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.model.AddressDataModel;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * An AddressBookModel filter.
 * This filter sorts the addresses property
 * Note: The code in this class and PatternedAddressBook
 *       is very similar -- in real life a superclass could
 *       contain the shared code, like we did with
 *       FilteredTableModel earlier in the article
 *
 * Creation date: (1/19/00 1:10:11 AM)
 * @author: Scott Stanchfield
 */
public class SortedAddressBook implements AddressBookModel {
	private AddressBookModel fieldRealAddressBook;
	protected transient PropertyChangeSupport propertyChange;

	// pcl is a holder for the PropertyChangeListener that
	//   we add to the real model. We need to hold a reference
	//   to it in case we change the real model later
	private PropertyChangeListener pcl;

	protected PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	// Simply delegate the add & find methods
	public void add(AddressDataModel data) {
		getRealAddressBook().add(data);
	}

	// support methods for the bound properties
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public AddressDataModel find(String name) {
		return getRealAddressBook().find(name);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public AddressDataModel[] getAddresses() {
		AddressDataModel[] data = getRealAddressBook().getAddresses();
		AddressDataModel[] sorted = new AddressDataModel[data.length];
		System.arraycopy(data, 0, sorted, 0, data.length);
	
		// do a simple inefficient bubble sort
		for (int i = 0; i < sorted.length - 1; i++)
			for (int j = i + 1; j < sorted.length; j++)
				if (sorted[i].getName().compareTo(sorted[j].getName()) > 0) {
					AddressDataModel temp = sorted[i];
					sorted[i] = sorted[j];
					sorted[j] = temp;
				}
		return sorted;
	}

	public AddressDataModel getAddresses(int index) {
		return getAddresses()[index];
	}

	// the realAddressBook property, tracking the real model
	//   that we filter
	public AddressBookModel getRealAddressBook() {
		return fieldRealAddressBook;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void setRealAddressBook(AddressBookModel realAddressBook) {
		AddressBookModel oldValue = fieldRealAddressBook;
		// if we used to have a value, remove the
		//   old property change listener
		if (oldValue != null)
			oldValue.removePropertyChangeListener(pcl);
	
		// set the property value
		fieldRealAddressBook = realAddressBook;
	
		// set up listener to delegate events
		if (pcl == null)
			pcl = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
			}
		};
		realAddressBook.addPropertyChangeListener(pcl);
	
		// report that the property has changed
		firePropertyChange("realAddressBook", oldValue, realAddressBook);
	}
}
