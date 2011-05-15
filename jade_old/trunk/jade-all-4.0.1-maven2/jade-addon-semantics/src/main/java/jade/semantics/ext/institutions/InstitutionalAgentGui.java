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


package jade.semantics.ext.institutions;

/*
* Class InstitutionalAgentGui.java
* Created by Carole Adam, February 15, 2008
* (inspired from DemoAgentGui created on 4 jan. 2005 by Thierry Martinez & Vincent Pautret)
*/

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.HashMap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
* DemoAgentGui.java = class that is a GUI base class for some demo agent.
* author Thierry Martinez - France Telecom
* date: 2005/01/04 17:00:00  Revision: 1.0
* 
* Modified into InstitutionalAgentGui.java by Carole Adam, 15 February 2008
* to be the default GUI of InstitutionalAgents
* @author wdvh2120
* @version 1.3 date August 2008
*/
public class InstitutionalAgentGui extends JFrame {
	
	// is the gui a simplified one ? by default NO
	// simplified GUI is used for demos (prevent from typing messages in text zones)
	private boolean SIMPLIFIED = false;
	
	// corresponding accessor
	public boolean isSimplified() {
		return SIMPLIFIED;
	}
	
   /*********************************************************************/
   /**                         CONSTRUCTORS    **/
   /*********************************************************************/

	/**
	 * retro compatibility
	 * constructor without the simplify boolean (set to false by default)
	 */
	public InstitutionalAgentGui(String name, String path, String file, Agent agt, boolean pbk, boolean showkb) {
		this(false,name,path,file,agt,pbk,showkb);
	}
	
   /**
    * Constructor
    * @param name name of the agent
    * @param file parameter file
    * @param agt the agent
    * @param pbk A boolean to tell if the read message has to be put back into the message queue
    * @param showkb A boolean to tell if the kbase should be displayed
    */
   public InstitutionalAgentGui(boolean simplify, String name, String path, String file, Agent agt, boolean pbk, boolean showkb)
   {
       super(name);
       SIMPLIFIED = simplify;
       
       agent = agt;
       putback = pbk;
       showkbase = showkb;
       agent.addBehaviour(new ReceivingBehaviour());

       // Building all needed panels
       // --------------------------
       JTabbedPane mainPanel = new JTabbedPane();
       JPanel firstPanel = new JPanel(new BorderLayout());
       JPanel secondPanel = new JPanel(new BorderLayout());
       JPanel northPanel = new JPanel(new BorderLayout());
       JPanel centeredPanel = new JPanel(new BorderLayout());
       JPanel southPanel = new JPanel(new BorderLayout());

       if ( showkbase && agent instanceof SemanticAgent ) {
           getContentPane().add(mainPanel);
           mainPanel.add("GUI", firstPanel);
           mainPanel.add("KBase", secondPanel);
           mainPanel.addChangeListener(new ChangeListener() {
               public void stateChanged(ChangeEvent e) {
                   kbaseList.setListData(((SemanticAgent)agent).getSemanticCapabilities().getMyKBase().toStrings().toArray());
               }
           });
       }
       else {
           getContentPane().add(firstPanel);
       }

       firstPanel.add(BorderLayout.NORTH, northPanel);
       firstPanel.add(BorderLayout.CENTER, centeredPanel);
       firstPanel.add(BorderLayout.SOUTH, southPanel);
       northPanel.add(customPanel);

       JPanel NorthEastPanel = new JPanel(new BorderLayout());
       northPanel.add(BorderLayout.EAST, NorthEastPanel);

       // Setting the messages list
       // --------------------------------------
       JScrollPane msgScroll = new JScrollPane(messagesList);
       msgScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Messages:"));
       msgScroll.setPreferredSize(new Dimension(220,120));
       NorthEastPanel.add(BorderLayout.NORTH, msgScroll);
       messagesList.addMouseListener(new ListMouseAdapter());

       // Setting the receiver list
       // --------------------------------------
       JScrollPane receiverScroll = new JScrollPane(receiversList);
       receiverScroll.setPreferredSize(new Dimension(220,80));
       receiverScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Receivers:"));
       NorthEastPanel.add(BorderLayout.SOUTH, receiverScroll);
       receiversList.addMouseListener(new ListMouseAdapter());

       // Setting the sending area
       // ---------------------------------
       JPanel p = new JPanel(new GridLayout(3,2));
       if (!SIMPLIFIED) {
    	   contentTextArea.setForeground(Color.BLUE);
    	   receiverTextArea.setForeground(Color.BLUE);
    	   performativeTextArea.setForeground(Color.BLUE);
    	   JScrollPane sp = new JScrollPane(performativeTextArea);
    	   sp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
    			   "Performative:",
    			   TitledBorder.LEFT,
    			   TitledBorder.DEFAULT_POSITION,
    			   //#DOTNET_EXCLUDE_BEGIN
    			   sp.getFont().deriveFont(0,9)));

    	   sp.setPreferredSize(new Dimension(100,52));
    	   p.add(sp);
    	   sp = new JScrollPane(receiverTextArea);
    	   sp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
    			   "Receiver:",
    			   TitledBorder.LEFT,
    			   TitledBorder.DEFAULT_POSITION,
    			   //#DOTNET_EXCLUDE_BEGIN
    			   sp.getFont().deriveFont(0,9)));
    	   p.add(sp);
    	   sp = new JScrollPane(contentTextArea);
    	   contentTextArea.setEditable(true);
    	   sp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
    			   "Content:",
    			   TitledBorder.LEFT,
    			   TitledBorder.DEFAULT_POSITION,
    			   //#DOTNET_EXCLUDE_BEGIN
    			   sp.getFont().deriveFont(0,9)));
    	   p.add(sp);
    	   centeredPanel.add(p);
       }
       
        //#DOTNET_EXCLUDE_BEGIN
       centeredPanel.add(BorderLayout.SOUTH,
               new JButton(new AbstractAction("Send this message") {
            	   
                   public void actionPerformed(ActionEvent evt) {
                       if (performativeTextArea.getText().equals("") ||
                               receiverTextArea.getText().equals("") ||
                               contentTextArea.getText().equals("")) {
                           JOptionPane.showMessageDialog(new JFrame(),
                                   "A parameter is missing.",
                                   "Error !",
                                   JOptionPane.ERROR_MESSAGE);
                       } else {
                           sendMessage(performativeTextArea.getText(),
                                   receiverTextArea.getText(),
                                   contentTextArea.getText());
                       }}}));

       // Setting the receiving area (SOUTH)
       // ----------------------------------
       JScrollPane receivedScroll = new JScrollPane(receivedMsgTextArea);
       receivedScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
               "Received messages:",
               TitledBorder.LEFT,
               TitledBorder.DEFAULT_POSITION,
                //#DOTNET_EXCLUDE_BEGIN
               receivedScroll.getFont().deriveFont(0,9)));

       southPanel.add(receivedScroll);
        //#DOTNET_EXCLUDE_BEGIN
       southPanel.add(BorderLayout.SOUTH,
               new JButton(new AbstractAction("Clear") {
            	   
            	   public void actionPerformed(ActionEvent evt) {
                       receivedMsgTextArea.setText("");
                   }}));

       // Setting the kbase viewer
       // ------------------------
       if ( showkbase && agent instanceof SemanticAgent )
        {
           JScrollPane kbaseScroll = new JScrollPane(kbaseList);
           kbaseScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"KBase:"));
           kbaseScroll.setPreferredSize(new Dimension(280,120));
           secondPanel.add(kbaseScroll);
           secondPanel.add(BorderLayout.SOUTH, p = new JPanel());
            //#DOTNET_EXCLUDE_BEGIN
           final JTextField text = new JTextField(20);
           p.add(text);
           p.add(new JButton(new AbstractAction("Assert") {
        	   
               public void actionPerformed(ActionEvent evt) {
                   Formula result;
                   if (text.getText() != null && !text.getText().trim().equals("")) {
                       result = SL.formula(text.getText());
                   } else {
                       result = null;
                   }
                   if (result != null) {
                          ((FilterKBase)((SemanticAgent)agent).getSemanticCapabilities().getMyKBase()).assertFormula(result);
                          kbaseList.setListData(((SemanticAgent)agent).getSemanticCapabilities().getMyKBase().toStrings().toArray());
                   }

                   }}));
           p.add(new JButton(new AbstractAction("Remove") {
        	   
               public void actionPerformed(ActionEvent evt) {
                   if ( kbaseList.getSelectedIndex() != -1 ) {
                       try {
                           Formula formPattern = SLParser.getParser().parseFormula(kbaseList.getSelectedValue().toString(), true);
						   ((SemanticAgent)agent).getSemanticCapabilities().getMyKBase().retractFormula(formPattern);
                           kbaseList.setListData(((SemanticAgent)agent).getSemanticCapabilities().getMyKBase().toStrings().toArray());
                       }
                       catch(ParseException pe) {pe.printStackTrace();}
                   }}}));

            //#DOTNET_EXCLUDE_BEGIN
           p.add(new JButton(new AbstractAction("Refresh")
            {
        	   
               public void actionPerformed(ActionEvent evt) {
                   kbaseList.setListData(((SemanticAgent)agent).getSemanticCapabilities().getMyKBase().toStrings().toArray());
               }}));
       }
       
       // image has the name of the agent
       String imgfile = file.replaceAll(".gui",".gif");
       imgfile = "agentsImages/"+imgfile;
       customPanel.add(new JLabel(getDemoIcon(path,imgfile)));
       

       // Reading the configuration file
       // ------------------------------
       String line = null;
       Vector<String> messages = new Vector<String>();
       Vector<String> receivers = new Vector<String>();
       try {
           java.io.BufferedReader reader = new BufferedReader(getDemoFileReader(path,file,"no gui config file: "+file));
           while ( (line = reader.readLine()) != null ) {
               if ( !line.equals("") ) {
                    String fields[] = line.split("#");
                   if (line.startsWith("image#"))
                   {
                	  // NEW : no need to give the name of the image in the gui file
                	  // The gui image should now have the name of the holding agent 
                      // customPanel.add(new JLabel(getDemoIcon("agentsImages/"+fields[1].trim()+".gif")));
                    		   //getLocalIcon(path+"agentsImages/",fields[1].trim())));
                   }
                   else if (line.startsWith("receiver#")) {
                       receivers.add(fields[1].trim());
                   }
                   else if (line.startsWith("message#")) {
                       String msgLabel = fields[1].trim();
                       messages.add(msgLabel);
                       msgMap.put(msgLabel, fields[2].trim());
                   }
               }
           }
       }
       catch (Exception e) {e.printStackTrace();}

       // Setting lists data
       // ------------------
       messagesList.setListData(messages);
       receiversList.setListData(receivers);

       // Showing the frame
       // -----------------
       setSize(400,500);
       pack();
       setVisible(true);
   }

   /*********************************************************************/
   /**                         METHODS     **/
   /*********************************************************************/
   /**
    * Returns the tempPanel to customize the GUI
    * @return a panel
    */
   JPanel getCustomPanel() {
       return customPanel;
   }

   /*********************************************************************/
   /**                         INNER CLASS **/
   /*********************************************************************/

   /**
    * Internal receiving Behaviour
    */
   class ReceivingBehaviour extends CyclicBehaviour {
	   
       /**
        *
        */
       @Override
	public void action() {
           ACLMessage msg = agent.receive();
           if ( msg != null ) {
               //String text = receivedMsgTextArea.getText();
               receivedMsgTextArea.append(msg.toString()+"\n");
               receivedMsgTextArea.setCaretPosition(receivedMsgTextArea.getDocument().getLength());
               if ( putback ) {
                   agent.putBack(msg);
               }
           }
           else {
               block();
           }
       } // End of action/0
   } // End of ReceivingBehaviour

   /**
    * Mouse adapter to handle mouse click within the messages or receivers list
    */
   class ListMouseAdapter extends MouseAdapter {
       /**
        * @param e a MouseEvent
        */
       @Override
	public void mouseClicked(MouseEvent e) {
           if ( e.getSource() == messagesList && messagesList.getSelectedIndex() != -1 ) {
               String msg = (String)msgMap.get(messagesList.getSelectedValue().toString());
               performativeTextArea.setText(msg.substring(0, msg.indexOf(" ")));
               contentTextArea.setText(msg.substring(msg.indexOf(" ")+1));
           }
           else if ( e.getSource() ==receiversList && receiversList.getSelectedIndex() != -1 ) {
               receiverTextArea.setText(receiversList.getSelectedValue().toString());
           }
       }
   }


   /*********************************************************************/
   /**                         INTERNALS   **/
   /*********************************************************************/

   /***
    * Text area
    */
   JTextArea receivedMsgTextArea = new JTextArea(5,10);

   /**
    * Text area describing message to send
    */
   JTextArea contentTextArea = new JTextArea();
   /**
    *
    */
   JTextArea receiverTextArea = new JTextArea();
   /**
    *
    */
   JTextArea performativeTextArea = new JTextArea();

   /**
    * JPanel to received a particular component to customise this frame
    */
   JPanel customPanel = new JPanel(new BorderLayout());

   /**
    * Map that makes a link between a label and the effective message
    */
   HashMap msgMap = new HashMap();

   /**
    * The jade agent associated with this GUI
    */
   Agent agent = null;

   /**
    * A boolean to tell if the read message has to be put back into the message queue
    */
   boolean putback = false;

   /**
    * A boolean to tell if the kbase should be displayed
    */
   boolean showkbase = false;

   /**
    * The messages and receivers lists
    */
   JList messagesList = new JList();
   /**
    *
    */
   JList receiversList = new JList();

   /**
    * The kbase list
    */
   JList kbaseList = new JList();

   /**
    * HashMap containing agent and clothing representations.
    */
   static final HashMap images = new HashMap();
   
   
   /**
    * Returns the GIF image icon the name is the given one.
    * System icons are icons stored in the JIA distribution. 
    * @param name the name of the GIF image icon to return (with no extension)
    * @return an image icon
    */
   static protected ImageIcon getSystemIcon(String name) {
       ImageIcon result = (ImageIcon)images.get(name);
       if ( result == null ) {
           java.net.URL url = Thread.currentThread().getContextClassLoader().getResource("jade/semantics/ext/institutions/imgs/"+name+".gif");
           if ( url != null ) {
               result = new ImageIcon(url);
               images.put(name, result);
           }
       }
       return result;
   } // End of getIcon/1


   /**
    * Returns the GIF image icon the name is the given one.
    * DemoIcons are icons stored in the particular application / demonstration directory,
    * so it needs the full absolute path to this specific directory.
    * The name includes the extension of the file (since there can be various types of files, 
    * .gif or .jpg for instance)
    * @param name the name of the image icon to return (WITH extension)
    * @return an image icon
    */
   static public ImageIcon getDemoIcon(String fullPath,String name) {
	   URL iconURL = Thread.currentThread().getContextClassLoader().getResource(fullPath+name);
	   if (iconURL == null) {
		   System.err.println("WARNING!!! Could not find image: " + fullPath+name);
		   return null;
	   }
	   return new ImageIcon(iconURL);
   }
   

   /**
    * Returns a stream reader corresponding to a resource file of an application.
    * WARNING: these resources files should always be stored in a "files/" directory.
    * @param demoPath is the full absolute path to the application directory
    * @param fileName is the name of the file with its extension (allowing to find various types of files)
    * @param errMsg is the message to print if an exception occurs (like file not found...)
    * 			(if the file is optional and should not trigger an exception when not found, this error
    * 			 message should be exactly the string "optional", then handled in a special case here)
    */
	public static InputStreamReader getDemoFileReader(String demoPath,String fileName,String errMsg) {
			String myPath = demoPath+"files/"+fileName;
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(myPath);
			if (stream == null) {
				if (!errMsg.equals("optional")) 
				{
					System.err.println(errMsg + ": file not found");
				}
				return null;
			}
			return new InputStreamReader(stream);
	}
	
   
   /**
    * Sends a message
    * @param permormative name of the performative
    * @param receiver receiver of the message
    * @param content content of the message
    */
   protected void sendMessage(String permormative, String receiver, String content){
       int perf = -1;
       if (permormative.equals("request-whenever") || permormative.equals("REQUEST-WHENEVER")) {perf = ACLMessage.REQUEST_WHENEVER;}
       else if (permormative.toLowerCase().equals("request-when")) {perf = ACLMessage.REQUEST_WHEN;}
       else if (permormative.toLowerCase().equals("not-understood")) {perf = ACLMessage.NOT_UNDERSTOOD;}
       else if (permormative.toLowerCase().equals("reject-proposal")) {perf = ACLMessage.REJECT_PROPOSAL;}
       else if (permormative.toLowerCase().equals("accept-proposal")) {perf = ACLMessage.ACCEPT_PROPOSAL;}
       else if (permormative.toLowerCase().equals("inform-if")) {perf = ACLMessage.INFORM_IF;}
       else if (permormative.toLowerCase().equals("inform-ref")) {perf = ACLMessage.INFORM_REF;}
       else if (permormative.toLowerCase().equals("query-if")) {perf = ACLMessage.QUERY_IF;}
       else if (permormative.toLowerCase().equals("query-ref")) {perf = ACLMessage.QUERY_REF;}
       else if (permormative.toLowerCase().equals("inform")) {perf = ACLMessage.INFORM;}
       else if (permormative.toLowerCase().equals("request")) {perf = ACLMessage.REQUEST;}
       else if (permormative.toLowerCase().equals("failure")) {perf = ACLMessage.FAILURE;}
       else if (permormative.toLowerCase().equals("confirm")) {perf = ACLMessage.CONFIRM;}
       else if (permormative.toLowerCase().equals("propose")) {perf = ACLMessage.PROPOSE;}
       else if (permormative.toLowerCase().equals("cancel")) {perf = ACLMessage.CANCEL;}
       else if (permormative.toLowerCase().equals("disconfirm")) {perf = ACLMessage.DISCONFIRM;}
       else if (permormative.toLowerCase().equals("agree")) {perf = ACLMessage.AGREE;}
       else if (permormative.toLowerCase().equals("refuse")) {perf = ACLMessage.REFUSE;}
       else if (permormative.toLowerCase().equals("subscribe")) {perf = ACLMessage.SUBSCRIBE;}
       else if (permormative.toLowerCase().equals("cfp")) {perf = ACLMessage.CFP;}

       ACLMessage msg = new ACLMessage(perf);
       msg.setSender(agent.getAID());
       msg.addReceiver(new AID(receiver.trim(), true));
       msg.setLanguage("fipa-sl");
       msg.setContent(content);
       agent.send(msg);
       System.err.println("["+msg+"] was just sent");
   }

} // End of class
