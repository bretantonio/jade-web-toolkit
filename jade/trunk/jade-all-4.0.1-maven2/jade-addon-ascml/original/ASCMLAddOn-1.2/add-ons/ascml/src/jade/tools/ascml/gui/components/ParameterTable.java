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


package jade.tools.ascml.gui.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import jade.tools.ascml.gui.models.ParameterTableModel;

public class ParameterTable extends JTable
{
	public ParameterTable(Object[] parameter)
	{
		super(new ParameterTableModel(parameter, false));
		setBackground(Color.WHITE);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		initColumnSizes();
	}

	/*
	* This method picks good column sizes.
	* If all column heads are wider than the column's cells'
	* contents, then you can just use column.sizeWidthToFit().
	*/
	private void initColumnSizes() 
	{
		ParameterTableModel model = (ParameterTableModel)getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;

		TableCellRenderer headerRenderer = getTableHeader().getDefaultRenderer();
	
		for (int i = 0; i < getColumnCount(); i++)
		{
			column = getColumnModel().getColumn(i);
	
			comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;
			
			Class columnClass = model.getColumnClass(i);
			TableCellRenderer tableRenderer = getDefaultRenderer(columnClass);
			comp = tableRenderer.getTableCellRendererComponent(this, getModel().getValueAt(0, i), false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;
			
			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}
}
