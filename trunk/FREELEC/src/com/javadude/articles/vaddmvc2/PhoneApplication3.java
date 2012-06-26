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
 * Creation date: (1/19/00 1:36:49 AM)
 * @author: Scott Stanchfield
 */
public class PhoneApplication3 extends Frame implements Serializable {

class IvjEventHandler implements java.awt.event.WindowListener {
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == PhoneApplication3.this) 
				connEtoC1(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};
	private AddressBookPatternListUI ivjAddressBookPatternListUI1 = null;
	private com.javadude.articles.vaddmvc1.AddressBookUI ivjAddressBookUI1 = null;
	private Panel ivjContentsPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private com.javadude.articles.vaddmvc1.HashAddressBook ivjhashAddressBook = null;
	private PatternedAddressBook ivjpatternedAddressBook = null;
	private com.javadude.articles.vaddmvc1.SimpleAddressData ivjsimpleAddressData = null;
	private com.javadude.articles.vaddmvc1.SimpleAddressDataFactory ivjsimpleAddressDataFactory = null;

	/**
	 * PhoneApplication3 constructor comment.
	 */
	public PhoneApplication3() {
		super();
		initialize();
	}

	/**
	 * PhoneApplication3 constructor comment.
	 * @param title java.lang.String
	 */
	public PhoneApplication3(String title) {
		super(title);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			PhoneApplication3 aPhoneApplication3;
			aPhoneApplication3 = new PhoneApplication3();
			aPhoneApplication3.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			aPhoneApplication3.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Frame");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the AddressBookPatternListUI1 property value.
	 * @return com.javadude.articles.vaddmvc2.AddressBookPatternListUI
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private AddressBookPatternListUI getAddressBookPatternListUI1() {
		if (ivjAddressBookPatternListUI1 == null) {
			try {
				ivjAddressBookPatternListUI1 = new com.javadude.articles.vaddmvc2.AddressBookPatternListUI();
				ivjAddressBookPatternListUI1.setName("AddressBookPatternListUI1");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressBookPatternListUI1;
	}

	/**
	 * Return the AddressBookUI1 property value.
	 * @return com.javadude.articles.vaddmvc1.AddressBookUI
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.javadude.articles.vaddmvc1.AddressBookUI getAddressBookUI1() {
		if (ivjAddressBookUI1 == null) {
			try {
				ivjAddressBookUI1 = new com.javadude.articles.vaddmvc1.AddressBookUI();
				ivjAddressBookUI1.setName("AddressBookUI1");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressBookUI1;
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
				getContentsPane().add(getAddressBookPatternListUI1(), getAddressBookPatternListUI1().getName());
				getContentsPane().add(getAddressBookUI1(), getAddressBookUI1().getName());
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
	private com.javadude.articles.vaddmvc1.HashAddressBook gethashAddressBook() {
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
	 * Return the patternedAddressBook property value.
	 * @return com.javadude.articles.vaddmvc2.PatternedAddressBook
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private PatternedAddressBook getpatternedAddressBook() {
		if (ivjpatternedAddressBook == null) {
			try {
				ivjpatternedAddressBook = new com.javadude.articles.vaddmvc2.PatternedAddressBook();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjpatternedAddressBook;
	}

	/**
	 * Return the simpleAddressData property value.
	 * @return com.javadude.articles.vaddmvc1.SimpleAddressData
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.javadude.articles.vaddmvc1.SimpleAddressData getsimpleAddressData() {
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
	private com.javadude.articles.vaddmvc1.SimpleAddressDataFactory getsimpleAddressDataFactory() {
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
	 * connEtoC1:  (PhoneApplication3.window.windowClosing(java.awt.event.WindowEvent) --> PhoneApplication3.dispose()V)
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
	 * connPtoP1SetTarget:  (patternedAddressBook.this <--> AddressBookUI1.addressBookModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			getAddressBookUI1().setAddressBookModel(getpatternedAddressBook());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (patternedAddressBook.this <--> AddressBookPatternListUI1.addressBookModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			getAddressBookPatternListUI1().setAddressBookModel(getpatternedAddressBook());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetTarget:  (patternedAddressBook.this <--> AddressBookPatternListUI1.patternedThing)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			getAddressBookPatternListUI1().setPatternedThing(getpatternedAddressBook());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP4SetTarget:  (hashAddressBook.this <--> patternedAddressBook.realAddressBook)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP4SetTarget() {
		/* Set the target from the source */
		try {
			getpatternedAddressBook().setRealAddressBook(gethashAddressBook());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP5SetTarget:  (simpleAddressData.this <--> AddressBookUI1.addressDataModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP5SetTarget() {
		/* Set the target from the source */
		try {
			getAddressBookUI1().setAddressDataModel(getsimpleAddressData());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP6SetTarget:  (simpleAddressDataFactory.this <--> hashAddressBook.addressDataFactory)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP6SetTarget() {
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
		connPtoP4SetTarget();
		connPtoP5SetTarget();
		connPtoP6SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("PhoneApplication3");
			setLayout(new java.awt.BorderLayout());
			setSize(426, 240);
			add(getContentsPane(), "Center");
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}
}
