package com.javadude.articles.vaddmvc1;

import java.awt.*;
import java.awt.event.*;
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
 * Creation date: (1/15/00 8:32:45 PM)
 * @author: Scott Stanchfield
 */
public class AddressDataEntryFormUI extends Panel implements Serializable {
	private Label ivjAddressLabel1 = null;
	private TextField ivjAddressTextField1 = null;
	private AddressDataModel ivjanAddressDataModel = null;
	private Label ivjBusinessPhoneLabel1 = null;
	private TextField ivjBusinessPhoneTextField1 = null;
	private Label ivjCityLabel1 = null;
	private TextField ivjCityTextField1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private boolean ivjConnPtoP8Aligning = false;
	private Label ivjCountryLabel1 = null;
	private TextField ivjCountryTextField1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Label ivjHomePhoneLabel1 = null;
	private TextField ivjHomePhoneTextField1 = null;
	private Label ivjNameLabel1 = null;
	private TextField ivjNameTextField1 = null;
	private Panel ivjPanel1 = null;
	private Label ivjPostalCodeLabel1 = null;
	private TextField ivjPostalCodeTextField1 = null;
	private Label ivjStateLabel1 = null;
	private TextField ivjStateTextField1 = null;
	protected transient PropertyChangeSupport propertyChange;

class IvjEventHandler implements java.awt.event.TextListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("name"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("address"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("city"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("state"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("country"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("postalCode"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("homePhone"))) 
				connPtoP7SetTarget();
			if (evt.getSource() == AddressDataEntryFormUI.this.getanAddressDataModel() && (evt.getPropertyName().equals("businessPhone"))) 
				connPtoP8SetTarget();
		};
		public void textValueChanged(java.awt.event.TextEvent e) {
			if (e.getSource() == AddressDataEntryFormUI.this.getNameTextField1()) 
				connPtoP1SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getAddressTextField1()) 
				connPtoP2SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getCityTextField1()) 
				connPtoP3SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getStateTextField1()) 
				connPtoP4SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getCountryTextField1()) 
				connPtoP5SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getPostalCodeTextField1()) 
				connPtoP6SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getHomePhoneTextField1()) 
				connPtoP7SetSource();
			if (e.getSource() == AddressDataEntryFormUI.this.getBusinessPhoneTextField1()) 
				connPtoP8SetSource();
		};
	};

	/**
	 * AddressDataEntryFormUI constructor comment.
	 */
	public AddressDataEntryFormUI() {
		super();
		initialize();
	}

	/**
	 * AddressDataEntryFormUI constructor comment.
	 * @param layout java.awt.LayoutManager
	 */
	public AddressDataEntryFormUI(LayoutManager layout) {
		super(layout);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			Frame frame = new java.awt.Frame();
			AddressDataEntryFormUI aAddressDataEntryFormUI;
			aAddressDataEntryFormUI = new AddressDataEntryFormUI();
			frame.add("Center", aAddressDataEntryFormUI);
			frame.setSize(aAddressDataEntryFormUI.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Panel");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the AddressLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getAddressLabel1() {
		if (ivjAddressLabel1 == null) {
			try {
				ivjAddressLabel1 = new java.awt.Label();
				ivjAddressLabel1.setName("AddressLabel1");
				ivjAddressLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjAddressLabel1.setText("Address:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressLabel1;
	}

	/**
	 * Return the AddressTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getAddressTextField1() {
		if (ivjAddressTextField1 == null) {
			try {
				ivjAddressTextField1 = new java.awt.TextField();
				ivjAddressTextField1.setName("AddressTextField1");
				ivjAddressTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressTextField1;
	}

	/**
	 * Return the anAddressDataModel property value.
	 * @return com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private AddressDataModel getanAddressDataModel() {
		// user code begin {1}
		// user code end
		return ivjanAddressDataModel;
	}

	/**
	 * Set the anAddressDataModel to a new value.
	 * @param newValue com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setanAddressDataModel(AddressDataModel newValue) {
		if (ivjanAddressDataModel != newValue) {
			try {
				com.javadude.articles.vaddmvc1.AddressDataModel oldValue = getanAddressDataModel();
				/* Stop listening for events from the current object */
				if (ivjanAddressDataModel != null) {
					ivjanAddressDataModel.removePropertyChangeListener(ivjEventHandler);
				}
				ivjanAddressDataModel = newValue;
	
				/* Listen for events from the new object */
				if (ivjanAddressDataModel != null) {
					ivjanAddressDataModel.addPropertyChangeListener(ivjEventHandler);
				}
				connPtoP1SetTarget();
				connPtoP2SetTarget();
				connPtoP3SetTarget();
				connPtoP4SetTarget();
				connPtoP5SetTarget();
				connPtoP6SetTarget();
				connPtoP7SetTarget();
				connPtoP8SetTarget();
				firePropertyChange("model", oldValue, newValue);
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
	 * Return the BusinessPhoneLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getBusinessPhoneLabel1() {
		if (ivjBusinessPhoneLabel1 == null) {
			try {
				ivjBusinessPhoneLabel1 = new java.awt.Label();
				ivjBusinessPhoneLabel1.setName("BusinessPhoneLabel1");
				ivjBusinessPhoneLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjBusinessPhoneLabel1.setText("Business:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBusinessPhoneLabel1;
	}

	/**
	 * Return the BusinessPhoneTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getBusinessPhoneTextField1() {
		if (ivjBusinessPhoneTextField1 == null) {
			try {
				ivjBusinessPhoneTextField1 = new java.awt.TextField();
				ivjBusinessPhoneTextField1.setName("BusinessPhoneTextField1");
				ivjBusinessPhoneTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBusinessPhoneTextField1;
	}

	/**
	 * Return the CityLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getCityLabel1() {
		if (ivjCityLabel1 == null) {
			try {
				ivjCityLabel1 = new java.awt.Label();
				ivjCityLabel1.setName("CityLabel1");
				ivjCityLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjCityLabel1.setText("City:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCityLabel1;
	}

	/**
	 * Return the CityTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getCityTextField1() {
		if (ivjCityTextField1 == null) {
			try {
				ivjCityTextField1 = new java.awt.TextField();
				ivjCityTextField1.setName("CityTextField1");
				ivjCityTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCityTextField1;
	}

	/**
	 * Return the CountryLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getCountryLabel1() {
		if (ivjCountryLabel1 == null) {
			try {
				ivjCountryLabel1 = new java.awt.Label();
				ivjCountryLabel1.setName("CountryLabel1");
				ivjCountryLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjCountryLabel1.setText("Country:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCountryLabel1;
	}

	/**
	 * Return the CountryTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getCountryTextField1() {
		if (ivjCountryTextField1 == null) {
			try {
				ivjCountryTextField1 = new java.awt.TextField();
				ivjCountryTextField1.setName("CountryTextField1");
				ivjCountryTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCountryTextField1;
	}

	/**
	 * Return the HomePhoneLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getHomePhoneLabel1() {
		if (ivjHomePhoneLabel1 == null) {
			try {
				ivjHomePhoneLabel1 = new java.awt.Label();
				ivjHomePhoneLabel1.setName("HomePhoneLabel1");
				ivjHomePhoneLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjHomePhoneLabel1.setText("Home:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjHomePhoneLabel1;
	}

	/**
	 * Return the HomePhoneTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getHomePhoneTextField1() {
		if (ivjHomePhoneTextField1 == null) {
			try {
				ivjHomePhoneTextField1 = new java.awt.TextField();
				ivjHomePhoneTextField1.setName("HomePhoneTextField1");
				ivjHomePhoneTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjHomePhoneTextField1;
	}

	/**
	 * Return the NameLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getNameLabel1() {
		if (ivjNameLabel1 == null) {
			try {
				ivjNameLabel1 = new java.awt.Label();
				ivjNameLabel1.setName("NameLabel1");
				ivjNameLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjNameLabel1.setText("Name:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNameLabel1;
	}

	/**
	 * Return the NameTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getNameTextField1() {
		if (ivjNameTextField1 == null) {
			try {
				ivjNameTextField1 = new java.awt.TextField();
				ivjNameTextField1.setName("NameTextField1");
				ivjNameTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNameTextField1;
	}

	/**
	 * Return the Panel1 property value.
	 * @return java.awt.Panel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Panel getPanel1() {
		if (ivjPanel1 == null) {
			try {
				ivjPanel1 = new java.awt.Panel();
				ivjPanel1.setName("Panel1");
				ivjPanel1.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsNameLabel1 = new java.awt.GridBagConstraints();
				constraintsNameLabel1.gridx = 1; constraintsNameLabel1.gridy = 3;
				constraintsNameLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsNameLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getNameLabel1(), constraintsNameLabel1);
	
				java.awt.GridBagConstraints constraintsNameTextField1 = new java.awt.GridBagConstraints();
				constraintsNameTextField1.gridx = 2; constraintsNameTextField1.gridy = 3;
				constraintsNameTextField1.gridwidth = 24;
				constraintsNameTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsNameTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsNameTextField1.weightx = 1.0;
				constraintsNameTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getNameTextField1(), constraintsNameTextField1);
	
				java.awt.GridBagConstraints constraintsAddressLabel1 = new java.awt.GridBagConstraints();
				constraintsAddressLabel1.gridx = 1; constraintsAddressLabel1.gridy = 6;
				constraintsAddressLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsAddressLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getAddressLabel1(), constraintsAddressLabel1);
	
				java.awt.GridBagConstraints constraintsAddressTextField1 = new java.awt.GridBagConstraints();
				constraintsAddressTextField1.gridx = 2; constraintsAddressTextField1.gridy = 6;
				constraintsAddressTextField1.gridwidth = 21;
				constraintsAddressTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsAddressTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsAddressTextField1.weightx = 1.0;
				constraintsAddressTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getAddressTextField1(), constraintsAddressTextField1);
	
				java.awt.GridBagConstraints constraintsCityLabel1 = new java.awt.GridBagConstraints();
				constraintsCityLabel1.gridx = 1; constraintsCityLabel1.gridy = 7;
				constraintsCityLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsCityLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getCityLabel1(), constraintsCityLabel1);
	
				java.awt.GridBagConstraints constraintsCityTextField1 = new java.awt.GridBagConstraints();
				constraintsCityTextField1.gridx = 2; constraintsCityTextField1.gridy = 7;
				constraintsCityTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsCityTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsCityTextField1.weightx = 1.0;
				constraintsCityTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getCityTextField1(), constraintsCityTextField1);
	
				java.awt.GridBagConstraints constraintsStateLabel1 = new java.awt.GridBagConstraints();
				constraintsStateLabel1.gridx = 3; constraintsStateLabel1.gridy = 7;
				constraintsStateLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsStateLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getStateLabel1(), constraintsStateLabel1);
	
				java.awt.GridBagConstraints constraintsStateTextField1 = new java.awt.GridBagConstraints();
				constraintsStateTextField1.gridx = 4; constraintsStateTextField1.gridy = 7;
				constraintsStateTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsStateTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsStateTextField1.weightx = 1.0;
				constraintsStateTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getStateTextField1(), constraintsStateTextField1);
	
				java.awt.GridBagConstraints constraintsCountryLabel1 = new java.awt.GridBagConstraints();
				constraintsCountryLabel1.gridx = 1; constraintsCountryLabel1.gridy = 8;
				constraintsCountryLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsCountryLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getCountryLabel1(), constraintsCountryLabel1);
	
				java.awt.GridBagConstraints constraintsCountryTextField1 = new java.awt.GridBagConstraints();
				constraintsCountryTextField1.gridx = 2; constraintsCountryTextField1.gridy = 8;
				constraintsCountryTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsCountryTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsCountryTextField1.weightx = 1.0;
				constraintsCountryTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getCountryTextField1(), constraintsCountryTextField1);
	
				java.awt.GridBagConstraints constraintsPostalCodeLabel1 = new java.awt.GridBagConstraints();
				constraintsPostalCodeLabel1.gridx = 3; constraintsPostalCodeLabel1.gridy = 8;
				constraintsPostalCodeLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsPostalCodeLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getPostalCodeLabel1(), constraintsPostalCodeLabel1);
	
				java.awt.GridBagConstraints constraintsPostalCodeTextField1 = new java.awt.GridBagConstraints();
				constraintsPostalCodeTextField1.gridx = 4; constraintsPostalCodeTextField1.gridy = 8;
				constraintsPostalCodeTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsPostalCodeTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsPostalCodeTextField1.weightx = 1.0;
				constraintsPostalCodeTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getPostalCodeTextField1(), constraintsPostalCodeTextField1);
	
				java.awt.GridBagConstraints constraintsHomePhoneLabel1 = new java.awt.GridBagConstraints();
				constraintsHomePhoneLabel1.gridx = 1; constraintsHomePhoneLabel1.gridy = 11;
				constraintsHomePhoneLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsHomePhoneLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getHomePhoneLabel1(), constraintsHomePhoneLabel1);
	
				java.awt.GridBagConstraints constraintsHomePhoneTextField1 = new java.awt.GridBagConstraints();
				constraintsHomePhoneTextField1.gridx = 2; constraintsHomePhoneTextField1.gridy = 11;
				constraintsHomePhoneTextField1.gridwidth = 10;
				constraintsHomePhoneTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsHomePhoneTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsHomePhoneTextField1.weightx = 1.0;
				constraintsHomePhoneTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getHomePhoneTextField1(), constraintsHomePhoneTextField1);
	
				java.awt.GridBagConstraints constraintsBusinessPhoneLabel1 = new java.awt.GridBagConstraints();
				constraintsBusinessPhoneLabel1.gridx = 1; constraintsBusinessPhoneLabel1.gridy = 14;
				constraintsBusinessPhoneLabel1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsBusinessPhoneLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getBusinessPhoneLabel1(), constraintsBusinessPhoneLabel1);
	
				java.awt.GridBagConstraints constraintsBusinessPhoneTextField1 = new java.awt.GridBagConstraints();
				constraintsBusinessPhoneTextField1.gridx = 2; constraintsBusinessPhoneTextField1.gridy = 14;
				constraintsBusinessPhoneTextField1.gridwidth = 6;
				constraintsBusinessPhoneTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsBusinessPhoneTextField1.anchor = java.awt.GridBagConstraints.WEST;
				constraintsBusinessPhoneTextField1.weightx = 1.0;
				constraintsBusinessPhoneTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getPanel1().add(getBusinessPhoneTextField1(), constraintsBusinessPhoneTextField1);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPanel1;
	}

	/**
	 * Return the PostalCodeLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getPostalCodeLabel1() {
		if (ivjPostalCodeLabel1 == null) {
			try {
				ivjPostalCodeLabel1 = new java.awt.Label();
				ivjPostalCodeLabel1.setName("PostalCodeLabel1");
				ivjPostalCodeLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjPostalCodeLabel1.setText("Zip/Postal Code:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPostalCodeLabel1;
	}

	/**
	 * Return the PostalCodeTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getPostalCodeTextField1() {
		if (ivjPostalCodeTextField1 == null) {
			try {
				ivjPostalCodeTextField1 = new java.awt.TextField();
				ivjPostalCodeTextField1.setName("PostalCodeTextField1");
				ivjPostalCodeTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPostalCodeTextField1;
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
	 * Return the StateLabel1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getStateLabel1() {
		if (ivjStateLabel1 == null) {
			try {
				ivjStateLabel1 = new java.awt.Label();
				ivjStateLabel1.setName("StateLabel1");
				ivjStateLabel1.setFont(new java.awt.Font("serif", 3, 14));
				ivjStateLabel1.setText("State/Province:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStateLabel1;
	}

	/**
	 * Return the StateTextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getStateTextField1() {
		if (ivjStateTextField1 == null) {
			try {
				ivjStateTextField1 = new java.awt.TextField();
				ivjStateTextField1.setName("StateTextField1");
				ivjStateTextField1.setBackground(java.awt.SystemColor.window);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStateTextField1;
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 * @param listener java.beans.PropertyChangeListener
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * connPtoP1SetSource:  (anAddressDataModel.name <--> NameTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setName(getNameTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP1Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP1Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP1SetTarget:  (anAddressDataModel.name <--> NameTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getNameTextField1().setText(getanAddressDataModel().getName());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP1Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP1Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetSource:  (anAddressDataModel.address <--> AddressTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP2Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP2Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setAddress(getAddressTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP2Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP2Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (anAddressDataModel.address <--> AddressTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP2Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP2Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getAddressTextField1().setText(getanAddressDataModel().getAddress());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP2Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP2Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetSource:  (anAddressDataModel.city <--> CityTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP3Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP3Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setCity(getCityTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP3Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP3Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetTarget:  (anAddressDataModel.city <--> CityTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP3Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP3Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getCityTextField1().setText(getanAddressDataModel().getCity());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP3Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP3Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP4SetSource:  (anAddressDataModel.state <--> StateTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP4SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP4Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP4Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setState(getStateTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP4Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP4Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP4SetTarget:  (anAddressDataModel.state <--> StateTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP4SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP4Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP4Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getStateTextField1().setText(getanAddressDataModel().getState());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP4Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP4Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP5SetSource:  (anAddressDataModel.country <--> CountryTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP5SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP5Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP5Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setCountry(getCountryTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP5Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP5Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP5SetTarget:  (anAddressDataModel.country <--> CountryTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP5SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP5Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP5Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getCountryTextField1().setText(getanAddressDataModel().getCountry());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP5Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP5Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP6SetSource:  (anAddressDataModel.postalCode <--> PostalCodeTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP6SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP6Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP6Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setPostalCode(getPostalCodeTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP6Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP6Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP6SetTarget:  (anAddressDataModel.postalCode <--> PostalCodeTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP6SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP6Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP6Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getPostalCodeTextField1().setText(getanAddressDataModel().getPostalCode());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP6Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP6Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP7SetSource:  (anAddressDataModel.homePhone <--> HomePhoneTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP7SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP7Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP7Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setHomePhone(getHomePhoneTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP7Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP7Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP7SetTarget:  (anAddressDataModel.homePhone <--> HomePhoneTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP7SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP7Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP7Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getHomePhoneTextField1().setText(getanAddressDataModel().getHomePhone());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP7Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP7Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP8SetSource:  (anAddressDataModel.businessPhone <--> BusinessPhoneTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP8SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP8Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP8Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getanAddressDataModel().setBusinessPhone(getBusinessPhoneTextField1().getText());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP8Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP8Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP8SetTarget:  (anAddressDataModel.businessPhone <--> BusinessPhoneTextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP8SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP8Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP8Aligning = true;
				if ((getanAddressDataModel() != null)) {
					getBusinessPhoneTextField1().setText(getanAddressDataModel().getBusinessPhone());
				}
				// user code begin {2}
				// user code end
				ivjConnPtoP8Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP8Aligning = false;
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
	 * Method generated to support the promotion of the model attribute.
	 * @return com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	public AddressDataModel getModel() {
		return getanAddressDataModel();
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
		getNameTextField1().addTextListener(ivjEventHandler);
		getAddressTextField1().addTextListener(ivjEventHandler);
		getCityTextField1().addTextListener(ivjEventHandler);
		getStateTextField1().addTextListener(ivjEventHandler);
		getCountryTextField1().addTextListener(ivjEventHandler);
		getPostalCodeTextField1().addTextListener(ivjEventHandler);
		getHomePhoneTextField1().addTextListener(ivjEventHandler);
		getBusinessPhoneTextField1().addTextListener(ivjEventHandler);
		connPtoP1SetTarget();
		connPtoP2SetTarget();
		connPtoP3SetTarget();
		connPtoP4SetTarget();
		connPtoP5SetTarget();
		connPtoP6SetTarget();
		connPtoP7SetTarget();
		connPtoP8SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("AddressDataEntryFormUI");
			setLayout(new java.awt.BorderLayout());
			setSize(337, 239);
			add(getPanel1(), "North");
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 * @param listener java.beans.PropertyChangeListener
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * Method generated to support the promotion of the model attribute.
	 * @param arg1 com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	public void setModel(AddressDataModel arg1) {
		setanAddressDataModel(arg1);
	}
}
