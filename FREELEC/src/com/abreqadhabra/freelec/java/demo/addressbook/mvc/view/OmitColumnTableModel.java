package com.abreqadhabra.freelec.java.demo.addressbook.mvc.view;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * A sample filtering model that omits
 *   a single column in a table
 *
 * Creation date: (1/19/00 1:10:11 AM)
 * @author: Scott Stanchfield
 */
public class OmitColumnTableModel extends FilteredTableModel {
	private int fieldHiddenColumn = 0;

	/** Returns a fewer columns than the real
	 *    TableModel actually has, as we hide one
	 */
	public int getColumnCount() {
		return getRealTableModel().getColumnCount() - 1;
	}

	/**
	 * Gets the hiddenColumn property (int) value.
	 * @return The hiddenColumn property value.
	 * @see #setHiddenColumn
	 */
	public int getHiddenColumn() {
		return fieldHiddenColumn;
	}

	/** Provides a mapping from a requested
	 *    column to the column in the real model
	 */
	protected int mapColumn(int col) {
		if (col >= getHiddenColumn())
			col++;
		return col;
	}

	/**
	 * Sets the hiddenColumn property (int) value.
	 * @param hiddenColumn The new value for the property.
	 * @see #getHiddenColumn
	 */
	public void setHiddenColumn(int hiddenColumn) {
		fieldHiddenColumn = hiddenColumn;
	}
}
