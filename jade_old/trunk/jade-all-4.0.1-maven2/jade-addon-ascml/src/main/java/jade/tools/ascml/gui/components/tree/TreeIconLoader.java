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


package jade.tools.ascml.gui.components.tree;

import jade.tools.ascml.repository.loader.ImageIconLoader;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TreeIconLoader
{
	private final static String ICON_PATH = "icons/";

	public static ImageIcon whiteIcon = createImageIcon(ICON_PATH + "circle_white.gif");
	public static ImageIcon blueIcon = createImageIcon(ICON_PATH + "circle_blue.gif");
	public static ImageIcon lightBlueIcon = createImageIcon(ICON_PATH + "circle_lightblue.gif");
	public static ImageIcon lightlightBlueIcon = createImageIcon(ICON_PATH + "circle_lightlightblue.gif");
	public static ImageIcon grayIcon = createImageIcon(ICON_PATH + "circle_gray.gif");
	public static ImageIcon redIcon = createImageIcon(ICON_PATH + "circle_red.gif");
	public static ImageIcon greenIcon = createImageIcon(ICON_PATH + "circle_green.gif");
	public static ImageIcon greenRedIcon = createImageIcon(ICON_PATH + "circle_greenred.gif");
	public static ImageIcon exceptionIcon = createImageIcon(ICON_PATH + "circle_exception.gif");
	public static ImageIcon ascmlIcon = createImageIcon(ICON_PATH + "circle_ascml.gif");
	public static ImageIcon remoteAscmlIcon = createImageIcon(ICON_PATH + "circle_remote_ascml.gif");
	public static ImageIcon lightblueConeIcon  = createImageIcon(ICON_PATH + "cone_lightblue.gif");
	public static ImageIcon grayConeIcon  = createImageIcon(ICON_PATH + "cone_gray.gif");

	public static ImageIcon lightblueStarIcon = createImageIcon(ICON_PATH + "star_lightblue.gif");
	public static ImageIcon greenStarIcon = createImageIcon(ICON_PATH + "star_green.gif");
	public static ImageIcon yellowStarIcon = createImageIcon(ICON_PATH + "star_yellow.gif");
	public static ImageIcon redStarIcon = createImageIcon(ICON_PATH + "star_red.gif");

	public static ImageIcon blueFolderIcon = createImageIcon(ICON_PATH + "folder_blue.gif");
	public static ImageIcon lightBlueFolderIcon = createImageIcon(ICON_PATH + "folder_lightblue.gif");

	public static ImageIcon blueSmileyIcon = createImageIcon(ICON_PATH + "smiley_blue.gif");
	public static ImageIcon yellowSmileyIcon = createImageIcon(ICON_PATH + "smiley_yellow.gif");
	public static ImageIcon greenSmileyIcon = createImageIcon(ICON_PATH + "smiley_green.gif");
	public static ImageIcon redSmileyIcon = createImageIcon(ICON_PATH + "smiley_red.gif");

	public static ImageIcon blueSmileySocietyIcon = createImageIcon(ICON_PATH + "smileysociety_blue.gif");

	public static ImageIcon blueFlowerIcon = createImageIcon(ICON_PATH + "flower_blue.gif");

	public static ImageIcon redArrowIcon = createImageIcon(ICON_PATH + "arrow_red.gif");
	public static ImageIcon yellowArrowIcon = createImageIcon(ICON_PATH + "arrow_yellow.gif");
	public static ImageIcon greenArrowIcon = createImageIcon(ICON_PATH + "arrow_green.gif");
	public static ImageIcon blueArrowIcon = createImageIcon(ICON_PATH + "arrow_blue.gif");

	public static ImageIcon basicRunIcon = createImageIcon(ICON_PATH + "basic_run.gif");
	public static ImageIcon basicStopIcon = createImageIcon(ICON_PATH + "basic_stop.gif");
	public static ImageIcon basicAddIcon = createImageIcon(ICON_PATH + "basic_add.gif");
	public static ImageIcon basicAddAgentIcon = createImageIcon(ICON_PATH + "basic_add_agent.gif");
	public static ImageIcon basicAddAgentSmallIcon = createImageIcon(ICON_PATH + "basic_add_agent_small.gif");
	public static ImageIcon basicAddSocietyIcon = createImageIcon(ICON_PATH + "basic_add_society.gif");
	public static ImageIcon basicAddSocietySmallIcon = createImageIcon(ICON_PATH + "basic_add_society_small.gif");
	public static ImageIcon basicOpenIcon = createImageIcon(ICON_PATH + "basic_open.gif");
	public static ImageIcon basicOpenProjectIcon = createImageIcon(ICON_PATH + "basic_open_project.gif");

	public static ImageIcon treeViewBasicIcon = createImageIcon(ICON_PATH + "treeview_basic.gif");
	public static ImageIcon treeViewExpertIcon = createImageIcon(ICON_PATH + "treeview_expert.gif");
	public static ImageIcon treeViewFullIcon = createImageIcon(ICON_PATH + "treeview_full.gif");

	public static ImageIcon dialogModelNotFoundIcon = createImageIcon(ICON_PATH + "dialog_modelnotfound.gif");

	public static ImageIcon defaultAgentTypeIcon = createImageIcon(ICON_PATH + "default_agenttype.gif");
	public static ImageIcon defaultSocietyTypeIcon = createImageIcon(ICON_PATH + "default_societytype.gif");

	public TreeIconLoader()
	{

	}

	private static ImageIcon createImageIcon(String path)
	{
		URL imgURL = TreeIconLoader.class.getResource(path);
		if (imgURL != null)
			return new ImageIcon(imgURL);
		else
		{
			System.err.println("Image-file " + path + " not found");
			return null;
		}
	}
}
