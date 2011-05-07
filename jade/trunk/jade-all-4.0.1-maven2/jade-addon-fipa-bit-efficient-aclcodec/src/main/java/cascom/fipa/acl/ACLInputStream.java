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
import cascom.fipa.util.BufferedInputStream;
import java.io.IOException;
import java.util.Date;
import jade.lang.acl.*;
import jade.core.AID;

/**
 * InputStream that reads fipa-bitefficient-std coded ACL messages from
 * given InputStream.
 *
 * @author Heikki Helin, Mikko Laukkanen 
 */
public class ACLInputStream extends BufferedInputStream
        implements ACLConstants {
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

        /**
	 * Initialize the ACLInputStream. If this constructor is used,
	 * the stream assumes that all messages are coded without 
	 * codetables.
	 *
	 * @param i The InputStream from where the messages are read.
 	 */
        public ACLInputStream(InputStream i) {
                super(i);
                coding = ACL_BITEFFICIENT;
                initialize(8);
        }
        /**
	 * Initialize the ACLInputStream and associated codetable.
	 *
	 * @param i The InputStream from where the messages are read.
	 * @param sz The size of the codetable (in bits)
	 */
        public ACLInputStream(InputStream i, int sz) {
                super(i);
                coding = ACL_BITEFFICIENT_CODETABLE;
                initialize(sz);
        }
        /**
	 * @deprecated Use ACLInputStream (InputStream i, DecoderCodetable ct) 
	 */
        public ACLInputStream (InputStream i, int sz, DecoderCodetable ct) {
                super (i);
                coding = ACL_BITEFFICIENT_CODETABLE;
                size = sz;
                initialize(size);
                this.ct = ct;
        }
        public ACLInputStream (InputStream i, DecoderCodetable ct) {
                super (i);
                coding = ACL_BITEFFICIENT_CODETABLE;
                size = ct.getSize();
                initialize(size);
                this.ct = ct;
        }
        public void initialize(int sz) {
                ct = (coding==ACL_BITEFFICIENT) ? null : new DecoderCodetable(sz);
                size = sz;
                m = new ACLMessage(0);
                as = new ACLPerformatives();
        }

        public DecoderCodetable getCodeTable() {
                return ct;
        }
        
        /**
	 * Reads an ACL message from the input stream.
	 * @return The ACL message read.
	 */
        public ACLMessage readMsg() throws Exception {
                m.reset();
                if (getCoding(getByte()) < 0)
                        throw new ACLCodec.CodecException("Unsupported coding", null);
                if (getVersion(getByte()) < 0)
                        throw new ACLCodec.CodecException("Unsupported version", null);
                if (getType(getByte()) < 0)
                        throw new ACLCodec.CodecException("Unsupported type", null);
                while (getMsgParam() != -1) ;

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



                        m.setPerformative (as.getCA (b));

                } else {
                        throw new ACLCodec.CodecException("Can t handle user defined performatives", null);
                }
                return 0;
        }
        
        private int getMsgParam() throws Exception {
                byte b = getByte();
                switch(b) {
                case ACL_END_OF_MSG:
                        return -1;
                case ACL_MSG_PARAM_SENDER:
                        m.setSender(getAID());
                        return 0;
                case ACL_MSG_PARAM_RECEIVER:
                        getReceivers();
                        return 0;
                case ACL_MSG_PARAM_CONTENT:

                        getContent(m);



                        return 0;
                case ACL_MSG_PARAM_REPLY_WITH:
                        m.setReplyWith(getParam());
                        return 0;
                case ACL_MSG_PARAM_REPLY_BY:

                        m.setReplyByDate(getDate());



                        return 0;
                case ACL_MSG_PARAM_IN_REPLY_TO:
                        m.setInReplyTo(getParam());
                        return 0;
                case ACL_MSG_PARAM_REPLY_TO:
                        getRepliesTo();
                        return 0;
                case ACL_MSG_PARAM_LANGUAGE:
                        m.setLanguage(getParam());
                        return 0;
                case ACL_MSG_PARAM_ONTOLOGY:
                        m.setOntology(getParam());
                        return 0;
                case ACL_MSG_PARAM_PROTOCOL:
                        m.setProtocol(getBinWord());
                        return 0;
                case ACL_MSG_PARAM_ENCODING:
                        m.setEncoding(getParam());

                        return 0;
                case ACL_MSG_PARAM_CONVERSATION_ID:
                        m.setConversationId(getParam());
                        return 0;
                case ACL_NEW_MSGPARAM_FOLLOWS:
                        m.addUserDefinedParameter(getParam(), getBinWord());
                        return 0;
                }
                throw new ACLCodec.CodecException("Unknown component or something like that", null);
        }
 
        private String getBinWord() throws IOException, ACLCodec.CodecException {
           byte type = getByte();
           if(type == ACL_NEW_WORD_FOLLOWS){
               String temp = getRealString(type);
               return temp;
           } else if(type == ACL_CT_WORD_FOLLOWS){
                return  ct.lookupStr(inputCode());
           }
           throw new ACLCodec.CodecException("Error decoding BinWord field: invalid type:"+type,  null);
        }
                        
        private void getReceivers() throws IOException, ACLCodec.CodecException {
                AID _aid;
                while ((_aid=getAID()) != null) {
                        m.addReceiver(_aid);
                }
        }
        
        private void getRepliesTo() throws IOException, ACLCodec.CodecException {
                AID _aid;
                while((_aid=getAID()) != null) {
                        m.addReplyTo(_aid);
                }
        }
        
        private AID getAID() throws IOException, ACLCodec.CodecException {
                byte b = getByte();
                return (getAID(b));
        }
        
        private AID getAID(byte t) throws IOException, ACLCodec.CodecException {
                AID _aid = new AID();
                byte b;

                if (t != ACL_AID_FOLLOWS) {
                        if (t == ACL_END_OF_COLLECTION) return null;
                        throw new ACLCodec.CodecException("not an agent-identifier", null);
                }
                /*
		 * Mandatory part of AID
		 */
                _aid.setName(getString());
                /*
		 * Optional part of AID
		 */
                while ((b=getByte())!=ACL_END_OF_COLLECTION) {
                        switch(b) {
                        case ACL_AID_ADDRESSES:
                                while((b=getByte())!=ACL_END_OF_COLLECTION)





                                        _aid.addAddresses(getRealString(b));

                                break;
                        case ACL_AID_RESOLVERS:
                                while((b=getByte())!=ACL_END_OF_COLLECTION)
                                        _aid.addResolvers(getAID(b));
                                break;
                        case ACL_AID_USERDEFINED:
                                String key = getString();
                                String value = getParam();

                                _aid.addUserDefinedSlot(key,value);

                                break;
                        default:
                                throw new ACLCodec.CodecException("Unexpected stuff in agent-identifier", null);
                        }
                }
                return _aid;
        }
        private byte getByte() throws IOException {
                return (byte)super.read();
        }
        byte _b[] = new byte[3];
        private int inputCode() throws IOException {
                int n;
                if (size > 8) {
                        super.read(_b, 0, 2);
                        n = (int)((_b[1]&0xff)+((_b[0]&0xff)<<8));
                } else {
                        byte b0 = (byte)super.read();
                        n = (int)(b0&0xff);
                }
                return n;
        }
        private String getParam() throws IOException {
                return ex.toText();
        }





        private void getContent(ACLMessage msg) throws IOException {
                byte type = getByte();
                        int len = 0;
                        byte [] __b = null;
                        /*
			 * ByteLengthEncoded content
			 */
                        switch(type) {
                            case ACL_NEW_STRING_FOLLOWS:
                                    m.setContent(getRealString(type));                                        
                                    return;
                            case ACL_CT_STRING_FOLLOWS:  
                                    m.setContent(ct.lookupStr(inputCode()));
                                    return;                                
                            case ACL_CT_BLE_STR_FOLLOWS:
                                __b = ct.lookupBytes(inputCode());
                                m.setByteSequenceContent(__b);
                                return;
                        case ACL_NEW_BLE_STR8_FOLLOWS:
                                len = (int)((byte)getByte()&0xff);
                                break;
                        case ACL_NEW_BLE_STR16_FOLLOWS:
                                blen[0] = getByte();
                                blen[1] = getByte();
                                len = (int)((blen[1]&0xff)+((blen[0]&0xff)<<8));
                                break;
                        case ACL_NEW_BLE_STR32_FOLLOWS:
                                super.read(blen, 0, 4);
                                len = (int)((blen[3]&0xff) +
                                        ((blen[2]&0xff)<<8)+
                                        ((blen[1]&0xff)<<16) +
                                        ((blen[0]&0xff)<<24));
                                break;
                            default :
                                System.err.println("error getting content, value invalid:"+type);
                        }

                        //if(BitEffACLCodec.compressor == null){
                            __b = new byte[len];
                            super.read(__b, 0, len);                            
                            m.setByteSequenceContent(__b);
                        //} else {} 
                                                        
                        if (coding == ACL_BITEFFICIENT_CODETABLE) {
                                ct.insert(__b);
                        }                                                                                                
        }
        private Date getDate() throws Exception {
                byte type = getByte();
                ba.reset();
                for (int i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte());
                String s = new BinDate().fromBin(ba.get());
                if (type == ACL_ABS_DATET_FOLLOWS || type == ACL_DATET_POS_FOLLOWS
                    || type == ACL_DATET_NEG_FOLLOWS){                        
                        s += (char)getByte();
                }
                return (ISO8601.toDate(s));
        }

        private String getString() throws IOException {
                byte type = getByte();
                return getRealString(type);
        }
        private String getRealString(byte type) throws IOException {
                byte b, t;
                byte [] __b;
                ba.reset();
                String s = null;
                int i = 0, len = 0;
                switch(type) {
                case ACL_NEW_WORD_FOLLOWS: case ACL_NEW_STRING_FOLLOWS:
                        /* New word (or string) */
                        do {
                                ba.add(b=getByte());
                        } while(b != ACL_END_OF_PARAM);
                        s = new String(ba.get(),0,ba.length()).trim();
                        if (coding == ACL_BITEFFICIENT_CODETABLE) {
                                ct.insert(s);
                        }
                        break;
                case ACL_CT_WORD_FOLLOWS: case ACL_CT_STRING_FOLLOWS:
                        /* Word or String from codetable */
                        s = ct.lookupStr(inputCode());
                        break;
                case ACL_CT_BLE_STR_FOLLOWS:
                        /* BLE from codetable */
                        /*
			 * Note that we are not using byte[]'s here;
			 * only when we are dealing with the :content slot
			 */
                        s = ct.lookupStr(inputCode());
                        break;
                case ACL_ABS_DATE_FOLLOWS: case ACL_ABS_DATET_FOLLOWS:

                        /* DateTimeToken */
                        for (i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte());
                        s = new BinDate().fromBin(ba.get());
                        if ((type & 0x01) != 0x00) s += (char)getByte();
                        break;
                case ACL_DECNUM_FOLLOWS: case ACL_HEXNUM_FOLLOWS:

                        /* Number token */
                        bb.reset();
                        while(((b=getByte()) & 0x0f) != 0x00) bb.add(b);
                        if (b != 0x00) bb.add(b);
                        s = bn.fromBin(bb.get());
                        break;
                case ACL_NEW_BLE_STR8_FOLLOWS:
                        /*
			 * Byte lenght encoded w/ 8 bit lenght
			 */
                        len = getByte();
                        __b = new byte[len+3];
                        super.read(__b,0,len);
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
                        /*
			 * Byte lenght encoded w/ 16 bit lenght
			 */
                        super.read(blen, 0, 2);
                        len = (int)((blen[1]&0xff)+((blen[0]&0xff)<<8));
                        __b = new byte[len+3];
                        super.read(__b,0,len);
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
                        /*
			 * Byte lenght encoded w/ 32 bit lenght
			 */
                        super.read(blen, 0, 4);
                        len = (int)((blen[3]&0xff) + ((blen[2]&0xff)<<8)+
                                ((blen[1]&0xff)<<16) + ((blen[0]&0xff)<<24));
                        __b = new byte[len+3];
                        super.read(__b,0,len);
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
                        throw new IOException ("Unknown field: " +
                                new Integer(0).toHexString(type));
                }
                return (s);
        }
        private class ExprParser {
                int level;
                String s;
                ByteArray ba;
                public ExprParser() {
                        ba = new ByteArray(512);
                }
                public String toText() throws IOException {
                        ba.reset();
                        level = 0;
                        byte b, x;

                        boolean beginOfParameter = true;

                        while ((b = getByte())!=-1) {
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
                                        s = getRealString(b);
                                        ba.add(s.getBytes(),s.length());
                                        if (level == 0)
                                                return new String(ba.get(),0,ba.length()).trim();
                                }
                        }
                        return new String(ba.get(),0,ba.length()).trim();
                }
        }
}
