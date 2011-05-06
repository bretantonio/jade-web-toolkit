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


package jade.tools.ascml.repository.loader;

import jade.tools.ascml.onto.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.io.File;


public class ImageIconLoader
{
	public static final String ASCML_FRAME_ICON				= "images/logo/logo_frameicon.gif";
	public static final String ASCML_LOGO					= "images/logo/logo.png";
	public static final String ASCML_LOGO_VERSION			= "images/logo/version.png";

	public static final String LIFECYCLE_CREATED			= "images/lifecycle/life_cycle.gif";
	public static final String LIFECYCLE_NOT_RUNNING		= "images/lifecycle/life_cycle.gif";
	public static final String LIFECYCLE_STARTING			= "images/lifecycle/life_cycle.gif";
	public static final String LIFECYCLE_RUNNING			= "images/lifecycle/life_cycle.gif";
	public static final String LIFECYCLE_PARTLY_RUNNING		= "images/lifecycle/life_cycle.gif";
	public static final String LIFECYCLE_STOPPING			= "images/lifecycle/life_cycle.gif";
	public static final String LIFECYCLE_ERROR				= "images/lifecycle/life_cycle.gif";

	public static final String STATUS_RUNNING 				= "images/status/running.gif";
	public static final String STATUS_PARTLY_RUNNING 		= "images/status/partly_running.gif";
	public static final String STATUS_STARTING 				= "images/status/starting_stopping.gif";
	public static final String STATUS_STOPPING 				= "images/status/starting_stopping.gif";
	public static final String STATUS_NOT_RUNNING 			= "images/status/not_running.gif";
	public static final String STATUS_CREATED 				= "images/status/created.gif";
	public static final String STATUS_ERROR 				= "images/status/error.gif";

	public static final String STATUS_MODEL_ERROR 			= "images/status/model_error.gif";
	public static final String STATUS_MODEL_REFERENCE_ERROR	= "images/status/model_reference_error.gif";

	public static final String AGENTTYPE 					= "images/agent/agenttype_50x50.gif";
	public static final String AGENTINSTANCE				= "images/agent/agentinstance_50x50.gif";
	public static final String SOCIETYTYPE 					= "images/society/societytype_50x50.gif";
	public static final String SOCIETYINSTANCE				= "images/society/societyinstance_16x16.gif";
	public static final String AGENT_DESCRIPTION			= "images/society/agent_desc_16x16.gif";
	public static final String RUNNING_INSTANCES			= "images/society/running_instances_16x16.gif";
	public static final String REFERENCE_DESCRIPTION		= "images/society/reference_desc_16x16.gif";
	public static final String SOCIETYINSTANCE_REFERENCE_LOCAL	= "images/society/local_societyinstance_reference_16x16.gif";
	public static final String SOCIETYINSTANCE_REFERENCE_REMOTE	= "images/society/remote_societyinstance_reference_16x16.gif";

	public static final String ADD_AGENTTYPE				= "images/agent/basic_add_agent.gif";

	public static final String EXCEPTION_HEAD				= "images/exception/exception_head.gif";
	public static final String EXCEPTION_MAIN				= "images/exception/exception_main.gif";
	public static final String EXCEPTION_DETAILS			= "images/exception/exception_details.gif";

	public static final String TREEVIEW_BASIC				= "images/treeview/basic.gif";
	public static final String TREEVIEW_EXPERT				= "images/treeview/expert.gif";

	public static final String TREEICON_BASIC_ADD			= "images/misc_treeicons/basic_add.gif";
	public static final String TREEICON_FOLDER_AGENTTYPES	= "images/misc_treeicons/folder_blue.gif";
	public static final String TREEICON_FOLDER_SOCIETYTYPES	= "images/misc_treeicons/folder_yellow.gif";

	public static final String BUTTON_APPLY		= "images/button/apply.png";
	public static final String BUTTON_SAVE		= "images/button/save.png";
	public static final String BUTTON_START		= "images/button/start.png";
    public static final String BUTTON_ADD		= "images/button/add.png";
	public static final String BUTTON_EDIT		= "images/button/edit.png";
	public static final String BUTTON_REMOVE	= "images/button/remove.png";
	public static final String BUTTON_CLOSE		= "images/button/cancel.png";

	private static HashMap imageCache = new HashMap();
	private static HashMap scaledImageCache = new HashMap();


	/**
	 *  Load and scale an image.
	 *  @param path  The icon-path.
	 *  @param width  (Scaled) width of the icon.
	 *  @param height  (Scaled) height of the icon.
	 *  @return  ImageIcon-object of the icon to load or null if icon-file could not be loaded.
	 */
	public static ImageIcon createImageIcon(String path, int width, int height)
	{
		ImageIcon returnIcon = createImageIcon(path);
		if (returnIcon != null)
			return scaleImageIcon(returnIcon, width, height);
		return null;
	}

	/**
	 *  Load an image.
	 *  @param path  The image-path.
	 *  @return  ImageIcon-object of the icon to load or null if icon-file could not be loaded.
	 */
	public static ImageIcon createImageIcon(String path)
	{
		if ((path == null) || (path.trim().equals("")))
			return null;

		if (imageCache.containsKey(path))
		{
			return (ImageIcon)imageCache.get(path);
		}
		else
		{
			ImageIcon returnIcon = null;
            try
			{
				String newPath = null;
				// this for-loop is perhaps a little bit dirty, but it's necessary because
				// icon-paths may be specified with unix- or windows-style and additionally the
				// classloader needs an extra-format (i believe it unix-style, but i'm not sure.
				// So a problem occurs when loading images from a jar, when using windows.
				// Following loop tries to solve this with trying each possible setting
				for (int i=0; (i < 3) && (returnIcon == null); i++)
				{
					if (i==0)
						newPath = path;
					else if (i==1)
						newPath = path.replace('/', '\\');
					else if (i==2)
						newPath = path.replace('\\', '/');

					try
					{
						// load via ClassLoader, path is interpreted relative to this class'-location
						URL imgURL = ImageIconLoader.class.getResource(newPath);
						if (imgURL == null)
							imgURL = ImageIconLoader.class.getClassLoader().getResource(newPath);
						if (imgURL == null)
							imgURL = new URL(newPath);

						returnIcon = new ImageIcon(imgURL);
						imageCache.put(path, returnIcon);
					}
					catch(Exception mexc)
					{
						// not important
					}
				}

				if (returnIcon == null)
					returnIcon = new ImageIcon(path);
				if (returnIcon == null)
					throw new Exception();
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
				System.err.println("ImageIconLoader: Failed to load image-file from " + path);
			}

			return returnIcon;
		}
	}

	public static ImageIcon scaleImageIcon(ImageIcon imageIcon, int width, int height)
	{
		String key = imageIcon.toString() + width + height;

		if (scaledImageCache.containsKey(key))
		{
			return (ImageIcon)scaledImageCache.get(key);
		}
		else
		{
			ImageIcon scaledIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
			scaledImageCache.put(key, scaledIcon);
			return scaledIcon;
		}
	}

	public static ImageIcon createRunnableStatusIcon(Status status, int width, int height)
	{
		if (status instanceof Starting)
			return createImageIcon(STATUS_STARTING, width, height);
		else if (status instanceof Functional)
			return createImageIcon(STATUS_RUNNING, width, height);
		if (status instanceof NonFunctional)
			return createImageIcon(STATUS_NOT_RUNNING, width, height);
		else if (status instanceof Stopping)
			return createImageIcon(STATUS_STOPPING, width, height);
		else if (status instanceof jade.tools.ascml.onto.Error)
			return createImageIcon(STATUS_ERROR, width, height);
		else if (status instanceof Born)
			return createImageIcon(STATUS_CREATED, width, height);
		else if (status instanceof Dead)
			return createImageIcon(STATUS_NOT_RUNNING, width, height);
        return null;
	}	
}
