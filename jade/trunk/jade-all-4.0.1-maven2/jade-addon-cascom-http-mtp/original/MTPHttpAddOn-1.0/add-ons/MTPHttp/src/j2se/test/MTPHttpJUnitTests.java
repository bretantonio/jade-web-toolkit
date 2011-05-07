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

package test;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junit.framework.JUnit4TestAdapter;

import cascom.fipa.util.ByteArray;
import cascom.fipa.acl.ACLConstants;
import cascom.fipa.acl.BinNumber;
import cascom.fipa.acl.BinDate;
import cascom.fipa.acl.BEParseException;


import java.io.*;
import java.util.*;
import java.util.Date;
import java.util.Calendar;
import jade.util.leap.List;
import jade.util.leap.Iterator;
import jade.util.leap.Properties;

import jade.lang.acl.*;
import jade.core.AID;
import jade.lang.acl.ISO8601;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;
import test.*;
import cascom.fipa.envelope.EnvelopeEncoder;
import cascom.fipa.envelope.EnvelopeDecoder;
import cascom.fipa.envelope.EnvelopeCodec;
import jade.mtp.http2.*;
import jade.mtp.*;
import cascom.fipa.acl.*;
import java.util.*;
import cascom.net.*;
import jade.core.Profile;
import jade.core.ProfileImpl;

/**
 * JUnit tests for MTP Http. Mainly for HTTPIO.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class MTPHttpJUnitTests implements InChannel.Dispatcher {
    private EnvelopeCodec envcodec = new cascom.fipa.envelope.BitEfficientEnvelopeCodec();
    private ACLCodec aclcodec = new cascom.fipa.acl.BitEffACLCodec();
    
    private int counter = 0;
    
    private LinkedList sentMsg =  new LinkedList();
    private LinkedList receivedMsg =  new LinkedList();
    
    
    public void dispatchMessage(Envelope env, byte[] payload){
        ACLMessage aclm32 = null;
        
        try {
            aclm32 = this.decodeAcl(payload);
            aclm32.setEnvelope(env);
            
            synchronized(this.receivedMsg){
                this.receivedMsg.addLast((Object)aclm32);
                this.receivedMsg.notifyAll();
            }
            
        } catch (Exception e){
            assertNotNull("Error: decoding aclmessage after receiving:"+e.getMessage());
        }
    }
    
    /**
     * Test HTTPIO with simple message
     */
    @Test public void testHTTPIORequestSimple() throws Exception {
        FipaHttpObject fho = this.createHttpRequest(this.getDefaultAclMsg());
        this.testEqualHttpObject(fho,this.encodeDecodeHttp(fho));
    }
    
    
    /**
     * Test HTTPIO with different size ACL messages
     * containing random content.
     */
    @Test public void testHTTPIORequestSize() throws Exception {
        for (int i = 1; i < 1000; i+=100){
            ACLMessage acl = this.getDefaultAclMsg();
            this.decodeAcl(this.encodeAcl(acl));
            acl.setContent(this.getRandomString(i));
            FipaHttpObject fho = this.createHttpRequest(acl);
            this.testEqualHttpObject(fho,this.encodeDecodeHttp(fho));
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Test sending and receiving using the whole MTP
     */
    public void sendReceive() throws  Exception {
        int tests = 100;
        
        MTP mtp = new jade.mtp.http2.MessageTransportProtocol();
        String address = "http://"+InetAddress.getLocalHost()+":10500/acc";
        
        
        for (int i = 1; i <= tests; i+=100){
            ACLMessage acl = this.getDefaultAclMsg();
            acl.setContent(this.getRandomString(i));
            acl.setInReplyTo(this.getRandomString(i));
            acl.setReplyWith(this.getRandomString(i));
            Envelope env = this.setDefaultEnvelope(acl);
            this.sentMsg.addLast((Object)acl);
        }
        
        HTTPAddress hta = new HTTPAddress(address);
        mtp.activate((InChannel.Dispatcher)this,hta,(Profile)new ProfileImpl());
        synchronized(this){
            try {
                wait(2000);
            } catch (Exception e){}
        }
        
        ListIterator lit = this.sentMsg.listIterator();
        ACLMessage tac;
        while(lit.hasNext()){
            tac = (ACLMessage) lit.next();
            try {
                mtp.deliver(address,tac.getEnvelope(),this.encodeAcl(tac));
            } catch (Exception e){
                assertNotNull("Exception while sending message:"+e.getMessage());
            }
        }
        
        
        try {
            synchronized(this.receivedMsg){
                while(this.receivedMsg.size() != tests){
                    this.receivedMsg.wait();
                }
            }
        } catch  (Exception e){
            assertNotNull("Exception while waiting for messages:"+e.getMessage());
        }
        
        
        
        ACLMessage smsg = null;
        ACLMessage rmsg = null;
        int counter = 0;
        
        ListIterator sent = this.sentMsg.listIterator();
        ListIterator received = this.receivedMsg.listIterator();
        while(sent.hasNext() && received.hasNext()){
            smsg = (ACLMessage) sent.next();
            rmsg = (ACLMessage) received.next();
            if(!this.testACLEquals(smsg,rmsg)){
                System.out.println("Order was "+counter);
            }
            counter++;
            
        }
        System.out.println("Total msg was "+counter);
        try {
            mtp.deactivate();
        } catch (Exception e){}
        
    }
    
    private boolean testACLEquals(ACLMessage acl1, ACLMessage acl2){
        String ac1 = acl1.getContent().trim();
        String ac2 = acl2.getContent().trim();
        if(!ac1.equals(ac2)){
            System.out.println("*******************************************************");
            System.out.println("ACL1 Content:"+ac1+"||");
            System.out.println("ACL2 Content:"+ac2+"||");
            System.out.println("*******************************************************");
            assertEquals(acl1.getContent().trim(),acl2.getContent().trim());
            return false;
        }
        assertEquals(acl1.getContent().trim(),acl2.getContent().trim());
        
        if(acl1.getEnvelope()!=null){
            return testEnvelopeEquals(acl1.getEnvelope(), acl2.getEnvelope());
        }
        return true;
    }
    
    private boolean testEnvelopeEquals(Envelope e1, Envelope e2){
        Iterator to = e1.getAllIntendedReceiver();
        Iterator orig = e2.getAllIntendedReceiver();
        String tohap,orighap,name1,name2;
        while(to.hasNext()){
            tohap = ((AID)to.next()).getHap().trim();
            orighap = ((AID)orig.next()).getHap().trim();
            if(!tohap.equals(orighap)){
                assertNotNull("Not equal hap: expected hap "+orighap+" but got "+tohap);
                return false;
            }
        }
        return true;
    }
    
    
    
    private FipaHttpObject encodeDecodeHttp(FipaHttpObject fho) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        HTTPIO.writeHttpRequest((OutputStream)bout,fho,fho.getHostAddr(),false);
        FipaHttpObject fho2 = new FipaHttpObject();
        HTTPIO.parseRequest(fho2,new cascom.fipa.util.BufferedInputStream((InputStream)new ByteArrayInputStream(bout.toByteArray())));
        return fho2;
    }
    
    
    
    private void testEqualHttpObject(FipaHttpObject orig, FipaHttpObject res) throws Exception {
        Enumeration headers = orig.getHeaderNames();
        String name;
        
        while(headers.hasMoreElements()){
            name = (String)headers.nextElement();
            assertEquals(orig.getHttpHeader(name).trim(),res.getHttpHeader(name).trim());
        }
        
        if(orig.getACLMessage() != null || res.getACLMessage() != null){
            byte[] acl1 = orig.getACLMessage();
            byte[] acl2 = res.getACLMessage();
            
            if(acl1 == null){
                System.err.println("ACl1 null");
            }
            
            if(acl2 == null){
                System.err.println("ACl2 null acllength "+acl1.length);
            }
            
            assertEquals(acl1.length,acl2.length);
            for(int i=0; i < acl1.length; i++){
                assertEquals(acl1[i],acl2[i]);
            }
            ACLMessage acle1 = this.decodeAcl(acl1);
            ACLMessage acle2 = this.decodeAcl(acl2);
        }
        
        /*
        if(orig.getEnvelope() != null || res.getEnvelope() != null){
            byte[] acl1 = orig.getEnvelope();
            byte[] acl2 = res.getEnvelope();
            assertEquals(acl1.length,acl2.length);
            for(int i=0; i < acl1.length; i++){
                assertEquals(acl1[i],acl2[i]);
            }
            Envelope env1 = this.decodeEnvelope(acl1);
            Envelope env2 = this.decodeEnvelope(acl2);
        }
        assertEquals(orig.isHttpRequest(),res.isHttpRequest());
        assertEquals(orig.isFipaRequest(),res.isFipaRequest());
        assertEquals(orig.isResponse(),res.isResponse());
        assertEquals(orig.getHostAddr().toString().trim(),res.getHostAddr().toString().trim());
         */
        
    }
    
    
    
    /**
     * Returns random generated String.
     */
    private String getRandomString(int length){
        StringBuffer stb = new StringBuffer(length);
        char next;
        for(int j=0; j < length ; j++){
            next = (char)(32+Math.random()*(127-32));
            while(next == '#'){
                next = (char)(32+Math.random()*(127-32));
            }
            stb.append(next);
        }
        return stb.toString();
    }
    
    
    /**
     * Returns string containing all ASCII characters starting from
     * lowChar and ending to highChar.
     */
    private String getASCIIString(byte lowChar, byte highChar){
        StringBuffer stb = new StringBuffer(highChar - lowChar +2);
        char next;
        for(byte j= lowChar; j <= highChar ; j++){
            next = (char)j;
            stb.append(next);
        }
        return stb.toString();
    }
    
    /**
     * For earlier versions of JUnit
     */
    public static junit.framework.Test suite(){
        return new JUnit4TestAdapter(BitEffACLCodec.class);
    }
    
    
    /**
     * Gets default ACL message
     */
    private ACLMessage getDefaultAclMsg(){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("getDefaultAclMsg()");
        AID rec = new AID("localagent@boo", AID.ISGUID);
        rec.addAddresses("http://www.helsinki.fi");
        msg.addReceiver(rec);
        msg.setDefaultEnvelope();
        msg.getEnvelope().setAclRepresentation("fipa.acl.bitefficient.std");
        return msg;
    }
    
    private byte[] encodeEnvelope(Envelope env) throws Exception {
        try {
            return this.envcodec.encode(env);
        } catch (Exception ee) {
            ee.printStackTrace();
            throw new MTPException("Error while encoding envelope:"+ee.getMessage());
        }
    }
    
    private byte[] encodeAcl(ACLMessage acl) throws Exception {
        try {
            return this.aclcodec.encode(acl,"ISO-8859-1");
        } catch (Exception ee) {
            ee.printStackTrace();
            throw new MTPException("Error while encoding acl:"+ee.getMessage());
        }
    }
    private Envelope decodeEnvelope(byte[] bytes) throws Exception {
        try {
            return this.envcodec.decode(bytes);
        } catch (Exception ee) {
            ee.printStackTrace();
            throw new MTPException("Error while decoding envelope:"+ee.getMessage());
        }
    }
    
    private ACLMessage decodeAcl(byte[] bytes) throws Exception {
        try {
            return this.aclcodec.decode(bytes,"ISO-8859-1");
        } catch (Exception ee) {
            ee.printStackTrace();
            throw new MTPException("Error while decoding acl:"+ee.getMessage());
        }
    }
    
    /**
     *  Gets default FipaHttpObject
     */
    private FipaHttpObject createHttpRequest(ACLMessage acl) throws Exception {
        FipaHttpObject fho = new FipaHttpObject();
        byte[] aclm = this.encodeAcl(acl);
        fho.setHostAddr(new HTTPAddress("http://131.133.24.255"));
        fho.setACLMessage(aclm);
        fho.setACLMsgPresentation(this.aclcodec.getName());
        fho.setIsFipaRequest(true);
        fho.setPayloadEncoding("ISO-8859-1");
        fho.setEnvelopePresentation(this.envcodec.getName());
        fho.setEnvelope(this.encodeEnvelope(this.setDefaultEnvelope(acl)));
        return fho;
    }
    
    
    /**
     * Sets default envelope for given ACL message
     */
    private Envelope setDefaultEnvelope(ACLMessage acl){
        acl.setDefaultEnvelope();
        acl.getEnvelope().setAclRepresentation("fipa.acl.rep.bitefficient.std");
        return acl.getEnvelope();
    }
    
    
    /**
     * Tests between two instances of JADE platforms. Agent-to-Agent tests for
     * terminating JADE. Some messages may disappear when JADE does not send 
     * agent not found message when platform is not yet fully started but communication
     * should work after restarting agent platform.
     */    
    public void testJADETermination(){
        
    }
    
    
    
    
    
    
    
    /**
     * Run some tests without JUnit so that you can see output immediately.
     */
    public static void main(String[] args){
        MTPHttpJUnitTests t = new MTPHttpJUnitTests();
    }
    
}
