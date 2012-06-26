package com.abreqadhabra.freelec.java.demo.addressbook.mvc.bin;

import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.FixedTableModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.ReverseColumnTableModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.TwoTableJoinTableModel;
/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Insert the type's description here.
 * Creation date: (1/19/00 1:57:31 AM)
 * @author: Scott Stanchfield
 */
public class TableJoinExample2 extends JFrame implements Serializable {
	private FixedTableModel ivjfixedTableModel1 = null;
	private FixedTableModel ivjfixedTableModel2 = null;
	private JPanel ivjJFrameContentPane = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JTable ivjScrollPaneTable = null;
	private TwoTableJoinTableModel ivjtwoTableJoinTableModel = null;
	private ReverseColumnTableModel ivjreverseColumnTableModel = null;

	/**
	 * TableJoinExample constructor comment.
	 */
	public TableJoinExample2() {
		super();
		initialize();
	}

	/**
	 * TableJoinExample constructor comment.
	 * @param title java.lang.String
	 */
	public TableJoinExample2(String title) {
		super(title);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			TableJoinExample2 aTableJoinExample2;
			aTableJoinExample2 = new TableJoinExample2();
			aTableJoinExample2.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			aTableJoinExample2.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of JFrame");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the fixedTableModel1 property value.
	 * @return FixedTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private FixedTableModel getfixedTableModel1() {
		if (ivjfixedTableModel1 == null) {
			try {
				ivjfixedTableModel1 = new FixedTableModel();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjfixedTableModel1;
	}

	/**
	 * Return the fixedTableModel2 property value.
	 * @return FixedTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private FixedTableModel getfixedTableModel2() {
		if (ivjfixedTableModel2 == null) {
			try {
				ivjfixedTableModel2 = new FixedTableModel();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjfixedTableModel2;
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
	 * Return the twoTableJoinTableModel property value.
	 * @return TwoTableJoinTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private TwoTableJoinTableModel gettwoTableJoinTableModel() {
		if (ivjtwoTableJoinTableModel == null) {
			try {
				ivjtwoTableJoinTableModel = new TwoTableJoinTableModel();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjtwoTableJoinTableModel;
	}

	/**
	 * connPtoP1SetTarget:  (fixedTableModel2.this <--> twoTableJoinTableModel.model2)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			gettwoTableJoinTableModel().setModel2(getreverseColumnTableModel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (fixedTableModel1.this <--> twoTableJoinTableModel.model1)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			gettwoTableJoinTableModel().setModel1(getfixedTableModel1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetTarget:  (fixedTableModel2.this <--> reverseColumnTableModel.realTableModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			getreverseColumnTableModel().setRealTableModel(getfixedTableModel2());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP4SetTarget:  (twoTableJoinTableModel.this <--> ScrollPaneTable.model)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP4SetTarget() {
		/* Set the target from the source */
		try {
			getScrollPaneTable().setModel(gettwoTableJoinTableModel());
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
		connPtoP2SetTarget();
		connPtoP3SetTarget();
		connPtoP1SetTarget();
		connPtoP4SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("TableJoinExample");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(344, 162);
			setContentPane(getJFrameContentPane());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}
}
