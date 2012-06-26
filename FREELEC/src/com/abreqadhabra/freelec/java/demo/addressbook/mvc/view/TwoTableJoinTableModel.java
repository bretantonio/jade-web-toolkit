package com.abreqadhabra.freelec.java.demo.addressbook.mvc.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * A Join Model, combining the columns in two models
 * Creation date: (1/19/00 1:14:54 AM)
 * @author: Scott Stanchfield
 */
public class TwoTableJoinTableModel extends AbstractTableModel implements TableModelListener {
	protected transient PropertyChangeSupport propertyChange;
	private TableModel fieldModel1 = null;
	private TableModel fieldModel2 = null;

	protected PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	// standard bound property support
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/** Return the type of the column, mapping the column into
	   *  the appropriate table
	   */
	public Class getColumnClass(int columnIndex) {
		int col1Count = getModel1().getColumnCount();
		if (columnIndex < col1Count)
			return getModel1().getColumnClass(columnIndex);
		else
			return getModel2().getColumnClass(columnIndex - col1Count);
	}

	/** Return the number of columns, which is the sum of the
	   *  the number of columns in each joined table
	   */
	public int getColumnCount() {
		return getModel1().getColumnCount() + getModel2().getColumnCount();
	}

	/** Return the name of the column, mapping the column into
	   *  the appropriate table
	   */
	public String getColumnName(int columnIndex) {
		int col1Count = getModel1().getColumnCount();
		if (columnIndex < col1Count)
			return getModel1().getColumnName(columnIndex);
		else
			return getModel2().getColumnName(columnIndex - col1Count);
	}

	// properties to track the two real models
	public TableModel getModel1() {
		return fieldModel1;
	}

	public TableModel getModel2() {
		return fieldModel2;
	}

	/** return the number of rows in this table -- we'll use
	   *  the maximum number of rows in either joined table
	   */
	public int getRowCount() {
		return Math.max(getModel1().getRowCount(), getModel2().getRowCount());
	}

	/** get the row/column value, mapping it to the appropriate
	   *  real table position. If an empty position is requested,
	   *  we return null
	   */
	public Object getValueAt(int rowIndex, int columnIndex) {
		int col1Count = getModel1().getColumnCount();
		if (columnIndex < col1Count) {
			if (rowIndex < getModel1().getRowCount())
				return getModel1().getValueAt(rowIndex, columnIndex);
		}
		else
			if (rowIndex < getModel2().getRowCount())
				return getModel2().getValueAt(rowIndex, columnIndex - col1Count);
		return null;
	}

	/** delegate the check to see if a cell is editable to the
	   *  appropriate real model
	   */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		int col1Count = getModel1().getColumnCount();
		if (columnIndex < col1Count) {
			if (rowIndex < getModel1().getRowCount())
				return getModel1().isCellEditable(rowIndex, columnIndex);
		}
		else
			if (rowIndex < getModel2().getRowCount())
				return getModel2().isCellEditable(rowIndex, columnIndex - col1Count);
		return false;
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	// The set methods need to add us as a listener
	//   so that we can forward the events to _our_ listeners
	public void setModel1(TableModel model1) {
		if (fieldModel1 != null)
			fieldModel1.removeTableModelListener(this);
		TableModel oldValue = fieldModel1;
		fieldModel1 = model1;
		fieldModel1.addTableModelListener(this);
		firePropertyChange("model1", oldValue, model1);
	}

	public void setModel2(TableModel model2) {
		if (fieldModel1 != null)
			fieldModel1.removeTableModelListener(this);
		TableModel oldValue = fieldModel2;
		fieldModel2 = model2;
		fieldModel1.addTableModelListener(this);
		firePropertyChange("model2", oldValue, model2);
	}

	/** set the row/column value, mapping it to the appropriate
	   *  real table position. If an empty position is requested,
	   *  we return null
	   */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		int col1Count = getModel1().getColumnCount();
		if (columnIndex < col1Count)
			getModel1().setValueAt(aValue, rowIndex, columnIndex);
		else
			getModel2().setValueAt(aValue, rowIndex, columnIndex - col1Count);
	}

	/** Catch the table model events of the real
	   *  models and refire it to _our_ listeners
	   */
	public void tableChanged(TableModelEvent e) {
		fireTableChanged(new TableModelEvent(this, e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType()));
	}
}
