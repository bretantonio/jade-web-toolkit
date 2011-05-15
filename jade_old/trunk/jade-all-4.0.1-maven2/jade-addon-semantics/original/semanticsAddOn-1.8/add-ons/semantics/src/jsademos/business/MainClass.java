/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/


package jsademos.business;

import jade.Boot;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

public class MainClass {

	public static String myDemoPath = "jsademos/business/";
	
	// boolean true to have a simplified interface for enterprise agents
	// also determine if interfaces of business agents are hidden
	// TODO: lines beginning with #gui# in config file should be read only when SIMPLIF=false (for now never read)
	public static boolean SIMPLIFIED = true;
	
	/**
	 * @param args
	 */
	static JButton launch = new JButton("demo mode");
	static JButton run = new JButton ("expert mode");
	static JButton quit = new JButton("QUIT !!!");
	
	public static void main(String[] args) {
		/***********************
		 * MAIN FRAME SETTINGS *
		 ***********************/
		// create a main frame
		JFrame main = new JFrame("B2B mediation platform - by Orange");
		// when the main frame is closed, quit the application
		main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	

		
		/*****************
		 * THREE BUTTONS *
		 *****************/
		// create a button to launch the application IN DEMO MODE
		launch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SIMPLIFIED = true;
				String[] table = new String[]{"-gui", "-nomtp","-port","2000","-name","test", "buyer:jsademos.business.BuyerAgent(b2b);seller:jsademos.business.SellerAgent(b2b);b2b:jsademos.business.B2BInstitution(b2b);bankAagent:jsademos.business.LoaningBankAgent(b2b);bankBagent:jsademos.business.LoaningBankAgent(b2b);mysniffer:jade.tools.sniffer.Sniffer;mediator:jsademos.business.MediatingAgent(b2b);intermediary:jsademos.business.IntermediaryAgent(b2b);enterpriseA:jsademos.business.EnterpriseInterface(b2b);enterpriseB:jsademos.business.EnterpriseInterface(b2b);bankA:jsademos.business.EnterpriseInterface(b2b);bankB:jsademos.business.EnterpriseInterface(b2b)"};
				Boot.main(table);
				launch.setEnabled(false);
				launch.setText("running ...");
				run.setEnabled(false);
				run.setText("running ...");
			}
		});
		
		// create a button to run the FULL application (in EXPERT MODE)
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SIMPLIFIED = false;
				String[] table = new String[]{"-gui", "-nomtp","-port","2000","-name","test", "buyer:jsademos.business.BuyerAgent(b2b);seller:jsademos.business.SellerAgent(b2b);b2b:jsademos.business.B2BInstitution(b2b);bankAagent:jsademos.business.LoaningBankAgent(b2b);bankBagent:jsademos.business.LoaningBankAgent(b2b);mysniffer:jade.tools.sniffer.Sniffer;mediator:jsademos.business.MediatingAgent(b2b);intermediary:jsademos.business.IntermediaryAgent(b2b);enterpriseA:jsademos.business.EnterpriseInterface(b2b);enterpriseB:jsademos.business.EnterpriseInterface(b2b);bankA:jsademos.business.EnterpriseInterface(b2b);bankB:jsademos.business.EnterpriseInterface(b2b)"};
				Boot.main(table);
				launch.setEnabled(false);
				launch.setText("running ...");
				run.setEnabled(false);
				run.setText("running ...");
			}
		});
		
		// create a button to quit the application
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		
		/***************
		 * MAIN LAYOUT * 
		 ***************/
		// split the window, with an image on the left, two buttons on the right
		JSplitPane spip = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.add(quit);
		JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split2.add(run);
		split2.add(launch);
		split.add(split2);
		// give an Orange icon to the main frame, and add an Orange image in the frame
		URL orangePath = Thread.currentThread().getContextClassLoader().getResource(myDemoPath+"documentImages/orange.gif");
		if (orangePath == null) {
			System.err.println("malformed url = "+myDemoPath+"documentImages/orange.ico");
		}
		else {
			main.setIconImage(new ImageIcon(orangePath).getImage());
			spip.add(new JLabel(new ImageIcon(orangePath)));
		}
		// add the split layout to the frame
		spip.add(split);
		main.add(spip);
		
		
		/**************************
		 * SIZE, POSITION, SHOW ! *
		 **************************/
		// size
		main.setMinimumSize(new Dimension(400,100));
		Dimension myScreen = Toolkit.getDefaultToolkit().getScreenSize();
		main.setLocation(myScreen.width-400,0);
		// show the frame
		main.pack();
		main.setVisible(true);
	}//end method main()

}//end class Main
