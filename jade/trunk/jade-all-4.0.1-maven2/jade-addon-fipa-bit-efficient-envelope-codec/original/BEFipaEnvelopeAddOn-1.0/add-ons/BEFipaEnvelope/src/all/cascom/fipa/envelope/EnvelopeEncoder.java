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
import cascom.fipa.acl.BinDate;
import cascom.fipa.acl.BinNumber;
import cascom.fipa.acl.BEParseException;

import java.util.Date;
import jade.util.leap.List;
import jade.util.leap.Iterator;
import java.util.Enumeration;
import jade.util.leap.Properties;
import jade.core.AID;
import jade.lang.acl.*;
import jade.domain.FIPAAgentManagement.*;


/**
 * Implements an encoder for bit-efficient envelopes as specificied
 * in FIPA Specification number 88 (FIPA Agent Message Transport
 * Envelope in Bit-Efficient Encoding Specification). The specification
 * is available at URL: <http://www.fipa.org>
 *
 * @author Heikki Helin <Heikki.j.Helin@sonera.com>, Ahti Syreeni <ahti.syreeni@teliasonera.com>
 */
public class EnvelopeEncoder implements EnvelopeConstants, ACLConstants {
    /** bd Bit-efficient date */
    private BinDate bd = new BinDate();
    
    private BinNumber bn = new BinNumber();
    
    /**
     * Default buffer size of result envelope. Note that size
     * size is automatically increased whenever needed.
     */
    private static final int DEFAULT_BUF_SIZE = 1024;
    
    /**
     * Constructor for the encoder.
     */
    public EnvelopeEncoder() { }
    
    /**
     * Encodes an envelope bit-efficiently
     * @param e Envelope to encode
     * @return Byte array containing bit-efficient representation of
     *	supplied envelope
     */
    public ByteArray encode(Envelope e) throws BEParseException {
        return encode(e, null);
    }
    /**
     * Encodes a base envelope bit-efficiently
     *
     * @param e Envelope to encode
     * @param buf A Buffer to which the bit-efficient envelope is created
     * @return Byte array containing bit-efficient representation of
     *	supplied envelope
     */
    public ByteArray encode(Envelope e, ByteArray buf)
    throws BEParseException {
        if (e == null) throw new BEParseException("No envelope?");
        if (buf == null) buf = new ByteArray(DEFAULT_BUF_SIZE);
        
                /*
                 * Encode the External Envelopes if needed. For each stamp there
                 * must be own external envelope. Note that parameters for stamps
                 * are not stored in JADE ExtEnvelopes are without parameters.
                 * This is a bug in JADE 3.3.
                 */
        ReceivedObject[] ros = e.getStamps();
        if(ros != null && ros.length > 0){
            for(int i = ros.length -1; i >= 0; i--) {
                outputExtEnvelope(ros[i],buf);
            }
        }
        
                /*
                 * Encode the base envelope
                 */
        outputEnvelope(e, buf);
        return buf;
    }
    /**
     * Create a base envelope header
     */
    private void baseMsgHeader(Envelope e, ByteArray buf)
    throws BEParseException {
        /* Output header */
        buf.add(ENV_BASEMSGID);
                /*
                 * Add a space for envelope length
                 */
        buf.add((byte)0x00);
        buf.add((byte)0x00);
                /*
                 * Add ACL representation
                 */
        addACLRepresentation(e, buf);
        addDate(e, buf);
    }
    
    /**
     * Create a sub-envelope header
     */
    private void extMsgHeader(ReceivedObject ro, ByteArray buf)
    throws BEParseException {
        /* Output header */
        buf.add(ENV_EXTMSGID);
                /*
                 * Add a space for envelope lenght
                 */
        buf.add((byte)0x00);
        buf.add((byte)0x00);
        
                /*
                 * Add (mandatory) received object
                 */
        
        addReceived(ro, buf);
    }
    
    
    /**
     * Create a sub-envelope
     */
    private ByteArray outputExtEnvelope(ReceivedObject ro, ByteArray buf)
    throws BEParseException {
                /*
                 * Save the position in ByteArray
                 */
        int pos = buf.length();
        
        extMsgHeader(ro, buf);
        
                /*
                 * Add Slots should be here but Jade Envelope - class is not able to store parameters for
                 * each ReceivedObject so parameters for ExtEnvelopes cannot be implemented.
                 */
        
                /*
                 * Add end of envelope
                 */
        buf.add(ENV_END_OF_ENVELOPE);
                /*
                 * And finally, add the length
                 */
        addLength(buf, pos);
        return buf;
    }
    
    /**
     * Create a base envelope
     */
    private ByteArray outputEnvelope(Envelope e, ByteArray buf)
    throws BEParseException {
                /*
                 * Save the position in ByteArray
                 */
        int pos = buf.length();
        
        baseMsgHeader(e, buf);
        
                /*
                 * Add Slots
                 */
        addSlots(e, buf, true);
                /*
                 * Add end of envelope
                 */
        buf.add(ENV_END_OF_ENVELOPE);
                /*
                 * And finally, add the length
                 */
        addLength(buf, pos);
        return buf;
    }
    
    /**
     * Add the length to the envelope
     */
    void addLength(ByteArray buf, int pos) {
        
        if(buf.length() - pos <= 65536) {
                        /*
                         * Easy case
                         */
            buf.addToPos((byte) (((buf.length() -pos) >> 8) & 0xff), 1+pos);
            buf.addToPos((byte) ((buf.length() -pos)& 0xff), 2+pos);
            //System.out.println("Len is " + (buf.length() - pos));
        } else {
                        /*
                         * Jumbo envelope.
                         */
            long size = buf.length() - pos;
            byte[] jumboSize = new byte[4];
            jumboSize[0] = (byte) ((size >> 24) & 0xff);
            jumboSize[1] = (byte) ((size >> 16) & 0xff);
            jumboSize[2] = (byte) ((size >> 8) & 0xff);
            jumboSize[3] = (byte) (size  & 0xff);
            buf.insertArrayToPos(jumboSize, pos +3);
            //System.out.println("Len is (jumbo) " + size);
        }
        
    }
    
    
    /**
     *
     */
    private void addACLRepresentation(Envelope e, ByteArray buf)
    throws BEParseException {
        String s = e.getAclRepresentation();
        if (s == null) {
                        /*
                         * ACL-representation is mandatory...
                         */
            throw new BEParseException("Mandatory ACL-representation slot missing");
        }
                /*
                 * First check if ACL representation is some of the
                 * predefined representations
                 */
        if (s.compareTo(jade.domain.FIPANames.ACLCodec.BITEFFICIENT)==0) {
            buf.add(ENV_ACL_BITEFFCIENT);
        } else if (s.compareTo(jade.domain.FIPANames.ACLCodec.STRING)==0) {
            buf.add(ENV_ACL_STRING);
        } else if (s.compareTo(jade.domain.FIPANames.ACLCodec.XML)==0) {
            buf.add(ENV_ACL_XML);
        } else {
                        /*
                         * Not a predefined representation
                         */
            buf.add(ENV_ACL_USERDEFINED);
            addNullTerminatedStr(s, buf);
        }
    }
    
    /**
     *
     */
    private void addDate(Envelope e, ByteArray buf) {
        addDate(buf, e.getDate());
    }
    
    /**
     *
     */
    private void addDate(ByteArray buf, Date d) {
        if (d == null) {
            d = new Date();
        }
        
        String s = new String(ISO8601.toString(d));
        
        if (s == null || s.length() < 1) {
            /* FIXME: What if? */
            return;
        }
        
        /* Output DateTimeToken id field (0x20-0x26) */
        buf.add(bd.getDateFieldId(s));
        
        /* Output the Date */
        buf.add(bd.toBin(s), ACL_DATE_LEN);
        
        /* Output (possible) type designator */
        if (bd.containsTypeDg(s) == true )
            buf.add((byte)s.charAt(s.length()-1));
        
    }
    
    
    /**
     * Adds the predefined or user defined parameters. The user defined
     * parameter should have prefix X-COMPANYNAME according to
     * the FIPA specs, it can't be added here. The value of user
     * defined parameter must be a String or it will not be added
     * to the envelope. User defined parameters are from
     * jade.domain.FIPAAgentManagement.Envelope.getAllProperties()
     * method, which unfortunately allow any kind of Object to be used as value.
     */
    private void addSlots(Envelope e, ByteArray buf, boolean received)
    throws BEParseException {
        
        addTo(e.getAllTo(), buf);
        addFrom(e.getFrom(), buf);
        addIntendedReceivers(e.getAllIntendedReceiver(), buf);
        addComments(e.getComments(), buf);
        addPayloadLength(e.getPayloadLength(), buf);
        addPayloadEncoding(e.getPayloadEncoding(), buf);
        if (received) addReceived(e.getReceived(), buf);
                /*
                 * Transport-behaviour
                 */
                /*
                 * FIXME: Transport-behaviour not supported by JADE
                 */
        
                /*
                 * User-defined parameters from Envelope. The properties should
                 * have X-COMPANYNAME- -prefix and values must be String.
                 *
                 */
        
        Iterator it = e.getAllProperties();
        Property temp;
        while(it.hasNext()){
            temp = (Property) it.next();
            // if value is String, it can be converted
            if(temp.getValue().getClass().isInstance((Object)" ")){
                this.addNullTerminatedStr(ENV_USER_SLOT_FOLLOWS, temp.getName()+" "+ (String)temp.getValue(), buf);
            } else {
                System.err.println("Error, user parameter value not String: "+temp.getName());
            }
        }
    }
    
    /**
     *
     */
    private void addReceived(ReceivedObject ro, ByteArray buf)
    throws BEParseException {
        String str, url;
        
        if (ro == null) return;
        buf.add(ENV_RECEIVED_OBJECT_FOLLOWS);
        url = ro.getBy();
        if (url == null) {
            throw new BEParseException("No 'by' in Received");
        }
        addNullTerminatedStr(url, buf);
        
        Date d = ro.getDate();
        
        
        
        if (d == null) {
            throw new BEParseException("No date in Received");
        }
        addDate(buf, d);
        if ((url=ro.getFrom()) != null)
            addNullTerminatedStr(ENV_RECEIVED_FROM, url, buf);
        
        
        
        if ((str=ro.getId()) != null)
            addNullTerminatedStr(ENV_RECEIVED_ID, str, buf);
        if ((str=ro.getVia()) != null)
            addNullTerminatedStr(ENV_RECEIVED_VIA, str, buf);
        
                /*
                 * UserDefinedParameters should be here, but Jade don't support
                 */
        
        buf.add(ENV_END_OF_COLLECTION);
    }
    
    
    /**
     *
     */
    private void addTo(Iterator i, ByteArray buf) {
        if (i == null) return;
        buf.add(ENV_TO_FOLLOWS);
        while (i.hasNext())
            addAID((AID)i.next(), buf);
        buf.add(ENV_END_OF_COLLECTION);
    }
    
    
    /**
     *
     */
    private void addFrom(AID aid, ByteArray buf) {
        if (aid != null) {
            buf.add(ENV_FROM_FOLLOWS);
            addAID(aid, buf);
        }
    }
    
    /**
     *
     */
    private void addComments(String s, ByteArray buf) {
        addNullTerminatedStr(ENV_COMMENT_FOLLOWS,s, buf);
    }
    
    
    /**
     *
     */
    private void addPayloadLength(Long __ilen, ByteArray buf) {
        long ilen = __ilen.longValue();
        
        
        if (ilen < 0) return;
        String len = String.valueOf(ilen);
        
        if (len != null && len.length() > 0) {
            buf.add(ENV_PAYLOAD_LEN_FOLLOWS);
            ByteArray bx = bn.toBin(len);
            //System.err.println("System.err.println get():"+bx.get().length+" len:"+bx.length()+" value "+(new String(bx.get())));
            buf.add(bx.get(), bx.length());
        }
    }
    
    /**
     *
     */
    private void addPayloadEncoding(String s, ByteArray buf) {
        addNullTerminatedStr(ENV_PAYLOAD_ENCODING_FOLLOWS, s, buf);
    }
    
    /**
     * Add Intended receivers slot
     */
    private void addIntendedReceivers(Iterator i, ByteArray buf) {
        if (i != null && i.hasNext()) {
            buf.add(ENV_INTENDED_RECEIVER_FOLLOWS);
        } else return;
        while (i.hasNext()) {
            addAID((AID)i.next(), buf);
        }
        buf.add(ENV_END_OF_COLLECTION);
    }
    
    /**
     * Add user defined slots
     */
    private void addAID(AID aid, ByteArray buf) {
        Iterator addrs = aid.getAllAddresses();
        Iterator rslvrs = aid.getAllResolvers();
        
        /* Start of agent-identifier */
        buf.add(ACL_AID_FOLLOWS);
        
        /* Agent name (i.e., :name parameter) */
        addNullTerminatedStr(aid.getName(), buf);
        
        /* (Optional) addresses (UrlCollection) */
        if (addrs != null && addrs.hasNext()) {
            buf.add(ACL_AID_ADDRESSES);
            while (addrs.hasNext())
                addNullTerminatedStr((String)addrs.next(),buf);
            buf.add(ACL_END_OF_COLLECTION);
        }
        /* (Optional) resolvers (AgentIdentifierCollection) */
        if (rslvrs != null && rslvrs.hasNext()) {
            buf.add(ACL_AID_RESOLVERS);
            while(rslvrs.hasNext()) {
                addAID((AID)rslvrs.next(), buf);
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
            addNullTerminatedStr(t, buf);
            addNullTerminatedStr(uds.getProperty(t), buf);
        }
        buf.add(ACL_END_OF_COLLECTION);
    }
    
    /**
     *
     */
    private void addNullTerminatedStr(byte b, String s, ByteArray buf) {
        if (s == null || s.length() < 1) return;
        buf.add(b);
        addNullTerminatedStr(s, buf);
    }
    
    /**
     *
     */
    private void addNullTerminatedStr(String s, ByteArray buf) {
        if (s == null || s.length() < 1) return;
        buf.add(s.getBytes(), s.length());
        buf.add((byte)ENV_END_OF_STR);
    }
}
