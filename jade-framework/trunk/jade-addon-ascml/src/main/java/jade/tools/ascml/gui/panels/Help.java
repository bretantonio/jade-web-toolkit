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


package jade.tools.ascml.gui.panels;

import jade.tools.ascml.repository.loader.ImageIconLoader;

import javax.swing.*;
import java.awt.*;

public class Help extends AbstractPanel
{
	private final static String HELP_STRING = "<html><h2>Write me !</h2>Mailing-List: ascml@i4.informatik.rwth-aachen.de<p>&nbsp;<p>Nightly builds and ASCML-WIKI available at<p>http://www-i4.informatik.rwth-aachen.de/ascml<hr>Note:<ul><li> Java Version 1.5 (or 5.0) is required</li><li>Be sure the classpath contains every (!) library you need<br>(otherwise you will see a lot of <i>ClassNotFoundExceptions</i>)</li><li>Special characters like &auml;,&ouml;,&uuml; and &szlig; are not allowed in xml-files</li></ul></html>";
		
	public Help(AbstractMainPanel mainPanel)
	{
		super(mainPanel);
		
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO)), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		this.add(new JLabel(HELP_STRING), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
	}
}
