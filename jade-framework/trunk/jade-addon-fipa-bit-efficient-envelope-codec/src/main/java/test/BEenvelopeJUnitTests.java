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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junit.framework.JUnit4TestAdapter;
import cascom.fipa.acl.BitEffACLCodec;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.lang.acl.ACLCodec;
import java.lang.StringBuffer;
import cascom.fipa.envelope.*;
import jade.domain.FIPAAgentManagement.*;
import java.util.Date;
import java.util.Calendar;
import cascom.fipa.acl.BEParseException;

/**
 * JUnit tests for BEFipaEnvelope.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class BEenvelopeJUnitTests  {
    private final static String ACLREP ="fipa.acl.rep.bitefficient.std";
    
    @Test public void defaultEnvelope(){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("this is very nice testmessage");
        AID rec = new AID("localagent@boo", AID.ISGUID);
        rec.addAddresses("http://www.helsinki.fi");
        msg.addReceiver(rec);
        msg.setDefaultEnvelope();
        msg.getEnvelope().setAclRepresentation("fipa.acl.bitefficient.std");
        try {
            Envelope env = this.encodeDecode(msg.getEnvelope());
        } catch (Exception e){
            assertNotNull("Error:"+e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test envelopes with different field values
     */
    @Test public void testEnvelope() throws Exception {
        // lengths
        int length = 10000;
        int increment = 10;
        Envelope env,env2;
        for(int i=2; i < length; i+=increment){
            StringBuffer stb = new StringBuffer(i);
            char next;
            byte ran;
            for(int j=0; j < i ; j++){
                ran = (byte)(32+Math.random()*(127-32));
                next = (char)ran;
                stb.append(next);
            }
            
            env = new Envelope();
            AID aid = new AID(stb.toString()+"@dfefjkeewpd",AID.ISGUID);
            aid.addAddresses("http://"+stb.toString());
            env.addIntendedReceiver(aid);
            
            
            ReceivedObject ro = new ReceivedObject();
            ro.setBy(stb.toString());
            ro.setFrom("http://"+stb.toString());
            ro.setDate(Calendar.getInstance().getTime());
            ro.setId(stb.toString());
            ro.setVia("http://"+stb.toString());
            env.setReceived(ro);
            
            env.setAclRepresentation(ACLREP);
            env.setComments(stb.toString());
            
            try {
                env2 = encodeDecode(env);
                if(env == null){
                    System.err.println("Test failed : env null "+stb.toString());
                    assertEquals("a","b");
                } else {
                    assertEquals(env.getAclRepresentation(),env2.getAclRepresentation());
                    assertEquals(env.getComments().trim(),env2.getComments().trim());
                    assertEquals(env.getFrom(),env2.getFrom());
                    assertEquals(env.getPayloadEncoding(),env2.getPayloadEncoding());
                    assertEquals(env.getPayloadLength(),env2.getPayloadLength());
                }
            } catch (Exception e){
                e.printStackTrace();
                System.err.println("Test failed :"+i+" :"+e.getMessage());
                assertEquals("a","b");
            }
        }
        System.out.println();
    }
    
    
    /**
     * Tests different payload sizes
     */
    @Test public void payloadSize(){
        Envelope env, env2;
        for(long i=0; i < 100000; i++){
            env = new Envelope();
            AID aid = new AID("vdv@dfefjkeewpd",AID.ISGUID);
            aid.addAddresses("http://helsinki.fi");
            env.addIntendedReceiver(aid);
            
            env.setAclRepresentation(ACLREP);
            env.setComments("Test");
            env.setPayloadLength(i);
            try {
                env2 = encodeDecode(env);
                assertEquals(env.getPayloadLength(),env2.getPayloadLength());
            } catch (Exception e){
                System.err.println("Test failed with payload length "+i+" :"+e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }
    
    
    /**
     * Encodes and decodes the given envelope
     */
    public Envelope encodeDecode(Envelope aclm) throws Exception {
        EnvelopeCodec codec = new BitEfficientEnvelopeCodec();
        return codec.decode(codec.encode(aclm));
    }
    
    
    /**
     * For earlier versions of JUnit
     */
    public static junit.framework.Test suite(){
        return new JUnit4TestAdapter(BitEffACLCodec.class);
    }
    
}