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

import jade.lang.acl.*;
import java.io.IOException;
import java.io.OutputStream;
import cascom.fipa.util.BufferedOutputStream;
import cascom.fipa.util.ByteArray;

/**
 * OutputStream that writes fipa-bitefficient-std coded messages into stream.
 * @author     Heikki Helin, Mikko Laukkanen
 */
public class ACLOutputStream extends BufferedOutputStream {
    private ACLEncoder e;
    private ByteArray ba;
    
    /**
     * Initialise the ACL output stream with given OutputStream.
     *
     * @param o OutputStream to which message are written.
     */
    public ACLOutputStream(OutputStream o) {
        super(o);
        initialize(0);
    }
    /**
     * Initialise the ACL output stream with given OutputStream and
     * codetable size.
     *
     * @param o OutputStream to which message are written.
     * @param sz Size for the code table.
     */
    public ACLOutputStream(OutputStream o, int sz) {
        super(o);
        initialize(sz);
    }
    public EncoderCodetable getCodeTable() {
        return e.getCodeTable();
    }
    
    private void initialize(int sz) {
        e = new ACLEncoder(sz);
        ba = new ByteArray();
    }
    /**
     * Writes ACL message to output stream
     * @param m Message to be written
     */
    public void write(ACLMessage m) throws Exception {
        ba = e.encode(m);
        super.write(ba.get(),0,ba.length());
        super.flush();
    }
    /**
     * Writes ACL message output stream using specified coding.
     * @param m Message to be written
     * @param c Coding scheme (ACL_BITEFFICIENT_CODETABLE or
     *		ACL_BITEFFICIENT_NO_CODETABLE)
     */
    public void write(ACLMessage m, byte c) throws Exception {
        ba = e.encode(m, c);
        super.write(ba.get(),0,ba.length());
        super.flush();
    }
    private EncoderCodetable encoderCodetable;
    
    /**
     * Getter of the property <tt>encoderCodetable</tt>
     * @return  Returns the encoderCodetable.
     */
    public EncoderCodetable getEncoderCodetable() {
        return encoderCodetable;
    }
    
    /**
     * Setter of the property <tt>encoderCodetable</tt>
     * @param encoderCodetable  The encoderCodetable to set.
     */
    public void setEncoderCodetable(EncoderCodetable encoderCodetable) {
        this.encoderCodetable = encoderCodetable;
    }
}
