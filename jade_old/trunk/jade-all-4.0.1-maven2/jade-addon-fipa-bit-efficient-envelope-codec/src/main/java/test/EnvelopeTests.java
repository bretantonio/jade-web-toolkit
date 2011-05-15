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

package test.midp;

import java.io.*;
import cascom.fipa.acl.*;
import cascom.fipa.util.ByteArray;
import test.TestEnvelopes;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.*;
import jade.core.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import cascom.fipa.envelope.EnvelopeEncoder;
import cascom.fipa.envelope.EnvelopeDecoder;

/**
 *  MIDlet test class for EnvelopeEncoder and EnvelopeDecoder. The tests
 *  are same as in J2SE-version but results are not written to file. In tests
 *  the test Envelopes are first encoded and then decoded, errors are showed
 *  on the screen if such occures.
 *
 *  @author Ahti Syreeni - TeliaSonera
 */
public class EnvelopeTests extends MIDlet implements CommandListener {
    
    private Form form;
    private TextField statusDisp;
    private javax.microedition.lcdui.Command executeButton;
    
    /**
     *  Creates the user interface for MIDP.
     */
    public EnvelopeTests() {
        this.form = new Form("BE-Test");
        
        this.statusDisp = new TextField("Status","",10000,TextField.UNEDITABLE);
        this.executeButton = new javax.microedition.lcdui.Command("Execute",javax.microedition.lcdui.Command.ITEM,1);
        this.form.append(this.statusDisp);
        form.addCommand(executeButton);
        form.addCommand(new javax.microedition.lcdui.Command("Quit", javax.microedition.lcdui.Command.EXIT, 0));
        form.setCommandListener(this);
        Display.getDisplay(this).setCurrent(this.form);
    }
    
    /**
     *  Calls encoding and decoding methods of BEEnvelope codec and displays errors if such occures
     */
    public void commandAction(javax.microedition.lcdui.Command c, javax.microedition.lcdui.Displayable s) {
        if(c == this.executeButton) {
            this.statusDisp.setString("Test Started, please wait...");
            
            TestEnvelopes envs =  new TestEnvelopes();
            
            if(this.doTest(envs.getSimpleEnvelopeWithoutRO(), "Envelope without ReceivingObject") &&
                    this.doTest(envs.getSimpleEnvelopeWithRO(), "Envelope with ReceivingObject") &&
                    this.doTest(envs.getStampedEnvelope(), "Envelope with stamps") &&
                    this.doTest(envs.getPropEnvelope(), "Envelope with user defined parameters") &&
                    this.doTest(envs.getComplexEnvelope(), "Comples envelope") &&
                    this.doTest(envs.getLongEnvelope(), "Jumbo envelope")
                    ){
                this.statusDisp.setString(this.statusDisp.getString()+"\nAll tests succeeded");
            } else {
                this.statusDisp.setString(this.statusDisp.getString()+"\nErrors occured in tests");
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
    
    
  /*
   *    Do the encoding and decoding.
   */
    private boolean doTest(Envelope e, String testName){
        EnvelopeEncoder enc = new EnvelopeEncoder();
        ByteArray ba = null;
        try {
            ba = enc.encode(e);
        } catch (Exception ex) {
            this.statusDisp.setString(this.statusDisp.getString()+"\n Error while encoding in test: "+testName+": "+ex.getMessage());
            return false;
        }
        EnvelopeDecoder dec = new EnvelopeDecoder();
        Envelope decoded = null;
        
        byte[] temp = ba.get();
        try {
            decoded = dec.getEnvelope(temp);
        } catch (Exception ex) {
            this.statusDisp.setString(this.statusDisp.getString()+"\n Error while decoding in test: "+testName+": "+ex.getMessage());
            return false;
        }
        return true;
    }
}
