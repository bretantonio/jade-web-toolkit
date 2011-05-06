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

import javax.swing.*;
import java.awt.*;

public class Changes extends AbstractPanel
{
	private final static String CHANGES_PREFIX = "<html><ul>";
	private final static String CHANGES_SUFFIX = "</ul></html>";

	private String changes;

	public Changes(AbstractMainPanel mainPanel)
	{
		super(mainPanel);
		
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.changes = "";

		// Add your change-comment here (use html-tags for linebreak and text-decorations):
		// The list is sorted in reverse order when viewed in the ASCML, so add your comment
		// at the end of this list
		addChange("March 2nd '05", "- Fixed bugs (tree-view, removing of agents, gui-shutdown)<br>- Tool-Options may now be turned on/off via GUI.<br>- ASCML-frame got the ASCML's name on the platform as title.<br>- There are no longer numbers appended to agent-names when <br>&nbsp;&nbsp;no namingscheme is supplied<br>- references to remote societytypes no longer have to be fully qualified.");
        addChange("March 6th '05", "- Fixed some dependency-loading bugs <br>- Added dependency-tabs to agentinstance- and<br>&nbsp;&nbsp;societyinstance-reference-view");
		addChange("March 11th '05", "- Fixed icon-loading bug <br>- added ASCML-startup-argument <i>nogui</i>.");
		addChange("March 12th '05", "- <i>Full-View</i> of the repository-tree has been removed (not needed). <p><font color=\"#005CEC\">If you experience any problems loading the ASCML-properties, please<br> delete the file <i>ascml.repository.properties.xml</i> and restart the ASCML.</font>");
		addChange("March 13th '05", "- Dependency-Tab now shows all specified dependencies");
        addChange("March 17th '05", "- Added support for Status-Changed messages");
		addChange("July 19th '05", "- Fixed some viewing bugs<br>- Slightly improved performance<br>- Model-nodes are now sorted alphabetically in the RepositoryTree");
		addChange("July 21st '05", "- The ASCML is now able to start Jadex-agents.<br>&nbsp;&nbsp;A bunch of Jadex-examples have been added.<br>&nbsp;&nbsp;The ASCML-Wiki 'Programmer-Guide-Section'<br>&nbsp;&nbsp;explains the Jadex-porting in detail.");
        addChange("March '06", "The new version 0.9 of the ASCML is publicly available. The changes in detail:<br>- You can now edit agent- and society-settings using the GUI, you no longer have to edit the xml-files.<br>- A new functional-state has been introduced for societies.<br>&nbsp;&nbsp;You may now define which agents are important for a society to be in the state functional and which are not.<br>&nbsp;&nbsp;Only if the state of an important agent changes, the status of the society will change.<br>- The ASCML can now find description-files in the filesystem automatically<br>&nbsp;&nbsp;when given the fully-qualified name of an Agent- or SocietyType.<br>- The launcher-core has been refactored.<br>- A lot of bugs were fixed.<br>- The XML-scheme for societies has slightly been changed.<br>&nbsp;&nbsp;<font color=\"#005CEC\">This leads to incompatibility of your old society-descritpion-files with the new version of the ASCML.<br>&nbsp;&nbsp;Please have a look at the Wiki</font>");

		// create and add labels and scrollpane
		JLabel changesLabel = new JLabel(CHANGES_PREFIX + changes + CHANGES_SUFFIX);

		// not used yet, cause it messes layout when only a few changes are posted.
		JScrollPane scrollPane = new JScrollPane(changesLabel);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		// this.add(new JLabel("<html><h2>Changes</h2></html>"), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		// this.add(scrollPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
        this.add(new JLabel("<html><h2>&nbsp;Changes</h2></html>"), BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void addChange(String date, String change)
	{
		changes = "<li><b>"+date+"</b><br>"+change+"<br>&nbsp;</li>" + changes;
	}
}
