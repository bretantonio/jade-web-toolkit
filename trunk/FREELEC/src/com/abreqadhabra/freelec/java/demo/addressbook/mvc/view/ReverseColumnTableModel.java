package com.abreqadhabra.freelec.java.demo.addressbook.mvc.view;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * This TableModel reverses the column display
 *
 * Creation date: (1/19/00 1:10:11 AM)
 * @author: Scott Stanchfield
 */
public class ReverseColumnTableModel extends FilteredTableModel {

	/** Change the column mapping
	 *    to return the columns in
	 *    reverse order
	 */
	protected int mapColumn(int col) {
		return getColumnCount() - col - 1;
	}
}
