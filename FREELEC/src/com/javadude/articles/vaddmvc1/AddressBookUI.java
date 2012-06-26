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
 * Creation date: (1/16/00 12:13:12 PM)
 * @author: Scott Stanchfield
 */
public class AddressBookUI extends Panel implements Serializable {
	private Button ivjaddButton = null;
	private Panel ivjbutonPanel = null;
	private AddressDataEntryFormUI ivjentryUI = null;
	private Button ivjfindButton = null;
	private AddressBookModel ivjanAddressBookModel = null;
	private AddressDataModel ivjanAddressDataModel = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient PropertyChangeSupport propertyChange;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddressBookUI.this.getfindButton()) 
				connEtoM1(e);
			if (e.getSource() == AddressBookUI.this.getaddButton()) 
				connEtoM3(e);
			if (e.getSource() == AddressBookUI.this.getokButton()) 
				connEtoM5(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == AddressBookUI.this.getentryUI() && (evt.getPropertyName().equals("model"))) 
				connPtoP1SetSource();
		};
	};
	private Panel ivjContentsPane = null;
	private Dialog ivjDialog1 = null;
	private Label ivjLabel1 = null;
	private Button ivjokButton = null;
	private Panel ivjPanel1 = null;
	private FlowLayout ivjPanel1FlowLayout = null;

	/**
	 * AddressBookUI constructor comment.
	 */
	public AddressBookUI() {
		super();
		initialize();
	}

	/**
	 * AddressBookUI constructor comment.
	 * @param layout java.awt.LayoutManager
	 */
	public AddressBookUI(LayoutManager layout) {
		super(layout);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			Frame frame = new java.awt.Frame();
			AddressBookUI aAddressBookUI;
			aAddressBookUI = new AddressBookUI();
			frame.add("Center", aAddressBookUI);
			frame.setSize(aAddressBookUI.getSize());
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
	 * Return the addButton property value.
	 * @return java.awt.Button
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Button getaddButton() {
		if (ivjaddButton == null) {
			try {
				ivjaddButton = new java.awt.Button();
				ivjaddButton.setName("addButton");
				ivjaddButton.setLabel("Add");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjaddButton;
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
				ivjanAddressBookModel = newValue;
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
				ivjanAddressDataModel = newValue;
				connPtoP1SetTarget();
				firePropertyChange("addressDataModel", oldValue, newValue);
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
	 * Return the butonPanel property value.
	 * @return java.awt.Panel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Panel getbutonPanel() {
		if (ivjbutonPanel == null) {
			try {
				ivjbutonPanel = new java.awt.Panel();
				ivjbutonPanel.setName("butonPanel");
				ivjbutonPanel.setLayout(new java.awt.FlowLayout());
				getbutonPanel().add(getfindButton(), getfindButton().getName());
				getbutonPanel().add(getaddButton(), getaddButton().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjbutonPanel;
	}

	/**
	 * Return the ContentsPane property value.
	 * @return java.awt.Panel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Panel getContentsPane() {
		if (ivjContentsPane == null) {
			try {
				ivjContentsPane = new java.awt.Panel();
				ivjContentsPane.setName("ContentsPane");
				ivjContentsPane.setLayout(new java.awt.BorderLayout());
				getContentsPane().add(getPanel1(), "South");
				getContentsPane().add(getLabel1(), "North");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjContentsPane;
	}

	/**
	 * Return the Dialog1 property value.
	 * @return java.awt.Dialog
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Dialog getDialog1() {
		if (ivjDialog1 == null) {
			try {
				ivjDialog1 = new java.awt.Dialog(new java.awt.Frame());
				ivjDialog1.setName("Dialog1");
				ivjDialog1.setLayout(new java.awt.BorderLayout());
				ivjDialog1.setBounds(65, 335, 204, 95);
				getDialog1().add(getContentsPane(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDialog1;
	}

	/**
	 * Return the entryUI property value.
	 * @return com.javadude.articles.vaddmvc1.AddressDataEntryFormUI
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private AddressDataEntryFormUI getentryUI() {
		if (ivjentryUI == null) {
			try {
				ivjentryUI = new com.javadude.articles.vaddmvc1.AddressDataEntryFormUI();
				ivjentryUI.setName("entryUI");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjentryUI;
	}

	/**
	 * Return the findButton property value.
	 * @return java.awt.Button
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Button getfindButton() {
		if (ivjfindButton == null) {
			try {
				ivjfindButton = new java.awt.Button();
				ivjfindButton.setName("findButton");
				ivjfindButton.setLabel("Find");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjfindButton;
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
				ivjLabel1.setFont(new java.awt.Font("serif", 3, 24));
				ivjLabel1.setAlignment(java.awt.Label.CENTER);
				ivjLabel1.setText("Name not found!");
				ivjLabel1.setForeground(java.awt.Color.red);
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
	 * Return the okButton property value.
	 * @return java.awt.Button
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Button getokButton() {
		if (ivjokButton == null) {
			try {
				ivjokButton = new java.awt.Button();
				ivjokButton.setName("okButton");
				ivjokButton.setLabel("Ok");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjokButton;
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
				ivjPanel1.setLayout(getPanel1FlowLayout());
				getPanel1().add(getokButton(), getokButton().getName());
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
	 * Return the Panel1FlowLayout property value.
	 * @return java.awt.FlowLayout
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.FlowLayout getPanel1FlowLayout() {
		java.awt.FlowLayout ivjPanel1FlowLayout = null;
		try {
			/* Create part */
			ivjPanel1FlowLayout = new java.awt.FlowLayout();
			ivjPanel1FlowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjPanel1FlowLayout;
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
	 * connEtoM1:  (findButton.action.actionPerformed(java.awt.event.ActionEvent) --> anAddressBookModel.find(Ljava.lang.String;)Lcom.javadude.articles.vaddmvc1.AddressDataModel;)
	 * @return com.javadude.articles.vaddmvc1.AddressDataModel
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private AddressDataModel connEtoM1(java.awt.event.ActionEvent arg1) {
		com.javadude.articles.vaddmvc1.AddressDataModel connEtoM1Result = null;
		try {
			// user code begin {1}
			// user code end
			if ((getanAddressDataModel() != null)) {
				connEtoM1Result = getanAddressBookModel().find(getanAddressDataModel().getName());
			}
			connEtoM2(connEtoM1Result);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			connEtoM4(ivjExc);
		}
		return connEtoM1Result;
	}

	/**
	 * connEtoM2:  ( (findButton,action.actionPerformed(java.awt.event.ActionEvent) --> anAddressBookModel,find(Ljava.lang.String;)Lcom.javadude.articles.vaddmvc1.AddressDataModel;).normalResult --> anAddressDataModel.this)
	 * @param result com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM2(AddressDataModel result) {
		try {
			// user code begin {1}
			// user code end
			setanAddressDataModel(result);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM3:  (addButton.action.actionPerformed(java.awt.event.ActionEvent) --> anAddressBookModel.add(Lcom.javadude.articles.vaddmvc1.AddressDataModel;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM3(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			if ((getanAddressDataModel() != null)) {
				getanAddressBookModel().add(getanAddressDataModel());
			}
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM4:  ( (findButton,action.actionPerformed(java.awt.event.ActionEvent) --> anAddressBookModel,find(Ljava.lang.String;)Lcom.javadude.articles.vaddmvc1.AddressDataModel;).exceptionOccurred --> Dialog1.show()V)
	 * @param exception java.lang.Throwable
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM4(java.lang.Throwable exception) {
		try {
			// user code begin {1}
			// user code end
			getDialog1().show();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM5:  (okButton.action.actionPerformed(java.awt.event.ActionEvent) --> Dialog1.dispose()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM5(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getDialog1().dispose();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP1SetSource:  (anAddressDataModel.this <--> entryUI.model)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				setanAddressDataModel(getentryUI().getModel());
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
	 * connPtoP1SetTarget:  (anAddressDataModel.this <--> entryUI.model)
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
					getentryUI().setModel(getanAddressDataModel());
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
	 * Method generated to support the promotion of the addressDataModel attribute.
	 * @return com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	public AddressDataModel getAddressDataModel() {
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
		getentryUI().addPropertyChangeListener(ivjEventHandler);
		getfindButton().addActionListener(ivjEventHandler);
		getaddButton().addActionListener(ivjEventHandler);
		getokButton().addActionListener(ivjEventHandler);
		connPtoP1SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("AddressBookUI");
			setLayout(new java.awt.BorderLayout());
			setSize(292, 234);
			add(getbutonPanel(), "South");
			add(getentryUI(), "Center");
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
	public void setAddressBookModel(AddressBookModel arg1) {
		setanAddressBookModel(arg1);
	}

	/**
	 * Method generated to support the promotion of the addressDataModel attribute.
	 * @param arg1 com.javadude.articles.vaddmvc1.AddressDataModel
	 */
	public void setAddressDataModel(AddressDataModel arg1) {
		setanAddressDataModel(arg1);
	}
}
