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
package jade.tools.DummyAgent;

import jade.core.AID;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;
import jade.gui.AIDGui;
import jade.gui.TimeChooser;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ACLParser;
import jade.lang.acl.ISO8601;
import jade.lang.acl.ParseException;
import jade.semantics.interpreter.Finder;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SL;
import jade.tools.sl.SLFormatter;
import jade.util.Logger;
import jade.util.leap.ArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class AclGui extends JPanel
{
  public DefaultListModel listModel;
  public JList kbaseList;
  
  DummyAgent myAgent;
  // Controls for ACL message parameter editing
  static String ADD_NEW_RECEIVER = "Insert receiver"; 
  
  //the owner  of the panel.
  private Component ownerGui;
  
  //the logger
  private Logger logger = Logger.getMyLogger(this.getClass().getName());  
  /**
  @serial
  */
  AID SenderAID = new AID();
  /**
  @serial
  */
  AID newAIDSender = null;
  
  /**
  @serial
  */
  AID fromAID = new AID();
  /**
  @serial
  */
  AID newAIDFrom = null;
  
 
  /**
  @serial
  */
  VisualAIDList receiverListPanel;
  /**
  @serial
  */
  VisualAIDList replyToListPanel;
  /**
  @serial
  */
  VisualPropertiesList propertiesListPanel;
  
  /**
  @serial
  */
  private boolean      guiEnabledFlag;
  /**
  @serial
  */
  private JTextField   sender;
  /**
  @serial
  */
  private boolean      senderEnabledFlag;
  
  /**
  @serial
  */
  private JComboBox    communicativeAct;
  
  /**
  @serial
  */
  private JComboBox    predefinedContent;
  /**
  @serial
  */
  private JTextArea    content;
  /**
  @serial
  */
  private JTextField   language;
  /**
  @serial
  */
  private JTextField   ontology;
  /**
  @serial
  */
  private JComboBox    protocol;
  /**
  @serial
  */
  private JTextField   conversationId;
  /**
  @serial
  */
  private JTextField   inReplyTo;
  /**
  @serial
  */
  private JTextField   replyWith;
  /**
  @serial
  */
  private JTextField   replyBy;
  
  /**
  @serial
  */
  private JTextField   encoding;
  
  /**
  @serial
  */
  private JButton      replyBySet;
  /**
  @serial
  */
  private Date         replyByDate;
  /**
  @serial
  */
  private Date    dateDate;
  /**
  @serial
  */
  private Date    dateRecDate;
  // Data for panel layout definition
  /**
  @serial
  */
  GridBagLayout lm = new GridBagLayout();
  
  /**
  @serial
  */
  GridBagConstraints constraint = new GridBagConstraints();
  /**
  @serial
  */
  private int leftBorder, rightBorder, topBorder, bottomBorder;
  /**
  @serial
  */
  private int xSpacing, ySpacing;
  /**
  @serial
  */
  private int gridNCol, gridNRow;
  /**
  @serial
  */
  private int colWidth[];
  private static final int TEXT_SIZE = 30;

  /**
  @serial
  */  
  private static int    N_FIPA_PROTOCOLS = 8;
  private static String fipaProtocols[] = {FIPANames.InteractionProtocol.FIPA_ENGLISH_AUCTION,
                                           FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION,
                                           FIPANames.InteractionProtocol.FIPA_CONTRACT_NET,
                                           FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET,
                                           FIPANames.InteractionProtocol.FIPA_QUERY,
                                           FIPANames.InteractionProtocol.FIPA_REQUEST,
                                           FIPANames.InteractionProtocol.FIPA_REQUEST_WHEN,
                                           FIPANames.InteractionProtocol.FIPA_PROPOSE };

                                             
  /**
  @serial
  */
  private ArrayList fipaProtocolArrayList;


  // Data for the editing of user defined iteration protocols
  /**
  @serial
  */
  private int    lastSelectedIndex;
  /**
  @serial
  */
  private String lastSelectedItem;
  private static final String LABEL_TO_ADD_PROT = "ADD USER-DEF PROTOCOL";
 
  // These data are used to correctly handle the resizing of the AclGui panel
  /**
  @serial
  */
  private JPanel       aclPanel;
  /**
  @serial
  */
  private Dimension    minDim;
  /**
  @serial
  */
  private boolean      firstPaintFlag;

  // Other data used
  private static ACLMessage editedMsg;
  
  /**
  @serial
  */
  private JButton senderButton;

  /**
  @serial
  */
  private VisualAIDList toPanel;
  
  /**
  @serial
  */
  private JTextField from;
  
  /**
  @serial
  */
  private JTextArea comments;
  
  /**
  @serial
  */
  private JTextField representation;
    
  /**
  @serial
  */
  private JTextField payloadLength;
  /**
  @serial
  */
  private JTextField payloadEncoding;

  /**
  @serial
  */
  private JTextField date;
  
  /**
  @serial
  */
  private VisualAIDList intendedReceiverPanel;
  /**
  @serial
  */
  private JButton defaultEnvelopeButton;
  
  /**
  @serial
  */
  private JButton fromButton;
  
  /**
  @serial
  */
  private JButton dateButton;
  
  /**
  @serial
  */
  private JButton dateRecButton;

  /**
  @serial
  */
  private JTextField by;
  /**
  @serial
  */
  private JTextField fromRec;
  /**
  @serial
  */
  private JTextField dateRec;
  /**
  @serial
  */
  private JTextField via;
  /**
  @serial
  */
  private JTextField id;


  private class AclMessagePanel extends JPanel
    implements DropTargetListener
  {
      AclMessagePanel()
      {
  
    JLabel l;
    int    i;
    
    // Initialize the Vector of interaction protocols
    fipaProtocolArrayList = new ArrayList();
  
    for (i = 0;i < N_FIPA_PROTOCOLS; ++i)
      fipaProtocolArrayList.add((Object) fipaProtocols[i]);
    
    aclPanel = new JPanel();

    new DropTarget(aclPanel, this);

    
    //aclPanel.setLayout(lm);
    aclPanel.setLayout(new BoxLayout(aclPanel, BoxLayout.Y_AXIS));
//    formatGrid(21,   // N of rows 
//            3,   // N of columns
//            5,   // Right border 
//            5,   // Left border
//            5,   // Top boredr
//            5,   // Bottom border
//            2,   // Space between columns
//            2);  // Space between rows
//    setGridColumnWidth(0, 115);
//    setGridColumnWidth(1, 40);
//    setGridColumnWidth(2, 170);
    
    
    // Sender  (line # 0)
    l = new JLabel("Sender:");
    new DropTarget(l, this);
  //  put(aclPanel,l, 0, 0, 1, 1, false); 
    senderEnabledFlag = true; // The sender field is enabled by default, but can be disabled with the setSenderEnabled() method.
    sender = new JTextField();
    sender.setPreferredSize(new Dimension(120,26));
    sender.setMinimumSize(new Dimension(120,26));
    sender.setMaximumSize(new Dimension(120,26));
    sender.setEditable(false);
    sender.setBackground(Color.white);
    senderButton = new JButton("Set");
    senderButton.setMargin(new Insets(2,3,2,3));
  
    //put(aclPanel,senderButton,1,0,1,1,false);
    //put(aclPanel,sender, 2, 0, 1, 1, false);  
  
    JPanel senderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    senderPanel.add(l);
    senderPanel.add(senderButton);
    senderPanel.add(sender);
    aclPanel.add(senderPanel);
    
    senderButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        String command = e.getActionCommand();
        AIDGui guiSender = new AIDGui(ownerGui);
        
        if(command.equals("Set"))
        {
          AID senderToView = SenderAID;
          //another sender was already inserted.
          if(newAIDSender != null)
        senderToView = newAIDSender;
          senderToView = guiSender.ShowAIDGui(senderToView,true,true);
          //if the cancel button was clicked --> maintain the old value inserted.
          if (senderToView != null)
           {newAIDSender = senderToView;
         //the name can be different
        sender.setText(newAIDSender.getName());
           }
        }
        else
        if(command.equals("View"))
          guiSender.ShowAIDGui(SenderAID, false,false);
        
      }
    });
    
    // Receiver (line # 1)
    l = new JLabel("Receivers:");
    new DropTarget(l, this);
    //put(aclPanel,l,0,1,1,1,false);
    receiverListPanel = new VisualAIDList(new ArrayList().iterator(),ownerGui);
    receiverListPanel.setDimension(new Dimension(205,37));
    //put(aclPanel,receiverListPanel,1,1,2,1,false);

    JPanel receiverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    receiverPanel.add(l);
    receiverPanel.add(receiverListPanel);
    aclPanel.add(receiverPanel);
    
    //Reply-to (line #2)
    l = new JLabel("Reply-to:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 2, 1, 1,false);
    replyToListPanel = new VisualAIDList(new ArrayList().iterator(),ownerGui);
    replyToListPanel.setDimension(new Dimension(205,37));
    //put(aclPanel,replyToListPanel,1,2,2,1,false);
    JPanel replyToPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    replyToPanel.add(l);
    replyToPanel.add(replyToListPanel);
    aclPanel.add(replyToPanel);
    
    // Communicative act (line # 3)
    l = new JLabel("Communicative act:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 3, 1, 1, false);  
    communicativeAct = new JComboBox(); 
    
    String[] comm_Act = ACLMessage.getAllPerformativeNames();
    for (int ii=0; ii<comm_Act.length; ii++)
        communicativeAct.addItem(comm_Act[ii].toLowerCase());
      
    communicativeAct.setSelectedIndex(0);
    //put(aclPanel,communicativeAct, 1, 3, 2, 1, true);
    JPanel actPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    actPanel.add(l);
    actPanel.add(communicativeAct);
    aclPanel.add(actPanel);
    
    // Content (line # 5-8)
    l = new JLabel("Content:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 5, 3, 1, false);     
    content = new JTextArea(3,TEXT_SIZE);
    JScrollPane contentPane = new JScrollPane();
    contentPane.getViewport().setView(content);   
    //put(aclPanel,contentPane, 0, 6, 3, 4, false);
    contentPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);    
    contentPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);    
    JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    contentPanel.add(l);
    contentPanel.add(contentPane);
    aclPanel.add(contentPanel);

    
    // Predfined Content (line #4)
    l = new JLabel("Predefined content:");
    new DropTarget(l, this);
   // put(aclPanel,l, 0, 4, 1, 1, false);  
    predefinedContent = new JComboBox(); 
    
    predefinedContent.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if ( e.getSource() == predefinedContent ) {
                
                content.setText("");
                if (predefinedContent.getSelectedIndex() > 0) {
                    content.setText(predefinedContent.getSelectedItem().toString());
                }
            }
        }
    });
    if (myAgent == null || ((DummyAgent)myAgent).getFileName() == null) {
        predefinedContent.setEnabled(false);
    } else {
        String line = null;
        try {
            java.io.BufferedReader reader = new BufferedReader(new FileReader(((DummyAgent)myAgent).getFileName()));
            predefinedContent.addItem("------------------");
            while ( (line = reader.readLine()) != null ) {
                if (!line.startsWith("//")) {predefinedContent.addItem(line);}
            }
            predefinedContent.setSelectedIndex(0);
        }catch (FileNotFoundException e) {
            System.err.println("Configuration file : " + ((DummyAgent)myAgent).getFileName() + " not found");
        } catch (IOException ioe) {
            System.err.println("IO Error on file : " + ((DummyAgent)myAgent).getFileName());
        }
    }
    predefinedContent.setPreferredSize(new Dimension(280,20));
    //put(aclPanel,predefinedContent, 1, 4, 2, 1, false);
    JPanel predContentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    predContentPanel.add(l);
    predContentPanel.add(predefinedContent);
    aclPanel.add(predContentPanel);

    
    // Language (line # 9)
    l = new JLabel("Language:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 10, 1, 1, false);     
    language = new JTextField(20);
    language.setBackground(Color.white);
    //put(aclPanel,language, 1, 10, 2, 1, false);  
    JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    langPanel.setSize(40,10);
    langPanel.add(l);
    langPanel.add(language);
    aclPanel.add(langPanel);
  
    //Encoding (line # 10)
    l = new JLabel("Encoding:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 11, 1, 1, false);      
    encoding = new JTextField(20); 
    encoding.setBackground(Color.white);
    //put(aclPanel,encoding, 1, 11, 2, 1, false); 
    JPanel encodingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    encodingPanel.setSize(30,10);
    encodingPanel.add(l);
    encodingPanel.add(encoding);
    aclPanel.add(encodingPanel);
    
    // Ontology (line # 11)
    l = new JLabel("Ontology:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 12, 1, 1, false);      
    ontology = new JTextField(20);
    ontology.setBackground(Color.white);
    //put(aclPanel,ontology, 1, 12, 2, 1, false); 
    JPanel ontologytPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ontologytPanel.add(l);
    ontologytPanel.add(ontology);
    aclPanel.add(ontologytPanel);

    // Protocol (line # 12)
    l = new JLabel("Protocol:");
    new DropTarget(l, this);
    //put(aclPanel,l, 0, 13, 1, 1, false);      
    protocol = new JComboBox();   
    for (i = 0;i < fipaProtocolArrayList.size(); ++i)
      protocol.addItem((String) fipaProtocolArrayList.get(i));
    protocol.addItem(LABEL_TO_ADD_PROT);
    protocol.addItem("Null");
    protocol.setSelectedItem("Null");
    lastSelectedIndex = protocol.getSelectedIndex();
    lastSelectedItem = (String) protocol.getSelectedItem();
    //put(aclPanel,protocol, 1, 13, 2, 1, true);
    protocol.addActionListener( new ActionListener()
                      {
            public void actionPerformed(ActionEvent e)
            {    
              String param = (String) protocol.getSelectedItem();
                        
              // BEFORE THE CURRENT SELECTION THE JComboBox WAS NON EDITABLE (a FIPA protocol or null was selected)
              if (!protocol.isEditable()) 
              {
                // If a user defined protocol has just been selected --> set editable to true
                if (fipaProtocolArrayList.indexOf((Object) param) < 0 && !param.equals("Null"))
                {
                  protocol.setEditable(true);
                }
              }
              // BEFORE THE CURRENT SELECTION THE JComboBox WAS EDITABLE (an editable protocol was selected)
              else 
              {
                // The user selected a FIPA protocol or null (he didn't perform any editing operation) 
                if (fipaProtocolArrayList.indexOf((Object) param) >= 0 || param.equals("Null"))
                {
                  protocol.setEditable(false);
                  protocol.setSelectedItem(param);
                }
                // The user selected the label to add a new protocol (he didn't perform any editing operation) 
                else if (param.equals(LABEL_TO_ADD_PROT))
                {
                  protocol.setSelectedItem(param);
                } 
                // The user added a new protocol
                else if (lastSelectedItem.equals(LABEL_TO_ADD_PROT))
                {     
                  // The new protocol is actually added only if it is != "" and is not already present  
                  if (!param.equals("")) 
                  {
                protocol.addItem(param);
                int cnt = protocol.getItemCount();
                protocol.setSelectedItem(param);
                int n = protocol.getSelectedIndex();
                if (n != cnt-1)
                  protocol.removeItemAt(cnt-1);
                  }
                  else 
                  {
                protocol.setEditable(false);
                protocol.setSelectedItem("Null");
                  }
                }
                // The user modified/deleted a previously added user defined protocol
                else if (lastSelectedItem != LABEL_TO_ADD_PROT)
                {
                  protocol.removeItemAt(lastSelectedIndex);  // The old protocol is removed
                  if (param.equals("")) // Deletion
                  {
                protocol.setEditable(false);
                protocol.setSelectedItem("Null");
                  }
                  else // Modification
                  {
                protocol.addItem(param);
                protocol.setSelectedItem(param);
                  }
                }
              }

              lastSelectedIndex = protocol.getSelectedIndex();
              lastSelectedItem = (String) protocol.getSelectedItem();
            }
              } );
    JPanel protocolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    protocolPanel.add(l);
    protocolPanel.add(protocol);
    aclPanel.add(protocolPanel);

      // Conversation-id (line # 13)
      l = new JLabel("Conversation-id:");
      new DropTarget(l, this);
      //put(aclPanel,l, 0, 14, 1, 1, false);      
      conversationId = new JTextField(20);
      conversationId.setBackground(Color.white);
      //put(aclPanel,conversationId, 1, 14, 2, 1, false); 
      JPanel conversationIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      conversationIdPanel.add(l);
      conversationIdPanel.add(conversationId);
      aclPanel.add(conversationIdPanel);

      // In-reply-to (line # 14)
      l = new JLabel("In-reply-to:");
      new DropTarget(l, this);
      //put(aclPanel,l, 0, 15, 1, 1, false);      
      inReplyTo = new JTextField(20);   
      inReplyTo.setBackground(Color.white);
      //put(aclPanel,inReplyTo, 1, 15, 2, 1, false);  
      JPanel inReplyToPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      inReplyToPanel.add(l);
      inReplyToPanel.add(inReplyTo);
      aclPanel.add(inReplyToPanel);

      // Reply-with (line # 15)
      l = new JLabel("Reply-with:");
      new DropTarget(l, this);
      //put(aclPanel,l, 0, 16, 1, 1, false);      
      replyWith = new JTextField(20);   
      replyWith.setBackground(Color.white);
      //put(aclPanel,replyWith, 1, 16, 2, 1, false);  
      JPanel replyWithPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      replyWithPanel.add(l);
      replyWithPanel.add(replyWith);
      aclPanel.add(replyWithPanel);
    
      // Reply-by (line # 16)
      replyByDate = null;
      l = new JLabel("Reply-by:");
      new DropTarget(l, this);
      //put(aclPanel,l, 0, 17, 1, 1, false);
      replyBySet = new JButton("Set");
      replyBySet.setMargin(new Insets(2,3,2,3));
      replyBy = new JTextField(20);
      replyBy.setBackground(Color.white);
      //put(aclPanel,replyBySet, 1, 17, 1, 1, false);
      //put(aclPanel,replyBy, 2, 17, 1, 1, false);  
      replyBySet.addActionListener(new  ActionListener()
      { // BEGIN anonumous class
          public void actionPerformed(ActionEvent e)
          {
        String command = e.getActionCommand();
        //TimeChooser t = new TimeChooser(replyByDate);
        TimeChooser t = new TimeChooser();
        String d = replyBy.getText();
        if (!d.equals(""))
        {
          try
          {
            t.setDate(ISO8601.toDate(d));
          }
          catch (Exception ee) { 
          if(logger.isLoggable(Logger.SEVERE))
            logger.log(Logger.WARNING,"Incorrect date format"); }
        }
        if (command.equals("Set"))
        {
          if (t.showEditTimeDlg(null) == TimeChooser.OK)
          {
            replyByDate = t.getDate();
            if (replyByDate == null)
              replyBy.setText("");
            else
              replyBy.setText(ISO8601.toString(replyByDate));
          }
        }
        else if (command.equals("View"))
        {         
          t.showViewTimeDlg(null);
        }
          } // END actionPerformed(ActionEvent e)
      } // END anonymous class
    ); 
      JPanel replyByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      replyByPanel.add(l);
      replyByPanel.add(replyBySet);
      replyByPanel.add(replyBy);
      aclPanel.add(replyByPanel);

    
      //Properties (line #17)
      l = new JLabel("User Properties:");
      new DropTarget(l, this);
      //put(aclPanel,l, 0, 18, 1, 1, false);
      propertiesListPanel = new VisualPropertiesList(new Properties(),ownerGui);
      propertiesListPanel.setDimension(new Dimension(205,37));
      //put(aclPanel,propertiesListPanel,1,18,2,1,false);
      JPanel propPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      propPanel.add(l);
      propPanel.add(propertiesListPanel);
      aclPanel.add(propPanel);
    
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      
      add(aclPanel);

    } // END AclMessagePanel()

    public void dragOver(java.awt.dnd.DropTargetDragEvent p1) {
      if(logger.isLoggable(Logger.FINEST))
        logger.log(Logger.FINEST,"dragOver");
    }    

    public void dropActionChanged(java.awt.dnd.DropTargetDragEvent p1) {
      if(logger.isLoggable(Logger.FINEST))
        logger.log(Logger.FINEST,"dropActionChanged");
    }
    
    public void dragEnter(java.awt.dnd.DropTargetDragEvent dragEvent) {
      if(logger.isLoggable(Logger.FINEST))
        logger.log(Logger.FINEST,"dragEnter");
    }

    public void dragExit(java.awt.dnd.DropTargetEvent p1) {
      if(logger.isLoggable(Logger.FINEST))
        logger.log(Logger.FINEST,"dragExit");
    }

    public void drop(java.awt.dnd.DropTargetDropEvent dropEvent)
    {
    boolean completionStatus = false;
    java.util.List fileList = null;

    try {
      dropEvent.acceptDrop(DnDConstants.ACTION_COPY);

      Transferable xferInfo = dropEvent.getTransferable();
      fileList = (java.util.List)(xferInfo.getTransferData(DataFlavor.javaFileListFlavor));
      completionStatus = true;
    }
    catch (UnsupportedFlavorException exc) {
        completionStatus = false;
    }
    catch (IOException exc) {
        if(logger.isLoggable(Logger.WARNING))
            logger.log(Logger.WARNING,"DragAndDrop operation failed: " + exc);
        completionStatus = false;
    }
    finally {
        dropEvent.dropComplete(completionStatus);
    }

    if (fileList != null)
    {
       Iterator fileItor = fileList.iterator();
       ACLParser aclParser = ACLParser.create();
       while (fileItor.hasNext())
       {
           try {
         java.io.File f = (java.io.File)(fileItor.next());
         FileReader aclMsgFile = new FileReader(f);
         Enumeration receivers = receiverListPanel.getContent();
         setMsg( aclParser.parse(aclMsgFile) );
         if ( receivers.hasMoreElements() ) {
             if(logger.isLoggable(Logger.FINE))
                logger.log(Logger.FINE,"revert to saved list");
             ArrayList list = new ArrayList();
             while(receivers.hasMoreElements()) {
               list.add(receivers.nextElement());
             }
             receiverListPanel.resetContent(list.iterator());
         }
           }
           catch (IOException exc) {
          if(logger.isLoggable(Logger.WARNING))
              logger.log(Logger.WARNING,"DragAndDrop operation failed: " + exc);
           }
           catch (ParseException exc) {
          if(logger.isLoggable(Logger.WARNING))
            logger.log(Logger.WARNING,"DragAndDrop operation failed: " + exc);
           }
           catch (Exception exc) {
          if(logger.isLoggable(Logger.WARNING))
            logger.log(Logger.WARNING,"DragAndDrop operation failed: " + exc);
           }
           catch (Error exc) {
          if(logger.isLoggable(Logger.WARNING))
            logger.log(Logger.WARNING,"DragAndDrop operation failed: " + exc);
           }
           catch (Throwable exc) {
          if(logger.isLoggable(Logger.WARNING))
            logger.log(Logger.WARNING,"DragAndDrop operation failed: " + exc);
           }
       } //~ while (fileItor.hasNext())
    } //~ if (selectedItems != null)

    } // END drop(dropEvent)

  } // END class AclMessagePanel
  
  //this private class build a panel to show the envelope field of an ACLMessage
  private class EnvelopePanel extends JPanel
  {
    
	EnvelopePanel()
    {
  
    JLabel l;
   
    //minDim = new Dimension();
    aclPanel = new JPanel();
    //aclPanel.setBackground(Color.lightGray); 
    aclPanel.setLayout(lm);
    
    formatGrid(21,   // N of rows 
                3,   // N of columns
                5,   // Right border 
                5,   // Left border
                5,   // Top boredr
                5,   // Bottom border
                2,   // Space between columns
                2);  // Space between rows
    setGridColumnWidth(0, 115);
    setGridColumnWidth(1, 40);
    setGridColumnWidth(2, 170);
    
    // To  (line # 0)
    l = new JLabel("To:");
    put(aclPanel,l, 0, 0, 1, 1, false); 
    toPanel = new VisualAIDList(new ArrayList().iterator(),ownerGui);
    toPanel.setDimension(new Dimension(205,37));  
    put(aclPanel,toPanel, 1, 0, 2, 1, false); 
  
    //From (line #1)
    l = new JLabel("From:");
    put(aclPanel,l, 0, 1, 1, 1,false);
    fromButton = new JButton("Set");
    fromButton.setMargin(new Insets(2,3,2,3));
    put(aclPanel,fromButton,1,1,1,1,false);
    from = new JTextField();
    from.setEditable(false);
    from.setBackground(Color.white);
    put(aclPanel,from,2,1,1,1,false);
    fromButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        AIDGui guiFrom = new AIDGui(ownerGui);
        if(command.equals("Set"))
        {
          AID fromToView = fromAID;
          if (newAIDFrom != null)
            fromToView = newAIDFrom;
          fromToView = guiFrom.ShowAIDGui(fromToView,true,true);
          if(fromToView != null)
            {
              newAIDFrom = fromToView;
              from.setText(newAIDFrom.getName());
            }
        }else
        {
          if(command.equals("View"))
            guiFrom.ShowAIDGui(fromAID,false,false);
        }
      }
    });
    
    //Comments (line # 2-6)
    l = new JLabel("Comments:");
    put(aclPanel,l,0,2,1,1,false);
    comments = new JTextArea(4,TEXT_SIZE);
    JScrollPane commentsPane = new JScrollPane();
    commentsPane.getViewport().setView(comments);
    put(aclPanel,commentsPane,0,3,3,4,false);
    commentsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    commentsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    
    //aclRappresentation (line # 7)
    l = new JLabel("ACLRepresentation:");
    put(aclPanel,l, 0, 7, 1, 1, false);     
    representation = new JTextField(); 
    representation.setBackground(Color.white);
    put(aclPanel,representation, 1, 7, 2, 1, false);  
  
    //payloadLength (line # 8)
    l = new JLabel("Payload Length:");
    put(aclPanel,l, 0, 8, 1, 1, false);     
    payloadLength = new JTextField(); 
    payloadLength.setBackground(Color.white);
    put(aclPanel,payloadLength, 1, 8, 2, 1, false);
    
      //payloadEncoding (line # 9)
    l = new JLabel("Payload Encoding:");
    put(aclPanel,l, 0, 9, 1, 1, false);     
    payloadEncoding = new JTextField(); 
    payloadEncoding.setBackground(Color.white);
    put(aclPanel,payloadEncoding, 1, 9, 2, 1, false);
    
    //Date (line # 10)
    dateDate = null;
    l = new JLabel("Date:");
    put(aclPanel,l, 0, 10, 1, 1, false);
    dateButton = new JButton("Set");
    dateButton.setMargin(new Insets(2,3,2,3));
    date = new JTextField();
    date.setBackground(Color.white);
    put(aclPanel,dateButton, 1, 10, 1, 1, false);
    put(aclPanel,date, 2, 10, 1, 1, false); 
    dateButton.addActionListener(new  ActionListener()
                      {
                        public void actionPerformed(ActionEvent e)
                        {
                          String command = e.getActionCommand();
                          //TimeChooser t = new TimeChooser(replyByDate);
                          TimeChooser t = new TimeChooser();
                          String d = date.getText();
                          if (!d.equals(""))
                          {
                            try
                            {
                              t.setDate(ISO8601.toDate(d));
                            }
                            catch (Exception ee) { 
                            if(logger.isLoggable(Logger.WARNING))
                                logger.log(Logger.WARNING,"Incorrect date format"); }
                          }
                          if (command.equals("Set"))
                          {
                            if (t.showEditTimeDlg(null) == TimeChooser.OK)
                            {
                              dateDate = t.getDate();
                              if (dateDate == null)
                                date.setText("");
                              else
                                date.setText(ISO8601.toString(dateDate));
                            }
                          }
                          else if (command.equals("View"))
                          {         
                            t.showViewTimeDlg(null);
                          }
                        }
                      } );
    
    //intendedReceiver (line #11)
    l = new JLabel("Intended Receiver:");
    put(aclPanel,l,0,11,1,1,false);
    intendedReceiverPanel = new VisualAIDList(new ArrayList().iterator(),ownerGui);
    intendedReceiverPanel.setDimension(new Dimension(205,37));
    put(aclPanel,intendedReceiverPanel, 1, 11,2,1,false);
    
    //ReceivedObject (line #12-15)
    JPanel recPanel = new JPanel();
    recPanel.setLayout(new BoxLayout(recPanel,BoxLayout.Y_AXIS));
    JPanel tempPane = new JPanel();
    tempPane.setLayout(new BoxLayout(tempPane,BoxLayout.X_AXIS));
    recPanel.setBorder(new TitledBorder("Received Object"));
    l = new JLabel("By:");
    l.setPreferredSize(new Dimension(115,24));
    l.setMinimumSize(new Dimension(115,24));
    l.setMaximumSize(new Dimension(115,24));
    tempPane.add(l);
    by = new JTextField();
    by.setBackground(Color.white);
    tempPane.add(by);
    recPanel.add(tempPane);
    tempPane = new JPanel();
    tempPane.setLayout(new BoxLayout(tempPane,BoxLayout.X_AXIS));

    l = new JLabel("From:");
    l.setPreferredSize(new Dimension(115,24));
    l.setMinimumSize(new Dimension(115,24));
    l.setMaximumSize(new Dimension(115,24));
    tempPane.add(l);
    fromRec = new JTextField();
    fromRec.setBackground(Color.white);
    tempPane.add(fromRec);
    recPanel.add(tempPane);
    
    tempPane = new JPanel();
    tempPane.setLayout(new BoxLayout(tempPane,BoxLayout.X_AXIS));

    dateRecDate = null;
    l = new JLabel("Date:");
    l.setPreferredSize(new Dimension(115,24));
    l.setMinimumSize(new Dimension(115,24));
    l.setMaximumSize(new Dimension(115,24));
    tempPane.add(l);
    dateRecButton = new JButton("Set");
    tempPane.add(dateRecButton);
    dateRecButton.addActionListener(new ActionListener()
                      {
                        public void actionPerformed(ActionEvent e)
                        {
                          String command = e.getActionCommand();
                          //TimeChooser t = new TimeChooser(replyByDate);
                          TimeChooser t = new TimeChooser();
                          String d = dateRec.getText();
                          if (!d.equals(""))
                          {
                            try
                            {
                              t.setDate(ISO8601.toDate(d));
                            }
                            catch (Exception ee) { 
                            if(logger.isLoggable(Logger.WARNING))
                                logger.log(Logger.WARNING,"Incorrect date format"); }
                          }
                          if (command.equals("Set"))
                          {
                            if (t.showEditTimeDlg(null) == TimeChooser.OK)
                            {
                              dateRecDate = t.getDate();
                              if (dateRecDate == null)
                                dateRec.setText("");
                              else
                                dateRec.setText(ISO8601.toString(dateRecDate));
                            }
                          }
                          else if (command.equals("View"))
                          {         
                            t.showViewTimeDlg(null);
                          }
                        }
                      } );

    dateRec = new JTextField();
    dateRec.setBackground(Color.white);
    tempPane.add(dateRec);
    recPanel.add(tempPane);

    tempPane = new JPanel();
    tempPane.setLayout(new BoxLayout(tempPane,BoxLayout.X_AXIS));

    l = new JLabel("ID:");
    l.setPreferredSize(new Dimension(115,24));
    l.setMinimumSize(new Dimension(115,24));
    l.setMaximumSize(new Dimension(115,24));
    tempPane.add(l);
    id = new JTextField();
    id.setBackground(Color.white);
    tempPane.add(id);
    recPanel.add(tempPane);
    
    tempPane = new JPanel();
    tempPane.setLayout(new BoxLayout(tempPane,BoxLayout.X_AXIS));

    l = new JLabel("Via:");
    l.setPreferredSize(new Dimension(115,24));
    l.setMinimumSize(new Dimension(115,24));
    l.setMaximumSize(new Dimension(115,24));
    tempPane.add(l);
    via = new JTextField();
    via.setBackground(Color.white);
    tempPane.add(via);
    recPanel.add(tempPane);

    put(aclPanel,recPanel,0,12,3,1,false);
    
    //(line 17)
    JPanel tmpPanel = new JPanel();
    //tmpPanel.setBackground(Color.lightGray);
    defaultEnvelopeButton = new JButton("Set Default Envelope");
    tmpPanel.add(defaultEnvelopeButton);
    
    defaultEnvelopeButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
          String command = e.getActionCommand();
          if(command.equals("Set Default Envelope"))
          {
            ACLMessage tmp = getMsg();
            tmp.setDefaultEnvelope();
            Envelope envtmp = tmp.getEnvelope();
            showEnvelope(envtmp);
          }
        }
    });
    put(aclPanel,tmpPanel,0,17,3,1,false);
    //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(aclPanel);

  
  }

  }
  
  public class KbasePanel extends JPanel {
      
      public KbasePanel() {
      
          setLayout(new BorderLayout());
      /////////////////////////////////////////////////////////////////////////
      ///// KBASE
      listModel = new DefaultListModel();
      kbaseList = new JList(listModel);
      
      kbaseList.setCellRenderer(new MyCellRenderer()); 
      
      
      JScrollPane kbaseScroll = new JScrollPane(kbaseList);
      //kbaseScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"KBase:"));
      kbaseScroll.setPreferredSize(new Dimension(280,120));
      add(BorderLayout.CENTER, kbaseScroll);
      JPanel buttons = new JPanel();
      JButton removeButton = new JButton(new AbstractAction("Remove") {
          public void actionPerformed(ActionEvent evt) {
              Object[] selectedIndex = kbaseList.getSelectedValues();
              if ( selectedIndex.length > 0 ) {
                  try {
                      for (int j = 0; j < selectedIndex.length; j++) {
                          Formula formPattern = SLParser.getParser().parseFormula(((MsgLog)selectedIndex[j]).getMessage(), true);
                          myAgent.getSemanticCapabilities().getMyKBase().retractFormula((Formula)formPattern);
                      }
                      updateGuiKBList();
                  }
                  catch(jade.semantics.lang.sl.parser.ParseException pe) {pe.printStackTrace();}
              }}});
      removeButton.setToolTipText("Remove the selected belief. The closed predicates are still closed.");
      buttons.add(removeButton);
      buttons.add(new JButton(new AbstractAction("Assert") {
          public void actionPerformed(ActionEvent evt) {
              AskFormula askFormula = new AskFormula("Formula:", "Assert","formula", "", myAgent, listModel);
              askFormula.showWindow();
              Formula result = askFormula.getResult();
              if (result != null) {
                     myAgent.getSemanticCapabilities().getMyKBase().assertFormula(result);
                     updateGuiKBList();
              }
          }}));
      buttons.add(new JButton(new AbstractAction("Query") {
          public void actionPerformed(ActionEvent evt) {
              AskFormulaAndResult askFormula = new AskFormulaAndResult("Formula:", "Query","formula", "", myAgent, listModel);
              askFormula.showWindow();
          }
      }));

      JButton removeAll = new JButton(new AbstractAction("RemoveAll") {
          public void actionPerformed(ActionEvent evt) {
              myAgent.getSemanticCapabilities().getMyKBase().retractFormula(SL.formula("??X"));
              myAgent.getSemanticCapabilities().getMyKBase().removeClosedPredicate(new Finder() {
                  public boolean identify(Object object) {
                      return true;
                  }
              });
              listModel.clear();
              }});
      removeAll.setToolTipText("Remove all belief. The closed predicates are not closed any more.");
      buttons.add(removeAll);
      buttons.add(new JButton(new AbstractAction("Refresh") 
       {
          public void actionPerformed(ActionEvent evt) {
              updateGuiKBList();
          }}));
      add(BorderLayout.SOUTH, buttons);
      }
  }
  
  
  /////////////////
  // CONSTRUCTOR
  /////////////////
  /**
    Ordinary <code>AclGui</code> constructor.
    @see jade.lang.acl.ACLMessage#ACLMessage(int)
  */

  public AclGui(Component owner, DummyAgent agent)
  { 
    
    firstPaintFlag = true;
    guiEnabledFlag = true;
    minDim = new Dimension();
    ownerGui = owner;
    myAgent = agent;
    JTabbedPane tabbed = new JTabbedPane();
    AclMessagePanel aclPane = new AclMessagePanel();
    EnvelopePanel envelope = new EnvelopePanel();
    KbasePanel basePane = new KbasePanel();
    tabbed.addTab("ACLMessage",aclPane);
    tabbed.addTab("Envelope",envelope);
    tabbed.add("KBase",basePane);
    
    //to enable the textfields if needed.
    updateEnabled();  
    add(tabbed);

    // Try inserting formatted SL content.
    // any Exception is catched in order to remove unwished dependency
    // on the jade.tools.sl package from this package at run-time.
    try { 
    slFormatter = new SLFormatter();
    } catch (Exception e) {
    }
  }

//  private void updateGuiKBList() {
//      ArrayList base = myAgent.getSemanticCapabilities().getMyKBase().toStrings();
//      listModel.clear();
//      for (int i = 0; i < base.size(); i++) {
//          if (myAgent.getSemanticCapabilities().getMyKBase().isClosed(SL.formula((String)base.get(i)),null)) {
//              listModel.addElement(new MsgLog((String)base.get(i),Color.red));
//          } else {
//              listModel.addElement(new MsgLog((String)base.get(i),Color.black));
//          }
//      }
//  }
  
  // modified 29/01/08 Carole Adam
  private void updateGuiKBList() {
      listModel.clear();
      for (jade.util.leap.Iterator i = myAgent.getSemanticCapabilities().getMyKBase().toStrings().iterator(); i.hasNext(); ) {
      	String formula = (String)i.next();
          if (myAgent.getSemanticCapabilities().getMyKBase().isClosed(SL.formula(formula),null)) {
              listModel.addElement(new MsgLog(formula,Color.red));
          } else {
              listModel.addElement(new MsgLog(formula,Color.black));
          }
      }
  }
  
  public AclGui(Component owner)
  { 
    
    firstPaintFlag = true;
    guiEnabledFlag = true;
    minDim = new Dimension();
    ownerGui = owner;
    
    JTabbedPane tabbed = new JTabbedPane();
    AclMessagePanel aclPane = new AclMessagePanel();
    EnvelopePanel envelope = new EnvelopePanel();
    tabbed.addTab("ACLMessage",aclPane);
    tabbed.addTab("Envelope",envelope);
    
    //to enable the textfields if needed.
    updateEnabled();  
    add(tabbed);

    // Try inserting formatted SL content.
    // any Exception is catched in order to remove unwished dependency
    // on the jade.tools.sl package from this package at run-time.
    try { 
    slFormatter = new SLFormatter();
    } catch (Exception e) {
    }
  }
  
  
  
  
  
  
  
//  public AclGui(Component owner)
//  { 
//    
//    firstPaintFlag = true;
//    guiEnabledFlag = true;
//    minDim = new Dimension();
//    ownerGui = owner;
//    JTabbedPane tabbed = new JTabbedPane();
//    AclMessagePanel aclPane = new AclMessagePanel();
//    EnvelopePanel envelope = new EnvelopePanel();
//    tabbed.addTab("ACLMessage",aclPane);
//    tabbed.addTab("Envelope",envelope);
//    //to enable the textfields if needed.
//    updateEnabled();  
//    add(tabbed);
//
//    // Try inserting formatted SL content.
//    // any Exception is catched in order to remove unwished dependency
//    // on the jade.tools.sl package from this package at run-time.
//    try { 
//    slFormatter = new SLFormatter();
//    } catch (Exception e) {
//    }
//  }
    SLFormatter slFormatter;
  ////////////////////
  // PRIVATE METHODS
  ////////////////////
  private void formatGrid(int nr, int nc, int lb, int rb, int tb, int bb, int xs, int ys)
  {
    gridNRow = nr;
    gridNCol = nc;
    colWidth = new int[3];
    //colWidth[0] = 120;
    //colWidth[1] = 63;
    //colWidth[2] = 180;
    leftBorder = lb;
    rightBorder = rb;
    topBorder = tb;
    bottomBorder = bb;
    xSpacing = xs;
    ySpacing = ys;
  }

  private void setGridColumnWidth(int col, int width)
  {
    colWidth[col] = width;
  }

  private void put(JPanel panel, JComponent c, int x, int y, int dx, int dy, boolean fill)
  {
  int leftMargin, rightMargin, topMargin, bottomMargin;
  int preferredWidth, preferredHeight;
    
    constraint.gridx = x;
    constraint.gridy = y;
    constraint.gridwidth = dx;
    constraint.gridheight = dy;
    constraint.anchor = GridBagConstraints.WEST;
    if (fill)
      constraint.fill = GridBagConstraints.BOTH;
    else
      constraint.fill = GridBagConstraints.VERTICAL;

    leftMargin =   (x == 0 ? leftBorder : 0);
    rightMargin =  (x+dx == gridNCol ? rightBorder : xSpacing);
    topMargin =    (y == 0 ? topBorder : 0);
    bottomMargin = (y+dy == gridNRow ? bottomBorder : ySpacing);

    int i;
    preferredWidth = 0; 
    for (i = 0; i < dx; ++i)
      preferredWidth += colWidth[x+i] + xSpacing;
    preferredWidth -= xSpacing;
    preferredHeight = c.getPreferredSize().height;
    c.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

    constraint.insets = new Insets(topMargin, leftMargin, bottomMargin, rightMargin);
    lm.setConstraints(c,constraint); 
    panel.add(c);
  }

  private void updateEnabled()
  {
    communicativeAct.setEnabled(guiEnabledFlag);
    senderButton.setText((guiEnabledFlag && senderEnabledFlag) ? "Set" : "View");
  
    receiverListPanel.setEnabled(guiEnabledFlag);
    replyToListPanel.setEnabled(guiEnabledFlag);
    propertiesListPanel.setEnabled(guiEnabledFlag);
    
    replyWith.setEditable(guiEnabledFlag);
    inReplyTo.setEditable(guiEnabledFlag);
    conversationId.setEditable(guiEnabledFlag);
    replyBy.setEditable(false);
    replyBySet.setEnabled(true);
    replyBySet.setText(guiEnabledFlag ? "Set" : "View");
    encoding.setEditable(guiEnabledFlag);
    protocol.setEnabled(guiEnabledFlag);
    language.setEditable(guiEnabledFlag);
    ontology.setEditable(guiEnabledFlag);
    content.setEditable(guiEnabledFlag);
    
    //Envelope
    fromButton.setText(guiEnabledFlag && senderEnabledFlag ? "Set": "View");
    toPanel.setEnabled(guiEnabledFlag);
    comments.setEnabled(guiEnabledFlag);
    representation.setEnabled(guiEnabledFlag);
    payloadLength.setEnabled(guiEnabledFlag);
    payloadEncoding.setEnabled(guiEnabledFlag);
    date.setEditable(false);
    dateButton.setText(guiEnabledFlag ? "Set" : "View");

    intendedReceiverPanel.setEnabled(guiEnabledFlag);
    defaultEnvelopeButton.setVisible(guiEnabledFlag);
      //ReceivedObject
    by.setEditable(guiEnabledFlag);
    fromRec.setEditable(guiEnabledFlag);
    dateRec.setEditable(false);
    dateRecButton.setText(guiEnabledFlag ? "Set" : "View");
    id.setEditable(guiEnabledFlag);
    via.setEditable(guiEnabledFlag);
  }

  private void showEnvelope(Envelope envelope)
  {
      String param; 
      try {
        this.fromAID = envelope.getFrom();
        param = fromAID.getName();
      } catch (NullPointerException e1) {
        param = "";
        this.fromAID = new AID();
      }
      from.setText(param);

      toPanel.resetContent(envelope.getAllTo());
      try{
        AID fromAID = envelope.getFrom();
        param = fromAID.getName();
        
      }catch(NullPointerException e1){param = "";}
      from.setText(param);
      
      try{
        param = envelope.getComments();
      }catch(NullPointerException e1){param ="";}
      comments.setText(param);
      
      try{
        param = envelope.getAclRepresentation();
      }catch(NullPointerException e1){param ="";}
      representation.setText(param);
      
      try{
        param = envelope.getPayloadLength().toString();
      }catch(NullPointerException e1){param ="-1";}
      payloadLength.setText(param);
      
      try{
        param = envelope.getPayloadEncoding();
      }catch(NullPointerException e1){param ="";}
      payloadEncoding.setText(param);

      //Date
      dateDate = envelope.getDate();
      if (dateDate != null)
          date.setText(ISO8601.toString(dateDate)); 
      else
        date.setText("");
      
      intendedReceiverPanel.resetContent(envelope.getAllIntendedReceiver());
      
      ReceivedObject recObject = envelope.getReceived();
      try{
        param = recObject.getBy();
      }catch(NullPointerException e){
        param = "";
      }
      by.setText(param);
      try{
        param = recObject.getFrom();
      }catch(NullPointerException e){
        param = "";
      }
      fromRec.setText(param);
  
      try{
        dateRecDate = recObject.getDate();
        param = ISO8601.toString(dateRecDate);
      }catch(NullPointerException e){
       param = "";      
      }
      dateRec.setText(param);

      try{
        param = recObject.getId();
      }catch(NullPointerException e){
        param = "";
      }
      id.setText(param);
      try{
        param = recObject.getVia();
      }catch(NullPointerException e){
        param = "";
      }
      via.setText(param);
  }

  /////////////////////////////////////////////
  // MESSAGE GETTING and SETTING PUBLIC METHODS
  /////////////////////////////////////////////
  /**
    Displays the specified ACL message into the AclGui panel 
    @param msg The ACL message to be displayed
    @see AclGui#getMsg()
  */
  public void setMsg(ACLMessage msg)
  {
    int    i;
    String param, lowerCase;
    
    int perf = msg.getPerformative(); 
    lowerCase = (ACLMessage.getPerformative(perf)).toLowerCase();
    
    //No control if the ACLMessage is a well-known one
    //if not present the first of the comboBox is selected
    communicativeAct.setSelectedItem(lowerCase);  
    
    try {
      this.SenderAID = msg.getSender();
      param = SenderAID.getName();
    } catch (NullPointerException e) {
      param = "";
      this.SenderAID = new AID();
    }
  
    sender.setText(param);
    
    receiverListPanel.resetContent(msg.getAllReceiver());
    replyToListPanel.resetContent(msg.getAllReplyTo());
    
    Enumeration e =   msg.getAllUserDefinedParameters().propertyNames();
    ArrayList list = new ArrayList();
    while(e.hasMoreElements())
      list.add(e.nextElement());
    propertiesListPanel.resetContent(list.iterator());
    propertiesListPanel.setContentProperties(msg.getAllUserDefinedParameters());
    
    if ((param = msg.getReplyWith()) == null) param = "";
    replyWith.setText(param);
    if ((param = msg.getInReplyTo()) == null) param = "";
    inReplyTo.setText(param);
    if ((param = msg.getConversationId()) == null) param = "";
    conversationId.setText(param);
    try {
    param=ISO8601.toString(msg.getReplyByDate());
    } catch (Exception exc) {
    param="";
    }
    replyBy.setText(param);
    
    if((param = msg.getProtocol()) == null)
      protocol.setSelectedItem("Null");
    else if (param.equals("") || param.equalsIgnoreCase("Null"))
      protocol.setSelectedItem("Null");
    else
    {
      lowerCase = param.toLowerCase();
      if ((i = fipaProtocolArrayList.indexOf((Object) lowerCase)) < 0)
      {
        // This is done to avoid inserting the same user-defined protocol more than once
        protocol.addItem(param);
        int cnt = protocol.getItemCount();
        protocol.setSelectedItem(param);
        int n = protocol.getSelectedIndex();
        if (n != cnt-1)
          protocol.removeItemAt(cnt-1);
      }
      else
        protocol.setSelectedIndex(i);
    }
    String lang;
    if ((lang = msg.getLanguage()) == null) lang = "";
    language.setText(lang);
    if ((param = msg.getOntology()) == null) param = "";
    ontology.setText(param);

    if ((param = msg.getContent()) == null) param = "";
    if ( (lang.equalsIgnoreCase(FIPANames.ContentLanguage.FIPA_SL0) ||
      lang.equalsIgnoreCase(FIPANames.ContentLanguage.FIPA_SL1) ||
      lang.equalsIgnoreCase(FIPANames.ContentLanguage.FIPA_SL2) ||
      lang.equalsIgnoreCase(FIPANames.ContentLanguage.FIPA_SL)) &&
     (slFormatter != null))
    // Try inserting formatted SL content.
        param = SLFormatter.format(param);
    content.setText(param);

    if((param = msg.getEncoding())== null) param = "";
    encoding.setText(param);
  
    //Envelope
    Envelope envelope = msg.getEnvelope();
        
    if(envelope != null)
        showEnvelope(envelope);
      
      
  }
  
    
  /**
    Get the ACL message currently displayed by the AclGui panel 
    @return The ACL message currently displayed by the AclGui panel as an ACLMessage object
    @see AclGui#setMsg(ACLMessage msg)
  */
  public ACLMessage getMsg()
  {
    String param;
    param = (String) communicativeAct.getSelectedItem();
    int perf = ACLMessage.getInteger(param);
    ACLMessage msg = new ACLMessage(perf);
    
    if(newAIDSender != null)
      SenderAID = newAIDSender;
    
    /*if ( ((param = sender.getText()).trim()).length() > 0 )
      SenderAID.setName(param);*/
    // check if SenderAID has a guid. SenderAID is surely not null here
    if (SenderAID.getName().length() > 0)
       msg.setSender(SenderAID);

    Enumeration rec_Enum = receiverListPanel.getContent();
    while(rec_Enum.hasMoreElements())
      msg.addReceiver((AID)rec_Enum.nextElement());
    
    Enumeration replyTo_Enum = replyToListPanel.getContent();
    while(replyTo_Enum.hasMoreElements())
      msg.addReplyTo((AID)replyTo_Enum.nextElement());
      
    Properties user_Prop = propertiesListPanel.getContentProperties();
    Enumeration keys = user_Prop.propertyNames();
    while(keys.hasMoreElements())
    {
      String k = (String)keys.nextElement();
      msg.addUserDefinedParameter(k,user_Prop.getProperty(k));
    }
    
    param = replyWith.getText().trim();
    if (param.length() > 0)
      msg.setReplyWith(param);
      
    param = inReplyTo.getText().trim(); 
    if (param.length() > 0)
      msg.setInReplyTo(param);
      
    param = conversationId.getText().trim();  
    if (param.length() > 0)
      msg.setConversationId(param);
      
    param = replyBy.getText().trim(); 
    try {
    msg.setReplyByDate(ISO8601.toDate(param));
    } catch (Exception e) {}
    
    if (!(param = (String) protocol.getSelectedItem()).equals("Null"))
      msg.setProtocol(param);
      
    param = language.getText().trim();
    if (param.length()>0)
      msg.setLanguage(param);
      
    param = ontology.getText().trim();  
    if (param.length()>0)
      msg.setOntology(param);
      
    param = content.getText().trim();   
    if (param.length()>0)
      msg.setContent(param);
    
    param = (encoding.getText()).trim();
    if(param.length() > 0)
      msg.setEncoding(param);
    
    Envelope env = new Envelope();  
    
    Enumeration to_Enum = toPanel.getContent();
    while(to_Enum.hasMoreElements())
      env.addTo((AID)to_Enum.nextElement());

    if(newAIDFrom!= null)
      fromAID = newAIDFrom;
    if (fromAID.getName().length() > 0)
       env.setFrom(fromAID);

    param = comments.getText().trim();
    if(param.length()>0)
      env.setComments(param);
      
    param = representation.getText().trim();
    if(param.length()>0)
      env.setAclRepresentation(param);

    try {
  param = payloadLength.getText().trim();
  env.setPayloadLength(new Long(param));
    } catch (Exception e) { 
  //System.err.println("Incorrect int format. payloadLength must be an integer. Automatic reset to -1.");
  //env.setPayloadLength(new Long(-1));
  //payloadLength.setText("-1");
    }
  
    param = payloadEncoding.getText().trim();
    if(param.length()>0)
      env.setPayloadEncoding(param);
   
    //setDate require a Date not a String
    if (dateDate != null) 
      env.setDate(dateDate);
    
    Enumeration int_Enum = intendedReceiverPanel.getContent();
    while(int_Enum.hasMoreElements())
      env.addIntendedReceiver((AID)int_Enum.nextElement());
   
   
    param = language.getText().trim();
    if (param.length()>0)
      msg.setLanguage(param);
  
    
    /* ReceivedObject recObject = new ReceivedObject();
    boolean filled = false;
    param = by.getText().trim();
    
    if(param.length()>0)
    {
      filled = true;
      recObject.setBy(param);
    }
    param = fromRec.getText().trim();
    if(param.length()>0)
    {
      filled = true;
      recObject.setFrom(param);
    }
    
    if (dateRecDate != null)  
    {
      filled = true;
      recObject.setDate(dateRecDate);
    }

    param = id.getText().trim();
    if(param.length()>0)
    {
      filled = true;
      recObject.setId(param);
    }
      
    param = via.getText().trim();
    if(param.length()>0)
    {
      filled = true;
      recObject.setVia(param);
    }
    
    if(filled)
      env.setReceived(recObject);
      */
    msg.setEnvelope(env);
    return msg;
  }

  
  /////////////////////////
  // UTILITY PUBLIC METHODS
  /////////////////////////
  /** 
    Enables/disables the editability of all the controls in an AclGui panel (default is enabled)
    @param enabledFlag If true enables editability 
    @see AclGui#setSenderEnabled(boolean enabledFlag)
  */
  public void setEnabled(boolean enabledFlag)
  {
    guiEnabledFlag = enabledFlag;
    updateEnabled();
  }

  /** 
    Enables/disables the editability of the sender field of an AclGui panel (default is enabled)
    @param enabledFlag If true enables editability 
    @see AclGui#setEnabled(boolean enabledFlag)
  */
  public void setSenderEnabled(boolean enabledFlag)
  {
    senderEnabledFlag = enabledFlag;
    updateEnabled();
  }

  /** 
    Set the specified border to the AclGui panel
    @param b Specifies the type of border
  */
  /*public void setBorder(Border b)
  {
    if (aclPanel != null)
      aclPanel.setBorder(b);
  }*/

  /** 
    Paint the AclGui panel
  */
  public void paint(Graphics g)
  {
    if (firstPaintFlag)
    {
      firstPaintFlag = false;
      minDim = aclPanel.getSize();
    }
    else
      aclPanel.setMinimumSize(minDim);

    super.paint(g);
  }


  //////////////////
  // STATIC METHODS
  //////////////////
  /**
    Pops up a dialog window including an editing-disabled AclGui panel and displays the specified 
    ACL message in it. 
    @param msg The ACL message to be displayed
    @param parent The parent window of the dialog window
    @see AclGui#editMsgInDialog(ACLMessage msg, Frame parent)
  */
  public static void showMsgInDialog(ACLMessage msg, Frame parent)
  {
    final JDialog tempAclDlg = new JDialog(parent, "ACL Message", true);
  
    AclGui aclPanel = new AclGui(parent);
    //aclPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
    aclPanel.setEnabled(false);
    aclPanel.setMsg(msg);

    JButton okButton = new JButton("OK");
    JPanel buttonPanel = new JPanel();
    // Use default (FlowLayout) layout manager to dispose the OK button
    buttonPanel.add(okButton);

    tempAclDlg.getContentPane().setLayout(new BorderLayout());
    tempAclDlg.getContentPane().add("Center", aclPanel);
    tempAclDlg.getContentPane().add("South", buttonPanel);

    okButton.addActionListener(new ActionListener()
                     {
                      public void actionPerformed(ActionEvent e)
                      {
                        tempAclDlg.dispose();
                      }
                     } );

    tempAclDlg.pack();
    tempAclDlg.setResizable(false);
    if (parent != null) {
      int locx = parent.getX() + (parent.getWidth() - tempAclDlg.getWidth()) / 2;
      if (locx < 0)
        locx = 0;
      int locy = parent.getY() + (parent.getHeight() - tempAclDlg.getHeight()) / 2;
      if (locy < 0)
        locy = 0;
      tempAclDlg.setLocation(locx,locy);
    }
    tempAclDlg.setVisible(true);
  }

  /**
    Pops up a dialog window including an editing-enabled AclGui panel and displays the specified 
    ACL message in it. The dialog window also includes an OK and a Cancel button to accept or 
    discard the performed editing. 
    @param m The ACL message to be initially displayed
    @param parent The parent window of the dialog window
    @return The ACL message displayed in the dialog window or null depending on whether the user close the window
    by clicking the OK or Cancel button 
    @see AclGui#showMsgInDialog(ACLMessage msg, Frame parent)
  */
  public static ACLMessage editMsgInDialog(ACLMessage msg, Frame parent)
  {
    final JDialog tempAclDlg = new JDialog(parent, "ACL Message", true);
    final AclGui  aclPanel = new AclGui(parent);
    aclPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
    aclPanel.setSenderEnabled(true);
    aclPanel.setMsg(msg);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    okButton.setPreferredSize(cancelButton.getPreferredSize());
    JPanel buttonPanel = new JPanel();
    // Use default (FlowLayout) layout manager to dispose the OK and Cancel buttons
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);  

    tempAclDlg.getContentPane().setLayout(new BorderLayout());
    tempAclDlg.getContentPane().add("Center", aclPanel);
    tempAclDlg.getContentPane().add("South", buttonPanel);
    
    okButton.addActionListener(new ActionListener()
                     {
                      public void actionPerformed(ActionEvent e)
                      {
                        editedMsg = aclPanel.getMsg();
                        tempAclDlg.dispose();
                      }
                     } );
    cancelButton.addActionListener(new ActionListener()
                       {
                        public void actionPerformed(ActionEvent e)
                        {
                          editedMsg = null;
                          tempAclDlg.dispose();
                        }
                       } );
    
    tempAclDlg.pack();
    tempAclDlg.setResizable(false);
    
    if (parent != null)
      {
        int x = parent.getX() + (parent.getWidth() - tempAclDlg.getWidth()) / 2;
        int y = parent.getY() + (parent.getHeight() - tempAclDlg.getHeight()) / 2;
        tempAclDlg.setLocation(x > 0 ? x :0, y>0 ? y :0);
      }
      
    tempAclDlg.setVisible(true);
     
    ACLMessage m = null;
    if (editedMsg != null)
      m = (ACLMessage) editedMsg.clone();
    
    return m;
  }

  public static void main(String[] args)
  {
    JFrame f = new JFrame();
  
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    msg.setLanguage("language");
    msg.setOntology("onto");
    Envelope env = new Envelope();
    env.setComments("Commento");
    env.setAclRepresentation("ACLRepresentation");
    msg.setEnvelope(env);
    //AclGui.showMsgInDialog(msg,f);
    DummyAgent agt = new DummyAgent();
    f.add(new AclGui(new JFrame(), agt));
    f.pack();
    f.setVisible(true);
  }
  
  class MyCellRenderer extends JLabel implements ListCellRenderer { 
      
      MyCellRenderer() 
      {
          super();
          setOpaque(true);

      }
      
      public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) 
      {
          Font courier = new Font("Courier", Font.BOLD, 12);
          setFont(courier);
          MsgLog msgLog = (MsgLog)value; 
          setText(msgLog.getMessage());
          if (isSelected) {
              setBackground(Color.black);
              if (msgLog.getColor().equals(Color.black))  {
                  setForeground(Color.white);
              } else {
                  setForeground(msgLog.getColor());
              }
          } else {
              
              setForeground(msgLog.getColor());
              setBackground(Color.white);
          }
               
          
          return this; 
      }
      
      
}




}
