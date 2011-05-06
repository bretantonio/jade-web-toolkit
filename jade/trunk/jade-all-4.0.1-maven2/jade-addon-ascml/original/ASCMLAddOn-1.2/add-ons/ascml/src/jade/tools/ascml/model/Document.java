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


package jade.tools.ascml.model;

import jade.tools.ascml.absmodel.IDocument;

/**
 *  The document model for file related info.
 */
public class Document implements IDocument
{

	/** The source. */
	protected String source = SOURCE_UNKNOWN;

	/** indicates whether this document has changed and is saved or not. */
	protected boolean isSaved = true;

	//-------- constructors --------

	/**
	 *  Create a new document.
	 */
	public Document()
	{
		this.source = "";
	}

	/**
	 *  Create a new document.
	 *  @param src  The source-path of this document
	 */
	public Document(String src)
	{
		this();
		this.source = src;
	}

	//-------- methods --------

	/**
	 *  Set the source (path + file-name).
	 *  @param source  The source-object.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 *  Get the source (path + file-name).
	 *  @return  The source-object.
	 */
	public String getSource()
	{
		if ((source == null) || (source.equals("")))
			source = SOURCE_UNKNOWN;
		return source;
	}

	/**
	 * Get the source-path.
	 * If the source is a file, than the path within the file-system is returned.
	 * @return  The source-path.
	 */
	public String getSourcePath()
	{
		if ((source == null) || source.equals("") || source.equals(SOURCE_UNKNOWN))
			return SOURCE_UNKNOWN;

		int lastIndexOfSeparator = source.lastIndexOf('\\');
		if (lastIndexOfSeparator == -1)
			lastIndexOfSeparator = source.lastIndexOf('/');

		return source.substring(0, lastIndexOfSeparator+1);
	}

	/**
	 * Set the source-path.
	 * @param path  The new source-path.
	 */
	public void setSourcePath(String path)
	{
		if ((path == null) || (path.equals("")))
			return;

		if (path.contains("\\") && !path.endsWith("\\"))
			path += "\\";
		else if (path.contains("/") && !path.endsWith("/"))
			path+= "/";

		source =  path + getSourceName();
	}

	/**
	 * Set the source-name (i.e. file-name - must end with '.agent.xml' or '.society.xml').
	 * @param name  The new source-name (must end with '.agent.xml' or '.society.xml')
	 */
	public void setSourceName(String name)
	{
		if ((name == null) || name.equals(""))
			return;
		if (!name.endsWith(".agent.xml") && !name.endsWith(".society.xml"))
			return;

		source =  getSourcePath() + name;
	}

	/**
	 * Get the source-name.
	 * If the source is a file, than the file-name (without it's path) is returned.
	 * @return  The source-path.
	 */
	public String getSourceName()
	{
		if ((source == null) || source.equals("") || source.equals(SOURCE_UNKNOWN))
			return SOURCE_UNKNOWN;

		int lastIndexOfSeparator = source.lastIndexOf('\\');
		if (lastIndexOfSeparator == -1)
			lastIndexOfSeparator = source.lastIndexOf('/');

		return source.substring(lastIndexOfSeparator+1, source.length());
	}

	/**
	 * Return whether the document is saved or not.
	 * @return true, if this document is saved as it is (without any pending changes), false otherwise.
	 */
	public boolean isSaved()
	{
		return isSaved;
	}

	/**
	 * Set whether the document is saved or not.
	 * @param isSaved  true, if this document is saved as it is (without any pending changes), false otherwise.
	 */
	public void setSaved(boolean isSaved)
	{
		this.isSaved = isSaved;
	}
}
