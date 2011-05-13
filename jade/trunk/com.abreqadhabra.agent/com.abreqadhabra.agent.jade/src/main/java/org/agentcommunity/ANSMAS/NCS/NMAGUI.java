package org.agentcommunity.ANSMAS.NCS;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import org.agentcommunity.ANSMAS.NCS.ontologies.*;
import org.agentcommunity.ANSMAS.*;

public class NMAGUI extends JFrame implements NIVocabulary{

/*****************************************************************
 *                 global attributs                              *
 *****************************************************************/


	public HashMap nsr = new HashMap();
	public  HashMap locations = new HashMap();

	protected JPanel contentPane;
	public JTextArea RMjTextArea;
	public JTextArea SMjTextArea;
	public JTextArea SLjTextArea;
	protected JTree naTree ;
	protected JTable nsrJTable;
	protected JList naList;
	protected DefaultListModel naListModel;
	protected JButton startNegotiaionBtn, newNAgentBtn,killNAgentBtn,clonNAgentBtn;

	protected NMA nma;
	protected TableDataModel tdm;
	protected String startFlag;
	
	protected boolean DEBUG = false;
	protected boolean ALLOW_COLUMN_SELECTION = false;
    protected boolean ALLOW_ROW_SELECTION = true;
	
/*****************************************************************
 *                 constructer                                   *
 *****************************************************************/

	public NMAGUI(NMA n) {
		this.nma = n;
		try {
			jbInit();
		}catch(Exception e) {
			e.printStackTrace();
		}//try
	}//NMAGUI(NMA n)

/*****************************************************************
 *                 jbInit()                                      *
 *****************************************************************/

  protected void jbInit() throws Exception {

//--------------------------------------------------------------------
// 레이 아웃 설정                                                    
//--------------------------------------------------------------------
	
	contentPane = (JPanel) this.getContentPane();
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
    contentPane.setLayout(gbl);

//--------------------------------------------------------------------
// 메뉴바 생성                                                    
//--------------------------------------------------------------------

	JMenuBar menuBar = createJMenuBar();
	setJMenuBar(menuBar);
	
//--------------------------------------------------------------------
// 테이블 생성 - 협상 공유 저장소(샘플 데이터)                                                   
//--------------------------------------------------------------------

//그리드백 레이아웃 
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
//테이블 모델 생성
	nsr = nma.nsr;
	tdm = new TableDataModel(nsr);
	nsrJTable = new JTable(tdm);
//사용자 선택 감지
     nsrJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//행 선택
//---------------------------------------------------------------------
	if (ALLOW_ROW_SELECTION) { // true by default

		ListSelectionModel rowSM = nsrJTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					//  System.out.println("No rows are selected.");
					startFlag = null;
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					// System.out.println("Row " + selectedRow + " is now selected." + nsrJTable.getValueAt(selectedRow,0));
					startFlag = (String) nsrJTable.getValueAt(selectedRow,0);
				}//else
			}//valueChanged(ListSelectionEvent e)
		});//rowSM.addListSelectionListener
	} else {
		nsrJTable.setRowSelectionAllowed(false);
	}//if (ALLOW_ROW_SELECTION)
//열 선택
//---------------------------------------------------------------------
	if (ALLOW_COLUMN_SELECTION) { // false by default
			if (ALLOW_ROW_SELECTION) {
				//We allow both row and column selection, which
				//implies that we *really* want to allow individual
				//cell selection.
				nsrJTable.setCellSelectionEnabled(true);
			}//if (ALLOW_ROW_SELECTION)
			nsrJTable.setColumnSelectionAllowed(true);
			ListSelectionModel colSM = nsrJTable.getColumnModel().getSelectionModel();
			colSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					//Ignore extra messages.
					if (e.getValueIsAdjusting()) return;
					ListSelectionModel lsm = (ListSelectionModel)e.getSource();
					if (lsm.isSelectionEmpty()) {
					   // System.out.println("No columns are selected.");
					}else {
						int selectedCol = lsm.getMinSelectionIndex();
						// System.out.println("Column " + selectedCol + " is now selected.");
					}//esle
				}// valueChanged(ListSelectionEvent e)
			});//colSM.addListSelectionListener
		}//if (ALLOW_COLUMN_SELECTION)
//디버그
//---------------------------------------------------------------------
	if (DEBUG) {
		nsrJTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				printDebugData(nsrJTable);
			}//mouseClicked(MouseEvent e)
		});//addMouseListener(new MouseAdapter()
	}//(DEBUG)
//스크롤 페인에 추가
	JScrollPane NSRjScrollPane = new JScrollPane(nsrJTable);
	NSRjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	NSRjScrollPane.setPreferredSize(new Dimension(600, 100));
	NSRjScrollPane.setBorder(new TitledBorder("NSR:Negotiation Shared Repository"));
	contentPane.add(NSRjScrollPane, gbc);
 //   contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));



//--------------------------------------------------------------------
// 테이블 생성 - 협상 공유 저장소(샘플 데이터)                                                   
//--------------------------------------------------------------------



//협상 시작 버튼
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	JPanel startPanel = new JPanel();
	
//자동화된 협상 시작 버튼
	startNegotiaionBtn = new JButton("Start Negotiaion");
startPanel.setBorder(new TitledBorder("Start Automated Negotiaion"));
	startPanel.add(startNegotiaionBtn);
 contentPane.add(startPanel, gbc);
	startNegotiaionBtn.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ev)
      {
			if(startFlag != null){
				new MsgBox(NMAGUI.this, "Negotiation UID." + startFlag + " Starting.......");
				GuiEvent ge = new GuiEvent(this, nma.AUTOMATED_NEGOTIATION_START);
				ge.addParameter((String)startFlag);
nma.postGuiEvent(ge);
			}else{
				new MsgBox(NMAGUI.this, "You must select one in the NSR table");
			}
      }//actionPerformed
    });//addActionListener

// 수신 메세지
	gbc.gridwidth=1; 
	RMjTextArea = new JTextArea();
	RMjTextArea.setLineWrap(true);
	JScrollPane RMjScrollPane = new JScrollPane(RMjTextArea);
	RMjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	RMjScrollPane.setBorder(new TitledBorder("Recieve Message Monitor"));
	contentPane.add(RMjScrollPane, gbc);
// 송신 메세지
	gbc.gridwidth=1; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	SMjTextArea = new JTextArea();
	SMjTextArea.setLineWrap(true);
	JScrollPane SMjScrollPane = new JScrollPane(SMjTextArea);
	SMjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	SMjScrollPane.setBorder(new TitledBorder("Send Message Monitor"));
	contentPane.add(SMjScrollPane, gbc);
// 시스템 로그
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	SLjTextArea = new JTextArea();
	SLjTextArea.setLineWrap(true);
	JScrollPane SLjScrollPane = new JScrollPane(SLjTextArea);
	SLjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	SLjScrollPane.setBorder(new TitledBorder("NCA Log Monitor"));
	contentPane.add(SLjScrollPane, gbc);





	this.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
		    shutDown();

	}
	});

//	contentPane.setBackground(Color.WHITE);
    this.setSize(new Dimension(600, 500));
	this.setLocation(0, 0); // 프레임의 위치 설정

    this.setTitle("NCA:Negotiation Control Server - "+ nma.getName());	
      this.pack();

  }

 

//Detecting User Selections 

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

  /**
   * NegotiationStrategyAgentGUI 메뉴바 생성
   */

 	protected JMenuBar createJMenuBar(){
	final JMenuBar menuBar = new JMenuBar();

	JMenu mFile = new JMenu("File");
	mFile.setMnemonic('f');
	menuBar.add(mFile);


	/* File 메뉴의 Exit 메뉴 아이템*/

	mFile.addSeparator(); // 메뉴 구분선 설정


	/* File 메뉴의 Exit 메뉴 아이템 - NegotiationStrategyAgent를 AP에서 제거한다*/
	JMenuItem item = new JMenuItem("Exit"); // 메뉴 아이템 추가
	item.setMnemonic('x'); // 키보드 단축키 할당
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK)); // 키보드 엑셀레이터 설정
	ActionListener lst = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	GuiEvent ev = new GuiEvent ((Object) this, nma.NMA_EXIT);
	nma.postGuiEvent(ev);
	}
	};//액션리스너 설정

	item.addActionListener(lst);
	mFile.add(item);
	menuBar.add(mFile);
	/* File 메뉴의 Exit 메뉴 아이템*/


	return menuBar;
	}

   void shutDown() {
// -----------------  Control the closing of this gui

      GuiEvent ge = new GuiEvent(this, nma.NMA_EXIT);
      nma.postGuiEvent(ge);
   }
   public void updateList(Vector v) {
// ----------------------------------

      naListModel.clear();
      for (int i = 0; i < v.size(); i++){
         naListModel.addElement(v.get(i));
		 System.out.println(v.get(i));
	  }
	 System.out.println( naListModel.toString () );
   }





};



// =========================== External class ============================//

/* TableDataModel:
*  --------------
*  External class for the definition of the tables data model, used to
*  control the display of data within the different tables
**/
   class TableDataModel extends AbstractTableModel {
// -------------------------------------------------

		//Negotiation Information Ontology - uid name author version algorithm issues description relativeURI


		private String[] uid;//NEGOTIATION INFORMATION UID
		private String[] catalogId;//NEGOTIATION INFORMATION CATALOG ID
		private String[] unspscId;//NEGOTIATION INFORMATION UNSPSC ID

		private String[] providerId;//NEGOTIATION INFORMATION PROVIDER ID
		private String[] providerStrategyUid;//NEGOTIATION INFORMATION PROVIDER STRATEGY UID
		private String[] deciderId;//NEGOTIATION INFORMATION DECIDER ID
		private String[] deciderStrategyUid;//NEGOTIATION INFORMATION DECIDER STRATEGY UID
		private String[] result;//NEGOTIATION INFORMATION RESULT

		private  String[] names = {"UID", "Catalog ID", "UNSPSC ID", "ProvideR ID", "Provider Strategy UID", "Decider ID", "Decider Strategy ID", "Result"};
		int rowLength;
		NegotiationInformation ni = new NegotiationInformation();

   public TableDataModel(HashMap tdm) {
// ----------------------------------------------------------  Constructor
	  	rowLength =  tdm.size();
		Set set = tdm.keySet();
        Object [] tdmKeys = set.toArray();
	
		uid = new String[tdmKeys.length];
		catalogId = new String[tdmKeys.length];
		unspscId = new String[tdmKeys.length];

		providerId = new String[tdmKeys.length];
		providerStrategyUid = new String[tdmKeys.length];
		deciderId = new String[tdmKeys.length];
		deciderStrategyUid = new String[tdmKeys.length];
		result = new String[tdmKeys.length];

        for(int i = 0; i < tdmKeys.length; i++)
        {
			String key = (String)tdmKeys[i];
            ni = (NegotiationInformation) tdm.get(key);

			uid[i]= ni.getUID();
			catalogId[i] = ni.getCatalogId();
			unspscId[i] = ni.getUnspscId();
			providerId[i] = ni.getProviderId();
			providerStrategyUid[i] = ni.getProviderStrategyUid();
			deciderId[i] = ni.getDeciderId();
			deciderStrategyUid[i] = ni.getDeciderStrategyUid();

			result[i] = ni.getResult();


//System.out.println(ni.toString());
					

	    }//for
   }//TableDataModel(HashMap hm)

  /** Returns number of columns - used for displaying table - MANDATORY */
  public int getColumnCount() {
    return names.length;
  }

  /** Returns number of rows - used for displaying table - MANDATORY */
  public int getRowCount() {
    return rowLength;
  }

  /** Returns value at cell [row,col] - used for displaying table - MANDATORY */
  public Object getValueAt(int row, int col) {
    switch (col){

		
		case 0:
		return uid[row];
		case 1:
	return catalogId[row];
		case 2:
		return unspscId[row];
		case 3:

		return providerId[row];
		case 4:
		return providerStrategyUid[row];
		case 5:
		return deciderId[row];
		case 6:
		return deciderStrategyUid[row];
		case 7:
		return result[row];


	}
    return null;
  }


  public String getColumnName(int col) {
    return names[col];
  }

}// end TableDataModel

class MsgBox extends JDialog
{
  private String message;

 

  public MsgBox(Frame owner, String message)
  {
    super(owner, message, true);  // true: dialog를 modal로 설정
    this.message = message;

 
            jbInit();

            this.setSize(new Dimension(300, 120));
		    this.setTitle("Negitiation Dialog Window");	
            this.setResizable(false);
            this.setLocationRelativeTo(owner);
            this.setVisible(true);
  }




  private void jbInit()
  {
    JButton button = new JButton("Ok");
	    button.setVerticalTextPosition(AbstractButton.BOTTOM);
    button.setHorizontalTextPosition(AbstractButton.CENTER);

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev)
      {
        dispose();
      }
    });


	JPanel messagejPanel = new JPanel();
	
	messagejPanel.add(new Label(message));
	messagejPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 		JPanel buttonjPanel = new JPanel(); //use default FlowLayout
        buttonjPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    buttonjPanel.add(button);
	getContentPane().add(messagejPanel, BorderLayout.NORTH);
	getContentPane().add(buttonjPanel, BorderLayout.CENTER);
  }
}
