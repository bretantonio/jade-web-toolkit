package com.javadude.articles.vaddmvc1;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.awt.List;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/16/00 9:30:21 PM)
 * @author: Scott Stanchfield
 */
public class AddressBookListUI extends List implements Serializable {
	private AddressBookModel ivjanAddressBookModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient PropertyChangeSupport propertyChange;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == AddressBookListUI.this.getanAddressBookModel() && (evt.getPropertyName().equals("addresses"))) 
				connEtoC1(evt);
		};
	};

	/**
	 * AddressBookListUI constructor comment.
	 */
	public AddressBookListUI() {
		super();
		initialize();
	}

	/**
	 * AddressBookListUI constructor comment.
	 * @param rows int
	 */
	public AddressBookListUI(int rows) {
		super(rows);
	}

	/**
	 * AddressBookListUI constructor comment.
	 * @param rows int
	 * @param multipleMode boolean
	 */
	public AddressBookListUI(int rows, boolean multipleMode) {
		super(rows, multipleMode);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			Frame frame = new java.awt.Frame();
			AddressBookListUI aAddressBookListUI;
			aAddressBookListUI = new AddressBookListUI();
			frame.add("Center", aAddressBookListUI);
			frame.setSize(aAddressBookListUI.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of com.javadude.articles.vaddmvc1.AddressBookListUI");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the anAddressBookModel property value.
	 * @return com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private AddressBookModel getanAddressBookModel() {
		// user code begin {1}
		// user code end
		return ivjanAddressBookModel;
	}

	/**
	 * Set the anAddressBookModel to a new value.
	 * @param newValue com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setanAddressBookModel(AddressBookModel newValue) {
		if (ivjanAddressBookModel != newValue) {
			try {
				com.javadude.articles.vaddmvc1.AddressBookModel oldValue = getanAddressBookModel();
				/* Stop listening for events from the current object */
				if (ivjanAddressBookModel != null) {
					ivjanAddressBookModel.removePropertyChangeListener(ivjEventHandler);
				}
				ivjanAddressBookModel = newValue;
	
				/* Listen for events from the new object */
				if (ivjanAddressBookModel != null) {
					ivjanAddressBookModel.addPropertyChangeListener(ivjEventHandler);
				}
				firePropertyChange("addressBookModel", oldValue, newValue);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		};
		// user code begin {3}
		// user code end
	}

	/**
	 * Accessor for the propertyChange field.
	 * @return java.beans.PropertyChangeSupport
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 * @param listener java.beans.PropertyChangeListener
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * connEtoC1:  (anAddressBookModel.addresses --> AddressBookListUI.loadList([Lcom.javadude.articles.vaddmvc1.AddressDataModel;Ljava.awt.List;)V)
	 * @param arg1 java.beans.PropertyChangeEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.loadList(getanAddressBookModel().getAddresses(), this);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 * @param propertyName java.lang.String
	 * @param oldValue java.lang.Object
	 * @param newValue java.lang.Object
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Method generated to support the promotion of the addressBookModel attribute.
	 * @return com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	public AddressBookModel getAddressBookModel() {
		return getanAddressBookModel();
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}

	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("AddressBookListUI");
			setSize(125, 151);
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	public void loadList(AddressDataModel[] addresses, List list) {
		list.removeAll();
		
		for(int i = 0; i<addresses.length; i++)
			list.addItem(addresses[i].getName());
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 * @param listener java.beans.PropertyChangeListener
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * Method generated to support the promotion of the addressBookModel attribute.
	 * @param arg1 com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	public void setAddressBookModel(AddressBookModel arg1) {
		setanAddressBookModel(arg1);
	}
}
