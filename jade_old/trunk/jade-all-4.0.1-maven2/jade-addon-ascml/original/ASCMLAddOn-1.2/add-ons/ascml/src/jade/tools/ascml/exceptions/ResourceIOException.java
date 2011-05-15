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

public class ResourceIOException extends ASCMLException
{

	public final static int PROPERTIES_IO	= 0;
	public final static int IMAGE_IO		= 100;
	public final static int MODEL_IO		= 200;

	/** The resource, that couldn't be i/o-ed. */
	private String resource;

	/**
	 *  Instantiate a new ResourceIOException with a default messageText. 
	 */
	public ResourceIOException()
	{
		super();
	}

	/**
	 *  Instantiate a new ResourceIOException with a given messageText and an errorCode.
	 *  @param msg  The exception's messageText.
	 *  @param errorCode  The exception's errorCode.
	 */
	public ResourceIOException(String msg, int errorCode)
	{
		super(msg, errorCode);
	}

	/**
	 *  Instantiate a new ResourceIOException with a given messageText,
	 *  the specified resource and an errorCode.
	 *  @param msg  The exception's messageText.
	 *  @param resource  The resource, that couldn't be found.
	 *  @param errorCode  The exception's errorCode.
	 */
	public ResourceIOException(String msg, String resource, int errorCode)
	{
		super(msg, errorCode);
		this.resource = resource;
	}

	/**
	 *  Instantiate a new ResourceIOException with a given messageText and a nested
	 *  Exception-object.
	 *  @param msg  The exception's messageText.
	 *  @param nestedException  This Exception-object is the root of all evil.
	 *                          In case this object itself is an instance of ResourceIOException
	 *                          the source-exception object of this object is stored.
	 *  @param errorCode  The exception's errorCode.
	 */
	public ResourceIOException(String msg, Exception nestedException, int errorCode)
	{
		super(msg, errorCode);
		super.addNestedException(nestedException);
	}

	/**
	 * Get the resource's name and/or location.
	 * @return  The name (e.g. file-name) or location (e.g. file-name and path) of the resource.
	 */
	public String getResourceString()
	{
		return resource;
	}
}
