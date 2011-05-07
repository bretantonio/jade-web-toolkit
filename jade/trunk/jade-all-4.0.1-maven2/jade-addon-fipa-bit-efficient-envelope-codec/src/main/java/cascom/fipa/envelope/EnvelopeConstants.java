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


/**
 * fipa.mts.env.rep.bitefficient.std concrete envelope transport syntax
 * related constants.
 *
 * @see <a href=http://www.fipa.org/specs/fipa00088/>FIPA Agent
 * 	Message Transport Envelope Representation in Bit Efficient
 * 	Specification</a>
 *
 * @author Heikki Helin <Heikki.j.Helin@sonera.com>
 */
public interface EnvelopeConstants {
        /*
         * Message identifiers
         */
    byte ENV_BASEMSGID = (byte) 0xFE;
    byte ENV_EXTMSGID = (byte) 0xFD;
    
        /*
         * Envelope slots
         */
    
    /**
     * Binary code for 'To' envelope slot
     */
    byte ENV_TO_FOLLOWS = (byte) 0x02;
    /**
     * Binary code for 'From' envelope slot
     */
    byte ENV_FROM_FOLLOWS = (byte) 0x03;
    /**
     * Binary code for 'acl-representation' envelope slot
     */
    byte ENV_ACLREP_FOLLOWS = (byte) 0x04;
    /**
     * Binary code for 'comments' envelope slot
     */
    byte ENV_COMMENT_FOLLOWS = (byte) 0x05;
    /**
     * Binary code for 'payload-length' envelope slot
     */
    byte ENV_PAYLOAD_LEN_FOLLOWS = (byte) 0x06;
    /**
     * Binary code for 'payload-encoding' envelope slot
     */
    byte ENV_PAYLOAD_ENCODING_FOLLOWS = (byte) 0x07;
    /**
     * Binary code for 'intended-receiver' envelope slot
     */
    byte ENV_INTENDED_RECEIVER_FOLLOWS = (byte) 0x09;
    /**
     * Binary code for 'reveived' envelope slot
     */
    byte ENV_RECEIVED_OBJECT_FOLLOWS = (byte) 0x0a;
    /**
     * Binary code for 'transport-behaviour' envelope slot
     */
    byte ENV_TRANSPORT_BEHAV_FOLLOWS = (byte) 0x0b;
    /**
     * Binary code for 'user-defined' envelope slots
     */
    byte ENV_USER_SLOT_FOLLOWS = (byte) 0x00;
    
    
        /*
         * Components of ReceivedObject
         */
    /**
     * Binary code for 'from' slot inside a ReceivedObject
     */
    byte ENV_RECEIVED_FROM = (byte)0x02;
    /**
     * Binary code for 'id' slot inside a ReceivedObject
     */
    byte ENV_RECEIVED_ID = (byte)0x03;
    /**
     * Binary code for 'via' slot inside a ReceivedObject
     */
    byte ENV_RECEIVED_VIA = (byte)0x04;
    
    
    
        /*
         * Predefined ACL representations
         */
    /**
     * Binary code for syntactic representation of ACL in
     * bitefficient form.
     * @see <a href=http://www.fipa.org/specs/fipa00069/>FIPA Spec</a>
     */
    byte ENV_ACL_BITEFFCIENT = (byte) 0x10;
    /**
     * Binary code for syntactic representation of ACL in
     * String form.
     * @see <a href=http://www.fipa.org/specs/fipa00070/>FIPA Spec</a>
     */
    byte ENV_ACL_STRING = (byte) 0x11;
    /**
     * Binary code for syntactic representation of ACL in
     * XML form.
     * @see <a href=http://www.fipa.org/specs/fipa00071/>FIPA Spec</a>
     */
    byte ENV_ACL_XML = (byte) 0x12;
    /**
     * Binary code for syntactic representation of ACL in
     * any other from than Bitefficient, String, or XML.
     */
    byte ENV_ACL_USERDEFINED = (byte) 0x00;
    
    byte ENV_END_OF_STR = (byte) 0x00;
    byte ENV_END_OF_COLLECTION = (byte) 0x01;
    byte ENV_END_OF_ENVELOPE = (byte) 0x01;
    byte ENV_NULL_TERM_ANY = (byte)0x14;
    /**
     * EmptyLen16 (used for eg. JumboEnvelopes
     */
    byte[] EMPTY_LEN_16 = {(byte) 0x00, (byte) 0x00};
}
