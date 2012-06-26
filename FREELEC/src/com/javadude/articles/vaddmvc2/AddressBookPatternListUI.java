package com.javadude.articles.vaddmvc2;

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
 * Insert the type's description here.
 * Creation date: (1/19/00 1:29:51 AM)
 * @author: Scott Stanchfield
 */
public class AddressBookPatternListUI extends Panel implements Serializable {
	private com.javadude.articles.vaddmvc1.AddressBookListUI ivjAddressBookListUI1 = null;
	private com.javadude.articles.vaddmvc1.AddressBookModel ivjanAddressBookModel = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Label ivjLabel1 = null;
	private Panel ivjPanel1 = null;
	private TextField ivjTextField1 = null;
	private Patterned ivjaPatternedThing = null;
	private boolean ivjConnPtoP2Aligning = false;
	protected transient PropertyChangeSupport propertyChange;

class IvjEventHandler implements java.awt.event.TextListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == AddressBookPatternListUI.this.getAddressBookListUI1() && (evt.getPropertyName().equals("addressBookModel"))) 
				connPtoP1SetSource();
			if (evt.getSource() == AddressBookPatternListUI.this.getaPatternedThing() && (evt.getPropertyName().equals("pattern"))) 
				connPtoP2SetTarget();
		};
		public void textValueChanged(java.awt.event.TextEvent e) {
			if (e.getSource() == AddressBookPatternListUI.this.getTextField1()) 
				connPtoP2SetSource();
		};
	};

	/**
	 * AddressBookPatternListUI constructor comment.
	 */
	public AddressBookPatternListUI() {
		super();
		initialize();
	}

	/**
	 * AddressBookPatternListUI constructor comment.
	 * @param layout java.awt.LayoutManager
	 */
	public AddressBookPatternListUI(LayoutManager layout) {
		super(layout);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			Frame frame = new java.awt.Frame();
			AddressBookPatternListUI aAddressBookPatternListUI;
			aAddressBookPatternListUI = new AddressBookPatternListUI();
			frame.add("Center", aAddressBookPatternListUI);
			frame.setSize(aAddressBookPatternListUI.getSize());
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
	 * Return the AddressBookListUI1 property value.
	 * @return com.javadude.articles.vaddmvc1.AddressBookListUI
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.javadude.articles.vaddmvc1.AddressBookListUI getAddressBookListUI1() {
		if (ivjAddressBookListUI1 == null) {
			try {
				ivjAddressBookListUI1 = new com.javadude.articles.vaddmvc1.AddressBookListUI();
				ivjAddressBookListUI1.setName("AddressBookListUI1");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressBookListUI1;
	}

	/**
	 * Return the anAddressBookModel property value.
	 * @return com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.javadude.articles.vaddmvc1.AddressBookModel getanAddressBookModel() {
		// user code begin {1}
		// user code end
		return ivjanAddressBookModel;
	}

	/**
	 * Set the anAddressBookModel to a new value.
	 * @param newValue com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setanAddressBookModel(com.javadude.articles.vaddmvc1.AddressBookModel newValue) {
		if (ivjanAddressBookModel != newValue) {
			try {
				com.javadude.articles.vaddmvc1.AddressBookModel oldValue = getanAddressBookModel();
				ivjanAddressBookModel = newValue;
				connPtoP1SetTarget();
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
	 * Return the aPatternedThing property value.
	 * @return com.javadude.articles.vaddmvc2.Patterned
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private Patterned getaPatternedThing() {
		// user code begin {1}
		// user code end
		return ivjaPatternedThing;
	}

	/**
	 * Set the aPatternedThing to a new value.
	 * @param newValue com.javadude.articles.vaddmvc2.Patterned
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setaPatternedThing(Patterned newValue) {
		if (ivjaPatternedThing != newValue) {
			try {
				com.javadude.articles.vaddmvc2.Patterned oldValue = getaPatternedThing();
				/* Stop listening for events from the current object */
				if (ivjaPatternedThing != null) {
					ivjaPatternedThing.removePropertyChangeListener(ivjEventHandler);
				}
				ivjaPatternedThing = newValue;
	
				/* Listen for events from the new object */
				if (ivjaPatternedThing != null) {
					ivjaPatternedThing.addPropertyChangeListener(ivjEventHandler);
				}
				connPtoP2SetTarget();
				firePropertyChange("patternedThing", oldValue, newValue);
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
	 * Return the Label1 property value.
	 * @return java.awt.Label
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Label getLabel1() {
		if (ivjLabel1 == null) {
			try {
				ivjLabel1 = new java.awt.Label();
				ivjLabel1.setName("Label1");
				ivjLabel1.setText("Pattern:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel1;
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
				ivjPanel1.setLayout(new java.awt.BorderLayout());
				getPanel1().add(getLabel1(), "West");
				getPanel1().add(getTextField1(), "Center");
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
	 * Return the TextField1 property value.
	 * @return java.awt.TextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.TextField getTextField1() {
		if (ivjTextField1 == null) {
			try {
				ivjTextField1 = new java.awt.TextField();
				ivjTextField1.setName("TextField1");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTextField1;
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 * @param listener java.beans.PropertyChangeListener
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * connPtoP1SetSource:  (anAddressBookModel.this <--> AddressBookListUI1.addressBookModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				setanAddressBookModel(getAddressBookListUI1().getAddressBookModel());
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
	 * connPtoP1SetTarget:  (anAddressBookModel.this <--> AddressBookListUI1.addressBookModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				if ((getanAddressBookModel() != null)) {
					getAddressBookListUI1().setAddressBookModel(getanAddressBookModel());
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
	 * connPtoP2SetSource:  (aPatternedThing.pattern <--> TextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP2Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP2Aligning = true;
				if ((getaPatternedThing() != null)) {
					getaPatternedThing().setPattern(getTextField1().getText());
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
	 * connPtoP2SetTarget:  (aPatternedThing.pattern <--> TextField1.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP2Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP2Aligning = true;
				if ((getaPatternedThing() != null)) {
					getTextField1().setText(getaPatternedThing().getPattern());
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
	public com.javadude.articles.vaddmvc1.AddressBookModel getAddressBookModel() {
		return getanAddressBookModel();
	}

	/**
	 * Method generated to support the promotion of the patternedThing attribute.
	 * @return com.javadude.articles.vaddmvc2.Patterned
	 */
	public Patterned getPatternedThing() {
		return getaPatternedThing();
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
		getAddressBookListUI1().addPropertyChangeListener(ivjEventHandler);
		getTextField1().addTextListener(ivjEventHandler);
		connPtoP1SetTarget();
		connPtoP2SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("AddressBookPatternListUI");
			setLayout(new java.awt.BorderLayout());
			setSize(197, 193);
			add(getPanel1(), "South");
			add(getAddressBookListUI1(), "Center");
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
	 * Method generated to support the promotion of the addressBookModel attribute.
	 * @param arg1 com.javadude.articles.vaddmvc1.AddressBookModel
	 */
	public void setAddressBookModel(com.javadude.articles.vaddmvc1.AddressBookModel arg1) {
		setanAddressBookModel(arg1);
	}

	/**
	 * Method generated to support the promotion of the patternedThing attribute.
	 * @param arg1 com.javadude.articles.vaddmvc2.Patterned
	 */
	public void setPatternedThing(Patterned arg1) {
		setaPatternedThing(arg1);
	}
}
