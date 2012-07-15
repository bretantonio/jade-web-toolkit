package com.abreqadhabra.freelec.java.workshop.addressbook.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import suncertify.db.Data;

public class ClientView extends JPanel implements ActionListener {

    private ClientController _controller;

    private boolean _isAllowRowSelection = true;

    private boolean _isSelectedRow = false;

    protected String _selectedRowFlightNumber;

    private JFrame _frame;

    protected JTable _dataTable;

    private JTable _resultTable;

    private JComboBox[] _findOptionComboBox;

    private String _findButtonString = "Find";

    private String _bookButtonString = "Book";

    private String[] _findOptionLabels = {"Origin airport",
	    "Destination airport", "Carrier", "Day" };

    /**
     * @param controller
     */
    public ClientView(ClientController controller) {
	super(new GridBagLayout());
	this._controller = controller;
	JDialog.setDefaultLookAndFeelDecorated(true);
	createOptionDialog(_controller);
	GridBagLayout gridbag = (GridBagLayout) getLayout();
	GridBagConstraints c = new GridBagConstraints();
	JScrollPane dataTableScrollPane = createDataTableScrollPane();
	c.fill = GridBagConstraints.BOTH; // Fill entire cell.
	c.weighty = 1.0; // Button area and message area have equal
	// height.
	c.gridwidth = GridBagConstraints.REMAINDER; // end of row
	gridbag.setConstraints(dataTableScrollPane, c);
	this.add(dataTableScrollPane);
	JPanel controlPanel = createControlPanel();
	c.fill = GridBagConstraints.BOTH; // Fill entire cell.
	c.weighty = 1.0; // Button area and message area have equal
	// height.
	c.gridwidth = GridBagConstraints.REMAINDER; // end of row
	gridbag.setConstraints(controlPanel, c);
	this.add(controlPanel);
	JScrollPane resultTableScrollPane = createResultTableScrollPane();
	c.fill = GridBagConstraints.BOTH; // Fill entire cell.
	c.weighty = 1.0; // Button area and message area have equal
	// height.
	c.gridwidth = GridBagConstraints.REMAINDER; // end of row
	gridbag.setConstraints(resultTableScrollPane, c);
	this.add(resultTableScrollPane);

	createAndShowGUI();

    }

    /**
     * @param controller
     */
    private void createOptionDialog(ClientController controller) {
	String[] options = {
		ClientController.resources
			.getString("GUI.OptionDialog.ClientMode.Local"),
		ClientController.resources
			.getString("GUI.OptionDialog.ClientMode.Remote"),
		ClientController.resources.getString("Default.Exit") };
	int value = JOptionPane.showOptionDialog(this,
		ClientController.resources
			.getString("GUI.OptionDialog.ClientMode.Message"),
		ClientController.resources.getString("Default.Title"),
		JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
		options, options[0]);
	if (value == JOptionPane.CLOSED_OPTION) {
	    System.exit(1);
	} else if (options[value].equals(ClientController.resources
		.getString("GUI.OptionDialog.ClientMode.Local"))) {
	    // ClientController.printLogMessage("Error.INFO",
	    // "GUI.OptionDialog.ClientMode.Local.Log");
	     controller.setDatabaseMode(ClientController.resources
	     .getString("GUI.OptionDialog.ClientMode.Local"));
	} else if (options[value].equals(ClientController.resources
		.getString("GUI.OptionDialog.ClientMode.Remote"))) {

	    int isDefaultURLFormat = JOptionPane
		    .showConfirmDialog(
			    this,
			    "Are you using localhost for remote access?\nDefault value is rmi://localhost:1099/FlyByNightService",
			    ClientController.resources
				    .getString("Default.Title"),
			    JOptionPane.YES_NO_OPTION);
	    if (isDefaultURLFormat == 1) {
		String hostInfo = JOptionPane.showInputDialog(this,
			"Please input the host:port\ne.g. 127.0.0.1:1099",
			"INPUT", JOptionPane.QUESTION_MESSAGE);

		// controller.setUrlFormat("rmi://" + hostInfo +
		// "/FlyByNightService");
	    }
	    controller.setDatabaseMode(ClientController.resources
		    .getString("GUI.OptionDialog.ClientMode.Remote"));
	    // ClientController.printLogMessage("Error.INFO",
	    // "GUI.OptionDialog.ClientMode.Remote.Log");

	    // TODO url format input OptionDialog

	} else if (options[value].equals(ClientController.resources
		.getString("Default.Exit"))) {
	    System.exit(1);
	}
    }

    /**
     * Gets the main data for the "Filght Information".
     * 
     * @return Returns the JScrollPane for the "Filght Information".
     * 
     * JTable is created using a suncertify.client.DataTableModel to use the
     * AbstractTableModel as the base class for creating subclasses.
     */
    private JScrollPane createDataTableScrollPane() {
	JScrollPane scrollPane = null;
	_dataTable = new JTable(getTableModel());
	_dataTable.setPreferredScrollableViewportSize(new Dimension(640, 320));
	_dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	if (_isAllowRowSelection) { // true by default
	    ListSelectionModel lsm = _dataTable.getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    if (e.getValueIsAdjusting()) {
			return;
		    }
		    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		    if (lsm.isSelectionEmpty()) {
			// System.out.println("No rows are selected.");
		    } else {
			int selectedRow = lsm.getMinSelectionIndex();
			_selectedRowFlightNumber = (String) _dataTable
				.getValueAt(selectedRow, 0);
			System.out.println("Row " + selectedRow
				+ " is now selected."
				+ _selectedRowFlightNumber);
			_isSelectedRow = true;
			setResultTable(_selectedRowFlightNumber.trim());
		    }
		}
	    });
	} else {
	    _dataTable.setRowSelectionAllowed(false);
	}
	scrollPane = new JScrollPane(_dataTable);
	scrollPane
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane
		.setBorder(BorderFactory
			.createCompoundBorder(
				BorderFactory
					.createTitledBorder("Flight Information - Fly By Night Service"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	return scrollPane;
    }

    /**
     * Gets the Control Panel for the "Find" and "Book" command.
     * 
     * @return Returns the JPanel for the Control Panel.
     * 
     * The clients are able to select the "Origin airport", "Destination
     * airport", "Carrier" and "Day" of filght that find to flight
     * information. All information will be shown up if select the "Any"
     * having a wildcard-like feature is chosen.
     */
    private JPanel createControlPanel() {
	JPanel panel = new JPanel();
	_findOptionComboBox = new JComboBox[_findOptionLabels.length];
	TreeSet[] comboBoxItems = {new TreeSet(), new TreeSet(),
		new TreeSet(), new TreeSet()};
	int count = _controller.getRecordCount();
	for (int i = 0; i < count; i++) {
	    if (i == 0) {
		comboBoxItems[0].add(" ANY");
		comboBoxItems[1].add(" ANY");
		comboBoxItems[2].add(" ANY");
		comboBoxItems[3].add(" ANY");
	    }
	    String[] recordValues = _controller.getRecord(i + 1);
	    comboBoxItems[0].add(recordValues[1]);
	    comboBoxItems[1].add(recordValues[2]);
	    comboBoxItems[2].add(recordValues[3]);
	    comboBoxItems[3].add(recordValues[5]);
	}
	for (int i = 0; i < _findOptionLabels.length; i++) {
	    JPanel eachPanel = new JPanel();
	    JLabel label = new JLabel(_findOptionLabels[i], JLabel.TRAILING);
	    eachPanel.add(label);
	    _findOptionComboBox[i] = new JComboBox(comboBoxItems[i].toArray());
	    _findOptionComboBox[i].setEditable(false);
	    label.setLabelFor(_findOptionComboBox[i]);
	    eachPanel.add(_findOptionComboBox[i]);
	    panel.add(eachPanel);
	}
	JButton findButton = new JButton(_findButtonString);
	findButton.setVerticalTextPosition(AbstractButton.CENTER);
	findButton.setMnemonic(KeyEvent.VK_F);
	findButton.setPreferredSize(new Dimension(50, 30));
	// findPersonButton.setToolTipText("Click to button as ...);
	findButton.setBorder(BorderFactory.createRaisedBevelBorder());
	findButton.setActionCommand(_findButtonString);
	findButton.addActionListener(this);
	panel.add(findButton);

	JButton bookButton = new JButton(_bookButtonString);
	bookButton.setVerticalTextPosition(AbstractButton.CENTER);
	bookButton.setMnemonic(KeyEvent.VK_B);
	bookButton.setPreferredSize(new Dimension(50, 30));
	bookButton
		.setToolTipText("Please select one in the current flight information before click the book button");
	bookButton.setBorder(BorderFactory.createRaisedBevelBorder());
	bookButton.setActionCommand(_bookButtonString);
	bookButton.addActionListener(this);
	panel.add(bookButton);

	panel
		.setBorder(BorderFactory
			.createCompoundBorder(
				BorderFactory
					.createTitledBorder("Find the flight / Book the available seat"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	return panel;
    }

    /**
     * Gets the Result Table for the client is seleted specific one row in
     * "Filght Information".
     * 
     * @return Returns the JScrollPane for the Result Table.
     * 
     * Clients must be selected one in "Filght Information" before "Booking"
     * process.
     */
    private JScrollPane createResultTableScrollPane() {
	JScrollPane scrollPane = null;
	FilghtInformationTableModel tm = setColumnHeader();
	_resultTable = new JTable(tm);
	_resultTable.setPreferredScrollableViewportSize(new Dimension(640, 20));
	_resultTable.setRowSelectionAllowed(false);
	scrollPane = new JScrollPane(_resultTable);
	// scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
		.createTitledBorder("Selected filght information"),
		BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	return scrollPane;
    }

    /**
     * Gets the DataTableModel reroaded by all the records of the file,
     * db.db at the main data for the "Filght Information" as Data Table.
     * 
     * @return Returns the DataTableModel for the Result Table.
     */
    private FilghtInformationTableModel getTableModel() {
	FilghtInformationTableModel tm = new FilghtInformationTableModel(); // Create
	// AbstractTableModel

	    String[] columnHeader = _controller.getColumnHeader();
	    tm.addColumnHeader(columnHeader); // set Column Header
	    int totalCount = _controller.getRecordCount(); // Total Record
	    // Count
	    for (int j = 1; j <= totalCount; j++) {
		String[] recordValues = _controller.getRecord(j);
		tm.addRow(recordValues); // set each rows
	    }

	return tm;
    }

    /**
     * Sets the Result Table for the specific "Flight Number"'s data in
     * "Filght Information".
     * 
     */
    private void setResultTable(String flightNumber) {
	FilghtInformationTableModel tm = setColumnHeader();
	tm.addRow(_controller.getRecordByFlightNumber(flightNumber));
	_resultTable.setModel(tm);
    }

    /**
     * Sets the JTable's Column Header. This method picks good column sizes.
     */
    private FilghtInformationTableModel setColumnHeader() {
	FilghtInformationTableModel tm = null;
	String[] columnHeaders = _controller.getColumnHeader();
	tm = new FilghtInformationTableModel(); // Create AbstractTableModel
	tm.addColumnHeader(columnHeaders); // set Column Header
	return tm;
    }

    /**
     * Create the GUI and show it.
     */
    private void createAndShowGUI() {
	JFrame.setDefaultLookAndFeelDecorated(true);
	_frame = new JFrame("Client: Fly By Night Service");
	_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	_frame.setContentPane(this);
	// frame.setJMenuBar(createMenuBar());
	_frame.pack();
	_frame.setVisible(true);
    }

    /**
     * Reset of client's seletion for specific row Event in "Filght
     * Information".
     */
    private void resetRowSelection(JTable table) {
	_isSelectedRow = false;
	_dataTable.clearSelection();
	_resultTable.clearSelection();
	FilghtInformationTableModel tm = setColumnHeader();
	_dataTable.setModel(getTableModel());
	_resultTable.setModel(tm);
    }

    protected final boolean confirmReservation(int recordNumber,
	    String[] recordValues, String availableSeat) {
	return _controller.requestBook(recordNumber, recordValues,
		availableSeat);
    }

    /**
     * Event Handling for the "Find" and "Book" command.
     */
    public void actionPerformed(ActionEvent e) {
	if (_findButtonString.equals(e.getActionCommand())) {
	    String criteria = "";
	    for (int i = 0; i < _findOptionLabels.length; i++) {
		if (i == _findOptionLabels.length - 1) {
		    criteria += _findOptionLabels[i] + Data.DELIMITER_SECOND
			    + (String) _findOptionComboBox[i].getSelectedItem();
		} else {
		    criteria += _findOptionLabels[i] + Data.DELIMITER_SECOND
			    + (String) _findOptionComboBox[i].getSelectedItem()
			    + Data.DELIMITER_FIRST;
		}
	    }
	    resetRowSelection(_resultTable);
	    FilghtInformationTableModel tm = setColumnHeader();
	    Object[] resultSet = _controller.criteriaFind(criteria);
	    if (resultSet != null) {
		for (int i = 0; i < resultSet.length; i++) {
		    tm.addRow((Object[]) resultSet[i]); // set each rows
		}
		_dataTable.setModel(tm);
	    } else {
		JOptionPane.showMessageDialog(this,
			"No flights met your search criteria", "Find",
			JOptionPane.WARNING_MESSAGE);
		_dataTable.setModel(getTableModel());
	    }
	} else if (_bookButtonString.equals(e.getActionCommand())) {

	    if (!_isSelectedRow) {
		JOptionPane
			.showMessageDialog(
				this,
				"Please select one in the current flight\ninformation before click the book button",
				"Booking", JOptionPane.WARNING_MESSAGE);
	    } else {
		int selectedRowRecordNumber = _controller
			.getRecordNumberByFlightNumber(_selectedRowFlightNumber);
		String[] recordLables = _controller.getColumnHeader();
		String[] recordValues = _controller
			.getRecordByFlightNumber(_selectedRowFlightNumber);

		BookDialog.showDialog(_frame, _frame, selectedRowRecordNumber,
			recordLables, recordValues, this);
		resetRowSelection(_resultTable);
	    }
	}
    }
}

/**
 * FilghtInformationTableModel Class to use the AbstractTableModel as the base
 * class for creating subclasses.
 */
class FilghtInformationTableModel extends AbstractTableModel {

    private Vector _dataSet = new Vector();

    private String[] _columnHeader;

    /**
     * @param ch
     */
    public void addColumnHeader(final String[] ch) {
	_columnHeader = ch;
    }

    /**
     * @param row
     */
    protected void addRow(final Object[] row) {
	if (row.length > 0 || row.length <= _columnHeader.length) {
	    _dataSet.add(row);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(final int col) {
	return _columnHeader[col];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public int getRowCount() {
	return _dataSet.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public int getColumnCount() {
	return _columnHeader.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public Object getValueAt(final int rowIdx, final int colIdx) {
	Object[] rowData = (Object[]) _dataSet.get(rowIdx);
	return rowData[colIdx];
    }
}

/**
 * The custom Dialog class for "Book" menu event.
 */
class BookDialog extends JDialog implements ActionListener {

    private static BookDialog _dialog;

    private String[] _recordLables;

    private String[] _recordValues;

    private ClientView _gui;

    private int _recordNumber;

    private int _availableSeat;

    private String _bookButtonString = "Book";

    private String _closeButtonString = "Close";

    private JSpinner _reservationSeatSpinner;

    /**
     * In this method, Client can catch the information and book the
     * available seats for the specific filght at the Fly By Night Service.
     */
    public static void showDialog(Component frameComp, Component locationComp,
	    int recordNumber, String[] recordLables, String[] recordValues,
	    ClientView view) {
	Frame frame = JOptionPane.getFrameForComponent(frameComp);
	_dialog = new BookDialog(frame, locationComp, recordNumber,
		recordLables, recordValues, view);
	_dialog.setVisible(true);
    }

    private BookDialog(Frame frame, Component locationComp, int recordNumber,
	    String[] recordLables, String[] recordValues, ClientView gui) {
	super(frame, "BOOK", true);
	this._recordLables = recordLables;
	this._recordValues = recordValues;
	this._recordNumber = recordNumber;
	this._gui = gui;

	JPanel recordPanel = createRecordPanel(recordLables, recordValues);
	JPanel bookPanel = createBookPanel();
	Container contentPane = this.getContentPane();
	contentPane.add(recordPanel, BorderLayout.CENTER);
	contentPane.add(bookPanel, BorderLayout.SOUTH);
	this.pack();
	this.setLocationRelativeTo(locationComp);
    }

    private JPanel createRecordPanel(String[] recordLables,
	    String[] recordValues) {
	JPanel panel = new JPanel(new GridLayout((recordLables.length / 2) + 1,
		2));

	for (int i = 0; i <= recordLables.length; i++) {
	    JPanel eachPanel = new JPanel(new GridLayout(1, 2));
	    eachPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

	    if (i == recordLables.length) {
		JLabel label = new JLabel("Reservation seats", JLabel.LEFT);
		eachPanel.add(label);
		_availableSeat = new Integer(recordValues[i - 1].trim())
			.intValue();
		_reservationSeatSpinner = new JSpinner(new SpinnerNumberModel(
			0, 0, _availableSeat, 1));
		JFormattedTextField jtf = ((JSpinner.DefaultEditor) _reservationSeatSpinner
			.getEditor()).getTextField();
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(
			_reservationSeatSpinner);
		_reservationSeatSpinner.setEditor(editor);		
		jtf.setBackground(Color.white);
		jtf.setEditable(false);
		eachPanel.add(_reservationSeatSpinner);
	    } else {
		JLabel label = new JLabel(recordLables[i], JLabel.LEFT);
		eachPanel.add(label);
		JTextField textField = new JTextField(recordValues[i].trim(),
			10);
		textField.setEditable(false);
		eachPanel.add(textField);
	    }
	    panel.add(eachPanel);
	}
	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	return panel;
    }

    private JPanel createBookPanel() {
	JPanel panel = new JPanel();
	JButton bookButton = new JButton("Book");
	bookButton.setVerticalTextPosition(AbstractButton.CENTER);
	bookButton.setMnemonic(KeyEvent.VK_B);
	bookButton.setPreferredSize(new Dimension(50, 30));
	bookButton.setBorder(BorderFactory.createRaisedBevelBorder());
	bookButton.setActionCommand("Book");
	bookButton.addActionListener(this);
	panel.add(bookButton);
	JButton closeButton = new JButton("Close");
	closeButton.setVerticalTextPosition(AbstractButton.CENTER);
	closeButton.setMnemonic(KeyEvent.VK_B);
	closeButton.setPreferredSize(new Dimension(50, 30));
	closeButton.setBorder(BorderFactory.createRaisedBevelBorder());
	closeButton.setActionCommand("Close");
	closeButton.addActionListener(this);
	panel.add(closeButton);

	return panel;
    }

    public void actionPerformed(ActionEvent e) {
	if ("Book".equals(e.getActionCommand())) {
	    if (_availableSeat == 0) {
		JOptionPane
			.showMessageDialog(
				this,
				"Sorry, this flight is fully booked\nThere are no more seats are available",
				"Booking about available seats",
				JOptionPane.WARNING_MESSAGE);
	    } else {
		int reservationSeat = new Integer((_reservationSeatSpinner
			.getValue()).toString()).intValue();
		if (reservationSeat == 0) {
		    JOptionPane.showMessageDialog(this,
			    "You haven't choosen the seat ",
			    "Booking about available seats ",
			    JOptionPane.INFORMATION_MESSAGE);
		} else {
		    int remainderSeat = _availableSeat - reservationSeat;
		    System.out
			    .println("Available seats - Reservation seats  = Remainder seats: "
				    + _availableSeat
				    + " - "
				    + reservationSeat
				    + " = " + remainderSeat);
		    String options[] = {"Yes", "No" };
		    int bookValue = JOptionPane.showOptionDialog(this,
			    "This is " + _recordValues[3].trim() + " flight "
				    + _recordValues[0].trim() + " bound for "
				    + _recordValues[1].trim() + " via "
				    + _recordValues[2].trim() + "\n\n"
				    + "Confirm your reservation : " + /*
				     * +
				     * reservationSeat +
				     */"Are you sure? Check it again carefully",
			    "Booking Reconfirmation",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.WARNING_MESSAGE, null, options,
			    options[0]);

		    if (bookValue == JOptionPane.CLOSED_OPTION) {
			System.out.println("Closed window");
		    } else if (options[bookValue].equals("Yes")) {

			_recordValues[8] = Integer.toString(remainderSeat);
			if (_gui.confirmReservation(_recordNumber,
				_recordValues, String.valueOf(_availableSeat))) {

			    // TODO success MESSAGE
			} else {
			    // TODO failure MESSAGE
			}
			this.setVisible(false);

		    }
		}
	    }
	} else if ("Close".equals(e.getActionCommand())) {
	    this.setVisible(false);
	}
    }
}
