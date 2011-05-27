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
	this.setLocation(300, 300); // �������� ��ġ ����

    this.setTitle("NAES" + nca.getName().substring(3,4) +":Negotiation Agent Execution Server - "+ nca.getName());	

	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
// ���� �޼���
	gbc.gridwidth=1; 
	RMjTextArea = new JTextArea();
	RMjTextArea.setLineWrap(true);
	JScrollPane RMjScrollPane = new JScrollPane(RMjTextArea);
	RMjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	RMjScrollPane.setBorder(new TitledBorder("Recieve Message Monitor"));
	contentPane.add(RMjScrollPane, gbc);
// �۽� �޼���
	gbc.gridwidth=1; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	SMjTextArea = new JTextArea();
	SMjTextArea.setLineWrap(true);
	JScrollPane SMjScrollPane = new JScrollPane(SMjTextArea);
	SMjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	SMjScrollPane.setBorder(new TitledBorder("Send Message Monitor"));
	contentPane.add(SMjScrollPane, gbc);
// �ý��� �α�
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
   * NegotiationStrategyAgentGUI �޴��� ����
   */

 	protected JMenuBar createJMenuBar(){
	final JMenuBar menuBar = new JMenuBar();

	JMenu mFile = new JMenu("File");
	mFile.setMnemonic('f');
	menuBar.add(mFile);


	/* File �޴��� Exit �޴� ������*/

	mFile.addSeparator(); // �޴� ���м� ����


	/* File �޴��� Exit �޴� ������ - NegotiationStrategyAgent�� AP���� �����Ѵ�*/
	JMenuItem item = new JMenuItem("Exit"); // �޴� ������ �߰�
	item.setMnemonic('x'); // Ű���� ����Ű �Ҵ�
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK)); // Ű���� ���������� ����
	ActionListener lst = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	GuiEvent ev = new GuiEvent ((Object) this, nca.EXIT);
	nca.postGuiEvent(ev);
	}
	};//�׼Ǹ����� ����

	item.addActionListener(lst);
	mFile.add(item);
	menuBar.add(mFile);
	/* File �޴��� Exit �޴� ������*/


	return menuBar;
	}


};