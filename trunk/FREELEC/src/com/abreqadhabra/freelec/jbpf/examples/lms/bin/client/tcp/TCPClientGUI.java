package com.abreqadhabra.freelec.jbpf.examples.lms.bin.client.tcp;

import com.abreqadhabra.freelec.jbpf.examples.lms.user.*;
import com.abreqadhabra.freelec.jbpf.examples.lms.dao.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events


public class TCPClientGUI extends JPanel implements ActionListener, ItemListener {


    protected static final String findNameRadioButtonString = "name";
    protected static final String findSsnRadioButtonString = "ssn";
    protected static final String findUserTextFieldString = "findUserTextField";
    protected static final String findUserButtonString = "findUserButton";
    protected static final String createUserButtonString = "createUserButton";
    protected static final String deleteUserButtonString = "deleteUserButton";
    protected static final String updateUserButtonString = "updateUserButton";
    protected static final String listUserButtonString = "listUserButton";
    protected static final String userNoString = "user_no";
    protected static final String userNameString = "name";
    protected static final String userSsnString = "ssn";
    protected static final String userAgeString = "age";
    protected static final String userAddressString = "address";
    protected static final String userStudentNoString = "studentNo";
    protected static final String userCourseString = "course";
    protected static final String userDepartmentString = "department";
	protected static final String userStudentNoComboBoxString = "studentNo";
    protected static final String userCourseComboBoxString = "course";
    protected static final String userDepartmentComboBoxString = "department";
    protected static final String dataCardString = "데이터타입";
	protected static final String studentDataCardString = "학생";
    protected static final String professorDataCardString = "교수";
    protected static final String employeeDataCardString = "교직원";

	private boolean DEBUG = false;	 //usersTable
	private boolean displaySet;
    private boolean ALLOW_COLUMN_SELECTION = false;	//usersTable
    private boolean ALLOW_ROW_SELECTION = true;	//usersTable
    private boolean isSelectedRow = false;	//usersTable

	protected UserTableDataModel userTDM;
	protected ResultTableDataModel resultTDM;
	protected TCPClient tcpc;

	protected ButtonGroup group;
	protected JComboBox cb ;
	protected JPanel addtionalDataCardPanel;
	protected JTable usersTable;
	protected JTable resultTable;
	protected JTextArea logTextArea;
	protected JTextArea editDisplayTextArea;
	protected JTextField findUserTextField;
	protected JTextField[] defaultDataTextField   = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15)};
	protected JTextField[] addtionalDataTextField =  {new JTextField(15), new JTextField(15), new JTextField(15)};
	
	protected String[] defaultDataLabels = {"회원번호", "이름 ","나이", "주민등록번호","주소" };
	protected String[] addtionalDataLabels = {"학번", "교과목", "부서"};
	protected String comboBoxItems[] = {dataCardString, studentDataCardString, professorDataCardString, employeeDataCardString};
	protected String selectedRowObjectType;
	protected String selectedRowAddtionalData;
	protected String selectedRowSsn;
	protected String originalCategory = null;
	protected String originalSequence = null;

//--------------------------------------------------------------------
//  TCPClientGUI(TCPClient tcpc) 생성자
//--------------------------------------------------------------------

public TCPClientGUI(TCPClient tcpc) {
//set up Grid Bag Layout
	super(new GridBagLayout());
	this.tcpc = tcpc;
	this.displaySet =false;

	JDialog.setDefaultLookAndFeelDecorated(true);
	String options[] = {"id", "pwd"};
	options[0] = JOptionPane.showInputDialog("아이디를 입력해 주세요."); 
	options[1] = JOptionPane.showInputDialog("패스워드를 입력해 주세요."); 
	if(!"admin".equals(options[0]) && !"admin".equals(options[0])){
			 JOptionPane.showMessageDialog(this, "로그인이 실패하였습니다.\n아이디와 패스워드를 정확히\n확인후 재실행하여 주세요", "로그인", JOptionPane.WARNING_MESSAGE); 
			System.exit(0);
	}
	
//	Lay out the text controls and the labels.
	GridBagLayout gridbag = (GridBagLayout)getLayout();
	GridBagConstraints c = new GridBagConstraints();	

//  User 객체 데이터 테이블
	 usersTable  = createUserTable();	
//	Create the scroll pane and add the table to it.
	JScrollPane scrollPane = new JScrollPane(usersTable);
	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.setBorder(
		BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("회원 목록"),
			BorderFactory.createEmptyBorder(5,5,5,5)
		));
//	Add the scroll pane to this panel.
	c.fill = GridBagConstraints.BOTH; //Fill entire cell.
	c.weighty = 1.0;  //Button area and message area have equal height.
	c.gridwidth = GridBagConstraints.REMAINDER; //end of row
	gridbag.setConstraints(scrollPane, c);
	this.add(scrollPane);
// 탭드페인 수정/입력, 시스템 로그
	JPanel editTabbedPanel = createTabbedPanel();
	c.weighty = 1.0;  //Button area and message area have equal height.
	c.fill = GridBagConstraints.BOTH; //Fill entire cell.
	gridbag.setConstraints(editTabbedPanel, c);
	this.add(editTabbedPanel);
//	Make sure we have nice window decorations.
	JFrame.setDefaultLookAndFeelDecorated(true);
//	Create and set up the window.
	JFrame frame = new JFrame("LMS: Learning Management System 클라이언트");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setContentPane(this);
	frame.setJMenuBar(createJMenuBar());
//	Display the window.
	frame.pack();
	frame.setVisible(true);

	}

//--------------------------------------------------------------------
//  User 객체 데이터 테이블
//--------------------------------------------------------------------

private JTable createUserTable(){
//Create a User Ojbect Table
	userTDM = new UserTableDataModel(new ArrayList());
	final JTable  table =  new JTable(userTDM);
	table.setGridColor(Color.DARK_GRAY );
	table.setPreferredScrollableViewportSize(new Dimension(600, 100));

//	단일 선택 리스너	
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//	로우 셀렉션
	if (ALLOW_ROW_SELECTION) { // true by default
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()){
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
//					System.out.println("No rows are selected.");
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					//System.out.println("Row " + selectedRow + " is now selected.");
//	테이블 선택/ 해당 로우의 서버 존재여부 검사
					String selectedSsn = (String) table.getModel().getValueAt(selectedRow , 2);
					findUser(findSsnRadioButtonString,  selectedSsn);
					defaultDataTextField[3].setEditable(false);
				}
			}
		});
	} else {
		table.setRowSelectionAllowed(false);
	}
//	 컬럼 섹렉션
	if (ALLOW_COLUMN_SELECTION) { // false by default
		if (ALLOW_ROW_SELECTION) {
			//We allow both row and column selection, which
			//implies that we *really* want to allow individual
			//cell selection.
			table.setCellSelectionEnabled(true);
		}
		table.setColumnSelectionAllowed(true);
		ListSelectionModel colSM = table.getColumnModel().getSelectionModel();
		colSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()){
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					System.out.println("No columns are selected.");
				}else {
					int selectedCol = lsm.getMinSelectionIndex();
					System.out.println("Column " + selectedCol + " is now selected.");
				}
			}
		});
	}
//테이블 리스너 디버거
	if (DEBUG) {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				printDebugData(table);
			}
		});
	}
		return table;
}

//--------------------------------------------------------------------
//  User 객체 데이터 테이블 디버거 유틸 메쏘드
//--------------------------------------------------------------------

private void printDebugData(JTable table) {
	int numRows = table.getRowCount();
    int numCols = table.getColumnCount();
    javax.swing.table.TableModel model = table.getModel();  
		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j=0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

//--------------------------------------------------------------------
// 탭드페인 생성 (메뉴와 입력/수정폼, 시스템 로그)
//--------------------------------------------------------------------

private  JPanel createTabbedPanel(){
	JPanel panel = new JPanel(new GridLayout(1, 1));
	JTabbedPane tabbedPane = new JTabbedPane();     
	JComponent editPanel = createEditPanel();
	JComponent logPanel = createLogPanel();
	tabbedPane.addTab("메뉴", null, editPanel,  "입력 및 수정") ;
	tabbedPane.addTab("시스템로그", null, logPanel,  "입력 및 수정") ;
	panel.add(tabbedPane);
	return panel;
}

//--------------------------------------------------------------------
// 메뉴 생성 (검색/목록갱신)
//--------------------------------------------------------------------

private JPanel createUserMenu(){
//검색 라디오 버튼	
	final int numButtons = 2;
	JRadioButton[] findUserRadioButtons = new JRadioButton[numButtons];
	group = new ButtonGroup();
	findUserRadioButtons[0] = new JRadioButton("주민등록번호");
	findUserRadioButtons[0].setActionCommand(findSsnRadioButtonString);
	findUserRadioButtons[1] = new JRadioButton("이름");
	findUserRadioButtons[1].setActionCommand(findNameRadioButtonString);
	for (int i = 0; i < numButtons; i++) {
		group.add(findUserRadioButtons[i]);
	}
	findUserRadioButtons[1].setSelected(true);
// 검색 텍스트필드
	findUserTextField = new JTextField(15); 
//	findUserTextField.setPreferredSize(new Dimension(30, 10));
	findUserTextField.setActionCommand(findUserTextFieldString);
	findUserTextField.addActionListener(this);
// 검색 버튼
	JButton findUserButton = new  JButton("검색");
//	findUserButton.setPreferredSize(new Dimension(40, 10));
	findUserButton.setVerticalTextPosition(AbstractButton.CENTER);
	//findUserButton.setMnemonic(KeyEvent.VK_P); //단축키
	findUserButton.setToolTipText("검색어를 입력하고 검색 버튼을 클릭하세요.");
	findUserButton.setActionCommand(findUserButtonString);
	findUserButton.addActionListener(this);
//  목록 갱신버튼
	JButton listUserButton = new  JButton("목록 갱신");
//    listUserButton.setPreferredSize(new Dimension(80, 20));
	listUserButton.setVerticalTextPosition(AbstractButton.CENTER);
	listUserButton.setToolTipText("회원 목록을 최신 데이터로 보기하려면 목록 갱신 버튼을 클릭하세요.");
	listUserButton.setActionCommand(listUserButtonString);
	listUserButton.addActionListener(this);
//  검색 판넬(그리드 레이아웃)
	JPanel panel = new JPanel(new GridLayout(3, 1));
	JPanel radioButtonPanel = new JPanel(new SpringLayout());
	for (int i = 0; i < numButtons; i++) {
		radioButtonPanel.add(findUserRadioButtons[i]);
	}	
//  Spring Layout Utilities  class
	SpringUtilities.makeCompactGrid(radioButtonPanel
																		,1, 2 	//rows, cols
																		,2, 2		//initX, initY
																		,2, 2);  //xPad, yPad
	JPanel findPanel = new JPanel(new SpringLayout());
	findPanel.add(findUserTextField);
	findPanel.add(findUserButton);
	findPanel.add(listUserButton);
//  Spring Layout Utilities  class
	SpringUtilities.makeCompactGrid(findPanel
																		,1, 3 	//rows, cols
																		,2, 2		//initX, initY
																		,2, 2);  //xPad, yPad
	panel.add(radioButtonPanel );
	panel.add(findPanel);
	//panel.add(new JSeparator(SwingConstants.HORIZONTAL));
	panel.add(createMenuPanel());
	return panel;
}

//--------------------------------------------------------------------
// 메뉴 생성 (수정/삭제/입력)
//--------------------------------------------------------------------

private  JPanel createMenuPanel(){
//	전송 및 리셋버튼
	cb = new JComboBox(comboBoxItems);
	cb.setEditable(false);
	cb.setPreferredSize(new Dimension(60, 20));
	cb.addItemListener(this);

//	입력
	JButton createUserButton = new  JButton("입력");
	createUserButton.setPreferredSize(new Dimension(60, 20));
	createUserButton.setVerticalTextPosition(AbstractButton.CENTER);
	createUserButton.setToolTipText("새로운 데이터를 입력하려면 입력 버튼을 클릭하세요.");
	createUserButton.setActionCommand(createUserButtonString);
	createUserButton.addActionListener(this);

//	수정
	JButton updateUserButton = new  JButton("수정");
	updateUserButton.setPreferredSize(new Dimension(60, 20));
	updateUserButton.setVerticalTextPosition(AbstractButton.CENTER);
	updateUserButton.setToolTipText("기존 데이터를 수정하려면 수정 버튼을 클릭하세요.");
	updateUserButton.setActionCommand(updateUserButtonString);
	updateUserButton.addActionListener(this);

	//삭제
	JButton deleteUserButton = new  JButton("삭제");
	deleteUserButton.setPreferredSize(new Dimension(60, 20));
	deleteUserButton.setVerticalTextPosition(AbstractButton.CENTER);
	deleteUserButton.setToolTipText("기존 데이터를 삭제하려면 삭제 버튼을 클릭하세요.");
	deleteUserButton.setActionCommand(deleteUserButtonString);
	deleteUserButton.addActionListener(this);

//	메뉴 판넬(그리드 레이아웃)
	JPanel panel = new JPanel(new SpringLayout());
	 panel.add(cb);
	 panel.add(createUserButton);
	 panel.add(updateUserButton);
	 panel.add(deleteUserButton);

//  Spring Layout Utilities  class
	SpringUtilities.makeCompactGrid(panel  
																		,1, 4 	//rows, cols
																		,2, 2		//initX, initY
																		,2, 2);  //xPad, yPad
	return panel;
}

//--------------------------------------------------------------------
// User 데이터 수정/입력 
//--------------------------------------------------------------------

protected JComponent createEditPanel() {
	JPanel editUserPanel = new JPanel(new GridLayout(1, 2, 2, 2));
//	Lay out the text controls and the labels.
	editUserPanel.setBorder(
								BorderFactory.createCompoundBorder(
									BorderFactory.createTitledBorder("입력/수정 영역"),
									BorderFactory.createEmptyBorder(5,5,5,5)));	
	JPanel defaultDataPanel = new JPanel(new SpringLayout());
	String[] defaultDataActionCommand = {userNoString, userNameString, userSsnString, userAgeString, userAddressString};

	int defaultDataNumPairs = defaultDataLabels.length;
	for (int i = 0; i < defaultDataNumPairs; i++) {
		JPanel  panel = new JPanel(new GridLayout(1, 2, 2, 2));
		JLabel label = new JLabel(defaultDataLabels[i] + ": ", JLabel.TRAILING);
		label.setPreferredSize(new Dimension(50, 20));
		panel.add(label);
		defaultDataTextField[i].setActionCommand(defaultDataActionCommand[i]);
		defaultDataTextField[i].addActionListener(this);
		label.setLabelFor(defaultDataTextField[i]);
		panel.add(defaultDataTextField[i]);
		defaultDataPanel.add(panel);
	}
		defaultDataTextField[0].setEditable(false);
//	 학생/교수/교직원 데이터 입력 카드레이아웃
	addtionalDataCardPanel = new JPanel(new CardLayout());	
	String[] addtionalDataActionCommand = {userStudentNoString , userCourseString, userDepartmentString};
	String[] addtionalConstraint = {studentDataCardString, professorDataCardString, employeeDataCardString};
	int addtionalDataNumPairs = addtionalDataLabels.length;
	for (int i = 0; i < addtionalDataNumPairs; i++) {
		JPanel  panel = new JPanel(new GridLayout(1, 2, 2, 2));
		JLabel label = new JLabel(addtionalDataLabels[i] + ": ", JLabel.TRAILING);
		//label.setPreferredSize(new Dimension(50, 20));
//	Create the "cards".
		panel.add(label);
		addtionalDataTextField[i].setActionCommand(addtionalDataActionCommand[i]);
		addtionalDataTextField[i].addActionListener(this);
		label.setLabelFor(addtionalDataTextField[i]);
		panel.add(addtionalDataTextField[i]);
	   addtionalDataCardPanel.add(panel, addtionalConstraint[i]);
	}
	defaultDataPanel.add(addtionalDataCardPanel);
//	Spring Layout Utilities  class
	SpringUtilities.makeCompactGrid(defaultDataPanel  
																					,6, 1		//rows, cols
																					,2, 2		//initX, initY																					
																					,2, 2);  //xPad, yPad
	editUserPanel.add(defaultDataPanel);
   JPanel panel = new JPanel();     
   	panel.setBorder(
									BorderFactory.createCompoundBorder(
									BorderFactory.createTitledBorder("메뉴"),
									BorderFactory.createEmptyBorder(5,5,5,5)));
//	Create a User Ojbect Table
	resultTDM = new ResultTableDataModel(new ArrayList());
	resultTable =  new JTable(resultTDM);
	resultTable.setGridColor(Color.DARK_GRAY );
	resultTable.setPreferredScrollableViewportSize(new Dimension(500, 50));
//	Create the scroll pane and add the table to it.
	JScrollPane scrollPane = new JScrollPane(resultTable);
	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.setBorder(
		BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("회원목록 선택결과"),
			BorderFactory.createEmptyBorder(5,5,5,5)
		));
	JPanel editPanel = new JPanel(new BorderLayout());
	editPanel.add(scrollPane,  BorderLayout.NORTH);
	editPanel.add(editUserPanel, BorderLayout.CENTER);
//	검색 메뉴(이름/주민등록번호)
	panel.add(createUserMenu());
	editPanel.add(panel,  BorderLayout.EAST);
	return editPanel;
}

//--------------------------------------------------------------------
// 클라이언트 로크 판넬 생성 
//--------------------------------------------------------------------

protected JComponent createLogPanel() {
	JPanel panel = new JPanel(new GridLayout(1, 1));     
	logTextArea = new JTextArea();
	logTextArea.setLineWrap(true);
	logTextArea.setRows(5) ;
	JScrollPane logScrollPane = new JScrollPane(logTextArea);
	logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	panel.add(logScrollPane);
	return panel;
}

//--------------------------------------------------------------------
// 메뉴바 생성
//--------------------------------------------------------------------

protected JMenuBar createJMenuBar(){
	JMenuItem connect = new JMenuItem("접속하기(C)"); 
	connect.setMnemonic('c'); 
	connect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK)); 
	connect.addActionListener( new ActionListener() {
																		public void actionPerformed(ActionEvent e) {
																			tcpc.connect(null, 0);
																			tcpc.socketListening();
																		}});
	JMenuItem disconnect = new JMenuItem("접속끊기(D)"); 
	disconnect.setMnemonic('d'); // 키보드 단축키 할당
	disconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK)); // 키보드 엑셀레이터 설정
	disconnect.addActionListener(new ActionListener() {
																		public void actionPerformed(ActionEvent e) {
																			tcpc.close();
																		}});
	JMenuItem exit = new JMenuItem("종료(X)"); // 메뉴 아이템 추가
	exit.setMnemonic('x'); // 키보드 단축키 할당
	exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK)); // 키보드 엑셀레이터 설정
	exit.addActionListener(new ActionListener() {
																		public void actionPerformed(ActionEvent e) {
																			System.exit(0);
																		}});
	final JMenuBar menuBar = new JMenuBar();
	JMenu mFile = new JMenu("File");
	mFile.setMnemonic('f');
	mFile.add(connect);
	mFile.add(disconnect);
	mFile.addSeparator(); // 메뉴 구분선 설정
	mFile.add(exit);
	menuBar.add(mFile);
	return menuBar;
}

//--------------------------------------------------------------------
// 검색 메쏘드(주민등록번호, 이름)
//--------------------------------------------------------------------

public void findUser(String cmd, String value){
	if (cmd.equals("name")){
		if (value.length() ==0) {
			JOptionPane.showMessageDialog(this, "이름을 정확히 입력하여 주세요", "이름으로 검색", JOptionPane.WARNING_MESSAGE); 
		}else{
			tcpc.readUser(cmd, value);
		}
	}else if(cmd.equals("ssn")){
		if(checkSsn(value) && value != null){
			tcpc.readUser(cmd, value);
		}
	}
}

//--------------------------------------------------------------------
// 나이 체크 유틸 메쏘드
//--------------------------------------------------------------------

public boolean checkAge(String a){
		boolean validAge = false; 
			try{
				if((Integer.parseInt(a) <= 0) || (Integer.parseInt(a) >120) ){
					 JOptionPane.showMessageDialog(this, "나이를 정확히 입력하여 주세요(1~120세 사이)", "나이 검사", JOptionPane.WARNING_MESSAGE); 
				}else{
					return true;
				} // if
			}catch(NumberFormatException nfe){
				 JOptionPane.showMessageDialog(this, "나이를 숫자로 정확히 입력하여 주세요(1~120세 사이)", "나이 검사", JOptionPane.WARNING_MESSAGE); 
			}			return false;
	} // checkAge(String a)

//--------------------------------------------------------------------
// 주민등록번호 디지트 체크 유틸 메쏘드
//--------------------------------------------------------------------

public boolean checkSsn(String ssn){
	boolean check = false;
	/*
	int total = 0;
	int chk = 0;
	int [] temp = new int[13];
	try{
		for(int i=0; i<ssn.length(); i++){
				temp[i] = Integer.parseInt(ssn.substring(i, i+1));//s.charAt(i);
		}//for
		for(int i=0;i<temp.length-1;i++){
			if(i<=7){
				total+=temp[i]*(i+2);
			}else{
				total+=temp[i]*(i-6);
			}//if
		}//for
		chk=(11-(total%11))%10;
		if(chk==temp[12]){
			check=true;
		} //if

		if(!check){
			 JOptionPane.showMessageDialog(this, "주민등록번호를 정확히 입력하여 주세요", "주민등록번호로 검색", JOptionPane.WARNING_MESSAGE); 
		}
	}catch(NumberFormatException nfe){}
	*/

		//약식 체크

		if(ssn.length() !=12){
			check=true;
		}

		if(!check){
			 JOptionPane.showMessageDialog(this, "주민등록번호를 정확히 입력하여 주세요", "주민등록번호로 검색", JOptionPane.WARNING_MESSAGE); 
		}

	return check;
}

//--------------------------------------------------------------------
// 선택 테이블 데이터 입력 메쏘드
//--------------------------------------------------------------------

public void  setResultTable(ArrayList ar){
	resultTDM  = new ResultTableDataModel(ar);
	resultTable.setModel(resultTDM);
}

//--------------------------------------------------------------------
// 전체 목록 테이블 데이터 입력 메쏘드
//--------------------------------------------------------------------

public void  setUsersTable(ArrayList ar){
	userTDM  = new UserTableDataModel(ar);
	usersTable.setModel(userTDM);
}

//--------------------------------------------------------------------
// 서버 수신 데이터  설정 메쏘드
//--------------------------------------------------------------------

public  void setResultObject(String cmd, Object resultObject){


if( (cmd.equals("name")) |  (cmd.equals("ssn"))) {
		if (!(resultObject instanceof  UserImpl) && (cmd.equals("name"))){
			JOptionPane.showMessageDialog(this,"이름에 대한 검색결과가 없습니다.", "이름으로 검색",  JOptionPane.INFORMATION_MESSAGE); 
		}else if (!(resultObject instanceof  UserImpl) && (cmd.equals("ssn"))){
			JOptionPane.showMessageDialog(this, "주민등록번호에 대한 검색결과가 없습니다.", "주민등록번호로 검색",  JOptionPane.INFORMATION_MESSAGE); 
		}else if((resultObject instanceof  UserImpl) && ((cmd.equals("ssn")) | (cmd.equals("name")))){
			setInputForm(resultObject);
		}
	} else if ("create".equals(cmd)) {
		JOptionPane.showMessageDialog(this, (String) resultObject, "입력",  JOptionPane.INFORMATION_MESSAGE); 
		tcpc.listUser("list");		
	} else if ("update".equals(cmd)) {
		JOptionPane.showMessageDialog(this, (String) resultObject, "수정",  JOptionPane.INFORMATION_MESSAGE); 
		tcpc.listUser("list");		
	} //if
}

//--------------------------------------------------------------------
// 출력폼 리셋 메쏘드
//--------------------------------------------------------------------

public void resetOutPutData(boolean userTab, boolean resultTab){

	for(int i =0;i < defaultDataTextField.length; i++){
		defaultDataTextField[i].setText("");	
	}
	defaultDataTextField[3].setEditable(true);
	for(int i =0;i < addtionalDataTextField.length; i++){
		addtionalDataTextField[i].setText("");	
	}
	selectedRowObjectType = "";
	originalCategory = null;
	originalSequence = null;
	isSelectedRow = false;
	usersTable.clearSelection();
	resultTable.clearSelection();
	if (userTab){
		this.setUsersTable(new ArrayList());
	}
	if(resultTab){
		this.setResultTable(new ArrayList());
	}
}

//--------------------------------------------------------------------
// 데이터타입 콤보박스 리스너 (카드 레이아웃 이벤트)
//--------------------------------------------------------------------

public void itemStateChanged(ItemEvent evt) {
	CardLayout cl = (CardLayout)(addtionalDataCardPanel.getLayout());
	cl.show(addtionalDataCardPanel, (String)evt.getItem());
	if(resultTable.getModel().getRowCount()!= 0){
		originalCategory = (String) resultTable.getModel().getValueAt(0, 8);
		originalSequence = (String) resultTable.getModel().getValueAt(0, 0);
	}else{
		originalCategory ="모름";
	}
	if(evt.getStateChange()==1){ 
		String option = (String)cb.getSelectedItem();
		String typeName = null;	
		if (option.equals("학생")){
			typeName = "lms_student";
		} else if (option.equals("교수")){
			typeName = "lms_professor";
		} else if (option.equals("교직원")){
			typeName = "lms_employee";
		}
		if(typeName != null){
			if(originalCategory.equals((String)cb.getSelectedItem())){
				defaultDataTextField[0].setText(originalSequence);	
			}else{
				tcpc.createSequence("seq", typeName);
			}
		}

	}

}

//--------------------------------------------------------------------
// 입력폼 체크 메쏘드
//--------------------------------------------------------------------

private boolean checkInpuForm(String option){
		String empty="";		
			if(empty.equals(defaultDataTextField[0].getText())){
				JOptionPane.showMessageDialog(this,  "데이터타입을 선택하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			} 
			if (empty.equals(defaultDataTextField[1].getText())){
				JOptionPane.showMessageDialog(this,  defaultDataLabels [1] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			} 
			if (empty.equals(defaultDataTextField[2].getText())){
				JOptionPane.showMessageDialog(this,  defaultDataLabels [2] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			}
			if (empty.equals(defaultDataTextField[3].getText())){
				JOptionPane.showMessageDialog(this,  defaultDataLabels [3] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			}
			if (empty.equals(defaultDataTextField[4].getText())){
				JOptionPane.showMessageDialog(this,  defaultDataLabels [4] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			}
		
			if  (option.equals(studentDataCardString) & empty.equals(addtionalDataTextField[0].getText()) ) {
				JOptionPane.showMessageDialog(this,  addtionalDataLabels [0] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			} 
			 if (option.equals(professorDataCardString) & empty.equals(addtionalDataTextField[1].getText()) ) {
						JOptionPane.showMessageDialog(this,  addtionalDataLabels [1] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			}
			if (option.equals(employeeDataCardString) & empty.equals(addtionalDataTextField[2].getText())) {
						JOptionPane.showMessageDialog(this,  addtionalDataLabels [2] + "을 입력하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 		
				return false;
			}

			return true;
}

//--------------------------------------------------------------------
// 입력폼 셋 메쏘드
//--------------------------------------------------------------------

public void  setInputForm(Object resultObject ){

			CardLayout cl = (CardLayout)(addtionalDataCardPanel.getLayout());
			isSelectedRow = true;
			if(resultObject instanceof  UserImpl){
					UserImpl user = (UserImpl) resultObject;
					defaultDataTextField[0].setText(user.getUserNo());	
					defaultDataTextField[1].setText(user.getName());	
					defaultDataTextField[2].setText(user.getAge());			
					defaultDataTextField[3].setText(user.getSsn());			
					defaultDataTextField[4].setText(user.getAddress());			
					selectedRowSsn = user.getSsn();
					if (user instanceof  Student){
						selectedRowObjectType = studentDataCardString;
						Student stud = (Student ) resultObject;
						addtionalDataTextField[0].setText(stud.getStudentNo());
						selectedRowAddtionalData = stud.getStudentNo();
						cl.show(addtionalDataCardPanel, studentDataCardString);
					}else if (user instanceof  Professor){
						selectedRowObjectType = professorDataCardString;
						Professor prof =  (Professor) resultObject;
						addtionalDataTextField[1].setText(prof.getCourse());
						selectedRowAddtionalData = prof.getCourse();
						cl.show(addtionalDataCardPanel, professorDataCardString);
					}else if (user instanceof  Employee){
						selectedRowObjectType = employeeDataCardString;
						Employee emp = (Employee) resultObject;
						addtionalDataTextField[2].setText(emp.getDepartment());
						selectedRowAddtionalData = emp.getDepartment();
						cl.show(addtionalDataCardPanel, employeeDataCardString);
					}
			}
}

//--------------------------------------------------------------------
// 시퀀스 셋 메쏘드
//--------------------------------------------------------------------

	public void setSequence(String seq){
		defaultDataTextField[0].setText(seq);
	}
//--------------------------------------------------------------------
// actionPerformed
//--------------------------------------------------------------------

public void actionPerformed(ActionEvent e){     // 오버라이드
	if (findUserButtonString.equals(e.getActionCommand()) | findUserTextFieldString.equals(e.getActionCommand()) ) {
		String cmd = group.getSelection().getActionCommand();
		String value =  findUserTextField.getText();
		this.resetOutPutData(false, true);
		this.findUser(cmd, value);			
	}else if(listUserButtonString.equals(e.getActionCommand())){
		this.resetOutPutData(true, true);
		tcpc.listUser("list");		
	}else if(createUserButtonString.equals(e.getActionCommand())){
		String option = (String)cb.getSelectedItem();
		if  (option.equals(dataCardString)) {
			JOptionPane.showMessageDialog(this, "데이터타입을 선택하여 주세요", "입력", JOptionPane.WARNING_MESSAGE); 
		}else{
			String type = (String)cb.getSelectedItem();		
			String [] insertData =  new String [7];
			insertData[0] = defaultDataTextField[0].getText();	
			insertData[1] = defaultDataTextField[1].getText();	
			insertData[2] = defaultDataTextField[2].getText();		
			insertData[3] = defaultDataTextField[3].getText();	
			insertData[4] = defaultDataTextField[4].getText();		
			if (type.equals("학생")){
				insertData[5] = addtionalDataTextField[0].getText();
				insertData[6] = "lms_student";
			} else if (type.equals("교수")){
				insertData[5] = addtionalDataTextField[1].getText();
				insertData[6] = "lms_professor";
			} else if (type.equals("교직원")){
				insertData[5] = addtionalDataTextField[2].getText();				
				insertData[6] = "lms_employee";
			}
			if (checkInpuForm(option) && checkSsn(insertData[3]) && checkAge(insertData[2])){		
				this.resetOutPutData(true, true);
				tcpc.createUser("create", type, insertData);
			}								
		}
	}else if(updateUserButtonString.equals(e.getActionCommand())){
		String option = (String)cb.getSelectedItem();
		if (isSelectedRow == false	){
			JOptionPane.showMessageDialog(this, "회원 목록에서 수정할 데이터를 선택하여 주세요", "수정", JOptionPane.WARNING_MESSAGE); 
		}else{
			String type = (String)cb.getSelectedItem();		
			String [] insertData =  new String [9];
			if (!dataCardString.equals(type)){
				if (type.equals("학생")){
					insertData[5] = addtionalDataTextField[0].getText();
					insertData[7] = "lms_student";
				} else if (type.equals("교수")){
					insertData[5] = addtionalDataTextField[1].getText();
					insertData[7] = "lms_professor";
				} else if (type.equals("교직원")){
					insertData[5] = addtionalDataTextField[2].getText();				
					insertData[7] = "lms_employee";
				}
			}else{
				type = selectedRowObjectType;
					if (type.equals("학생")){
						insertData[5] = addtionalDataTextField[0].getText();
						insertData[7] = "lms_student";
					} else if (type.equals("교수")){
						insertData[5] = addtionalDataTextField[1].getText();
						insertData[7] = "lms_professor";
					} else if (type.equals("교직원")){
						insertData[5] = addtionalDataTextField[2].getText();				
						insertData[7] = "lms_employee";
					}
			}	
			if (checkInpuForm(selectedRowObjectType) && checkSsn(selectedRowSsn) && checkAge(defaultDataTextField[2].getText())){		
				String options[] = {"예", "아니오"};
				int dialogValue = JOptionPane.showOptionDialog(this,  selectedRowSsn + " 수정하시겠습니까?"," 수정", JOptionPane.YES_NO_OPTION, /* Need something  */  	JOptionPane.QUESTION_MESSAGE, null, /* Use default icon for message type*/ options,options[1]);
				if ( dialogValue == JOptionPane.CLOSED_OPTION) {
					System.out.println("Closed window");
				} else if(options[dialogValue].equals("예")){
					insertData[0] = defaultDataTextField[0].getText();	
					insertData[1] = defaultDataTextField[1].getText();	
					insertData[2] = defaultDataTextField[2].getText();		
					insertData[3] = defaultDataTextField[3].getText();	
					insertData[4] = defaultDataTextField[4].getText();		
					if(originalCategory == null){
						originalCategory = type;
					}
					if (originalCategory.equals("학생")){
						originalCategory = "lms_student";
					} else if (originalCategory.equals("교수")){
						originalCategory = "lms_professor";
					} else if (originalCategory.equals("교직원")){
						originalCategory = "lms_employee";
						insertData[8] = null;
					}
 					if(originalCategory.equals(insertData[7])){
						insertData[0] = originalSequence;
					}else{
						insertData[8] = insertData[0];
					}
					insertData[6] = originalCategory;
					tcpc.createUser("update", originalCategory, insertData);
					selectedRowObjectType = "";
					isSelectedRow = false;
					this.resetOutPutData(true, true);
				}
			}	
		}
	}else if(deleteUserButtonString.equals(e.getActionCommand())){
		if (isSelectedRow == false	){
			JOptionPane.showMessageDialog(this, "회원 목록에서 수정할 데이터를 선택하여 주세요", "수정", JOptionPane.WARNING_MESSAGE); 
		}else{
			if ((isSelectedRow == true )){			
				String options[] = {"예", "아니오"};
				int dialogValue = JOptionPane.showOptionDialog(
						this,  selectedRowSsn + " 삭제하시겠습니까?"," 삭제", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options,options[1]);
				if ( dialogValue == JOptionPane.CLOSED_OPTION) {
					System.out.println("Closed window");
				} else if(options[dialogValue].equals("예")){
					this.resetOutPutData(true, true);
					tcpc.deleteUser("delete", selectedRowSsn);
				}
			}
		}
	}	
}

}//class TCPClientGUI
