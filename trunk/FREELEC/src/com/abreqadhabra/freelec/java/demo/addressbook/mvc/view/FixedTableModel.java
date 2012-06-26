package com.abreqadhabra.freelec.java.demo.addressbook.mvc.view;

import javax.swing.table.AbstractTableModel;
/*
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 *
 * A simple table model with hardcoded data
 * Each cell displays (r,c) where r and c
 *   are integer row and column numbers
 * This model helps to show the effect of
 *   an applied filter model
 * 
 * Creation date: (1/19/00 12:44:53 AM)
 * @author: Scott Stanchfield
 */
public class FixedTableModel extends AbstractTableModel {

	/**
	 * Return the number of columns
	 *   in the model -- we explicitly use 6
	 */
	public int getColumnCount() {
	  return 6;
	}

	/**
	 * Return the number of rows
	 *   in the model -- we explicitly use 20
	 */
	public int getRowCount() {
	  return 20;
	}

	/**
	 * Return a string that describes the
	 *   location in the data
	 */
	public Object getValueAt(int row, int col) {
	  return col + "," + row;
	}
}
