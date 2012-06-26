package com.abreqadhabra.freelec.java.demo.addressbook.mvc.bin;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.FixedTableModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.OmitColumnTableModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.ReverseColumnTableModel;
/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/19/00 1:14:54 AM)
 * @author: Scott Stanchfield
 */
public class TableFilterTest extends JFrame {
	private FixedTableModel ivjfixedTableModel = null;
	private JPanel ivjJFrameContentPane = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JScrollPane ivjJScrollPane2 = null;
	private OmitColumnTableModel ivjomitColumnTableModel = null;
	private ReverseColumnTableModel ivjreverseColumnTableModel = null;
	private JTable ivjScrollPaneTable = null;
	private JTable ivjScrollPaneTable1 = null;

	/**
	 * TableFilterTest constructor comment.
	 */
	public TableFilterTest() {
		super();
		initialize();
	}

	/**
	 * TableFilterTest constructor comment.
	 * @param title java.lang.String
	 */
	public TableFilterTest(String title) {
		super(title);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			TableFilterTest aTableFilterTest;
			aTableFilterTest = new TableFilterTest();
			aTableFilterTest.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			aTableFilterTest.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of JFrame");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the fixedTableModel property value.
	 * @return FixedTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private FixedTableModel getfixedTableModel() {
		if (ivjfixedTableModel == null) {
			try {
				ivjfixedTableModel = new FixedTableModel();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjfixedTableModel;
	}

	/**
	 * Return the JFrameContentPane property value.
	 * @return JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			try {
				ivjJFrameContentPane = new JPanel();
				ivjJFrameContentPane.setName("JFrameContentPane");
				ivjJFrameContentPane.setLayout(new java.awt.GridLayout());
				getJFrameContentPane().add(getJScrollPane1(), getJScrollPane1().getName());
				getJFrameContentPane().add(getJScrollPane2(), getJScrollPane2().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Return the JScrollPane1 property value.
	 * @return JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new JScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				ivjJScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				getJScrollPane1().setViewportView(getScrollPaneTable());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane1;
	}

	/**
	 * Return the JScrollPane2 property value.
	 * @return JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JScrollPane getJScrollPane2() {
		if (ivjJScrollPane2 == null) {
			try {
				ivjJScrollPane2 = new JScrollPane();
				ivjJScrollPane2.setName("JScrollPane2");
				ivjJScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				getJScrollPane2().setViewportView(getScrollPaneTable1());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane2;
	}

	/**
	 * Return the omitColumnTableModel property value.
	 * @return OmitColumnTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private OmitColumnTableModel getomitColumnTableModel() {
		if (ivjomitColumnTableModel == null) {
			try {
				ivjomitColumnTableModel = new OmitColumnTableModel();
				ivjomitColumnTableModel.setHiddenColumn(3);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjomitColumnTableModel;
	}

	/**
	 * Return the reverseColumnTableModel property value.
	 * @return ReverseColumnTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ReverseColumnTableModel getreverseColumnTableModel() {
		if (ivjreverseColumnTableModel == null) {
			try {
				ivjreverseColumnTableModel = new ReverseColumnTableModel();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjreverseColumnTableModel;
	}

	/**
	 * Return the ScrollPaneTable property value.
	 * @return JTable
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JTable getScrollPaneTable() {
		if (ivjScrollPaneTable == null) {
			try {
				ivjScrollPaneTable = new JTable();
				ivjScrollPaneTable.setName("ScrollPaneTable");
				getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
				getJScrollPane1().getViewport().setBackingStoreEnabled(true);
				ivjScrollPaneTable.setBounds(0, 0, 200, 200);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjScrollPaneTable;
	}

	/**
	 * Return the ScrollPaneTable1 property value.
	 * @return JTable
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JTable getScrollPaneTable1() {
		if (ivjScrollPaneTable1 == null) {
			try {
				ivjScrollPaneTable1 = new JTable();
				ivjScrollPaneTable1.setName("ScrollPaneTable1");
				getJScrollPane2().setColumnHeaderView(ivjScrollPaneTable1.getTableHeader());
				getJScrollPane2().getViewport().setBackingStoreEnabled(true);
				ivjScrollPaneTable1.setBounds(0, 0, 200, 200);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjScrollPaneTable1;
	}

	/**
	 * connPtoP1SetTarget:  (omitColumnTableModel.this <--> ScrollPaneTable.model)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			getScrollPaneTable().setModel(getomitColumnTableModel());
			getScrollPaneTable().createDefaultColumnsFromModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (reverseColumnTableModel.this <--> ScrollPaneTable1.model)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			getScrollPaneTable1().setModel(getreverseColumnTableModel());
			getScrollPaneTable1().createDefaultColumnsFromModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetTarget:  (fixedTableModel.this <--> omitColumnTableModel.realTableModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			getomitColumnTableModel().setRealTableModel(getfixedTableModel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP4SetTarget:  (fixedTableModel.this <--> reverseColumnTableModel.realTableModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP4SetTarget() {
		/* Set the target from the source */
		try {
			getreverseColumnTableModel().setRealTableModel(getfixedTableModel());
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
		connPtoP3SetTarget();
		connPtoP1SetTarget();
		connPtoP4SetTarget();
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
			setName("TableFilterTest");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(341, 161);
			setContentPane(getJFrameContentPane());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}
}
