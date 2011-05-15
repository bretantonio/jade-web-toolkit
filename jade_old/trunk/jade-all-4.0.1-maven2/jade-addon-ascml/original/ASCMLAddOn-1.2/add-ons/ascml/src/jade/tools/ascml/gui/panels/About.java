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
import jade.tools.ascml.gui.BrowserLauncher;
import jade.tools.ascml.exceptions.ResourceNotFoundException;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.io.IOException;

public class About extends AbstractPanel implements HyperlinkListener
{
    private final static String HTML_STRING = "<html><table><tr><td><font face=\"Arial, Verdana\" size=\"-1\"><b>Contributors:</b><ul><li>Dirk Bade</li><li>Matthias B&auml;tzold</li><li>Lars Braubach</li><li>Fabian Emmes</li><li>Marek Jawurek</li><li>Karl-Heinz Krempels</li><li>Sven Lilienthal</li><li>Tim Niemueller</li><li>Andriy Panchenko</li><li>Alexander Pokahr</li><li>Sumedha Widyadharma</li></ul></font></td><td align=center><font face=\"Arial, Verdana\" size=\"-1\">&nbsp;<br><font face=\"Arial, Verdana\" size=\"-1\"><b>ASCML-Homepage</b><br><a href=\"http://www-i4.informatik.rwth-aachen.de/ascml\">www-i4.informatik.rwth-aachen.de/ascml</a></font><br><br><font face=\"Arial, Verdana\" size=\"-1\">University of Technology Aachen<br><a href=\"http://www-i4.informatik.rwth-aachen.de\">www-i4.informatik.rwth-aachen.de</a><br><br>University of Hamburg<br><a href=\"http://vsis-www.informatik.uni-hamburg.de\">vsis-www.informatik.uni-hamburg.de</a><br>&nbsp;<br>Germany, Spring '06</font></td></tr><tr><td colspan=2 align=center><font face=\"Arial, Verdana\" size=\"-1\">This project is supported by the German Research Foundation<br><a href=\"http://www.dfg.de\">www.dfg.de</a><br>Project SPP1083</font></td></table></html>";

	private JEditorPane editorPane;
    private URL url;
	private boolean viewingAboutPage;

	public About(final AbstractMainPanel mainPanel)
	{
		super(mainPanel);

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		initEditorPane();
        viewingAboutPage = true;

		this.add(new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO)), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		this.add(editorPane, new GridBagConstraints(0, 1, 1, 1, 0.5, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		// this.add(new JLabel(UNI_STRING), new GridBagConstraints(1, 1, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		// this.add(new JLabel("<html><center>This project is supported by the German Research Foundation<br><a href=\"\">http://www.dfg.de/</a><br>Project SPP1083</center></html>"), new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

	}

	private void initEditorPane()
	{
		editorPane = new JEditorPane("text/html", HTML_STRING);
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(this);
		editorPane.addMouseListener(new MouseAdapter()
		{
			// When popup trigger, open url in external browser.
			public void mousePressed(MouseEvent e)
			{
				if ( e.isPopupTrigger() || ( e.isControlDown() && e.getButton() == 1 ) || ( e.getButton() == 3) )
				{
					JPopupMenu menu = new JPopupMenu();
					if (!viewingAboutPage)
					{
						menu.add(new AbstractAction("Back to About-Page")
						{
							public void actionPerformed(ActionEvent e)
							{
								viewingAboutPage = true;
								editorPane.setContentType("text/html");
								editorPane.setText(HTML_STRING);
							}
						});
					}
					if (url != null)
					{
						if (!viewingAboutPage)
							menu.addSeparator();

						menu.add(new AbstractAction("Open in this Window")
						{
							public void actionPerformed(ActionEvent e)
							{
								try
								{
									viewingAboutPage = false;
									editorPane.setPage(url);
								}
								catch(IOException exc)
								{
									mainPanel.throwExceptionEvent(new ResourceNotFoundException("Error while opening URL '"+url+"'",exc));
								}
							}
						});
						menu.add(new AbstractAction("Open in external Browser")
						{
							public void actionPerformed(ActionEvent e)
							{
								try
								{
									BrowserLauncher.openURL(url.toString());
								}
								catch(IOException exc)
								{
									mainPanel.throwExceptionEvent(new ResourceNotFoundException("Error while opening URL '"+url+"'",exc));
								}
							}
						});
					}

					if (menu.getComponentCount() > 0)
						showPopup(menu, e.getPoint().getX(), e.getPoint().getY());
				}
			}
		});

		this.repaint();

	}

	private void showPopup(JPopupMenu menu, double x, double y)
	{
		menu.show(editorPane, (int)x, (int)y);
	}

	public void hyperlinkUpdate(HyperlinkEvent event)
	{
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			try
			{
				BrowserLauncher.openURL(url.toString());
			}
			catch(IOException exc)
			{
				mainPanel.throwExceptionEvent(new ResourceNotFoundException("Error while opening URL '"+url+"'",exc));
			}
		}
		else if(event.getEventType()==HyperlinkEvent.EventType.ENTERED)
		{
			url = event.getURL();
		}
		else if(event.getEventType()==HyperlinkEvent.EventType.EXITED)
		{
			url = null;
		}
	}
}
