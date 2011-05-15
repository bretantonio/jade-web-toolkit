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

import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.gui.dialogs.AbstractDialog;
import jade.tools.ascml.gui.dialogs.ExceptionDialog;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.launcher.AgentLauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractMainPanel extends AbstractPanel
{

	public final static String ADD_AGENTTYPE_DIALOG				= "add_agenttype_dialog";
	public final static String ADD_SOCIETYTYPE_DIALOG			= "add_societytype_dialog";
	public final static String ADD_PROJECT_DIALOG				= "add_project_dialog";
	public final static String CHOOSE_ICON_DIALOG				= "choose_icon_dialog";
	public final static String CHOOSE_DIRECTORY_DIALOG			= "choose_directory_dialog";
	public final static String CHOOSE_AGENTTYPE_FILE_DIALOG		= "choose_agenttype_file_dialog";
	public final static String CHOOSE_SOCIETYTYPE_FILE_DIALOG	= "choose_societytype_file_dialog";
	public final static String CHOOSE_JAVA_FILE_DIALOG			= "choose_java_file_dialog";
	public final static String EXIT_DIALOG						= "show_exit_dialog";

	public final static String CROSS_PLATFORM_LOOK_AND_FEEL		= "crossplatform";
	public final static String SYSTEM_LOOK_AND_FEEL				= "system";
	public final static String METAL_LOOK_AND_FEEL				= "javax.swing.plaf.metal.MetalLookAndFeel";
	public final static String WINDOWS_LOOK_AND_FEEL			= "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public final static String GTK_LOOK_AND_FEEL				= "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	public final static String MOTIF_LOOK_AND_FEEL				= "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

	// ----------------- End of constant-declaration -----------------

	private JFrame startupFrame;
    private Repository repository;

	/**
	 * This constructor creates an AbstractMainPanel, it should only be used by 'real' MainPanel,
	 * that means, each GUI (no matter if application or applet) has exactly one MainPanel, which
	 * manages the viewing of contentPanels, maybe the RepositoryTree, and so on.
	 * @param repository  The global repository
	 */
	public AbstractMainPanel(JFrame parentFrame, Repository repository)
	{
		super();
		this.repository = repository;

		super.parentFrame = parentFrame;
        super.setMainPanel(this);

		setLookAndFeel(repository.getProperties().getLookAndFeel());
		
		// prepare and show the 'splash-screen'
		startupFrame = new JFrame("ASCML is starting ...");
		startupFrame.getContentPane().setLayout(new FlowLayout());

		BufferedImage bi;
		Robot r = null;
		try {
			r = new Robot();
		}
		catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startupFrame.setSize(175, 220);
		startupFrame.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-startupFrame.getWidth()/2),
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-startupFrame.getHeight()/2));
		bi = r.createScreenCapture(new Rectangle(startupFrame.getBounds()));

		Image logo = ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO).getImage();
		Image versionImage = ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO_VERSION).getImage();
		ImagePanel p = new ImagePanel(bi, logo, versionImage, "v. " + AgentLauncher.ASCML_VERSION);

		startupFrame.setContentPane(p);

		// startupFrame.getContentPane().add(new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO)));
		// startupFrame.getContentPane().add(new JLabel("<html><h2>&nbsp;&nbsp;Starting up ...</h2></html>)"));
		// startupFrame.getContentPane().setBackground(Color.WHITE);
		startupFrame.setIconImage(ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO).getImage());
		startupFrame.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-startupFrame.getWidth()/2),
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-startupFrame.getHeight()/2));

		startupFrame.setUndecorated(true);
		startupFrame.setVisible(true);

        this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);
	}

	/**
	 * Has to be called by subclasses, when they finished initializing.
	 * Once the subclass finished, the splashscreen is removed and the user's screen
	 * updated.
	 */
	protected void finishedInit()
	{
		// dispose the startup-frame (defined in super-class)
		startupFrame.setVisible(false);
		startupFrame.dispose(); // just to make sure, the frame is gone...

		this.validate();
	}

	public Repository getRepository()
	{
		return this.repository;
	}

	/**
	 * This method sets a new GUI-view (if different views are supported.
	 * A PropertyChangedEvent is generated so the gui-component which
	 * support different views have to listen to these change-events and readt appropiatly.
	 * @param newView  The identifier for the new view. The identifier might be
	 * one of the following constants BASIC_VIEW or EXPERT_VIEW, defined by Project-class
	 */
	public void setView(String newView)
	{
		getProject().setView(newView);
	}

	/**
	 * This method is called, when the ASCML-GUI is going to shut down.
	 * Everything needed to finalize the gui-objects and to set them to
	 * be not-viewable is done here.
	 */
	public void exit()
	{
		super.exit();
	}

	/**
	 * This method has to be implemented by any MainWindows, because
	 * it is called by other panels, that might want to change the view somehow.
	 *
	 * Set a new panel in the content-area of the application-window.
	 * The content-area might be any container-component withinin the main-JPanel.
	 * In the content-area panels which present detailed information about something
	 * should be shown. For example, the ASCML Main-GUI (normally this class by default)
	 * is parted into four areas, the menuBar, the statusBar, the repository-tree and the content-area.
	 * An ASCML-gui-component may change only the content-area cause the other three parts are static.
	 * @param paneIdentifier  The String-identifier of the pane to view
	 * This should be one of the following constants(declared in TabbedPaneManager):
	 * REPOSITORY, ERROR.
	 * @param tabIdentifier  Either 0, if the default-tab should be viewed,
	 * or the int-identifier of the tab to view.
	 * This should be one of the following constants (declared in TabbedPaneManager):
	 * OPTIONS_TAB, AUTOSEARCH_TAB, HELP_TAB, ABOUT_TAB
	 */
	public abstract void setContentPanel(String paneIdentifier, int tabIdentifier);

	/**
	 * This method has to be implemented by any MainWindows, because
	 * it is called by other panels, that might want to show a dialog to the user.
	 * @param dialogIdentifier  The identifier String (specified in AbstractMainPanel) for the Dialog
	 * If the identifier-String cannot be matched against the default dialog-identifier the method
	 * should try to handle the identifier as a class-name and instantiate it via reflection.
	 */
	public abstract Object showDialog(String dialogIdentifier);

	/**
	 * Show a model-object. The application-/applet-MainPanel has to implement
	 * this method. It is called, when detailed information about the model should
	 * be shown on the screen. For example, the default ApplicationMainPanel dispachtes
	 * the model, that should be shown, to the TabbedPaneManager. This manager then decides
	 * on which set of pane's the information should be shown. 
	 * @param model The model-object that should be viewed
	 */
	public abstract void showModelDetails(Object model);

	/**
	 * Select a model-object. The application-/applet-MainPanel has to implement
	 * this method. It is called, when a model should be selected. For example,
	 * the selection-call is dispatched to the RepositoryTree to select the Model
	 * within the tree.
	 * @param model The model-object that should be selected
	 */
	public abstract void selectModel(Object model);

	/**
	 * Show a dialog. The component who initiates this dialog has to instantiate the object with
	 * the right parameters and then pass it to this method.
	 * This method does nothing more, than calling the showDialog-method on the dialog,
	 * passing it's parentFrame-object as only argument
	 * @param dialog  The dialog, that should be shown.
	 * @return  Dialogs may return a result-object, whatever it is.
	 */
	public Object showDialog(AbstractDialog dialog)
	{
		try
		{
			return dialog.showDialog(parentFrame);
		}
		catch(ModelException me)
		{
			showDialog(new ExceptionDialog(me));
		}
		return null;
	}

	/**
	 * Set the look & feel for the ASCML.
	 * @param lookAndFeelClassName  The look & feel-class name. You may use one of the constants
	 * CROSS_PLATFORM_LOOK_AND_FEEL, SYSTEM_LOOK_AND_FEEL, METAL_LOOK_AND_FEEL,
	 * WINDOWS_LOOK_AND_FEEL, GTK_LOOK_AND_FEEL, MOTIF_LOOK_AND_FEEL or
	 * specify your own class-name
	 */
	public void setLookAndFeel(String lookAndFeelClassName)
	{
		try
		{
			if (lookAndFeelClassName.equals(CROSS_PLATFORM_LOOK_AND_FEEL))
				lookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName();
			else if (lookAndFeelClassName.equals(SYSTEM_LOOK_AND_FEEL))
				lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();

			UIManager.setLookAndFeel(lookAndFeelClassName);
			super.getProperties().setLookAndFeel(lookAndFeelClassName);
			SwingUtilities.updateComponentTreeUI(super.parentFrame);
		}
		catch (Exception exc)
		{
			// look & feel not found, use the cross-platform look and feel
			try
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			catch (Exception exc2)
			{
				// even the platform look and feel is not available, something must be wrong with the
				// installation of the java-runtime-environment
			}
		}
	}

	/**
	 * This method may be called to throw an Exception-event to the exception-listener.
	 * @param exc  The ASCMLException-object that shall be dispatched to the exception-listeners.
	 */
	public void throwExceptionEvent(ASCMLException exc)
	{
		getRepository().throwExceptionEvent(exc);
	}

	/**
	 * This class paints the splash-screen panel
	 */
	class ImagePanel extends JPanel{
		private BufferedImage img1	= null;
		private Image img2			= null;
		private Image img3			= null;
		private String label		= null;

		public ImagePanel(BufferedImage img1, Image img2, Image img3, String label)
		{
			super();
			this.img1 = img1;
			this.img2 = img2;
			this.img3 = img3;
			this.label = label;
		}

		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
        	g.drawImage(img1, 0, 0, null);
			g.drawImage(img2, 0, 0, null);
			g.drawImage(img3, 55, 177, null);
			g.setFont(new Font("Arial", Font.BOLD, 16));
			g.drawString(this.label, 59, 193);

			/* for smooth font-rendering
			RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHints(renderHints);
			*/
		}
	}
}
