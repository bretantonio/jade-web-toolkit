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

import jade.tools.ascml.absmodel.IParameter;
import jade.tools.ascml.absmodel.IParameterSet;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class ParameterTableModel extends AbstractTableModel
{
	private final static String[] MAP_KEY_INDEX = new String[]
	{"Name", "Value", "Type", "Description", "Constraints", "Optional"};

	private HashMap entries;

	public ParameterTableModel(Object[] parameterOrParameterSet, boolean isParameterSet)
	{
		entries = new HashMap();
		// add an empty row, which should always be shown at the end of the table
		addEmptyRow();
		
		if(isParameterSet)
		{
			IParameterSet[] aps = (IParameterSet[])parameterOrParameterSet;
			for(int i=0; i<aps.length; i++)
			{
				addRow(aps[i]);
			}
		}
		else
		{
			IParameter[] ap = (IParameter[])parameterOrParameterSet;
			for(int i=0; i<ap.length; i++)
			{
				addRow(ap[i]);
			}
		}
	}

	private HashMap getParameterByRow(int row)
	{
		if(getRowCount() >= row)
		{
			Iterator iterator = entries.keySet().iterator();
			Object dummyKey = "";
			
			// iterate through the whole HashMap and stop, wenn index == 'row'
			for(int i = 0; i <= row; i++)
			{
				dummyKey = iterator.next();
			}
			return (HashMap)entries.get(dummyKey);
		}
		return null;
	}

	public int getRowCount()
	{
		return entries.size();
	}

	public int getColumnCount()
	{
		return MAP_KEY_INDEX.length;
	}

	public Object getValueAt(int row, int col)
	{
		HashMap returnMap = getParameterByRow(row);
		Object returnValue = returnMap.get(MAP_KEY_INDEX[col].toLowerCase());
		if((returnValue instanceof Collection) && (((Collection)returnValue).isEmpty()))
			return "";
		else
			return returnValue;
	}

	public void addRow(Object parameter)
	{
		if (parameter instanceof IParameterSet)
		{
			IParameterSet aps = (IParameterSet)parameter;
		
			HashMap oneparam = new HashMap();
			oneparam.put("name", aps.getName());
			oneparam.put("type", aps.getType());
			oneparam.put("optional", new Boolean(aps.isOptional()));
			oneparam.put("description", aps.getDescription());
			oneparam.put("value", aps.getValues());
			String constraintString = "";
			for (int i=0; i < aps.getConstraints().length; i++)
			{
				constraintString += aps.getConstraints()[i];
			}
			oneparam.put("constraints", constraintString);
			entries.put(aps.getName(), oneparam);
		}
		else
		{
			IParameter ap = (IParameter)parameter;
			
			HashMap oneparam = new HashMap();
			oneparam.put("name", ap.getName());
			oneparam.put("type", ap.getType());
			oneparam.put("optional", new Boolean(ap.isOptional()));
			oneparam.put("description", ap.getDescription());
			oneparam.put("value", ap.getValue());
			String constraintString = "";
			for (int i=0; i < ap.getConstraints().length; i++)
			{
				constraintString += ap.getConstraints()[i];
			}
			oneparam.put("constraints", constraintString);
			entries.put(ap.getName(), oneparam);
		}
		fireTableRowsInserted(entries.size(), entries.size());
	}
	
	private void addEmptyRow()
	{
		HashMap emptyRow = new HashMap();
		emptyRow.put("name", "");
		emptyRow.put("type", "");
		emptyRow.put("optional", new Boolean(false));
		emptyRow.put("description", "");
		emptyRow.put("value", "");
		emptyRow.put("constraints", "");
		entries.put("empty", emptyRow);
	}
	
	public void setValueAt(Object oneParam, int row, int col) 
	{
		System.err.println("ParameterTableModel.setValueAt: Uncomment me !!!");
		/*
		if (row > getRowCount()-1)
		{
			// insert a new row
			addRow(oneParam);
		}
		else
		{
			HashMap parameterToChange = getParameterByRow(row);
			parameterToChange.put(MAP_KEY_INDEX[col].toLowerCase(), oneParam);
		}
		fireTableCellUpdated(row, col);
		*/
	}

	public String getColumnName(int col)
	{
		return MAP_KEY_INDEX[col];
	}

	public Class getColumnClass(int c)
	{
		if(MAP_KEY_INDEX[c].equalsIgnoreCase("optional"))
			return Boolean.class;
		else
			return String.class;
	}
}
