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
 * Creation date: (1/16/00 4:55:43 PM)
 * @author: Scott Stanchfield
 */
public class PhoneApplication1 extends Frame implements Serializable {
	private AddressBookUI ivjbookUI = null;
	private Panel ivjContentsPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private HashAddressBook ivjhashAddressBook = null;
	private SimpleAddressData ivjsimpleAddressData = null;
	private SimpleAddressDataFactory ivjsimpleAddressDataFactory = null;

class IvjEventHandler implements java.awt.event.WindowListener {
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == PhoneApplication1.this) 
				connEtoC1(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};

	/**
	 * PhoneApplication1 constructor comment.
	 */
	public PhoneApplication1() {
		super();
		initialize();
	}

	/**
	 * PhoneApplication1 constructor comment.
	 * @param title java.lang.String
	 */
	public PhoneApplication1(String title) {
		super(title);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			PhoneApplication1 aPhoneApplication1;
			aPhoneApplication1 = new PhoneApplication1();
			aPhoneApplication1.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			aPhoneApplication1.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Frame");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the bookUI property value.
	 * @return com.javadude.articles.vaddmvc1.AddressBookUI
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private AddressBookUI getbookUI() {
		if (ivjbookUI == null) {
			try {
				ivjbookUI = new com.javadude.articles.vaddmvc1.AddressBookUI();
				ivjbookUI.setName("bookUI");
				ivjbookUI.setBackground(java.awt.SystemColor.control);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjbookUI;
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
				ivjContentsPane.setLayout(new java.awt.GridLayout());
				getContentsPane().add(getbookUI(), getbookUI().getName());
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
	 * Return the hashAddressBook property value.
	 * @return com.javadude.articles.vaddmvc1.HashAddressBook
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private HashAddressBook gethashAddressBook() {
		if (ivjhashAddressBook == null) {
			try {
				ivjhashAddressBook = new com.javadude.articles.vaddmvc1.HashAddressBook();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjhashAddressBook;
	}

	/**
	 * Return the simpleAddressData property value.
	 * @return com.javadude.articles.vaddmvc1.SimpleAddressData
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private SimpleAddressData getsimpleAddressData() {
		if (ivjsimpleAddressData == null) {
			try {
				ivjsimpleAddressData = new com.javadude.articles.vaddmvc1.SimpleAddressData();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjsimpleAddressData;
	}

	/**
	 * Return the simpleAddressDataFactory property value.
	 * @return com.javadude.articles.vaddmvc1.SimpleAddressDataFactory
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private SimpleAddressDataFactory getsimpleAddressDataFactory() {
		if (ivjsimpleAddressDataFactory == null) {
			try {
				ivjsimpleAddressDataFactory = new com.javadude.articles.vaddmvc1.SimpleAddressDataFactory();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjsimpleAddressDataFactory;
	}

	/**
	 * connEtoC1:  (PhoneApplication1.window.windowClosing(java.awt.event.WindowEvent) --> PhoneApplication1.dispose()V)
	 * @param arg1 java.awt.event.WindowEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.awt.event.WindowEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.dispose();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP1SetTarget:  (hashAddressBook.this <--> bookUI.addressBookModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			getbookUI().setAddressBookModel(gethashAddressBook());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (simpleAddressData.this <--> bookUI.addressDataModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			getbookUI().setAddressDataModel(getsimpleAddressData());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetTarget:  (simpleAddressDataFactory.this <--> hashAddressBook.addressDataFactory)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			gethashAddressBook().setAddressDataFactory(getsimpleAddressDataFactory());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
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
		this.addWindowListener(ivjEventHandler);
		connPtoP1SetTarget();
		connPtoP2SetTarget();
		connPtoP3SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("PhoneApplication1");
			setLayout(new java.awt.BorderLayout());
			setBackground(java.awt.SystemColor.control);
			setSize(532, 267);
			add(getContentsPane(), "Center");
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}
}
