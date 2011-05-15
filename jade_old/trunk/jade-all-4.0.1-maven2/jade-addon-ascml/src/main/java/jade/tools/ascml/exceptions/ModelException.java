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

import jade.tools.ascml.exceptions.ASCMLException;

import java.util.*;
import java.io.*;

/**
 *  This class contains methods for setting and getting errors and warnings
 *  that might occur during the creation of a model-object.
 */
public class ModelException extends ASCMLException
{
	public final static int ERRORCODE_AGENTTYPEMODEL_NOT_FOUND		= 10;
	public final static int ERRORCODE_SOCIETYTYPEMODEL_NOT_FOUND	= 11;
	public final static int ERRORCODE_ATTRIBUTES_MISSING			= 20;

	/**
	 *  Instantiate a new ModelException with a default messageText.
	 */
	public ModelException()
	{
		super();
	}

	/**
	 *  Instantiate a new ModelException with a given messageText.
	 *  @param msg  The exception's messageText.
	 */
	public ModelException(String msg)
	{
		super(msg);
	}

	/**
	 *  Instantiate a new ModelException with a given messageText and an errorCode.
	 *  @param msg  The exception's messageText.
	 *  @param errorCode  The exception's errorCode.
	 */
	public ModelException(String msg, int errorCode)
	{
		super(msg, errorCode);
	}

	/**
	 *  Instantiate a new ModelException with a given messageText and a nested
	 *  Exception-object.
	 *  @param msg  The exception's messageText.
	 *  @param nestedException  This Exception-object is the root of all evil.
	 */
	public ModelException(String msg, Exception nestedException)
	{
		super(msg);
		super.addNestedException(nestedException);
	}

	/**
	 * Instantiate a new ModelException with a given short and long messageText
	 * and an errorCode.
	 * @param shortMessage  A short description of this exception's reason.
	 * @param longMessage  A detailed description of this exception's reason.
	 */
	public ModelException(String shortMessage, String longMessage)
	{
		super(shortMessage, longMessage);
	}

	/**
	 * Instantiate a new ModelException with a given short and long messageText
	 * and an errorCode.
	 * @param shortMessage  A short description of this exception's reason.
	 * @param longMessage  A detailed description of this exception's reason.
	 * @param nestedException  This Exception-object is the root of all evil.
	 */
	public ModelException(String shortMessage, String longMessage, Exception nestedException)
	{
		super(shortMessage, longMessage, nestedException);
	}

	/**
	 * Instantiate a new ModelException with a given short and long messageText
	 * and an errorCode.
	 * @param shortMessage  A short description of this exception's reason.
	 * @param longMessage  A detailed description of this exception's reason.
	 * @param errorCode  The errorCode for this exception.
	 */
	public ModelException(String shortMessage, String longMessage, int errorCode)
	{
		super(shortMessage, longMessage, errorCode);
	}
}
