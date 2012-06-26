package com.javadude.articles.vaddmvc2;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
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
 * Creation date: (1/19/00 1:57:31 AM)
 * @author: Scott Stanchfield
 */
public class TableJoinExample extends JFrame implements Serializable {
	private FixedTableModel ivjfixedTableModel1 = null;
	private FixedTableModel ivjfixedTableModel2 = null;
	private JPanel ivjJFrameContentPane = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JTable ivjScrollPaneTable = null;
	private TwoTableJoinTableModel ivjtwoTableJoinTableModel = null;

	/**
	 * TableJoinExample constructor comment.
	 */
	public TableJoinExample() {
		super();
		initialize();
	}

	/**
	 * TableJoinExample constructor comment.
	 * @param title java.lang.String
	 */
	public TableJoinExample(String title) {
		super(title);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			TableJoinExample aTableJoinExample;
			aTableJoinExample = new TableJoinExample();
			aTableJoinExample.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			aTableJoinExample.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JFrame");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Return the fixedTableModel1 property value.
	 * @return com.javadude.articles.vaddmvc2.FixedTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private FixedTableModel getfixedTableModel1() {
		if (ivjfixedTableModel1 == null) {
			try {
				ivjfixedTableModel1 = new com.javadude.articles.vaddmvc2.FixedTableModel();
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
	 * @return com.javadude.articles.vaddmvc2.FixedTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private FixedTableModel getfixedTableModel2() {
		if (ivjfixedTableModel2 == null) {
			try {
				ivjfixedTableModel2 = new com.javadude.articles.vaddmvc2.FixedTableModel();
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
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			try {
				ivjJFrameContentPane = new javax.swing.JPanel();
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
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new javax.swing.JScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
	 * Return the ScrollPaneTable property value.
	 * @return javax.swing.JTable
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTable getScrollPaneTable() {
		if (ivjScrollPaneTable == null) {
			try {
				ivjScrollPaneTable = new javax.swing.JTable();
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
	 * @return com.javadude.articles.vaddmvc2.TwoTableJoinTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private TwoTableJoinTableModel gettwoTableJoinTableModel() {
		if (ivjtwoTableJoinTableModel == null) {
			try {
				ivjtwoTableJoinTableModel = new com.javadude.articles.vaddmvc2.TwoTableJoinTableModel();
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
			gettwoTableJoinTableModel().setModel2(getfixedTableModel2());
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
		connPtoP1SetTarget();
		connPtoP2SetTarget();
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
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
