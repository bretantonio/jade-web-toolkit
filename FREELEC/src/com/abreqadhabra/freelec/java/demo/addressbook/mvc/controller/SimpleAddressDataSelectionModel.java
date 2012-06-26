package com.abreqadhabra.freelec.java.demo.addressbook.mvc.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.abreqadhabra.freelec.java.demo.addressbook.mvc.model.AddressBookModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.model.AddressDataModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.AddressDataSelectionModel;


/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * A simple selection model that tracks
 * and address in a phone book
 *
 * Creation date: (1/19/00 1:10:11 AM)
 * @author: Scott Stanchfield
 */
public class SimpleAddressDataSelectionModel implements PropertyChangeListener, AddressDataSelectionModel {
	protected transient PropertyChangeSupport propertyChange;
	private AddressDataModel fieldSelection;
	private AddressBookModel fieldModel;

	protected PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	// support methods for the bound properties
	public void addPropertyChangeListener(
			  PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}  

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	// standard bound property support omitted for brevity
	
	// model property keeps track of the model that contains the
	//   selection
	public AddressBookModel getModel() {
		return fieldModel;
	}

	// selection property tracks which address is currently
	//   selected
	public AddressDataModel getSelection() {
		return fieldSelection;
	}

	/** listen for changes in the model where the selection
	   *    comes from. If we find out that the selection is
	   *    no longer in the model, we kill it...
	   */
	public void propertyChange(PropertyChangeEvent evt) {
		// if the model's list of addresses changes,
		//   check to be sure that the selection is still
		//   there
		if (evt.getPropertyName().equals("addresses")) {
			AddressDataModel[] data = getModel().getAddresses();
			for (int i = 0; i < data.length; i++)
				if (getSelection().getName().equals(data[i].getName()))
					return;
				// if not found, clear the selection
			AddressDataModel sel = getSelection();
			sel.setAddress("");
			sel.setBusinessPhone("");
			sel.setCity("");
			sel.setCountry("");
			sel.setHomePhone("");
			sel.setName("");
			sel.setPostalCode("");
			sel.setState("");
		}
	}

	public void removePropertyChangeListener(
			  PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}  

	public void setModel(AddressBookModel model) {
		// if we had a model before, remove us as a listener
		if (fieldModel != null)
			fieldModel.removePropertyChangeListener(this);
		AddressBookModel oldValue = fieldModel;
		fieldModel = model;
		// add us as a listener so we can make sure the selection
		//   still exists in the model
		fieldModel.addPropertyChangeListener(this);
		firePropertyChange("model", oldValue, model);
	}

	public void setSelection(AddressDataModel selection) {
		AddressDataModel oldValue = fieldSelection;
		fieldSelection = selection;
		firePropertyChange("selection", oldValue, selection);
	}
}
