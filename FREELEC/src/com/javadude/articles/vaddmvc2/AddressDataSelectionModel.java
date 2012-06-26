package com.javadude.articles.vaddmvc2;

import com.javadude.articles.vaddmvc1.*;
import java.beans.*;
/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Definition of a simple selection model interface
 * Creation date: (1/19/00 1:29:51 AM)
 * @author: Scott Stanchfield
 */

public interface AddressDataSelectionModel {

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public AddressBookModel getModel();

	public AddressDataModel getSelection();

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void setModel(AddressBookModel model);

	public void setSelection(AddressDataModel selection);
}
