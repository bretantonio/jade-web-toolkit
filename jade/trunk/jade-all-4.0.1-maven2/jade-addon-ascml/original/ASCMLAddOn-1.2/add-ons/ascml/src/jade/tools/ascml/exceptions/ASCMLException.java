/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.exceptions;

import java.util.*;
import java.io.StringWriter;
import java.io.PrintWriter;

public abstract class ASCMLException extends Exception
{
	public final static int ERRORCODE_UNSPECIFIED = 0;

	public final static String NO_DETAILS = "No details available";
	private int errorCode;
	private String shortMessage;
	private String longMessage;
	private Vector exceptionDetails;
	private Object userObject;
	
	/** the 'nestedException'-Vector is initialized in case exceptions
	    should be contained within this ASCMLException-object. */
	private Vector<Exception> nestedExceptions;
	
	/**
	 *  Instantiate a new ASCMLException with a default messageText. 
	 */
	public ASCMLException()
	{
		this.errorCode = ERRORCODE_UNSPECIFIED;
		this.nestedExceptions = new Vector<Exception>();
		shortMessage = "";
		longMessage = "";
		this.exceptionDetails = new Vector();
	}

	/**
	 *  Instantiate a new ASCMLException with a given messageText.
	 *  @param shortMessage  The exception's messageText.
	 */
	public ASCMLException(String shortMessage)
	{
		this(shortMessage, NO_DETAILS);
	}

	/**
	 *  Instantiate a new ASCMLException with a given messageText and an errorCode.
	 *  @param shortMessage  The exception's messageText.
	 *  @param errorCode  The exception's errorCode.
	 */
	public ASCMLException(String shortMessage, int errorCode)
	{
		this(shortMessage);
		this.errorCode = errorCode;
	}

	/**
	 *  Instantiate a new ASCMLException with a given short and long messageText.
	 *  @param shortMessage  The exception's messageText.
	 *  @param longMessage  A detailed description of the Exception's reason.
	 */
	public ASCMLException(String shortMessage, String longMessage)
	{
		this();
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	/**
	 *  Instantiate a new ASCMLException with a given short and long messageText.
	 *  @param shortMessage  The exception's messageText.
	 *  @param longMessage  A detailed description of the Exception's reason.
	 */
	public ASCMLException(String shortMessage, String longMessage, Exception nestedException)
	{
		this(shortMessage, longMessage);
		addNestedException(nestedException);
	}

	/**
	 *  Instantiate a new ASCMLException with a given short and long messageText.
	 *  @param shortMessage  The exception's messageText.
	 *  @param longMessage  A detailed description of the Exception's reason.
	 *  @param errorCode  The exception's errorCode.
	 */
	public ASCMLException(String shortMessage, String longMessage, int errorCode)
	{
		this(shortMessage, longMessage);
		this.errorCode = errorCode;
	}

	/**
	 *  Set the errorcode for this Exception.
	 *  @param errorCode  The error's type-code.
	 */
	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}
	
	/**
	 *  Get the errorCode, for this exception.
	 *  @return int, indicating the type of the error.
	 */
	public int getErrorCode()
	{
		return errorCode;
	}

	/**
	 *  Set the userObject for this Exception.
	 *  @param userObject  the userObject contained in this Exception.
	 */
	public void setUserObject(Object userObject)
	{
		this.userObject = userObject;
	}

	/**
	 *  Get the userObject, for this exception.
	 *  @return the userObject or null if no object was stored in this exception
	 */
	public Object getUserObject()
	{
		return userObject;
	}

	/**
	 *  Add a new nested Exception to this exception
	 *  @param exc  The nested Exception-object.
	 */
	public void addNestedException(Exception exc)
	{
		if (!nestedExceptions.contains(exc))
			nestedExceptions.add(exc);
	}

	/**
	 *  Get all the nested Exceptions of this exception
	 *  @return  A Vector containing ASCMLExceptions
	 */
	public Vector getNestedExceptions()
	{
		return nestedExceptions;
	}

	/**
	 *  This method returns 'true' if any nested exceptions were set, 'false' otherwise.
	 *  @return  flag, indicating if any nested exceptions are contained within this exception-object.
	 */
	public boolean hasNestedExceptions()
	{
		return (nestedExceptions.size() > 0);
	}

	public boolean hasExceptionDetails()
	{
		return exceptionDetails.size() > 0;
	}

	public Vector getExceptionDetails()
	{
		return exceptionDetails;
	}

	/**
	 *  This method overrides super.getMessage(). In case an individual message was set, this message
	 *  is returned, otherwise a default-message showing the number of errors and warnings is returned. 
	 *  @return  Message-String of this exception-object. 
	 */	
	public String getMessage()
	{
		String str = "";

		str = "ASCMLException: " + getShortMessage() + " (errorcode="+getErrorCode()+")\n";
		str += getLongMessage();
		if (nestedExceptions.size() > 0)
		{
			str += "\n";
			for (int i=0; i < nestedExceptions.size(); i++)
			{
				// nestedException.printStackTrace();
				str += ((ASCMLException)nestedExceptions.elementAt(i)).getShortMessage();
			}
		}
		return str;
	}

	public String getStackTraceString()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public void addExceptionDetails(String shortMessage, String longMessage)
	{
		exceptionDetails.add(new ASCMLExceptionDetails(shortMessage, longMessage, getStackTraceString()));
	}

	public void clearExceptionDetails()
	{
		exceptionDetails.clear();
	}
	
	public String getShortMessage()
	{
		return shortMessage;
	}

	public String getLongMessage()
	{
		return longMessage;
	}

	public String toLongString()
	{
		String str = "ASCMLException: " + getShortMessage() + "\n";
		for (int i=0; i < exceptionDetails.size(); i++)
		{
			str += " - " + exceptionDetails.elementAt(i) + "\n";
		}
		for (int i=0; i < nestedExceptions.size(); i++)
		{
			Exception nestedException = nestedExceptions.elementAt(i);
			if (nestedException instanceof ASCMLException)
			{
				str += ((ASCMLException)nestedException).toLongString() + "\n";
				str += ((ASCMLException)nestedException).getStackTraceString() + "\n";
			}
			else
			{
				StringWriter sw = new StringWriter();
				nestedException.printStackTrace(new PrintWriter(sw));
				str += sw.toString();
			}
			str += "Nested Exception: " + nestedException + "\n";

		}
		return str;
	}

	public String toString()
	{
		return this.getShortMessage();
	}
}
