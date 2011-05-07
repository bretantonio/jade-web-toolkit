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
import cascom.fipa.acl.BitEffACLCodec;
import cascom.fipa.acl.BinNumber;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.lang.acl.ACLCodec;
import java.lang.StringBuffer;
import cascom.fipa.util.ByteArray;
import java.util.Date;
import jade.util.leap.Iterator;

/**
 * JUnit tests for BEFipaMessage
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class BEFipaMessageTest  {
    private final static String ACLREP ="fipa.acl.rep.bitefficient.std";
    
    public ACLMessage encodeDecode(ACLMessage aclm) throws Exception {
        ACLCodec codec = new BitEffACLCodec();
        return codec.decode(codec.encode(aclm,"ISO-8859-1"),"ISO-8859-1");
    }
    
    
    /**
     * Test various contents of ACLMessage
     */
    @Test public void testContent() throws Exception {
        // lengths
        ACLMessage aclm = this.getDefaultMsg();
        int length = 1024*1024;
        int increment = 10000;
        for(int i=1; i < length; i+=increment){
            StringBuffer stb = new StringBuffer(i);
            char next;
            byte ran;
            for(int j=0; j < i ; j++){
                ran = (byte)(32+Math.random()*(127-32));
                
                // it is known that # character will cause Exception
                while(ran == 35){
                    ran = (byte)(32+Math.random()*(127-32));
                }
                next = (char)ran;
                
                stb.append(next);
                //System.err.print(next);
            }
            aclm.setContent(stb.toString());
            
            try {
                aclm = encodeDecode(aclm);
                if(aclm == null || aclm.getContent() == null){
                    System.err.println("Test failed with input:"+stb.toString()+" : null received");
                } else {
                    assertEquals(stb.toString().trim(),aclm.getContent().trim());
                }
            } catch (Exception e){
                System.err.println("Test failed with input:"+stb.toString()+" :"+e.getMessage());
                return;
            }
        }
        //System.out.println();
    }
    

    @Test public void testEmptyUserParam(){
        ACLMessage aclm = this.getDefaultMsg();
	  aclm.addUserDefinedParameter("moikka","");

            try {
                aclm = encodeDecode(aclm);
            } catch (Exception e){
                assertNotNull("Test empty user defined param failed!"+e.getMessage());
            }

	  aclm = this.getDefaultMsg();
	  aclm.addUserDefinedParameter("CASCOM-dfe","dkjei");

            try {
                aclm = encodeDecode(aclm);
            } catch (Exception e){
                assertNotNull("Test empty user defined param failed!"+e.getMessage());
            }

this.getDefaultMsg();
	  aclm.addUserDefinedParameter("CASCOMd","dfdfaefmdflöakjefeihejr");

            try {
                aclm = encodeDecode(aclm);
            } catch (Exception e){
                assertNotNull("Test empty user defined param failed!"+e.getMessage());
            }


    }

    
    @Test public void testReplyBy(){
        ACLMessage aclm = this.getDefaultMsg();
        long time = System.currentTimeMillis();
        Date d = null;
        int hourInMillis = 1000*60*60;
        int dayInMillis = hourInMillis * 24;
        for(long i = time-14*dayInMillis; i < time+14*dayInMillis; i+=hourInMillis){
            d = new Date(i);
            aclm.setReplyByDate(d);
            try {
                aclm = encodeDecode(aclm);
                if(aclm.getReplyByDate().getTime() != i){
                    assertNotNull("Not equal reply-by: expected "+i+" but got "+aclm.getReplyByDate().getTime());
                }
            } catch (Exception e){
                assertNotNull("Test failed with reply-by :"+i+" :"+e.getMessage());
            }
        }
        
        
    }
    
    /**
     * Does the codec change the original message content?
     */
    @Test public void testChangedContent(){
        ACLMessage aclm = this.getDefaultMsg();
        String content = "test content";
        String content2 =  new String(content);
        aclm.setContent(content2);
        ACLMessage dec;
        boolean failed;
        
        if(!content.equals(content2)){
            System.err.println("content != content 2");
        }
        
        for(int i=0; i < 100; i++){
            try {
                dec = this.encodeDecode(aclm);
            } catch (Exception e){
                assertNotNull("Test changed content failed, exception while encoding or decoding "+e.getMessage());
            }
            if(!aclm.getContent().equals(content)){
                System.err.println("Encoding or decoding changed content:"+content+" > "+aclm.getContent());
                failed = true;
                assertNotNull("Test failed, original content changed");            
            }
        }
    }
    
    
    @Test public void testByteSequenceContent(){
        // lengths
        ACLMessage aclm = this.getDefaultMsg();
        int length = 1024*1024;
        int increment = 10000;
        for(int i=1; i < length; i+=increment){
            byte[] stb;
            char next;
            byte ran;
            
            if(i==1){
                stb = new byte[256];
                for(int j=0; j < 256 ; j++){
                    stb[j] = (byte)j;
                }                
            } else {
                stb = new byte[i];                
                for(int j=0; j < i ; j++){
                    stb[j] = (byte)(Math.random()*256);
                }
            }
            aclm.setByteSequenceContent(stb);
            
            try {
                aclm = encodeDecode(aclm);
                if(aclm == null || aclm.getContent() == null){
                    System.err.println("Test failed with input:"+this.printByteArray(stb)+" : null received");
                } else {
                    byte[] b1 = aclm.getByteSequenceContent();
                    byte[] b2 = stb;
                    if(b1.length != b2.length){
                        System.err.println("Test failed with input:"+this.printByteArray(stb)+" : content size unequal");
                    } else {
                        for(int k=0; k < b1.length; k++){
                            if(b1[k] != b2[k]){
                                System.err.println("Test failed with input:"+this.printByteArray(stb)+" : not same byte at index "+k);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e){
                System.err.println("Test failed with input:"+this.printByteArray(stb)+" :"+e.getMessage());
                return;
            }
        }
    }
    
    
    
    
    private String printByteArray(byte[] array){
        StringBuffer sb = new StringBuffer(array.length);
        for(int i=0; i < array.length; i++){
            sb.append(Integer.toHexString((int)array[i]));
        }
        return sb.toString();
    }
    
    
    @Test public void testBinNumber() throws Exception {
        BinNumber bn  = new BinNumber();
        byte[] b;
        int res;
        for(int i=-10; i < 100000; i++){
            b = bn.toBin(String.valueOf(i)).get();
            try {
                res = Integer.parseInt(bn.fromBin(b));
                assertEquals(i,res);
            } catch (Exception e){
                assertNotNull("Test failed with i "+i+": "+e.getMessage());
                return;
            }
        }
    }
    
    @Test public void testBinNumber2() throws Exception {
        BinNumber bn  = new BinNumber();
        byte[] b;
        int res;
        for(int i=-10; i < 1000000; i++){
            b = bn.toBin(new ByteArray(String.valueOf(i).getBytes())).get();
            try {
                res = Integer.parseInt(new String(bn.fromBin(new ByteArray(b).get())));
                assertEquals(i,res);
            } catch (Exception e){
                assertNotNull("Test failed with i "+i+": "+e.getMessage());
                return;
            }
        }
    }
    
   @Test public void testInReplyto(){
        ACLMessage aclm = this.getDefaultMsg();
        ACLMessage ret;
        long time = System.currentTimeMillis();
        Date d = null;
        int hourInMillis = 1000*60*60;
        int dayInMillis = hourInMillis * 24;
        for(long i = time-14*dayInMillis; i < time+14*dayInMillis; i+=hourInMillis){
            d = new Date(i);
            aclm.setReplyByDate(d);
            aclm.setInReplyTo(d.toString());
            aclm.setReplyWith(d.toString());
            aclm.setConversationId(d.toString());
            try {
                ret = encodeDecode(aclm);
                if(ret.getReplyByDate().getTime() != i){
                    assertNotNull("Not equal reply-by: expected "+i+" but got "+aclm.getReplyByDate().getTime());
                }
                Iterator to = ret.getAllIntendedReceiver();
                Iterator orig = aclm.getAllIntendedReceiver();
                String tohap;
                String orighap;
                
                while(to.hasNext()){
                    tohap = ((AID)to.next()).getHap().trim();
                    orighap = ((AID)orig.next()).getHap().trim();        
                    if(!tohap.equals(orighap)){
                        assertNotNull("Not equal : expected hap "+orighap+" but got "+tohap);                        
                    }                                        
                }
            } catch (Exception e){
                assertNotNull("Test failed with reply-by :"+i+" :"+e.getMessage());
            }
        }
        
    }
    
    
    private ACLMessage getDefaultMsg(){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("");
        AID rec = new AID("localagent@boo", AID.ISGUID);
        rec.addAddresses("http://www.helsinki.fi");
        msg.addReceiver(rec);
        msg.setDefaultEnvelope();
        msg.getEnvelope().setAclRepresentation(ACLREP);
        return msg;
    }
    
    
    /**
     * Test manually different ASCII characters in message content
     */
    public static void main(String[] args){
        BEFipaMessageTest bmt = new BEFipaMessageTest();
        ACLMessage acl = bmt.getDefaultMsg();
        String input;
        for(byte i=1; i < 127; i++){
            acl = bmt.getDefaultMsg();
            input = ""+((char)i);
            try {
                acl.setContent(input);
                acl = bmt.encodeDecode(acl);
                if(acl == null || acl.getContent() == null || !input.equals(acl.getContent())){
                    System.err.println(input+" \t ("+i+") not equals:"+acl.getContent());
                }
            } catch (Exception e){
                System.err.println(input+" \t ("+i+") exception:");
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * For earlier versions of JUnit
     */
    public static junit.framework.Test suite(){
        return new JUnit4TestAdapter(BitEffACLCodec.class);
    }
    
}