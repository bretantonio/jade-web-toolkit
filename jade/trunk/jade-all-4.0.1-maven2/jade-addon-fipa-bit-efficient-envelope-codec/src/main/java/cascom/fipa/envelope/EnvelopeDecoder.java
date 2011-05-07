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
package cascom.fipa.envelope;

import cascom.fipa.util.ByteArray;
import cascom.fipa.acl.ACLConstants;
import cascom.fipa.acl.BinNumber;
import cascom.fipa.acl.BinDate;
import cascom.fipa.acl.BEParseException;


import java.io.*;
import java.util.Date;

import jade.core.AID;
import jade.lang.acl.ISO8601;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;
import jade.domain.FIPAAgentManagement.Property;

/**
 * Implements a decoder for bit-efficient envelopes as specificied
 * in FIPA Specification number 88 (FIPA Agent Message Transport
 * Envelope in Bit-Efficient Encoding Specification). The specification
 * is available at URL: <http://www.fipa.org>
 *
 * @author Heikki Helin <Heikki.j.Helin@sonera.com>
 */
public class EnvelopeDecoder implements EnvelopeConstants, ACLConstants {
    public final static String ACL_CODING="";
    private int pos;
    private static BinNumber bn = new BinNumber();
    
    /**
     * Translates a bit-efficiently encoded envelope to platform
     * internal representation.
     *
     * @param data Byte array containing bit-efficiently encoded envelope.
     * @return The envelope in platform's internal representation
     */
    public Envelope getEnvelope(byte[] data) throws BEParseException {
        Envelope e = new Envelope();
        pos = 0;
        while(hasMoreBytes(data) && getEnvelope(data,e) != null){
        }
        return e;
    }
    
    /**
     * Translates a bit-efficiently encoded envelope to platform
     * internal representation.
     *
     * @param data Byte array containing bit-efficiently encoded envelope.
     * @param env An envelope "container" to which the envelope is
     *	constructed.
     * @return The envelope in platform's internal representation
     */
    public Envelope getEnvelope(byte[] data, Envelope env) throws BEParseException {
        byte t = getByte(data);
        if (t == ENV_BASEMSGID) {
            getBaseEnvelope(env, data);
            return env;
        } else if (t == ENV_EXTMSGID) {
            getExtEnvelope(env, data);
            return env;
        }
        System.err.println("unknown envelope type, pos"+pos);
                /*
                 * Unknown envelope type
                 */
        return null;
    }
    
    /**
     *
     */
    private void getBaseEnvelope(Envelope e, byte[] data)
    throws BEParseException {
        long len = readLen16(data);
        if(len == 0){
                    /*
                     * JumboEnvelope
                     */
            len = readLen32(data);
            //System.err.println("Jumboenvelope detected");
        }
        //System.err.println("Base envelope, len: "+len+" startPos "+pos);
        
                /*
                 * Parse ACL representation
                 */
        getAclRepresentation(e, data);
                /*
                 * Parse date
                 */
        
        Date d = getDate(data);
        
        
        
        e.setDate(d);
        
                /*
                 * Other slots
                 */
        while(getEnvSlot(e, data) != -1);
    }
    
    /**
     *
     */
    private void getExtEnvelope(Envelope e, byte[] data)
    throws BEParseException {
        long len = readLen16(data);
        if(len == 0){
                    /*
                     * JumboEnvelope
                     */
            len = readLen32(data);
            //System.err.println("Jumboenvelope detected");
        }
        //System.err.println("Ext envelope, len: "+len+" startPos "+pos);
        
                /*
                 * Get mandatory received object
                 */
        e.addStamp(getReceived(data));
                /*
                 * Other slots
                 */
        while(getEnvSlot(e, data) != -1);
    }
    /**
     *
     */
    private int getEnvSlot(Envelope e, byte [] data)
    throws BEParseException{
        byte b = getByte(data);
        switch(b) {
            case ENV_END_OF_ENVELOPE:
                return -1;
            case ENV_TO_FOLLOWS:
                getTo(e, data);
                break;
            case ENV_FROM_FOLLOWS:
                e.setFrom(getAID(data));
                break;
            case ENV_ACLREP_FOLLOWS:
                e.setAclRepresentation(getNullTerminatedStr(data));
                break;
            case ENV_COMMENT_FOLLOWS:
                e.setComments(getNullTerminatedStr(data));
                break;
                
            case ENV_PAYLOAD_LEN_FOLLOWS:
                e.setPayloadLength(getPayloadLength(data));
                break;
            case ENV_PAYLOAD_ENCODING_FOLLOWS:
                e.setPayloadEncoding(getNullTerminatedStr(data));
                break;
            case ENV_INTENDED_RECEIVER_FOLLOWS:
                getIntendedReceivers(e, data);
                break;
            case ENV_RECEIVED_OBJECT_FOLLOWS:
                e.setReceived(getReceived(data));
                break;
            case ENV_TRANSPORT_BEHAV_FOLLOWS:
                getTransportBehaviour(e, data);
                break;
            case ENV_USER_SLOT_FOLLOWS:
                getUserSlot(e, data);
                break;
            default:
                        /*
                         */
                throw new BEParseException("Unknown code: "+b);
        }
        return 0;
    }
    
    /**
     * Parse user defined slot
     */
    private void getUserSlot(Envelope e, byte [] data) {
        String key = getNullTerminatedStr(data);
        String value = getNullTerminatedStr(data);
        e.addProperties(new Property(key, (Object)value));
    }
    
    private void getTo(Envelope e, byte [] data) throws BEParseException {
        AID _aid;
        
        while ((_aid=getAID(data)) != null)
            e.addTo(_aid);
        
    }
    private AID getAID(byte[] data) throws BEParseException {
        return getAID(data, getByte(data));
    }
    
    private AID getAID(byte[] data, byte t)
    throws BEParseException {
        AID _aid = new AID();
        byte b;
        
        
        
        if (t != ACL_AID_FOLLOWS) {
            if (t == ENV_END_OF_COLLECTION) return null;
            throw new BEParseException("Parse error...."+new Integer(0).toHexString(t));
        }
                /*
                 * Mandatory part of AID
                 */
        String _s = getNullTerminatedStr(data);
        _aid.setName(_s);
                /*
                 * Optional part of AID
                 */
        
        while ((b = getByte(data)) != ENV_END_OF_COLLECTION) {
            switch(b) {
                case ACL_AID_ADDRESSES:
                    while ((b=getByte(data))!=ENV_END_OF_COLLECTION)
                        
                        
                        _aid.addAddresses(getNullTerminatedStr(b,data));
                    
                    break;
                    
                case ACL_AID_RESOLVERS:
                    while ((b=getByte(data))!=ENV_END_OF_COLLECTION)
                        
                        _aid.addResolvers(getAID(data,b));
                    
                    break;
                    
                case ACL_AID_USERDEFINED:
                    String key = getNullTerminatedStr(data);
                    String value = getNullTerminatedStr(data);
                    
                    _aid.addUserDefinedSlot(key, value);
                    
                    break;
                default:
                    throw new BEParseException("Unknown field in AgentID");
            }
        }
        return _aid;
    }
    private void getIntendedReceivers(Envelope e, byte [] data)
    throws BEParseException{
        AID _aid;
        
        while ((_aid=getAID(data)) != null)
            e.addIntendedReceiver(_aid);
        
    }
    
    private ReceivedObject getReceived(byte [] data) throws BEParseException {
        
        ReceivedObject ro = new ReceivedObject();
        
                /*
                 * URL and date are mandatory
                 */
        String url = getNullTerminatedStr(data);
        
        if (url != null) ro.setBy(url);
        else return ro;
        Date d = getDate(data);
        if (d != null) ro.setDate(d);
        else return ro;
        
        byte b;
        String from = null, by = null, via = null, id = null;
        while((b=getByte(data))!=ENV_END_OF_COLLECTION) {
            switch(b) {
                case ENV_RECEIVED_FROM:
                    if (from != null) {
                        throw new BEParseException("Two From fields in ReceivedObject");
                    }
                    from = getNullTerminatedStr(data);
                    
                    ro.setFrom(from);
                    
                    break;
                case ENV_RECEIVED_ID:
                    if (id != null) {
                        throw new BEParseException("Two Id fields in Received Object");
                    }
                    id = getNullTerminatedStr(data);
                    
                    ro.setId(id);
                    
                    break;
                case ENV_RECEIVED_VIA:
                    if (via != null) {
                        throw new BEParseException("Two via fields in Received object");
                    }
                    via = getNullTerminatedStr(data);
                    ro.setVia(via);
                    
                    break;
                    
                case ENV_USER_SLOT_FOLLOWS:
                                /*
                                 * BUG: Jade doesn't support user parameters in ReceivedObjects
                                 */
                    this.getNullTerminatedStr(data);
                    this.getNullTerminatedStr(data);
                    System.err.println("User defined parameters in ReceivedObjects not supported in JADE");
                    break;
                default:
                    throw new BEParseException("Unknown field in received object");
            }
        }
        return ro;
    }
    
        /*
         * FIXME: Not tested until Jade supports transport behaviour.
         */
    private void getTransportBehaviour(Envelope e, byte [] data)
    throws BEParseException {
        System.err.println("Warning: TransportBehaviour not supported by Jade.");
        getAny(e, data);
    }
    
        /*
         * FIXME: not tested until Jade supports transport behaviour         *
         */
    private Any getAny(Envelope e, byte [] data) throws BEParseException {
        byte b;
        Any a = new Any();
        byte[] lenBytes;
        long strLen = 0;
        
        switch(b = getByte(data)) {
            case ENV_NULL_TERM_ANY:
                a.add(getNullTerminatedStr(data));
                return a;
            case ACL_NEW_BLE_STR8_FOLLOWS:
                strLen = (long) getByte(data);
                break;
            case ACL_NEW_BLE_STR16_FOLLOWS:
                strLen = (long) this.readLen16(data);
                break;
            case ACL_NEW_BLE_STR32_FOLLOWS:
                strLen = this.readLen32(data);
                break;
        }
        
        ByteArray ba = new ByteArray();
        for (long i = 0; i < strLen; ++i) {
            ba.add(getByte(data));
        }
        a.add(ba);
        return a;
    }
    
    
    private Long getPayloadLength(byte [] data) {
        ByteArray bx = new ByteArray();
        byte b;
        
        int c = 0;
        
        while(((b = getByte(data)) & 0x0f) != 0x00){
            bx.add(b);
            //c++;
        }
        bx.add(b);
        
                /*
                if (b != 0x00){
                    bx.add(b);
                    //c++;
                }
                 */
        //System.err.println("bx.get:"+bx.get().length+" real:"+c);
        return (new Long(Long.parseLong(bn.fromBin(bx.get()))));
    }
    
    
    private String getNullTerminatedStr(byte [] data) {
        return getNullTerminatedStr(getByte(data), data);
    }
    
    private String getNullTerminatedStr(byte t, byte [] data) {
        ByteArray ba = new ByteArray();
        byte b;
        ba.add(t);
        do {
            ba.add(b=getByte(data));
        } while (b != 0x00);
        return (new String(ba.get(), 0, ba.length()));
    }
    
    private void getAclRepresentation(Envelope e, byte[] data)
    throws BEParseException {
        switch(getByte(data)) {
            case ENV_ACL_BITEFFCIENT:
                e.setAclRepresentation(jade.domain.FIPANames.ACLCodec.BITEFFICIENT);
                break;
            case ENV_ACL_STRING:
                e.setAclRepresentation(jade.domain.FIPANames.ACLCodec.STRING);
                break;
            case ENV_ACL_XML:
                e.setAclRepresentation(jade.domain.FIPANames.ACLCodec.XML);
                break;
            case ENV_ACL_USERDEFINED:
                e.setAclRepresentation(getNullTerminatedStr(data).trim());
                break;
            default:
                throw new BEParseException("Unknown ACL encoding");
        }
    }
    
    private Date getDate(byte[] data) {
        
        ByteArray ba = new ByteArray();
        byte type = getByte(data);
        
        Date d = null;
        
        for (int i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte(data));
        
        String s = new BinDate().fromBin(ba.get());
        
        // TypeDesignator follows
        if (type == ACL_ABS_DATET_FOLLOWS || type == ACL_DATET_POS_FOLLOWS || type == ACL_DATET_NEG_FOLLOWS ) {
            s += getByte(data);
        }
        
        try {
            long l = ISO8601.toDate(s).getTime();
            d = new Date(l);
        } catch (Exception e) { System.err.println("error in date encoding"); e.getMessage();}
        
        return d;
    }
    
    
    private byte getByte(byte [] data) { return data[pos++]; }
    
    private boolean hasMoreBytes(byte[] data){ return (pos < data.length-1);}
    
    private class Any {
        public ByteArray b;
        public String s;
        int type = 0;
        void add(String s) {
            add(s, null);
        }
        void add(ByteArray b) {
            add(null, b);
        }
        void add(String s, ByteArray b) {
            this.s = s;
            this.b = b;
            type = (s == null) ? 2 : 1;
        }
    }
    
        /*
         * Reads len16 as int
         */
    private int readLen16(byte[] data){
        byte[] bytes = new byte[2];
        bytes[0] = this.getByte(data);
        bytes[1] = this.getByte(data);
        
        return (int)((bytes[1]&0xff) + ((bytes[0]&0xff)<<8));
    }
    
    
        /*
         * Reads len32 as long
         */
    private long readLen32(byte[] data){
        byte[] bytes = new byte[4];
        bytes[0] = this.getByte(data);
        bytes[1] = this.getByte(data);
        bytes[2] = this.getByte(data);
        bytes[3] = this.getByte(data);
        
        return (long)((bytes[3]&0xff) + ((bytes[2]&0xff)<<8)+ ((bytes[1]&0xff)<<16) + ((bytes[0]&0xff)<<24));
    }
}
