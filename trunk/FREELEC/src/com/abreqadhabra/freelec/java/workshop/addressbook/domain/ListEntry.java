/*
 * ListEntry.java
 *
 * Copyright 2006 Sun Microsystems, Inc. ALL RIGHTS RESERVED Use of 
 * this software is authorized pursuant to the terms of the license 
 * found at http://developers.sun.com/berkeley_license.html .
 *
 */

package com.abreqadhabra.freelec.java.workshop.addressbook.domain;

import java.lang.reflect.Field;


/**
 *
 * @author John O'Conner
 */
public class ListEntry {
    
    /** Creates a new instance of ListEntry */
    public ListEntry() {
        this("","","", -1);
    }
    
    public ListEntry(String lastName, String firstName, String middleName, int id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.id = id;
    }
    
    public String getName() {
        return lastName + ", " + firstName + " " + middleName;
    }
    
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String name) {
        this.lastName = name;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String name) {
        this.middleName = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
   
    private String lastName, firstName, middleName;
    private int id;
    
	  /**
	  * Intended only for debugging.
	  *
	  * <P>Here, a generic implementation uses reflection to print
	  * names and values of all fields <em>declared in this class</em>. Note that
	  * superclass fields are left out of this implementation.
	  *
	  * <p>The format of the presentation could be standardized by using
	  * a MessageFormat object with a standard pattern.
	  */
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}
}
