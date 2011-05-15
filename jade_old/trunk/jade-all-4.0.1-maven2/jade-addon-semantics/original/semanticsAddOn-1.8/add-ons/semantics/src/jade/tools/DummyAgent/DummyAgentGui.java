/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France T�l�com

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

/*
 * DummyAgentGui.java
 * Created on 4 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
//Import required Java classes 
import jade.core.AID;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.gui.JadeLogoButton;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.StringACLCodec;
import jade.util.Logger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
@author Giovanni Caire - CSELT S.p.A
@version $Date: 2004/07/19 15:44:17 $ $Revision: 2.9 $
*/
class DummyAgentGui extends JFrame 
{
    DummyAgent        myAgent;
    AID               agentName;
    AclGui            currentMsgGui;
    DefaultListModel  queuedMsgListModel;
    JList             queuedMsgList;
    File              currentDir;
  String                        logoDummy = "images/dummyagent.gif";
    DummyAgentGui thisGUI;
    
   
    //logging
    private Logger logger = Logger.getMyLogger(this.getClass().getName());
    
  // Constructor
    DummyAgentGui(DummyAgent a)
    {
        //////////////////////////
        // Call JFrame constructor
        super();

        thisGUI = this;
        
        //////////////////////////////////////////////////////////
        // Store pointer to the Dummy agent controlled by this GUI
        myAgent = a;

        /////////////////////////////////////////////////////////////////////
        // Get agent name and initialize the saving/opening directory to null 
        agentName = myAgent.getAID();
        currentDir = null;

        ////////////////////////////////////////////////////////////////
        // Prepare for killing the agent when the agent window is closed
            addWindowListener(new   WindowAdapter()
                                {
                            // This is executed when the user attempts to close the DummyAgent 
                            // GUI window using the button on the upper right corner
                            @Override
							public void windowClosing(WindowEvent e) 
                            {
                                myAgent.doDelete();
                            }
                        } );

        //////////////////////////
        // Set title in GUI window
        try{
            setTitle(agentName.getName() + " - DummyAgent");
        }catch(Exception e){setTitle("DummyAgent");}
        
      Image image = getToolkit().getImage(getClass().getResource(logoDummy));
    setIconImage(image);


        ////////////////////////////////
        // Set GUI window layout manager
        getContentPane().setLayout(new BorderLayout());

        //////////////////////////////////////////////////////////////////////////////////////
        // Add the queued message scroll pane to the CENTER part of the border layout manager
        queuedMsgListModel = new DefaultListModel();
        queuedMsgList = new JList(queuedMsgListModel);
        queuedMsgList.setCellRenderer(new ToFromCellRenderer());
        JScrollPane pane = new JScrollPane();
        pane.getViewport().setView(queuedMsgList);
        getContentPane().add("Center", pane);
        
        queuedMsgList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                MsgIndication selected = (MsgIndication)queuedMsgList.getSelectedValue();
                if (queuedMsgList.getSelectedIndex() != -1 && selected != null) {
                    queuedMsgList.setToolTipText(selected.getMessage().getContent());
                } else {
                    queuedMsgList.setToolTipText("");
                }
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////
        // Add the current message editing fields (an AclGui) to the WEST part of the border layout manager
        
        currentMsgGui = new AclGui(this, myAgent);
        
        //currentMsgGui.setBorder(new TitledBorder("Current message"));
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        
        msg.setSender(agentName);
        
        currentMsgGui.setMsg(msg);
        JScrollPane scroll = new JScrollPane(currentMsgGui);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //getContentPane().add(currentMsgGui, BorderLayout.WEST);
        getContentPane().add(scroll, BorderLayout.WEST);

        /////////////////////////////////////
        // Add main menu to the GUI window
        JMenuBar jmb = new JMenuBar();
        JMenuItem item;

        JMenu generalMenu = new JMenu ("General");
        generalMenu.add (item = new JMenuItem ("Exit"));
        Action exitAction = new AbstractAction("Exit"){
            public void actionPerformed(ActionEvent e)
            {
                    myAgent.doDelete();
            }
        };
        item.addActionListener (exitAction);
        jmb.add (generalMenu);
        
    Icon resetImg = GuiProperties.getIcon("reset");
    Icon sendImg = GuiProperties.getIcon("send");
    Icon openImg = GuiProperties.getIcon("open");
    Icon saveImg = GuiProperties.getIcon("save");
    Icon openQImg = GuiProperties.getIcon("openq");
    Icon saveQImg = GuiProperties.getIcon("saveq");
    Icon setImg = GuiProperties.getIcon("set");
    Icon replyImg = GuiProperties.getIcon("reply");
    Icon viewImg = GuiProperties.getIcon("view");
    Icon deleteImg = GuiProperties.getIcon("delete");
    Icon execMsgImg = GuiProperties.getIcon("execmsg");

    JMenu currentMsgMenu = new JMenu ("Current message");
        currentMsgMenu.add (item = new JMenuItem ("Reset"));
        
        Action currentMessageAction = new AbstractAction("Current message", resetImg){
        public void actionPerformed(ActionEvent e)
        {
            ACLMessage m = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            m.setSender(agentName);
        m.setEnvelope(new jade.domain.FIPAAgentManagement.Envelope());  
            currentMsgGui.setMsg(m);
        }
        };
        

        item.addActionListener(currentMessageAction);
      item.setIcon(resetImg);

      currentMsgMenu.add (item = new JMenuItem ("Send"));
        Action sendAction = new AbstractAction("Send", sendImg){
            public void actionPerformed(ActionEvent e) {
              ACLMessage m = currentMsgGui.getMsg();
              queuedMsgListModel.add(0, new MsgIndication(m, MsgIndication.OUTGOING, new Date()));
              
            StringACLCodec codec = new StringACLCodec();
            try {
          String charset;  
          Envelope env;
          if (((env = m.getEnvelope()) == null) ||
              ((charset = env.getPayloadEncoding()) == null)) {
                charset = ACLCodec.DEFAULT_CHARSET;
              }
          codec.decode(codec.encode(m,charset),charset);
          myAgent.send(m);
            } 
        catch (ACLCodec.CodecException ce) {    
              if(logger.isLoggable(Logger.WARNING))
                logger.log(Logger.WARNING,"Wrong ACL Message " + m.toString());
                ce.printStackTrace();
              JOptionPane.showMessageDialog(null,"Wrong ACL Message: "+"\n"+ ce.getMessage(),"Error Message",JOptionPane.ERROR_MESSAGE);
        }
            }
        };
        
      item.addActionListener (sendAction);
        item.setIcon(sendImg);
        
        currentMsgMenu.add (item = new JMenuItem ("Open"));
      Action openAction = new AbstractAction("Open", openImg){
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser(); 
            if (currentDir != null)
                  chooser.setCurrentDirectory(currentDir); 
            int returnVal = chooser.showOpenDialog(null); 
              if(returnVal == JFileChooser.APPROVE_OPTION)
              {
                  currentDir = chooser.getCurrentDirectory();
                  String fileName = chooser.getSelectedFile().getAbsolutePath();

                try {
          // Note the save/read functionality uses default US-ASCII charset
          StringACLCodec codec = new StringACLCodec(new FileReader(fileName),null);
          currentMsgGui.setMsg(codec.decode());
                }
                catch(FileNotFoundException e1) {
                        JOptionPane.showMessageDialog(null,"File not found: "+ fileName + e1.getMessage(),"Error Message",JOptionPane.ERROR_MESSAGE);
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"File Not Found: " + fileName); }
                catch (ACLCodec.CodecException e2) {
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"Wrong ACL Message in file: " +fileName);
                    // e2.printStackTrace(); 
                    JOptionPane.showMessageDialog(null,"Wrong ACL Message in file: "+ fileName +"\n"+ e2.getMessage(),"Error Message",JOptionPane.ERROR_MESSAGE);
                }
              } 
        }
      };
        item.addActionListener (openAction);
        item.setIcon(openImg);
        
        currentMsgMenu.add (item = new JMenuItem ("Save"));
        Action saveAction = new AbstractAction("Save", saveImg){
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser();
              if (currentDir != null)
                  chooser.setCurrentDirectory(currentDir); 
              int returnVal = chooser.showSaveDialog(null); 
              if(returnVal == JFileChooser.APPROVE_OPTION)
              {
                currentDir = chooser.getCurrentDirectory();
                String fileName = chooser.getSelectedFile().getAbsolutePath();

                  try {
                    FileWriter f = new FileWriter(fileName);
            // Note the save/read functionality uses default US-ASCII charset
                      StringACLCodec codec = new StringACLCodec(null,f);
                    ACLMessage ACLmsg = currentMsgGui.getMsg();
                    codec.write(ACLmsg);
            f.close();
                  }
                  catch(FileNotFoundException e3) { 
                  if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Can't open file: " + fileName); }
                  catch(IOException e4) {
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"IO Exception"); }
              } 
        }
        };
    item.addActionListener (saveAction);
        item.setIcon(saveImg);
        currentMsgMenu.addSeparator();
        jmb.add (currentMsgMenu);

        JMenu queuedMsgMenu = new JMenu ("Queued message");
        queuedMsgMenu.add (item = new JMenuItem ("Open queue"));
        Action openQAction = new AbstractAction("Open queue", openQImg){
      public void actionPerformed(ActionEvent e)
      {
        JFileChooser chooser = new JFileChooser(); 
            if (currentDir != null)
                chooser.setCurrentDirectory(currentDir); 
            int returnVal = chooser.showOpenDialog(null); 
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                // Flush current queue
                for (int i = 0;i < queuedMsgListModel.getSize(); ++i)
                {
                    queuedMsgListModel.removeElementAt(i);
                }

                currentDir = chooser.getCurrentDirectory();
                String fileName = chooser.getSelectedFile().getAbsolutePath();

                try
                {
                    BufferedReader inp = new BufferedReader(new FileReader(fileName));
                    // Read the number of messages in the queue
                    int n = -1;
                    try
                    {
                        Integer nn = new Integer(inp.readLine());
                        n = nn.intValue();
                    }
                    catch(IOException ioEx) { 
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"IO Exception reading the number of messages in the queue"); }
                    
                    // Read the messages and insert them in the queue
                    MsgIndication mi; 
                    for (int i = 0;i < n; ++i)
                    {
                        mi = MsgIndication.fromText(inp);
                        queuedMsgListModel.add(i, mi);
                    }
                }
                catch(FileNotFoundException e5) { 
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Can't open file: " + fileName); }
            } 

        }
        };
        item.addActionListener (openQAction);
        item.setIcon(openQImg);
        
        queuedMsgMenu.add (item = new JMenuItem ("Save queue"));
        Action saveQAction = new AbstractAction("Save queue", saveQImg){
      public void actionPerformed(ActionEvent e)
      {
        JFileChooser chooser = new JFileChooser(); 
            if (currentDir != null)
                chooser.setCurrentDirectory(currentDir); 
            int returnVal = chooser.showSaveDialog(null); 
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                currentDir = chooser.getCurrentDirectory();
                String fileName = chooser.getSelectedFile().getAbsolutePath();

                try
                {
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
                    // Write the number of messages in the queue
                    try
                    {
                        out.write(String.valueOf(queuedMsgListModel.getSize()));
                        out.newLine();
                    }
                    catch(IOException ioEx) { 
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"IO Exception writing the number of messages in the queue"); }

                    // Write the messages
                    MsgIndication mi;
                    for (int i = 0;i < queuedMsgListModel.getSize(); ++i)
                    {
                        mi = (MsgIndication) queuedMsgListModel.get(i);
                        mi.toText(out);
                    }
                }
                catch(FileNotFoundException e5) { 
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Can't open file: " + fileName); }
                catch(IOException e6) { 
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"IO Exception"); }
            } 

      }
      };
        item.addActionListener (saveQAction);
        item.setIcon(saveQImg);
        
        queuedMsgMenu.add (item = new JMenuItem ("Set as current"));
        Action setAction = new AbstractAction("Set as current", setImg){
      public void actionPerformed(ActionEvent e)
      {
        int i = queuedMsgList.getSelectedIndex();
            if (i != -1)
            {
                MsgIndication mi = (MsgIndication) queuedMsgListModel.getElementAt(i);
                ACLMessage m = mi.getMessage();
                currentMsgGui.setMsg(m);
            }

      }
      };

        item.addActionListener (setAction);
        item.setIcon(setImg);
        
        queuedMsgMenu.add (item = new JMenuItem ("Reply"));
    Action replyAction = new AbstractAction("Reply", replyImg){
      public void actionPerformed(ActionEvent e)
      {
        int i = queuedMsgList.getSelectedIndex();
            if (i != -1)
            {
                MsgIndication mi = (MsgIndication) queuedMsgListModel.getElementAt(i);
                ACLMessage m = mi.getMessage();
                ACLMessage reply = m.createReply();
                reply.setEnvelope(new jade.domain.FIPAAgentManagement.Envelope());
                //reply.setSender(myAgent.getAID());
                //currentMsgGui.setMsg(m.createReply());
                currentMsgGui.setMsg(reply);
            }

      }
      };
        item.addActionListener (replyAction);
        item.setIcon(replyImg);
        
        queuedMsgMenu.add (item = new JMenuItem ("View"));
        Action viewAction = new AbstractAction("View", viewImg){
      public void actionPerformed(ActionEvent e)
      {
        int i = queuedMsgList.getSelectedIndex();
            if (i != -1)
            {
                MsgIndication mi = (MsgIndication) queuedMsgListModel.getElementAt(i);
                ACLMessage m = mi.getMessage();
                AclGui.showMsgInDialog(m, thisGUI);
            }
      }
      };
        item.addActionListener (viewAction);
        item.setIcon(viewImg);
        
        queuedMsgMenu.add (item = new JMenuItem ("Delete"));
        Action deleteAction = new AbstractAction("Delete", deleteImg){
      public void actionPerformed(ActionEvent e)
      {
          int[] tab = queuedMsgList.getSelectedIndices();
          if (tab != null) {
              for (int i = tab.length - 1; i >= 0 ; i--) {
                  queuedMsgListModel.removeElementAt(i);
              }
          }

      }
      };
        item.addActionListener (deleteAction);
        item.setIcon(deleteImg);
        jmb.add (queuedMsgMenu);

        setJMenuBar(jmb);

        /////////////////////////////////////////////////////
        // Add Toolbar to the NORTH part of the border layout 
        JToolBar bar = new JToolBar();

        
        JButton resetB = new JButton();
      //resetB.setText("Reset");
        resetB.setText("");
      resetB.setIcon(resetImg);
        resetB.setToolTipText("New the current ACL message");
        resetB.addActionListener(currentMessageAction);
        bar.add(resetB);    
        
        JButton sendB = new JButton();
      sendB.setText("");
        sendB.setIcon(sendImg);
        sendB.setToolTipText("Send the current ACL message");
        sendB.addActionListener(sendAction);
        bar.add(sendB);     
        
        JButton openB = new JButton();
        openB.setText("");
        openB.setIcon(openImg);
        openB.setToolTipText("Read the current ACL message from file");
        openB.addActionListener(openAction);
        bar.add(openB);

        
        JButton saveB = new JButton();
        saveB.setText("");
        saveB.setIcon(saveImg);
        saveB.setToolTipText("Save the current ACL message to file");
        saveB.addActionListener(saveAction);
        bar.add(saveB);

        bar.addSeparator(new Dimension(50,30));

    
        JButton openQB = new JButton();
      openQB.setText("");
        openQB.setIcon(openQImg);
        openQB.setToolTipText("Read the queue of sent/received messages from file");
        openQB.addActionListener(openQAction);
        bar.add(openQB);


        JButton saveQB = new JButton();
      saveQB.setText("");
        saveQB.setIcon(saveQImg);
        saveQB.setToolTipText("Save the queue of sent/received messages to file");
        saveQB.addActionListener(saveQAction);
        bar.add(saveQB);

        bar.addSeparator();
        
        JButton setB = new JButton();
        setB.setText("");
        setB.setIcon(setImg);
        setB.setToolTipText("Set the selected ACL message to be the current message");
        setB.addActionListener(setAction);
        bar.add(setB);


        JButton replyB = new JButton();
      replyB.setText("");
        replyB.setIcon(replyImg);
        replyB.setToolTipText("Prepare a message to reply to the selected message");
        replyB.addActionListener(replyAction);
        bar.add(replyB);

        bar.addSeparator();
    
        JButton viewB = new JButton();
        viewB.setText("");
        viewB.setIcon(viewImg);
        viewB.setToolTipText("View the selected ACL message");
        viewB.addActionListener(viewAction);
        bar.add(viewB);


        JButton deleteB = new JButton();
      deleteB.setText("");
        deleteB.setIcon(deleteImg);
        deleteB.setToolTipText("Delete the selected ACL message");
        deleteB.addActionListener(deleteAction);
        bar.add(deleteB);
        
        bar.addSeparator();
        
        JButton executeFile = new JButton();
        executeFile.setText("");
        executeFile.setIcon(execMsgImg);
        executeFile.setToolTipText("Send the messages defined in the specified file");
        executeFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    
                    JFileChooser chooser = new JFileChooser();
                    int returnVal = chooser.showOpenDialog(DummyAgentGui.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        String line;
                        try {
                            java.io.BufferedReader reader = new BufferedReader(new FileReader(file));
                            while ( (line = reader.readLine()) != null ) {
                                if ( !line.equals("") ) {
                                     String[] fields = line.split("#");
                                     if (fields.length != 2) throw new Exception();
                                     String msgLabel = fields[0].trim();
                                     //ACLMessage m = currentMsgGui.getMsg();
                                     ACLMessage msg = new ACLMessage(ACLMessage.getInteger(msgLabel));
                                     msg.setSender(myAgent.getAID());
                                     Enumeration rec_Enum = currentMsgGui.receiverListPanel.getContent();
                                         while(rec_Enum.hasMoreElements())
                                             msg.addReceiver((AID)rec_Enum.nextElement());
                                     msg.setContent(fields[1].trim());
                                     queuedMsgListModel.add(0, new MsgIndication(msg, MsgIndication.OUTGOING, new Date()));
                                     StringACLCodec codec = new StringACLCodec();
                                     try {
                                         String charset;  
                                         Envelope env;
                                         if (((env = msg.getEnvelope()) == null) ||
                                                 ((charset = env.getPayloadEncoding()) == null)) {
                                             charset = ACLCodec.DEFAULT_CHARSET;
                                         }
                                         codec.decode(codec.encode(msg,charset),charset);
                                         myAgent.send(msg);
                                     } catch (ACLCodec.CodecException ce) {    
                                         if(logger.isLoggable(Logger.WARNING))
                                             logger.log(Logger.WARNING,"Wrong ACL Message " + msg.toString());
                                         ce.printStackTrace();
                                         JOptionPane.showMessageDialog(null,"Wrong ACL Message: "+"\n"+ ce.getMessage(),"Error Message",JOptionPane.ERROR_MESSAGE);
                                     }
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DummyAgentGui.this,
                                    "Bad file!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                    }
                }
        });
        
          bar.add(executeFile);

        bar.add(Box.createHorizontalGlue());
        JadeLogoButton logo = new JadeLogoButton();
        bar.add(logo);
        getContentPane().add("North", bar);
        

    }

    void showCorrect()
    {
        ///////////////////////////////////////////
        // Arrange and display GUI window correctly
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        setVisible(true);
    }
    
    public AclGui getCurrentMsgGui() {
        return currentMsgGui;
    }

}
