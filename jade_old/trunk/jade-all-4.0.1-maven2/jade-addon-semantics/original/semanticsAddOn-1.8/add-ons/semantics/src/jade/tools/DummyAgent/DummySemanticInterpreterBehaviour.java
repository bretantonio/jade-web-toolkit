/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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
 * DummySemanticInterpreterBehaviour.java
 * Created on 5 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.StringACLCodec;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpreterBehaviour;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.Iterator;

import java.awt.Color;
import java.util.Date;

import javax.swing.SwingUtilities;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class DummySemanticInterpreterBehaviour extends
        SemanticInterpreterBehaviour {

    MessageTemplate msgTemp;
    
    public DummySemanticInterpreterBehaviour(MessageTemplate template, SemanticCapabilities capabilities) {
        super(template, capabilities);
        msgTemp = template;
    }
    
    public ACLMessage receiveNextMessage()
    {
        ACLMessage msg = null;
        if (msgTemp != null) {
            msg = myAgent.receive(msgTemp);
        }
        else {
            msg = myAgent.receive();
        }
        if (msg != null) SwingUtilities.invokeLater(new EDTRequester((DummyAgent)myAgent, msg));
        return msg;
    }
    
    class EDTRequester implements Runnable {
        DummyAgent agent;
        ACLMessage msg;
        
        EDTRequester(DummyAgent a, ACLMessage m) {
            agent = a;
            msg = m;
        }

//        public void run() {
//            agent.getGui().queuedMsgListModel.add(0, (Object) new MsgIndication(msg, MsgIndication.INCOMING, new Date()));
//            String[] data = getMyCapabilities().getMyKBase().toStrings();
//            agent.getGui().getCurrentMsgGui().listModel.clear();
//            for (int i = 0; i < data.length; i++) {
//                if (getMyCapabilities().getMyKBase().isClosed(SL.formula(data[i]),null)) {
//                    
//                    agent.getGui().getCurrentMsgGui().listModel.addElement(new MsgLog(data[i],Color.red));
//                } else {
//                    
//                    agent.getGui().getCurrentMsgGui().listModel.addElement(new MsgLog(data[i],Color.black));
//                }
//            }
////            System.out.println("Size behv : " + agent.getGui().getCurrentMsgGui().listModel.size());
////            for (int u = 0; u < agent.getGui().getCurrentMsgGui().listModel.size(); u++) {
////                System.out.println("ListModel " + u + " : " + agent.getGui().getCurrentMsgGui().listModel.getElementAt(u));    
////            }
//            
//            agent.getGui().getCurrentMsgGui().kbaseList.repaint();
//            StringACLCodec codec = new StringACLCodec();
//          try {
//            String charset;
//        Envelope e;
//        if (((e = msg.getEnvelope()) == null) ||
//            ((charset = e.getPayloadEncoding()) == null)) {
//          charset = ACLCodec.DEFAULT_CHARSET;
//        }
//        codec.decode(codec.encode(msg,charset),charset);
//          } catch (ACLCodec.CodecException ce) {
//                    ce.printStackTrace();
//          }
//        }
        // modified 29January08 Carole Adam
        public void run() {
            agent.getGui().queuedMsgListModel.add(0, new MsgIndication(msg, MsgIndication.INCOMING, new Date()));
            agent.getGui().getCurrentMsgGui().listModel.clear();
            for (Iterator i = getMyCapabilities().getMyKBase().toStrings().iterator(); i.hasNext(); ) {
            	String formula = (String)i.next();
               if (getMyCapabilities().getMyKBase().isClosed(SL.formula(formula),null)) {
                	agent.getGui().getCurrentMsgGui().listModel.addElement(new MsgLog(formula,Color.red));
                } else {
                	agent.getGui().getCurrentMsgGui().listModel.addElement(new MsgLog(formula,Color.black));
                }
            }
//            System.out.println("Size behv : " + agent.getGui().getCurrentMsgGui().listModel.size());
//            for (int u = 0; u < agent.getGui().getCurrentMsgGui().listModel.size(); u++) {
//                System.out.println("ListModel " + u + " : " + agent.getGui().getCurrentMsgGui().listModel.getElementAt(u));    
//            }
            
            agent.getGui().getCurrentMsgGui().kbaseList.repaint();
            StringACLCodec codec = new StringACLCodec();
          try {
            String charset;
        Envelope e;
        if (((e = msg.getEnvelope()) == null) ||
            ((charset = e.getPayloadEncoding()) == null)) {
          charset = ACLCodec.DEFAULT_CHARSET;
        }
        codec.decode(codec.encode(msg,charset),charset);
          } catch (ACLCodec.CodecException ce) {
                    ce.printStackTrace();
          }
        }
        
    }
}
