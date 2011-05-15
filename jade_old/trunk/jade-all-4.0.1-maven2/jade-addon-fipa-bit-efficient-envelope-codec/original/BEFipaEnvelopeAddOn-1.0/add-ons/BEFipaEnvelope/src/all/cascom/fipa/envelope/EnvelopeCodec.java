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
import java.lang.Exception;

/**
 * Interface for all codecs intended for coding the message envelope
 * of FIPA ACL message. This should be removed to more general 
 * package in the future.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public interface EnvelopeCodec {
    /**
     * Decodes given bit-efficient envelope to envelope object.
     * @return Envelope the envelope object.
     * @param bytes the bit-efficient envelope
     * @throw Exception if such happens during decoding
     */
    public Envelope decode(byte[] bytes) throws Exception;

    /**
     * Encodes given envelope to bit-efficient.
     * @return the envelope bytes.
     * @param envelope Envelope object.
     * @throw Exception if such happens during decoding
     */
    public byte[] encode(Envelope envelope) throws Exception;
    
    /**
     * Returns the FIPA -name of the envelope codec, the
     * representation it uses.
     */
    public String getName();
}
