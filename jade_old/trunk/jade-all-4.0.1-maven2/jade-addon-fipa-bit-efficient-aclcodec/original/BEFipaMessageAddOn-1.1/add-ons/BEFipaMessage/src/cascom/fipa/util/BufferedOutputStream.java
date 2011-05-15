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
package cascom.fipa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is a simple buffer for java.io.OutputStream. Behaviour of
 * this class should corrrespond to java.io.BufferedOutputStream thus the
 * internal implementation is probably much different.
 *
 * This class is compatible with J2ME.
 * 
 * @author Ahti Syreeni - TeliaSonera
 */
public class BufferedOutputStream extends OutputStream{
    private byte[] buffer = new byte[2048];
    private int pos = 0;
    private OutputStream os;
    
    private BufferedOutputStream(){};
    
    
    /**
     *  Constructs new BufferedOutputStream for given OutputStream
     *  @param os OutputStream to be used.
     *
     */
    public BufferedOutputStream(OutputStream os) {
       this.os = os;
       this.pos = 0;
    }
    
    /*
     *  Clears the buffer.
     */
    private void emptyBuffer() throws IOException {
        if(this.pos > 0){
            this.os.write(this.buffer,0, this.pos);
            this.pos = 0;
        }
    }
    
    /*
     *  Returns the number of bytes left in buffer.
     */
    private int leftInBuffer(){
        return this.buffer.length - this.pos;
    }
    
    
    /**
     *  Writes the given array to buffer. If the end of given array is reached
     *  before the given len-parameter, the copying will stop but no Exception is
     *  thrown.
     *
     *  @param bytes The array to be written
     *  @param off Offset for copying bytes for given array.
     *  @param len Number of bytes to be written from the offset.
     *  @throw IOException If error occures while hanling the OutputStream.
     */
    public synchronized void write(byte[] bytes, int off, int len) throws IOException {
       //this.os.write(bytes,off, len);
       int toBeWritten = len; 
       // read to the end of the array if the array is smaller than the specified length
       if(off + len > bytes.length) {
           toBeWritten = bytes.length - off;
       }
       
       if(toBeWritten > this.leftInBuffer()){
           this.emptyBuffer();           
           if(toBeWritten > this.leftInBuffer()){
               // buffer not big enough, read directly from stream
               this.os.write(bytes, off, toBeWritten);
           } else {
               System.arraycopy(bytes,off,this.buffer, this.pos,toBeWritten);
               this.pos += toBeWritten;               
           }
       } else if(toBeWritten > 0){
           System.arraycopy(bytes,off, this.buffer, this.pos,toBeWritten);
           this.pos += toBeWritten;
       }
    }
   
    /**
     * Writes int to stream.     
     */    
    public synchronized void write(int i) throws IOException {
        this.os.write(i);
        if(this.leftInBuffer() < 1){
            this.emptyBuffer();
        }
        this.buffer[this.pos] = (byte)i;
        this.pos++;
    }
    
    /**
     *  Empty the buffer. This should be called after all data is written to avoid
     *  lost of data.
     *  @throw IOException If error occures while handling the OutputStream. 
     */
    public synchronized void flush() throws IOException {
        this.emptyBuffer();
        this.os.flush();
    }

    /**
     *  Close this OutputStream.
     */
    public synchronized void close() throws IOException {
        try {
        this.flush();
        } catch (Exception e){}
        super.close();
    }
}