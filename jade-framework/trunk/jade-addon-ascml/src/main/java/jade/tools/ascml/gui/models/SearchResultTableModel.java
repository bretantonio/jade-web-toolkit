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


package jade.tools.ascml.gui.models;

public class SearchResultTableModel // extends AbstractTableModel
{
	// not in use (so far)
	
	/*
	private final static String[] COL_NAMES = new String[]
	        {"Location", "Action"};	
	
	private HashMap entries;
	private MainWindow mainWindow;
	
	public SearchResultTableModel(MainWindow mainWindow, HashMap entries)
	{
		this.entries = entries;
		this.mainWindow = mainWindow;
	}
		
	public int getRowCount()
	{
		return entries.size();
	}
	
	public int getColumnCount()
	{
		return COL_NAMES.length;
	}
	
	private IDocument getModelInRow(int row)
	{
		Iterator iterator = entries.keySet().iterator(); 
		if (getRowCount() >= row)
		{		
			Object dummyKey = "";
			for (int i = 0; i <= row; i++)
			{
				dummyKey = iterator.next();
			}
			return (IDocument)entries.get(dummyKey);
		}
		return null;
	}
	
	public Object getValueAt(int row, int col)
	{
		IDocument desc = getModelInRow(row);
		if (col == 0)
		{
			return desc.getFilename();
		}
		else if (col == 1)
		{
			JButton addButton = new JButton("Add");
			if (desc.getFilename().endsWith(".agent.xml"))
				addButton.setActionCommand(GUIEventHandler.CMD_ADD_AGENTTYPE + desc.getFilename());
			else
				addButton.setActionCommand(GUIEventHandler.CMD_ADD_SOCIETYTYPE + desc.getFilename());
			addButton.addActionListener(mainWindow);
			return addButton;
		}
		else return null;
	}
	
	public boolean isCellEditable(int row, int col)
	{
		return false; 
	}
		
	public Class getColumnClass(int c) 
	{
		if (c == 0)
			return String.class;
		else if (c == 1)
			return JButton.class;
		else return null;
	}
	*/
}
