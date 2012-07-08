package suncertify.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

/**
 * This class implements the view(GUI) for sever application.
 * <p>
 *
 * @see ServerController
 * @author Dongsup Kim
 * @version 0.8 19-Dec-2006
 */
public class ServerView extends JPanel implements ActionListener {
    
    private JButton _startupButton;
    
    private JButton _shutDownButton;
    
    private JFileChooser _fileChooser;
    
    private ServerController _controller;
    
    private Logger _logger = Logger.getLogger(this.getClass().getName());
    
    protected JTextArea logTextArea;
    
    /**
     * This constructor create the GUI for sever application.
     *
     * @param controller
     *                The instance of Controller for sever application.
     */
    public ServerView(ServerController controller) {
        super(new BorderLayout());
        this._controller = controller;
        // Create a file chooser
        _fileChooser = new JFileChooser(new File("."));
        _fileChooser.addChoosableFileFilter(new CustomFileFilter());
        _fileChooser.setAcceptAllFileFilterUsed(false);
        // Create a button
        _startupButton = new JButton(ServerController.resources
                .getString("ServerView.Button.Startup"));
        _startupButton.addActionListener(this);
        _shutDownButton = new JButton(ServerController.resources
                .getString("ServerView.Button.ShutDown"));
        _shutDownButton.addActionListener(this);
        _shutDownButton.setEnabled(false);
        // For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); // use FlowLayout
        buttonPanel.add(_startupButton);
        buttonPanel.add(_shutDownButton);
        // Add the buttons to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        // Create and set up the window.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame(ServerController.resources
                .getString("ServerView.Title"));
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                _controller.shuttingDownServer();
            }
        });
        frame.setContentPane(this);
        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        // Handle startup button action.
        if (e.getSource() == _startupButton) {
            int returnVal = _fileChooser.showDialog(ServerView.this,
                    ServerController.resources
                    .getString("ServerView.FileChooser.Title"));
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = _fileChooser.getSelectedFile();
                // This is where a real application would open the file.
                _controller.startServer(file.getName());
                _startupButton.setEnabled(false);
                _shutDownButton.setEnabled(true);
            } else {
                _logger.log(Level.INFO, ServerController.resources
                        .getString("ServerView.INFO.A"));
            }
            // Handle shutdown button action.
        } else if (e.getSource() == _shutDownButton) {
            _controller.shuttingDownServer();
        }
    }
    
    class CustomFileFilter extends FileFilter {
        
        private final static String FILE_EXTENSION = "db";
        
        private final static String FILTER_DESCRIPTION = "database file";
        
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1) {
                extension = s.substring(i + 1).toLowerCase(Locale.getDefault());
            }
            if (extension != null) {
                if (extension.equals(FILE_EXTENSION)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
        
        // The description of this filter
        public String getDescription() {
            return FILTER_DESCRIPTION;
        }
    }
}
