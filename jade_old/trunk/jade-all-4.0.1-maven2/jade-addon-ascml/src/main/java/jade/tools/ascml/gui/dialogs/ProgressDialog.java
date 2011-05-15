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


package jade.tools.ascml.gui.dialogs;

import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.ProgressUpdateListener;
import jade.tools.ascml.events.ProgressUpdateEvent;
import jade.tools.ascml.gui.components.StatusBar;

import javax.swing.*;
import java.awt.*;


/**
 *  
 */
public class ProgressDialog extends AbstractDialog implements ProgressUpdateListener
{
    private Repository repository;
	private JProgressBar progressBar;
	private JLabel title;
	private JLabel currentProgress;
    private JPanel contentPane;
	private JDialog dialog;
    private JFrame parentFrame;

	public ProgressDialog(Repository repository, int estimatedProgressCount, String title, String progressString)
	{
		this(repository, title, progressString);
		progressBar.setIndeterminate(false);
		progressBar.setValue(0);
		progressBar.setMaximum(estimatedProgressCount);
		progressBar.setString("Initializing...");
	}

	public ProgressDialog(Repository repository, String title, String progressString)
	{
		super();
		this.repository = repository;
		this.title = new JLabel(title);
        this.currentProgress = new JLabel("Initializing");

		repository.getListenerManager().addProgressUpdateListener(this);

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString(progressString);

		this.contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new GridBagLayout());

		contentPane.add(this.title, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		contentPane.add(this.progressBar, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		contentPane.add(this.currentProgress, new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(5,5,5,5), 0, 0));
	}
	
	public Object showDialog(JFrame parentFrame)
	{
		this.parentFrame = parentFrame;
		parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		dialog = new JDialog(parentFrame, "Please Wait ... !", false);
		dialog.setContentPane(contentPane);

		dialog.setSize(385, 120);
		dialog.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - dialog.getWidth()/2),
		                   (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - dialog.getHeight()/2));
		/* int screenX = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenY = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		dialog.setLocation((int)(Math.random()*screenX-100) % screenX, (int)(Math.random()*screenY-100) % screenY);
		*/
		dialog.setVisible(true);

		return null;
	}

	public void updateProgress(final ProgressUpdateEvent evt)
	{
        if (evt.getEventCode() == ProgressUpdateEvent.PROGRESS_FINISHED)
		{
			repository.getListenerManager().removeProgressUpdateListener(this);
			dialog.setVisible(false);
			dialog.dispose();
			parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			StatusBar.setLabel("Ready");
		}
		else if (evt.getEventCode() == ProgressUpdateEvent.PROGRESS_ADVANCE)
		{
			if (!progressBar.isIndeterminate())
			{
				progressBar.setValue(progressBar.getValue()+1);
                progressBar.setString(progressBar.getValue() + "/" + progressBar.getMaximum());
				currentProgress.setText(evt.getCurrentProgress() + " (" + progressBar.getValue() + "/" + progressBar.getMaximum() + ")");
				StatusBar.setLabel(evt.getCurrentProgress() + " (" + progressBar.getValue() + "/" + progressBar.getMaximum() + ")");

				if (progressBar.getValue() == progressBar.getMaximum())
					repository.throwProgressUpdateEvent(new ProgressUpdateEvent(ProgressUpdateEvent.PROGRESS_FINISHED));
			}
			else
			{
				currentProgress.setText(evt.getCurrentProgress());
				StatusBar.setLabel(evt.getCurrentProgress());
			}
		}
	}
}

