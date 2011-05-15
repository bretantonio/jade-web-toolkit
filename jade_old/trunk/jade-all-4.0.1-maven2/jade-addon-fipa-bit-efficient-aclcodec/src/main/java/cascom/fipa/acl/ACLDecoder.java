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

import java.io.InputStream;
//import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Date;
import jade.lang.acl.*;
import jade.core.AID;


/**
 * Decoder for bit-efficient ACL messages. Decodes
 * bit-efficient ACL messages to the JADE ACLMessage -objects.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class ACLDecoder implements ACLConstants {
    /**
     * as Conversion between communicative acts (legacy <->
     * bit-efficient)
     */
    private static ACLPerformatives as;
    
    /** ct Codetable */
    private DecoderCodetable ct;
    
    /** bn Bit-efficient Number */
    private BinNumber bn = new BinNumber();
    
    /** size Size of the codetable */
    private int size;
    
    /** ba Buffer for parsing tokens */
    private ByteArray ba = new ByteArray();
    
    /** bb Buffer for parsing numbers */
    private ByteArray bb = new ByteArray(32);
    
    /** m ACLMessage to which the parsed message is stored */
    private ACLMessage m;
    
    /** coding Coding scheme (with or without codetables) */
    private int coding;
    
    /** ex Expression parser */
    private ExprParser ex = new ExprParser();
    
    private byte [] blen = new byte[4];
    
    private int current;
    
    //private Object currentCompressor;
    
    /**
     * Initialize the ACLDecoder. If this constructor is used,
     * all messages are decoded without codetables.
     */
    public ACLDecoder() {
        coding = ACL_BITEFFICIENT;
        initialize(8);
    }
    /**
     * Initialize the ACLDecoder with a given codetable size
     *
     * @param sz The size of the codetable (in bits)
     */
    public ACLDecoder(int sz) {
        coding = ACL_BITEFFICIENT_CODETABLE;
        initialize(sz);
    }
    /**
     * Initialize the ACLDecoder with a given codetable
     */
    public ACLDecoder(DecoderCodetable ct) {
        coding = ACL_BITEFFICIENT_CODETABLE;
        initialize(ct.getSize());
        this.ct = ct;
    }
    
    public void initialize(int sz) {
        if(coding==ACL_BITEFFICIENT){
            ct = null;
        } else {
            ct = new DecoderCodetable(sz);
        }
        size = sz;
        m = new ACLMessage(0);
        as = new ACLPerformatives();
    }
    
    public DecoderCodetable getCodeTable() {
        return ct;
    }
    /**
     * Parses an ACL message from byte array
     * @return The ACL message read.
     */
    public ACLMessage readMsg(byte [] inb) throws Exception {
        
        m = new ACLMessage(ACLMessage.INFORM);
        
        //m.reset(); this would be a bug
        
        current = 0;
        if (getCoding(getByte(inb)) < 0)
            throw new ACLCodec.CodecException("Unsupported coding", null);
        if (getVersion(getByte(inb)) < 0)
            throw new ACLCodec.CodecException("Unsupported version", null);
        if (getType(getByte(inb)) < 0)
            throw new ACLCodec.CodecException("Unsupported type", null);
        while (getMsgParam(inb) != -1);
        return m;
    }
    /**
     * Check the first byte of the message.
     * @return 0 if the first byte is supported message type, -1 otherwise.
     */
    private int getCoding(byte b) {
        return (b < ACL_BITEFFICIENT ||
                b > ACL_BITEFFICIENT_NO_CODETABLE) ? -1 : 0;
    }
    /**
     * Check the version number of coding scheme. This implementation
     * supports only version 1.0
     */
    private int getVersion(byte b) {
        return (b != ACL_VERSION) ? -1: 0;
    }
    /**
     * Handle message type (communicative act)
     */
    private int getType(byte b) throws ACLCodec.CodecException {
        if (b == -1) return -1;
        if (b != ACL_NEW_MSGTYPE_FOLLOWS) {
            
            
            
            m.setPerformative(as.getCA(b));
            
        } else {
            throw new ACLCodec.CodecException("Can not handle user defined performatives", null);
        }
        return 0;
    }
    
    private int getMsgParam(byte [] inb) throws Exception {
        byte b = getByte(inb);
        switch(b) {
            case ACL_END_OF_MSG:
                //System.out.println("ACL_END_OF_MSG");
                return -1;
            case ACL_MSG_PARAM_SENDER:
                //System.out.println("ACL_MSG_PARAM_SENDER");
                m.setSender(getAID(inb));
                return 0;
            case ACL_MSG_PARAM_RECEIVER:
                //System.out.println("ACL_MSG_PARAM_RECEIVER");
                getReceivers(inb);
                return 0;
            case ACL_MSG_PARAM_CONTENT:
                //System.out.println("ACL_MSG_PARAM_CONTENT");
                getContent(m, inb);
                return 0;
            case ACL_MSG_PARAM_REPLY_WITH:
                //System.out.println("ACL_MSG_PARAM_REPLY_WITH");
                m.setReplyWith(getParam(inb));
                return 0;
            case ACL_MSG_PARAM_REPLY_BY:
                //System.out.println("ACL_MSG_PARAM_REPLY_BY");
                m.setReplyByDate(getDate(inb));
                return 0;
            case ACL_MSG_PARAM_IN_REPLY_TO:
                //System.out.println("ACL_MSG_PARAM_IN_REPLY_TO");
                m.setInReplyTo(getParam(inb));
                return 0;
            case ACL_MSG_PARAM_REPLY_TO:
                //System.out.println("ACL_MSG_PARAM_REPLY_TO");
                getRepliesTo(inb);
                return 0;
            case ACL_MSG_PARAM_LANGUAGE:
                //System.out.println("ACL_MSG_PARAM_LANGUAGE");
                m.setLanguage(getParam(inb));
                return 0;
            case ACL_MSG_PARAM_ONTOLOGY:
                //System.out.println("ACL_MSG_PARAM_ONTOLOGY");
                m.setOntology(getParam(inb));
                return 0;
            case ACL_MSG_PARAM_PROTOCOL:
                //System.out.println("ACL_MSG_PARAM_PROTOCOL");
                m.setProtocol(getBinWord(inb));
                return 0;
            case ACL_MSG_PARAM_ENCODING:
                //System.out.println("ACL_MSG_PARAM_ENCODING");
                m.setEncoding(getParam(inb));
                return 0;
            case ACL_MSG_PARAM_CONVERSATION_ID:
                //System.out.println("ACL_MSG_PARAM_CONVERSATION_ID");
                m.setConversationId(getParam(inb));
                return 0;
            case ACL_NEW_MSGPARAM_FOLLOWS:
                //System.out.println("ACL_NEW_MSGPARAM_FOLLOWS");
                m.addUserDefinedParameter(getBinWord(inb), getParam(inb));
                return 0;
        }
            throw new ACLCodec.CodecException("Unknown component or something like that: byte value:"+b+", remaining unparsed content:"+(new String(inb)), null);
    }
    
    private String getBinWord(byte[] inb) throws IOException, ACLCodec.CodecException {
        byte type = getByte(inb);
        if(type == ACL_NEW_WORD_FOLLOWS){
            String temp = getRealString(type,inb);
            return temp;
        } else if(type == ACL_CT_WORD_FOLLOWS){
            return  ct.lookupStr(inputCode(inb));
        }
        throw new ACLCodec.CodecException("Error decoding BinWord field: invalid type:"+type,  null);
    }
    
    private void getReceivers(byte [] inb) throws IOException, ACLCodec.CodecException {
        AID _aid;
        while ((_aid=getAID(inb)) != null) {
            m.addReceiver(_aid);
        }
    }
    private void getRepliesTo(byte [] inb) throws IOException, ACLCodec.CodecException {
        AID _aid;
        while((_aid=getAID(inb)) != null) {
            m.addReplyTo(_aid);
        }
    }
    private AID getAID(byte [] inb) throws IOException, ACLCodec.CodecException {
        byte b = getByte(inb);
        return (getAID(b, inb));
    }
    private AID getAID(byte t, byte [] inb) throws IOException, ACLCodec.CodecException {
        AID _aid = new AID();
        byte b;
        
        if (t != ACL_AID_FOLLOWS) {
            if (t == ACL_END_OF_COLLECTION) return null;
            throw new ACLCodec.CodecException("not an agent-identifier", null);
        }
                /*
                 * Mandatory part of AID
                 */
        _aid.setName(getString(inb));
                /*
                 * Optional part of AID
                 */
        while ((b=getByte(inb))!=ACL_END_OF_COLLECTION) {
            switch(b) {
                case ACL_AID_ADDRESSES:
                    while((b=getByte(inb))!=ACL_END_OF_COLLECTION)
                        
                        
                        _aid.addAddresses(getRealString(b, inb));
                    
                    break;
                case ACL_AID_RESOLVERS:
                    while((b=getByte(inb))!=ACL_END_OF_COLLECTION)
                        _aid.addResolvers(getAID(b, inb));
                    
                    break;
                case ACL_AID_USERDEFINED:
                    String key = getString(inb);
                    String value = getParam(inb); // BinExpression
                    _aid.addUserDefinedSlot(key,value);
                    
                    break;
                default:
                    throw new ACLCodec.CodecException("Unexpected stuff in agent-identifier", null);
            }
        }
        return _aid;
    }
    private byte getByte(byte [] inb) throws IOException {
        return (byte)inb[current++];
    }
    byte _b[] = new byte[3];
    
    private int inputCode(byte [] inb) throws IOException {
        int n;
        if (size > 8) {
            _b[0] = getByte(inb);
            _b[1] = getByte(inb);
            n = (int)((_b[1]&0xff)+((_b[0]&0xff)<<8));
        } else {
            byte b0 = (byte)getByte(inb);
            n = (int)(b0&0xff);
        }
        return n;
    }
    private String getParam(byte [] inb) throws IOException {
        return ex.toText(inb);
    }
    
    private Date getDate(byte [] inb) throws Exception {
        byte type = getByte(inb);
        ba.reset();
        for (int i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte(inb));
        String s = new BinDate().fromBin(ba.get());
        
        if (type == ACL_ABS_DATET_FOLLOWS || type == ACL_DATET_POS_FOLLOWS
                || type == ACL_DATET_NEG_FOLLOWS){
            s += (char)getByte(inb);
        }
        return (ISO8601.toDate(s));
    }
    
    
    private void getContent(ACLMessage m, byte [] inb) throws IOException {
        byte type = getByte(inb);
        int len = 0;
        byte [] __b = null;
                        /*
                         * ByteLengthEncoded content
                         */
        
        
        switch(type) {
            case ACL_NEW_STRING_FOLLOWS:
                //System.out.println("ACL_NEW_STRING_FOLLOWS");
                m.setContent(getRealString(type, inb));
                return;
            case ACL_CT_STRING_FOLLOWS:
                //System.out.println("ACL_CT_STRING_FOLLOWS");
                m.setContent(ct.lookupStr(inputCode(inb)));
                return;
            case ACL_CT_BLE_STR_FOLLOWS:
                //System.out.println("ACL_CT_BLE_STR_FOLLOWS");
                __b = ct.lookupBytes(inputCode(inb));
                m.setByteSequenceContent(__b);
                return;
            case ACL_NEW_BLE_STR8_FOLLOWS:
                //System.out.println("ACL_NEW_BLE_STR8_FOLLOWS");
                len = (int) getByte(inb) & 0xff;
                break;
            case ACL_NEW_BLE_STR16_FOLLOWS:
                //System.out.println("ACL_NEW_BLE_STR16_FOLLOWS");
                blen[0] = getByte(inb);
                blen[1] = getByte(inb);
                len = (int)((blen[1]&0xff)+((blen[0]&0xff)<<8));
                break;
            case ACL_NEW_BLE_STR32_FOLLOWS:
                //System.out.println("ACL_NEW_BLE_STR32_FOLLOWS");
                arrayCopy(inb, blen, 4);
                len = (int)((blen[3]&0xff) +
                        ((blen[2]&0xff)<<8)+
                        ((blen[1]&0xff)<<16) +
                        ((blen[0]&0xff)<<24));
                break;
                
            default:
                throw new IOException("Unrecognized content encoding type:"+type);
        }
        
        /*
        int i=-2;        
        // Is the content compressed, does the content start with ContentCompressor.contentID?        
        for(i = 0; i < ContentCompressor.contentID.length() && i+current < inb.length;i++){
            if(inb[current+i] != ContentCompressor.contentID.charAt(i)){
                i = -2;
                break;
            }
        }
        
        
        if(i == ContentCompressor.contentID.length()){
            //System.err.println("len"+len+" i"+i+" current"+current+"length "+inb.length+" tobeRead:"+(len-i-1));            
            // extract compression method name
            StringBuffer sb = new StringBuffer(10);
            int j=current+i;
            while(j < inb.length){
                if(inb[j] != ContentCompressor.nameDelimiter) {
                    sb.append(inb[j]);
                } else {
                    break;
                }
                j++;
            }
            if(j >= inb.length){
                i = -2;
            } else {
                String codecName = sb.toString().trim();
                System.err.println("Codec name is:"+codecName);
                if(BitEffACLCodec.compressors.get(codecName) == null){
                    System.err.println("Compression not supported:"+codecName);
                    i = -2;
                } else {
                    current+=j;
                    __b = new byte[len-j-i-1];
                    //__b = new byte[len-i-1];
                    if(inb[current] == ContentCompressor.bSeqFlag){
                        current++;
                        arrayCopy(inb, __b, __b.length);
                        try {
                            m.setByteSequenceContent( ((ContentCompressor)this.currentCompressor).uncompress(__b));
                        } catch (Exception e){
                            i = -2;
                        }
                    } else if(inb[current] == ContentCompressor.cFlag){
                        current++;
                        arrayCopy(inb, __b, __b.length);
                        try {
                            String temp = new String(((ContentCompressor)this.currentCompressor).uncompress(__b));
                            m.setContent(temp);
                            //System.err.println("Unc ok string, length:"+temp.length()+":"+temp);
                        } catch (Exception e){
                            i = -2;
                        }
                    } else {
                        System.err.println("Error: SeqFlag missing");
                    }
                    
                    i = 1;
                }
            }    //System.err.println("OK!");
        } else {
            i = -2;
        }
        
        if(i==-2){
         */
        
              
       __b = new byte[len];
       arrayCopy(inb, __b, len);
       m.setByteSequenceContent(__b);
        
        
        if (coding == ACL_BITEFFICIENT_CODETABLE) {
            ct.insert(__b);
        }
        
    }
    
    
    private String getString(byte [] inb) throws IOException {
        byte type = getByte(inb);
        return getRealString(type, inb);
    }
    
    
    
    
    private String getRealString(byte type, byte [] inb) throws IOException {
        byte b, t;
        byte [] __b;
        ba.reset();
        String s = null;
        int i = 0, len = 0;
        switch(type) {
            case ACL_NEW_WORD_FOLLOWS: case ACL_NEW_STRING_FOLLOWS:
                /* New word (or string) */
                
                // trick: avoid growing ByteArray many times when long ACL message content
                // this makes the execution much faster
                
                //System.out.println("ACL_NEW_STRING_FOLLOWS");
                
                ba.growToSize(inb.length);
                
                b=getByte(inb);
                while(b != ACL_END_OF_PARAM){
                    //System.out.print(Integer.toHexString((new Integer(b)).intValue())+" ");
                    ba.add(b);
                    b = getByte(inb);
                }
                //System.out.println("====End content====");
                
                
                // trim() removed
                s = new String(ba.get(),0,ba.length());
                if (coding == ACL_BITEFFICIENT_CODETABLE) {
                    ct.insert(s);
                }
                break;
            case ACL_CT_WORD_FOLLOWS: case ACL_CT_STRING_FOLLOWS:
                //System.out.println("ACL_CT_WORD_FOLLOWS");
                
                /* Word or String from codetable */
                s = ct.lookupStr(inputCode(inb));
                break;
            case ACL_CT_BLE_STR_FOLLOWS:
                //System.out.println("ACL_CT_BLE_STR_FOLLOWS");
                /* BLE from codetable */
                        /*
                         * Note that we are not using byte[]'s here;
                         * only when we are dealing with the :content slot
                         */
                s = ct.lookupStr(inputCode(inb));
                break;
            case ACL_ABS_DATE_FOLLOWS: case ACL_ABS_DATET_FOLLOWS:
                //System.out.println("ACL_ABS_DATE_FOLLOWS");
                /* DateTimeToken */
                for (i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte(inb));
                s = new BinDate().fromBin(ba.get());
                if ((type & 0x01) != 0x00) s += (char)getByte(inb);
                break;
            case ACL_DECNUM_FOLLOWS: case ACL_HEXNUM_FOLLOWS:
                //System.out.println("ACL_DECNUM_FOLLOWS");
                
                /* Number token */
                bb.reset();
                while(((b=getByte(inb)) & 0x0f) != 0x00) bb.add(b);
                if (b != 0x00) bb.add(b);
                s = bn.fromBin(bb.get());
                break;
            case ACL_NEW_BLE_STR8_FOLLOWS:
                //System.out.println("ACL_NEW_BLE_STR8_FOLLOWS");
                        /*
                         * Byte lenght encoded w/ 8 bit lenght
                         */
                len = getByte(inb);
                __b = new byte[len+3];
                arrayCopy(inb, __b, len);
                        /*
                         * Note that we are not using byte[]'s here;
                         * only when we are dealing with the :content slot
                         */
                s = "#" + len + "\"" + new String(__b, 0, len);
                if (coding == ACL_BITEFFICIENT_CODETABLE) {
                    ct.insert(s);
                }
                break;
            case ACL_NEW_BLE_STR16_FOLLOWS:
                //System.out.println("ACL_NEW_BLE_STR16_FOLLOWS");
                        /*
                         * Byte lenght encoded w/ 16 bit lenght
                         */
                blen[0] = getByte(inb);
                blen[1] = getByte(inb);
                len = (int)((blen[1]&0xff)+((blen[0]&0xff)<<8));
                __b = new byte[len+3];
                arrayCopy(inb, __b, len);
                        /*
                         * Note that we are not using byte[]'s here;
                         * only when we are dealing with the :content slot
                         */
                s = "#"+len+"\""+new String(__b, 0, len);
                if (coding == ACL_BITEFFICIENT_CODETABLE) {
                    ct.insert(s);
                }
                break;
            case ACL_NEW_BLE_STR32_FOLLOWS:
                //System.out.println("ACL_NEW_BLE_STR32_FOLLOWS");
                
                        /*
                         * Byte lenght encoded w/ 32 bit lenght
                         */
                arrayCopy(inb, blen, 4);
                len = (int)((blen[3]&0xff) + ((blen[2]&0xff)<<8)+
                        ((blen[1]&0xff)<<16) + ((blen[0]&0xff)<<24));
                __b = new byte[len+3];
                arrayCopy(inb, __b, len);
                        /*
                         * Note that we are not using byte[]'s here;
                         * only when we are dealing with the :content slot
                         */
                s = "#"+len+"\""+new String(__b, 0, len);
                if (coding == ACL_BITEFFICIENT_CODETABLE) {
                    ct.insert(s);
                }
                break;
            default:
                throw new IOException("Unknown field: " +
                        new Integer(0).toHexString(type));
        }
        return (s);
    }
    
    private void arrayCopy(byte [] src, byte [] dst, int len) {
        System.arraycopy(src, current, dst, 0, len);
        current += len;
    }
    
    private class ExprParser {
        int level;
        String s;
        ByteArray ba;
        public ExprParser() {
            ba = new ByteArray(512);
        }
        public String toText(byte [] inb) throws IOException {
            ba.reset();
            level = 0;
            byte b, x;
            
            boolean beginOfParameter = true;
		boolean firstChar = true;
            
            while ((b = getByte(inb))!=-1) {
		    if(firstChar && b == ACL_END_OF_PARAM){
		    	 return "";
		    }
		    firstChar = false;

			
                if (b >= 0x40 && b < 0x60) {
                    /* Level UP */
                    ba.add((byte)')');
                    ba.add((byte)' ');
                    if (--level == 0)
                        return new String(ba.get(),0,ba.length()).trim();
                    b &= ~0x40; /* Clear the level bits */
                } else if (b >= 0x60 && b <0x80) {
                    /* Level Down */
                    ba.add((byte)' ');
                    ba.add((byte)'(');
                    ++level;
                    b &= ~0x60; /* Clear the level bits */
                }
                if (b != 0) {
                    if (!beginOfParameter)
                        ba.add((byte)' ');
                    else
                        beginOfParameter = false;
                    s = getRealString(b, inb);
                    ba.add(s.getBytes(),s.length());
                    if (level == 0)
                        return new String(ba.get(),0,ba.length()).trim();
                }
            }
            return new String(ba.get(),0,ba.length()).trim();
        }
    }
}
