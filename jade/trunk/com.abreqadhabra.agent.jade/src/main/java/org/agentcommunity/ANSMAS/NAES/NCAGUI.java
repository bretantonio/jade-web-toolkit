package org.agentcommunity.ANSMAS.NAES;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import org.agentcommunity.ANSMAS.*;

public class NCAGUI extends JFrame{

	protected NCA nca;

	protected JPanel contentPane;
	public JTextArea RMjTextArea;
	public JTextArea SMjTextArea;
	public JTextArea SLjTextArea;
	public JTextArea SEjTextArea;

	public NCAGUI(NCA n){

		nca = n;
		try {
			jbInit();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


  protected void jbInit() throws Exception {

	contentPane = (JPanel) this.getContentPane();
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
    contentPane.setLayout(gbl);
//	contentPane.setBackground(Color.WHITE);
    this.setSize(new Dimension(600, 700));
	this.setLocation(300, 300); // 프레임의 위치 설정

    this.setTitle("NAES" + nca.getName().substring(3,4) +":Negotiation Agent Execution Server - "+ nca.getName());	

	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
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
	SLjScrollPane.setBorder(new TitledBorder("NSSS Log Monitor"));
	contentPane.add(SLjScrollPane, gbc);




	this.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	GuiEvent ev = new GuiEvent ((Object) this, nca.EXIT);
	nca.postGuiEvent(ev);
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
	GuiEvent ev = new GuiEvent ((Object) this, nca.EXIT);
	nca.postGuiEvent(ev);
	}
	};//액션리스너 설정

	item.addActionListener(lst);
	mFile.add(item);
	menuBar.add(mFile);
	/* File 메뉴의 Exit 메뉴 아이템*/


	return menuBar;
	}


};