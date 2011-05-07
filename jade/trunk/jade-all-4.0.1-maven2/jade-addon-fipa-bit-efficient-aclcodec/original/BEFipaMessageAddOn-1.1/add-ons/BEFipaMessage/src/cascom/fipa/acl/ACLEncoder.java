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
import jade.util.leap.Properties;
import jade.util.leap.Iterator;

import java.util.Date;
import java.util.Enumeration;

import jade.util.leap.List;
import jade.core.AID;
import jade.lang.acl.*;
import jade.util.Logger;

/**
 * ACLEncoder implements an encoder for bit-efficient ACLMessages.
 * Encoders JADE ACLMessage objects to bit-efficient representation.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class ACLEncoder implements ACLConstants {
    /**
     * as Conversion between communicative acts (legacy <->
     * bit-efficient)
     */
    private static ACLPerformatives as;
    
    /** buf Buffer to which the bit-efficient message is generated */
    private ByteArray buf = new ByteArray(1024);
    
    /** bd Bit-efficient date */
    private BinDate bd = new BinDate();
    
    /** bn Bit-efficient Number */
    private BinNumber bn = new BinNumber();
    
    /** ct Codetable */
    private EncoderCodetable ct;
    
    /** size Size of the code table */
    private int size;
    
    /** baseCoding  Default coding scheme (with or without codetables) */
    private byte baseCoding;
    
    /**
     * currCoding Coding scheme for current message (might be
     * different than base_coding, if "0xFC" coding is used
     */
    private byte currCoding;
    
    /** ex Expression parser  */
    private ExprParser ex = new ExprParser();
    
    
    /**
     * Constructor for the encoder.
     * Initialises the ACL encoder with no codetable coding scheme.
     */
    private Logger logger = Logger.getMyLogger(this.getClass().getName());
    
    public ACLEncoder() {
        baseCoding = ACL_BITEFFICIENT;
        initialize(0);
    }
    
    /**
     * Constructor for the encoder.
     * Initializes the ACL encoder with a codetable.
     * @param sz the size for the codetable in bits
     *		(between 8 and 16)
     */
    public ACLEncoder(int sz) {
        baseCoding = (sz>0)
        ? ACL_BITEFFICIENT_CODETABLE
                : ACL_BITEFFICIENT;
        initialize(sz);
    }
    
    /**
     * Constructor for the encoder.
     * Initializes the ACL encoder with a codetable
     *  @param sz the size for the codetable in bits.
     *  @param ct the codetable to be used in encoding process.
     *  @deprecated Use ACLEncoder(EncoderCodetable ct) instead
     */
    public ACLEncoder(int sz, EncoderCodetable ct) {
        this.ct = ct;
        size = sz;
        as = new ACLPerformatives();
    }
    /**
     * Constructor for the encoder.
     * Initializes the ACL encoder with a codetable
     *  @param ct the codetable to be used in encoding process.
     */
    public ACLEncoder(EncoderCodetable ct) {
        this.ct = ct;
        size = ct.getSize();
        as = new ACLPerformatives();
    }
    
    private void initialize(int sz) {
        ct = (baseCoding == ACL_BITEFFICIENT || sz == 0)
        ? null
                : new EncoderCodetable(sz);
        size = sz;
        as = new ACLPerformatives();
    }
    
    /**
     * Return the codetable associated with this encoder
     */
    public EncoderCodetable getCodeTable() { return ct; }
    
    /**
     * Encodes an ACL message.
     * @param m Message to encode
     */
    public ByteArray encode(ACLMessage m) throws ACLCodec.CodecException {
        currCoding = baseCoding;
        return outputMessage(m);
    }
    
    /**
     * Encodes an ACL message.
     * @param m Message to encode
     * @param c Coding scheme (ACL_BITEFFICIENT_CODETABLE or
     *		ACL_BITEFFICIENT_NO_CODETABLE)
     */
    public ByteArray encode(ACLMessage m, byte c) throws ACLCodec.CodecException {
        currCoding = c;
        return outputMessage(m);
    }
        /*
         *
         */
    private ByteArray outputMessage(ACLMessage m) throws ACLCodec.CodecException{
        /* Assuming that content is a String */
        return (outputMessage(m, ACL_MSG_CONTENT_TYPE_STRING));
    }
        /*
         *
         */
    private ByteArray outputMessage(ACLMessage m, int content_type) throws ACLCodec.CodecException {
        String s;
        /* Output header */
        buf.reset();
        buf.add(currCoding);
        buf.add(ACL_VERSION);
        
        /* Output message type (communicative act) */
        dumpMsgType(m.getPerformative());
        
        /* Output message parameters */
        if ((s=m.getOntology())!=null && s.length() > 0)
            dumpParam(s, ACL_MSG_PARAM_ONTOLOGY);
        dumpSender(m.getSender(), ACL_MSG_PARAM_SENDER);
        dumpAIDList(m.getAllReceiver(), ACL_MSG_PARAM_RECEIVER);
        if ((s=m.getEncoding())!=null && s.length() > 0)
            dumpParam(s, ACL_MSG_PARAM_ENCODING);
        if ((s=m.getConversationId())!=null && s.length() > 0)
            dumpParam(s, ACL_MSG_PARAM_CONVERSATION_ID);
        dumpAIDList(m.getAllReplyTo(), ACL_MSG_PARAM_REPLY_TO);
        dumpDate(m.getReplyByDate());
                        
        //dumpContent(m);
        
        if ((s=m.getInReplyTo())!=null && s.length() > 0)
            dumpParam(s, ACL_MSG_PARAM_IN_REPLY_TO);
        if ((s=m.getReplyWith())!=null && s.length() > 0)
            dumpParam(s, ACL_MSG_PARAM_REPLY_WITH);
        if ((s=m.getLanguage())!=null && s.length() > 0)
            dumpParam(m.getLanguage(), ACL_MSG_PARAM_LANGUAGE);
        if ((s=m.getProtocol())!=null && s.length() > 0)
            dumpWordParam(s, ACL_MSG_PARAM_PROTOCOL);
        
       
        /* Possible user defined message parameters */
        dumpAllUserDefinedParameters(m);
        
        /**
         * Dump content last. Although the order of parameters 
         * is not defined by FIPA, this order makes sense, so 
         * like in email messages, all the headers are first and 
         * then the actual message content so message parser can read information 
         * from headers before parsing the actual content.
         */ 
        dumpContent(m);
        
        
        /* Write end-of-message marker */
        buf.add(ACL_END_OF_MSG);
        return buf;
    }
    
        /*
         * Output message type (performative)
         * FIXME: This routine cannot handle userdefined performatives.
         * These are still unsupported in JADE.
         */
    
    
    
    private void dumpMsgType(int p) {
        
        byte b = as.getCACode(p);
        buf.add(b);
    }
    
        /*
         * Output DateTimeToken
         */
    
    private void dumpDate(Date d) {
        if (d == null) return;
        String s = ISO8601.toString(d);
        
        /* Output message parameter code */
        buf.add(ACL_MSG_PARAM_REPLY_BY);
        
        /* Output DateTimeToken id field (0x20-0x26) */
        buf.add(bd.getDateFieldId(s));
        
        
        
        /* Output the Date */
        buf.add(bd.toBin(s), ACL_DATE_LEN);
        
        /* Output (possible) type designator */
        if (bd.containsTypeDg(s) == true)
            buf.add((byte) s.charAt(s.length()-1));
        
    }
        /*
         * Output a number
         */
    private void dumpNumber(ByteArray _bx, byte h) {
        if (_bx.length() < 1) {
            return;
        }
        buf.add((byte) (ACL_DECNUM_FOLLOWS | h));
        ByteArray bx = bn.toBin(_bx);
        buf.add(bx.get(), bx.length());
    }
    
    private void dumpString(String s) {
        dumpString(s, (byte) 0, false);
    }
    private void dumpWord(String s) {
        dumpString(s, (byte) 0, true);
    }
    private void dumpWord(String s, byte h) {
        dumpString(s, h, true);
    }
    private int parseBLEInt(String ble) {
        if (ble == null) return -1;
        int l = ble.length();
        int digit, result = 0;
        int i = 1;
        
        while (i < l) {
            digit = Character.digit(ble.charAt(i++), 10);
            if (digit < 0) {
                if (ble.charAt(i-1)=='"') return -result;
                throw new NumberFormatException(ble);
            }
            result *= 10;
            result -= digit;
        }
                /*
                 * If ble encoded string is correct, we should never
                 * come here
                 */
        throw new NumberFormatException(ble);
    }
    private String getBLEString(String ble) {
        int i = 0;
        while (ble.charAt(i++)!='"') ;
        return new String(ble.getBytes(), i, ble.length()-i);
    }
    
    private void dumpString(String s, byte h, boolean word) {
	  //FIXED: user defined parameter may be empty
          if (s == null || s.length() < 1){
		buf.add(ACL_END_OF_PARAM);
		return;
	  }
        
                /*
                 * FIX: Can we trust that there's no white space
                 * in the beginning of the String?
                 */
        boolean ble = (s.charAt(0)=='#');
        int len = (ble==true) ? parseBLEInt(s) : -1;
        /* Check if the string is in codetable */
        int code = (currCoding == ACL_BITEFFICIENT)
        ? -1
                : ct.lookup(s);
        if (code == -1) {
                        /*
                         * A new string. We add this to the codetable,
                         * iff we are using ACL_BITEFFICIENT_CODETABLE
                         * coding scheme.
                         */
            if (currCoding == ACL_BITEFFICIENT_CODETABLE) {
                ct.insert(s);
            }
            byte c = getStringID(word, ble, len);
            
            buf.add((byte) (c | h)); //
            if (ble) {
                s = getBLEString(s);
                dumpBLEHeader(s);
            }
            buf.add(s.getBytes(), s.length());
            if (!ble) buf.add(ACL_END_OF_PARAM);
        } else {
            /* Found from codetable */
            if (ble) {
                buf.add((byte) (ACL_CT_BLE_STR_FOLLOWS | h));
            } else {
                buf.add((byte) (ACL_CT_WORD_FOLLOWS | h));
            }
            outputCode(code);
        }
    }
    private void dumpBLEHeader(int len) {
        if (len < 256) {
            buf.add((byte) (len & 0xff));
        } else if (len < 65536) {
            buf.add((byte) ((len >> 8) & 0xff));
            buf.add((byte) (len & 0xff));
        } else {
            outputLong(len);
        }
    }
    private void dumpBLEHeader(String s) {
        dumpBLEHeader(s.length());
    }
    
    private byte getStringID(boolean word, boolean ble, int len) {
        if (word) return (byte)ACL_NEW_WORD_FOLLOWS;
        if (!ble) return (byte)ACL_NEW_STRING_FOLLOWS;
        if (len < 256) return (byte)ACL_NEW_BLE_STR8_FOLLOWS;
        if (len < 65536) return (byte)ACL_NEW_BLE_STR16_FOLLOWS;
        return (byte)ACL_NEW_BLE_STR32_FOLLOWS;
    }
    /**
     * Output index to codetable. If the size of the codetable is 2^8,
     * we output only one byte, otherwise two bytes.
     * @param n Index to output
     */
    private void outputCode(int n) {
        if (size > 8) {
            buf.add((byte) ((n >> 8) & 0xff));
            buf.add((byte) (n & 0xff));
        } else {
            buf.add((byte) n);
        }
    }
    
    private void outputLong(int n) {
        buf.add((byte) ((n >> 24) & 0xff));
        buf.add((byte) ((n >> 16) & 0xff));
        buf.add((byte) ((n >> 8) & 0xff));
        buf.add((byte) (n & 0xff));
    }
    
    /**
     * Output message sender (:sender)
     * @param s Sender as FIPA97 agent name (i.e., foo@somewhwere)
     * @param t Code for sender message parameter
     */
    private void dumpSender(AID _aid, byte t) {
        if (_aid == null) return;
        buf.add(t);
        dumpAID(_aid);
    }
    
    /**
     * Output message receiver (:receiver)
     * @param ag Group of agent names
     * @param t Code for receiver message parameter
     */
    private void dumpAIDList(Iterator i, byte t) {
        if (i != null && i.hasNext()) {
            buf.add(t);
            while(i.hasNext())
                dumpAID((AID)i.next());
            buf.add(ACL_END_OF_COLLECTION);
        }
    }
    
    /**
     * Output one agent name
     */
    private void dumpAID(AID aid) {
        Iterator addrs = aid.getAllAddresses();
        Iterator rslvrs = aid.getAllResolvers();
        
        /* Start of agent-identifier */
        buf.add(ACL_AID_FOLLOWS);
        
        /* Agent name (i.e., :name parameter) */
        dumpWord(aid.getName());
        
        /* (Optional) addresses (UrlCollection) */
        if (addrs != null && addrs.hasNext()) {
            buf.add(ACL_AID_ADDRESSES);
            while(addrs.hasNext())
                dumpWord((String)addrs.next());
            buf.add(ACL_END_OF_COLLECTION);
        }
        
        /* (Optional) resolvers (AgentIdentifierCollection) */
        if (rslvrs != null && rslvrs.hasNext()) {
            buf.add(ACL_AID_RESOLVERS);
            while(rslvrs.hasNext()) {
                dumpAID((AID)rslvrs.next());
            }
            buf.add(ACL_END_OF_COLLECTION);
        }
        
        /* (optional) UserDefinedSlots */
        Properties uds = aid.getAllUserDefinedSlot();
        Enumeration e = uds.propertyNames();
        String t;
        while(e.hasMoreElements()) {
            t = (String)e.nextElement();
            buf.add(ACL_AID_USERDEFINED);
            dumpWord(t);
            dumpString(uds.getProperty(t));
        }
        buf.add(ACL_END_OF_COLLECTION);
    }
    
    
    private void dumpAllUserDefinedParameters(ACLMessage m) {
        Properties u = m.getAllUserDefinedParameters();
        if (u == null) return;
        Enumeration e = u.propertyNames();
        String t;
        while (e.hasMoreElements()) {
            buf.add(ACL_NEW_MSGPARAM_FOLLOWS);
            t = (String)e.nextElement();
            dumpWord(t);
            dumpString(u.getProperty(t));
        }
    }
    
    /**
     * Dump <tt>:protocol</tt> message parameter.
     * <tt>:protocol</tt> is the only message parameter (currently)
     * that cannot take expression as a value, only Word.
     *
     * @param s Message parameter to output
     * @param t Code for message parameter name (:ontology, :language,...)
     */
    private void dumpWordParam(String s, byte t) {
        if (s == null || s.length() < 1) { return; }
        /* Output appropriate message parameter code */
        buf.add(t);
        dumpWord(s);
    }
    /**
     * Dump message parameter
     * @param s Message parameter to output
     * @param t Code for message parameter name (:ontology, :language,...)
     */
    private void dumpParam(String s, byte t) throws ACLCodec.CodecException {
        if (s == null || s.length() < 1) return;
        
        if (s.indexOf(' ') != -1) { // contains a blank inside
            if (s.charAt(0) != '"') {
                s = '"' + escape(s) + '"';
            }
        }
        
        /* Output appropriate message parameter code */
        buf.add(t);
        
        /* Check if the value is expression */
        char f = s.charAt(0);
        switch(f) {
            case '(':
                ex.fromString(s);
                break;
            case '#': case '\"':
                dumpString(s);
                break;
            default:
                dumpWord(s);
        }
    }
    
    private void dumpContent(ACLMessage m) throws ACLCodec.CodecException {
        if (m == null || (m.getContent()== null && !m.hasByteSequenceContent()) || m.getContent().equals("")){
            return;
        }
        
        /*
        // Optional: compress content if compressor is available
        byte contentFlag = 0;
        String cmpName = null;
        if(BitEffACLCodec.compressor != null) {
            if(m.hasByteSequenceContent() &&
                    m.getByteSequenceContent().length > compressor.minLength()){                
                try {
                    byte[] temp = m.getByteSequenceContent();
                    //System.out.println("byteslength:"+temp.length);
                    m.setByteSequenceContent(BitEffACLCodec.compressor.compress(temp));
                    contentFlag = ContentCompressor.bSeqFlag;
                    cmpName = BitEffACLCodec.compressor.getName();
                } catch (Exception e){
                    e.printStackTrace();
                    throw new ACLCodec.CodecException("Error compressing content:"+e.getMessage(),e);
                }
                
            } else if (((String)m.getContent()).length()>BitEffACLCodec.compressor.minLength()){
                try {
                    m.setByteSequenceContent(BitEffACLCodec.compressor.compress(((String)m.getContent()).getBytes()));
                    contentFlag = ContentCompressor.cFlag;
                    cmpName = BitEffACLCodec.compressor.getName();
                } catch(Exception e){
                    e.printStackTrace();
                    throw new ACLCodec.CodecException("Error compressing content:"+e.getMessage(),e);                    
                }
            }            
        }        
        */
        
        if (m.hasByteSequenceContent()) {
            byte b[];
            int l;
            if ((b=m.getByteSequenceContent()) == null) return;
            if ((l=b.length) < 1) return;
            
            
            int code = (currCoding == ACL_BITEFFICIENT)
            ? -1
                    : ct.lookup(b);
            buf.add(ACL_MSG_PARAM_CONTENT);
            if (code == -1) {
                /*
                if(contentFlag != 0){
                    l+= ContentCompressor.contentID.length()+1;
                    //System.err.println("Length of content:"+l);
                }
                */
                
                byte c = getStringID(false, true, l);
                buf.add((byte)c);
                dumpBLEHeader(l);
                
                // Optional: if message has compressed content, mark the content
                // as compressed putting ID string to the beginning of the
                // content slot. The content flag follows the ID, because
                // in order the compression to be invisible it must be known
                // if the content was originally byte sequence or not
                /*
                if(contentFlag != 0){
                    buf.add(ContentCompressor.contentID.getBytes(), ContentCompressor.contentID.length());
                    buf.add(cmpName.getBytes());
                    buf.add(ContentCompressor.nameDelimiter);
                    buf.add(contentFlag);
                }
                 */
                
                
                buf.add(b,b.length);
                if (currCoding == ACL_BITEFFICIENT_CODETABLE)
                    ct.insert(b);
            } else {
                buf.add(ACL_CT_BLE_STR_FOLLOWS);
                outputCode(code);
            }
        } else {
            buf.add(ACL_MSG_PARAM_CONTENT);
            dumpString((String)m.getContent());
        }
    }
    
    /**
     * @see jade.lang.acl.StringACLCodec
     */
    static private String escape(String s) {
        StringBuffer result = new StringBuffer(s.length()+20);
        for (int i=0; i < s.length(); i++)
            if (s.charAt(i) == '"' )
                result.append("\\\"");
            else
                result.append(s.charAt(i));
        return result.toString();
    }
    
    private class ExprParser {
        private int level;
        private int index;
        private int len;
        private ByteArray ba;
        private String _str;
        byte l;
        
        public ExprParser() {
            ba = new ByteArray();
        }
        
        public void fromString(String str) throws ACLCodec.CodecException {
            byte b;
            level = index = 0;
            l = 0;
            _str = str;
            len = _str.length();
            while ((b = getChar()) != -1) {
                parse(b);
            }
            if (level > 0) {
                throw new ACLCodec.CodecException("Invalid expression", null);
            }
            if (l > 0) {
                buf.add(l);
            }
        }
        
        private void parse(byte b) throws ACLCodec.CodecException {
            if (b == '(') {
                if (l == ACL_EXPR_LEVEL_DOWN ||
                        l == ACL_EXPR_LEVEL_UP) {
                    buf.add(l);
                }
                l = ACL_EXPR_LEVEL_DOWN;
                ++level;
            } else if (b == ')') {
                if (l == ACL_EXPR_LEVEL_DOWN ||
                        l == ACL_EXPR_LEVEL_UP) {
                    buf.add(l);
                }
                l = ACL_EXPR_LEVEL_UP;
                --level;
            } else if (b <= ' ') {
                                /*
                                 * Skip white space
                                 */
            } else if (b == '\"') {
                                /*
                                 * StringLiteral
                                 */
                byte temp = l;
                String s = getString(b);
                if (l != temp){
                    dumpString(s, temp, false);
                } else {
                    dumpString(s, l, false);
                }
                l = 0;
            } else if (b == '#') {
                                /*
                                 * ByteLenghtEncoded String
                                 */
                dumpString(getBLE(), l, false);
                l = 0;
            } else if ((b >= '0' && b <= '9') || b == '-') {
                                /*
                                 * Number
                                 */
                dumpNumber(getNumber(b), l);
                l = 0;
            } else if (b > 0x20) {
                                /*
                                 * Word
                                 */
                dumpWord(getWord(b), l);
                l = 0;
            } else {
                throw new ACLCodec.CodecException("Unknown char " + b, null);
            }
        }
        /**
         * Get number
         */
        private ByteArray getNumber(byte b) {
            ba.reset();
            ba.add(b);
            while ((b = getChar()) != -1) {
                if (b >= '0' && b <= '9' || b == 'e' ||
                        b == '.' || b == 'E' || b == '+' ||
                        b == '-') {
                    ba.add(b);
                } else {
                    --index;
                    break;
                }
            }
            return (ba);
        }
        /**
         * Get ByteLengthEncoded String
         */
        private String getBLE() {
            byte b;
            ba.reset();
                        /*
                         * Copy until we know the length
                         */
            ba.add((byte)'#');
            while((b = getChar()) != '"') ba.add(b);
            ba.add((byte)'"');
                        /*
                         * Get length as integer
                         */
            int len = parseBLEInt(new String(ba.get(), 0, ba.length()));
                        /*
                         * Copy rest of the string
                         */
            while (len-- > 0) ba.add(getChar());
            return new String(ba.get(), 0, ba.length());
        }
        
        private String getWord(byte b) {
            ba.reset();
            ba.add(b);
            while ((b = getChar()) != -1) {
                if (b > 0x020 && b != ')' && b != '(' ) {
                    ba.add(b);
                } else {
                    --index;
                    break;
                }
            }
            return new String(ba.get(), 0, ba.length());
        }
        
        /**
         * Parse a StringLiteral
         * Syntax for StringLiteral: <br>
         * StringLiteral = "\"" ([~"\""]|"\\\"")* "\""
         *
         * @param b Previously parsed byte
         * @return Parsed StringLiteral
         */
        private String getString(byte b) {
            ba.reset();
            byte prev = 0;
            ba.add(b);
            while ((b = getChar()) != -1) {
                if (b == '\\' && prev == '\\') {
                    ba.add(b);
                    prev = 0;
                    continue;
                } else if (b == '"' && prev == '\\') {
                    ba.add(b);
                } else if (b != '"') {
                    ba.add(b);
                } else {
                    ba.add(b);
                    break;
                }
                prev = b;
            }
            if (b == -1) {
                
                                /*
                                if(logger.isLoggable(Logger.INFO))
                                        logger.log(Logger.INFO,"String not ended correctly, treating as word");
                                 */
                index--;
                l = ACL_EXPR_LEVEL_DOWN;
                return new String(ba.get(), 0, ba.length()-1);
            }
            return new String(ba.get(), 0, ba.length());
        }
        
        private byte getChar() {
            return (index < len)
            ? (byte) _str.charAt(index++)
            : -1;
        }
    }
}
