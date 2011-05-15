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
package cascom.fipa.acl;

import cascom.fipa.util.ByteArray;
import java.io.*;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
//import jade.core.AID;
import java.util.Hashtable;

/**
 * This class implements the FIPA Bit-efficient codec for ACLMessages.
 * @author           Heikki Helin, Mikko Laukkanen
 */
public class BitEffACLCodec implements ACLCodec {

    public static final String _name = jade.domain.FIPANames.ACLCodec.BITEFFICIENT;
    
    private ACLEncoder e;
    private ACLDecoder d;
    private ByteArray ba;
    private Object lockEncode = new Object();
    private Object lockDecode = new Object();
    
    /**
     * Constructor for the codec.
     */
    public BitEffACLCodec() {
        initialize(0);
    }
    /**
     * Constructor for the codec.
     */
    public BitEffACLCodec(int sz) {
        initialize(sz);
    }
    private void initialize(int sz) {
        e = new ACLEncoder(sz);
        d = new ACLDecoder();
        ba = new ByteArray();
    }
    
    /**
     * @see ACLCodec#decode(byte[] data, String charset)
     */
    public ACLMessage decode(byte[] data, String charset) throws ACLCodec.CodecException {
        try {
            synchronized(this.lockDecode){
                
                return  d.readMsg(data);
                
                       /*
                       //long time = System.currentTimeMillis();
                       ACLMessage ret = d.readMsg(data);
                       //time = System.currentTimeMillis() - time;
                       //Logger.getMyLogger(this.getClass().getName()).log(Logger.INFO,"ACL Decode took "+time);
                       
                       if(ret.getContent() == null || ret.getContent().trim().equals("")){
                           Logger.getMyLogger(this.getClass().getName()).log(Logger.WARNING,"ACLMessage with no content from "+ret.getSender().getName()+" to "+((AID)ret.getAllReceiver().next()).getName());
                       } 
                       return ret;
                        */
                        
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;}
        
        
            /*
                InputStream i = new ByteArrayInputStream(data);
                ACLInputStream ai = new ACLInputStream(i);
             
                try {
                        return (ai.readMsg());
                } catch (IOException e) {
                        throw new ACLCodec.CodecException("IOException:"+e, null);
                } catch (Exception e) {
                        throw new ACLCodec.CodecException("Exception:"+e, null);
                }
             */
    }
    
    public void write(ACLMessage msg) {
        
    }
    
    /**
     * @see ACLCodec#encode(ACLMessage msg, String charset)
     */
    public byte[] encode(ACLMessage msg, String charset) {   
        try {
            synchronized(this.lockEncode){
                
                return e.encode(msg).get();
                
                        /*
                        long time = System.currentTimeMillis();
                        byte[] temp = e.encode(msg).get();
                        time = System.currentTimeMillis() - time;
                Logger.getMyLogger(this.getClass().getName()).log(Logger.INFO,"ACL Encode took "+time);
                         
                         
                        //for(int i=0; i < temp.length; i++){
                        //    System.err.print(Integer.toHexString((int)temp[i]));
                        //}
                         
                        return  temp;
                         */
            }
        } catch (Exception e) {return null;}
    }
    
    
    public ByteArray _encode(ACLMessage msg) {
        try {
            ba = e.encode(msg);
        } catch (Exception e) {}
        return(ba);
    }
    
    /**
     * @return the name of this encoding according to the FIPA
     *         specifications
     */
    public String getName() { return _name; }
    
}