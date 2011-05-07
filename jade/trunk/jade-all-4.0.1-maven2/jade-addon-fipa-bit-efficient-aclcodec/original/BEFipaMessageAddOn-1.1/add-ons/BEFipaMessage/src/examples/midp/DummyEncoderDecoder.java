/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package examples.midp;

import java.io.*;
import jade.lang.acl.*;
import jade.core.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import cascom.fipa.acl.ACLInputStream;
import cascom.fipa.acl.ACLOutputStream;

/**
 *  MIDlet test class for BEencoder and BEdecoder. Creates user interface where 
 *  you can give some parts of FIPA message. It will be shown in bit efficient 
 *  form and decoded back.
 *
 *  @author Ahti Syreeni
 */
public class DummyEncoderDecoder extends MIDlet implements CommandListener {
        
        private Form form;
        private TextField encodedDisp;
        private TextField contentDisp;
        private TextField addressDisp;
        private TextField languageDisp;
        private TextField errorDisp;
        private TextField aidDisp;
        private TextField protocolDisp;

        private TextField typeInput;
        private TextField addressInput;
        private TextField contentInput;
        private TextField languageInput;
        private TextField protocolInput;
        private TextField aidInput;

        private javax.microedition.lcdui.Command executeButton;
   
        /**
         *  Creates the user interface for MIDP.
         */
        public DummyEncoderDecoder() {
            this.form = new Form("BE-Test");
            
            
            this.encodedDisp = new TextField("BE Encoded:","",1000,TextField.UNEDITABLE);
            this.aidDisp = new TextField("Decoded/Sender AID:","",10000,TextField.UNEDITABLE);
            this.addressDisp = new TextField("Decoded/Sender Address:","",10000,TextField.UNEDITABLE);
            this.contentDisp = new TextField("Decoded/Content:","",10000,TextField.UNEDITABLE);
            this.languageDisp = new TextField("Decoded/Language:","",10000,TextField.UNEDITABLE);
            this.protocolDisp = new TextField("Decoded/Protocol:","",10000,TextField.UNEDITABLE);

            this.typeInput = new TextField("Type:","REQUEST",1000,TextField.UNEDITABLE);
            this.aidInput = new TextField("Sender AID:","da0@135.174.32.131:1099/JADE",1000,TextField.ANY);
            this.addressInput = new TextField("Sender Address:","http://foo.bar:7778/acc",10000,TextField.ANY);
            this.contentInput = new TextField("Content:","open db.txt for input",10000,TextField.ANY);
            this.languageInput = new TextField("Language:","db",10000,TextField.ANY);
            this.protocolInput = new TextField("Protocol:","fipa-request",10000,TextField.ANY);

            this.errorDisp = new TextField("Error:","",10000,TextField.UNEDITABLE);
           
            this.executeButton = new javax.microedition.lcdui.Command("Execute",javax.microedition.lcdui.Command.ITEM,1);
            
            
            
            this.form.append(this.typeInput);
            this.form.append(this.aidInput);
            this.form.append(this.addressInput);
            this.form.append(this.contentInput);
            this.form.append(this.languageInput);
            this.form.append(this.protocolInput);
                        
            this.form.append(this.encodedDisp);
            this.form.append(this.aidDisp);
            this.form.append(this.addressDisp);
            this.form.append(this.contentDisp);
            this.form.append(this.languageDisp);
            this.form.append(this.protocolDisp);
            this.form.append(this.errorDisp);
            form.addCommand(executeButton);
            form.addCommand(new javax.microedition.lcdui.Command("Quit", javax.microedition.lcdui.Command.EXIT, 0));
            form.setCommandListener(this);
            Display.getDisplay(this).setCurrent(this.form);

            
  }
        
  /**
   *  Calls encoding and decoding methods of BECodec and displays the results.
   */
  public void commandAction(javax.microedition.lcdui.Command c, javax.microedition.lcdui.Displayable s) {
    if(c == this.executeButton) {
        this.errorDisp.setString("");
            try {
            ACLMessage message = null;
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            ACLOutputStream os = new ACLOutputStream(outByteStream);
            
            try {
                    // Second paramater, charset, doesn't matter, it is not implemented in jade 3.3
                
                
                    message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(this.contentInput.getString());
                    AID aid = new AID(this.aidInput.getString(),true);
                    aid.addAddresses(this.addressInput.getString());
                    message.setSender(aid);
                    message.setLanguage(this.languageInput.getString());
                    message.setProtocol(this.protocolInput.getString());
                    
                    os.write(message);
                
            } catch (Exception e) {
                this.errorDisp.setString("ERROR: "+e.getMessage());
            }
            byte[] outByteArray = outByteStream.toByteArray();
            this.encodedDisp.setString(new String(outByteArray));
            // do the decoding
            
            ACLInputStream is = new ACLInputStream(new ByteArrayInputStream(outByteArray));
            ACLMessage message2 = null;
            try {                         
                    message2 = is.readMsg();
                    this.addressDisp.setString("error");
                    this.contentDisp.setString("error");
                    this.languageDisp.setString("error");
                    this.protocolDisp.setString("error");
                    this.aidDisp.setString("error");
                    
                    if(message2 != null){
                                                
                        this.addressDisp.setString(message2.getSender().getAddressesArray()[0]);
                        this.contentDisp.setString(message2.getContent());
                        this.languageDisp.setString(message2.getLanguage());
                        this.protocolDisp.setString(message2.getProtocol());
                        this.aidDisp.setString(message2.getSender().getName());
                    } else {
                        this.errorDisp.setString("Error: decoded message is null");
                    }
            } catch (Exception e) {
                this.errorDisp.setString("ERROR:"+e.getMessage());
            }
        } catch (Exception e) {
            this.errorDisp.setString("Error: "+e.getMessage());
        }
                
    } else {
        // command source was quit -button
        notifyDestroyed();
    } 
    }

  /**
   * Pauses the MIDlet.
   */
  public void pauseApp(){
  }
  
  
  /**
   * Displays the user interface.
   */
  public void startApp() {
    Display.getDisplay(this).setCurrent(this.form);
  }
  
  /**
   * Exit program
   */
  public void destroyApp(boolean unconditional) {}
    
}
