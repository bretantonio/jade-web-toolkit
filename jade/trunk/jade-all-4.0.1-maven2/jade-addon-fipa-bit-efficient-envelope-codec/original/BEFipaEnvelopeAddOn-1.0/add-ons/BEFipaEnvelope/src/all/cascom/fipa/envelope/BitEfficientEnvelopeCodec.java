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
import jade.domain.FIPAAgentManagement.Envelope;
import cascom.fipa.acl.BEParseException;
import java.lang.Exception;
import cascom.fipa.envelope.EnvelopeCodec;
import cascom.fipa.util.ByteArray;

/***
 * Main class for bit-efficient encoder and decoder. This class can be
 * used to decode and encode ACL messages to bit-efficient encoding.
 *
 * @see <a href=http://www.fipa.org/specs/fipa00088/>FIPA Agent
 * 	Message Transport Envelope Representation in Bit Efficient
 * 	Specification</a>
 *
 * This class is thread safe as the decode and encode methods are internally 
 * synchronized. More effective solutions can be considered in the future.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class BitEfficientEnvelopeCodec implements EnvelopeCodec {
    private static final String FIPA_NAME="fipa.acl.rep.bitefficient.std";
    EnvelopeDecoder dec = new EnvelopeDecoder();
    EnvelopeEncoder enc = new EnvelopeEncoder();
    
    
    /**
     * Returns FIPA name of this codec (fipa.acl.rep.bitefficient.std).
     */
    public String getName(){
        return FIPA_NAME;
    }
    
   /**
     * Decodes given bit-efficient envelope to envelope object. Thread safe, 
    *  internally synchronized.
     * @return Envelope the envelope object.
     * @param bytes the bit-efficient envelope
     * @throw Exception if such happens during decoding
     */
    public Envelope decode(byte[] bytes) throws Exception {
        try {
            synchronized(this.dec){
                return this.dec.getEnvelope(bytes);
            }
        } catch (BEParseException e){
            //e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
    
  /**
     * Encodes given envelope to bit-efficient. Thread safe, internally
    *  synchronized.
     * @return the envelope bytes.
     * @param envelope Envelope object.
     * @throw Exception if such happens during decoding
     */    
    public byte[] encode(Envelope envelope) throws Exception{
        try {
            synchronized(this.enc){
                return this.enc.encode(envelope).get();
            }
        } catch (BEParseException e){
            //e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
