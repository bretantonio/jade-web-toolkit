package com.abreqadhabra.freelec.java.workshop.addressbook.domain;

import java.io.Serializable;

/**
 * This class embodies the name of a field and the maximum width that it may
 * have.
 * 
 * @version 1.0 11-Sep-1997
 */
public class FieldInfo implements Serializable {
    private String name;

    private int length;

    /**
     * This constructs an initialized FieldInfo object.
     * 
     * @param name
     *            - the name of the field.
     * @param length
     *            - the length of the field.
     */
    public FieldInfo(String name, int length) {
	this.name = name;
	this.length = length;
    }

    public FieldInfo(String name) {
	this.name = name;
    }
    
    /**
     * This method returns the name of the field.
     * 
     * @return String - the name of the field.
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /**
     * This method returns the length of the field.
     * 
     * @return int - the length of the field.
     */
    public int getLength() {
	return length;
    }
}
