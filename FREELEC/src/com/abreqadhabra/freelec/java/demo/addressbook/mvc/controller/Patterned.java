package com.abreqadhabra.freelec.java.demo.addressbook.mvc.controller;

import java.beans.PropertyChangeListener;

/**
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * An interface that describes and object
 *   that keeps track of a pattern
 *
 * Creation date: (1/19/00 1:10:11 AM)
 * @author: Scott Stanchfield
 */
public interface Patterned {

	// Bound property support
	public void addPropertyChangeListener(
					PropertyChangeListener listener);

	// the pattern property
	public String getPattern();

	public void removePropertyChangeListener(
					PropertyChangeListener listener);

	public void setPattern(String pattern);
}
