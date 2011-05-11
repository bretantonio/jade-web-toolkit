package org.agentcommunity.ANSMAS.NCS;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import org.agentcommunity.ANSMAS.NCS.ontologies.*;
import org.agentcommunity.ANSMAS.*;

public class MCAGUI extends JFrame  implements NIVocabulary{
// -----------------------------------------------------------------------

	protected JList naList, naesList;
	protected DefaultListModel naListModel, naesListModel;
	protected JComboBox locations;
	protected JButton newAgent, move, clone, kill, quit;
	protected JPanel contentPane;
	public JTextArea RMjTextArea;
	public JTextArea SMjTextArea;
	public JTextArea SLjTextArea;
	protected MCA mca;

   public MCAGUI(MCA a, Set s) {
// -------------------------------------------------------

		 this.mca = a;

		try {
			jbInit(s);

	           

		}catch(Exception e) {
			e.printStackTrace();
		}//try
	}

/*****************************************************************
 *                 jbInit()                                      *
 *****************************************************************/

  protected void jbInit(Set s) throws Exception {

//--------------------------------------------------------------------
// 레이 아웃 설정                                                    
//--------------------------------------------------------------------
	//	contentPane.setBackground(Color.WHITE);

	contentPane = (JPanel) this.getContentPane();
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
    contentPane.setLayout(gbl);
	 this.setSize(new Dimension(600, 700));

	this.setLocation(0, 0); // 프레임의 위치 설정
    this.setTitle("MCA:Negotiation Control Server - "+ mca.getName());	

 
 //--------------------------------------------------------------------
// 메뉴바 생성                                                    
//--------------------------------------------------------------------

	JMenuBar menuBar = createJMenuBar();
	setJMenuBar(menuBar);
	

//그리드백 레이아웃 
	gbc.gridwidth=1; 


	naesListModel = new DefaultListModel();
	      for (int i = 0; i < s.toArray().length; i++){
	
         naesListModel.addElement(s.toArray()[i]);
	  }
	naesList = new JList(naesListModel);
	naesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	naesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);

	      naesList.addListSelectionListener( new ListSelectionListener() {
	  	 public void valueChanged(ListSelectionEvent e) {
	  		if (naList.getSelectedIndex() == -1){
			   move.setEnabled(false);
			   clone.setEnabled(false);
			   kill.setEnabled(false);
			}
			else {
			   move.setEnabled(true);
			   clone.setEnabled(true);
			   kill.setEnabled(true);
			}
	  	 }
	  });

	JScrollPane naesListScrollPane = new JScrollPane(naesList);
	naesListScrollPane.setPreferredSize(new Dimension(250, 80));
naesListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	JPanel naesListJPanel = new JPanel();
	naesListJPanel.add(naesListScrollPane);
	naesListJPanel.setBorder(new TitledBorder("NAES Information"));
	contentPane.add(naesListJPanel, gbc);

//그리드백 레이아웃 

	gbc.gridwidth=1; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;

	naListModel = new DefaultListModel();
	naList = new JList(naListModel);
	naList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	naList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	JScrollPane naListScrollPane = new JScrollPane(naList);
	naListScrollPane.setPreferredSize(new Dimension(250, 80));
      naList.addListSelectionListener( new ListSelectionListener() {
	  	 public void valueChanged(ListSelectionEvent e) {
	  		if (naList.getSelectedIndex() == -1){
			   move.setEnabled(false);
			   clone.setEnabled(false);
			   kill.setEnabled(false);
			}
			else {
			   move.setEnabled(true);
			   clone.setEnabled(true);
			   kill.setEnabled(true);
			}
	  	 }
	  });
naListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	JPanel naListJPanel = new JPanel();
	naListJPanel.add(naListScrollPane);
	naListJPanel.setBorder(new TitledBorder("NA(PNA/DNA) Inforamtion"));
	contentPane.add(naListJPanel, gbc);





//그리드백 레이아웃 
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
/*
	  JPanel naCreationJPanel = new JPanel();
      locations = new JComboBox(s.toArray());
	  naCreationJPanel.add(locations);
*/


//그리드백 레이아웃 
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;

     JPanel naControlJPanel = new JPanel();

	  naControlJPanel.add(newAgent = new JButton("Agent Creation"));
	 newAgent.addActionListener(new ActionListener(){
     public void actionPerformed(ActionEvent ev)
      {
         GuiEvent ge = new GuiEvent(this, mca.MCA_NEW_NAGENT);
         mca.postGuiEvent(ge);
      }//actionPerformed
    });//addActionListener
      newAgent.setToolTipText("Create a new agent");

      naControlJPanel.add(move = new JButton("Move"));

	  move.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ev)
      {
         GuiEvent ge = new GuiEvent(this, mca.MCA_MOVE_NAGENT);
         ge.addParameter((String)naList.getSelectedValue());
         //ge.addParameter((String)locations.getSelectedItem());
		 		          ge.addParameter((String)naesList.getSelectedValue());

         mca.postGuiEvent(ge);
      }//actionPerformed
    });//addActionListener

      move.setToolTipText("Move agent to a new location");
      naControlJPanel.add(clone = new JButton("Clone"));
	  	  clone.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ev)
      {
         
         GuiEvent ge = new GuiEvent(this, mca.MCA_CLONE_NAGENT);
         ge.addParameter((String)naList.getSelectedValue());
		          ge.addParameter((String)naesList.getSelectedValue());

      //   ge.addParameter((String)locations.getSelectedItem());
         mca.postGuiEvent(ge);
      }//actionPerformed
    });//addActionListener
      clone.setToolTipText("Clone selected agent");


      naControlJPanel.add(kill = new JButton("Kill"));
	  kill.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ev)
      {
         
         GuiEvent ge = new GuiEvent(this, mca.MCA_KILL_NAGENT);
       //  ge.addParameter((String)naList.getSelectedValue());
	            ge.addParameter((String)naList.getSelectedValue());

	   		          ge.addParameter((String)naesList.getSelectedValue());

         mca.postGuiEvent(ge);
      }//actionPerformed
    });//addActionListener
      kill.setToolTipText("Kill selected agent");


			   move.setEnabled(false);
			   clone.setEnabled(false);
			   kill.setEnabled(false);


	  naControlJPanel.setBorder(new TitledBorder("Negotiation Agent Control Panel"));

      contentPane.add(naControlJPanel, gbc);
	
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



      addWindowListener(new WindowAdapter() {
	     public void windowClosing(WindowEvent e) {
		    shutDown();
		 }
	  });


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
	GuiEvent ev = new GuiEvent ((Object) this, mca.MCA_EXIT);
	mca.postGuiEvent(ev);
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

      GuiEvent ge = new GuiEvent(this, mca.MCA_EXIT);
      mca.postGuiEvent(ge);
   }

   public void updateList(Vector v) {
// ----------------------------------

      naListModel.clear();
      for (int i = 0; i < v.size(); i++){
         naListModel.addElement(v.get(i));
	  }

   }
   public void updateLocations(Set s) {
// ----------------------------------
      naesListModel.clear();

      for (int i = 0; i < s.toArray().length; i++){
	
         naesListModel.addElement(s.toArray()[i]);
	  }
   }

}