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

// for testing only
//import  java.io.FileInputStream;
//import java.io.FileOutputStream;

/**
 * Simple buffering for java.io.InputStream. Class provides same
 * methods but using internal buffer. This class is not same
 * as java.io.BufferedInputStream because this class offers
 * methods to set end of stream. This means that buffer will
 * not be tried to filled over that limit unless user calls
 * read() -method when the limit have been reached. This avoids
 * blocking and situation when there is data in the buffer but
 * it cannot be returned because the method that fills the buffer
 * is blocked waiting for end of stream. This functionality is
 * needed for streaming type of inputstreams, e.g. for persistent
 * HTTP connection, where one HTTP message end is end of stream but
 * the actual end of stream is not received because the connection is
 * not closed after every message.
 *
 * This class does not use external functions outside java.io and is
 * compatible with J2ME.
 *
 * @author Ahti Syreeni -TeliaSonera
 *
 */
public class BufferedInputStream extends InputStream {
    private InputStream is;
    private final int BUFFER_SIZE = 10;
    byte[] buffer  =  new byte[this.BUFFER_SIZE];
    int pos = 0;
    int last = -1;
    boolean endOfStream = false;
    private int nextEnd = -1;
    private BufferedInputStream(){}
    
    /**
     *  Constructs new BufferedInputSteam for given InputStream
     *  @param in The InputStream to be used.
     */
    public BufferedInputStream(InputStream in) {
        this.is = in;
    }
    
    /*
     * Sets the next virtual end of stream, it will be
     * after specified number of bytes is being read from
     * underlyieing input stream. BufferedInputStream will not
     * try to read to buffer after that limit, unless read() -is called
     * when the limit have been reached. After that, read()-method will
     * read only one byte at time unless user sets the next
     * end of stream with this method.
     */
    public synchronized void setNextEOS(int value)  {
        this.nextEnd = value;
    }
    
    /**
     * Returns number of bytes left to the next virtual end of stream. If the
     * user has not set the next virtual end of stream with setNextEOS-method,
     * -1 will be returned.
     */
    public int bytesLeftToEOS(){
        return this.nextEnd;
    }
    
    
    /*
     * Fills the buffer.
     */
    private boolean fillBuffer() throws IOException {
        //System.err.println("Fillbuffer");
        int wasRead = 0;
        
        //System.err.println(this.nextEnd);
        if(this.nextEnd == -1){
            wasRead = this.is.read(this.buffer,0, this.buffer.length);
        } else {
            if(this.nextEnd == 0){
                // if we have reached the virtual end of stream,
                // the next read() is allowed to block
                wasRead = this.is.read(this.buffer,0, 1);
            } else if(this.nextEnd > this.buffer.length){
                wasRead = this.is.read(this.buffer,0, this.buffer.length);
                this.nextEnd -= wasRead;
            } else {
                wasRead = this.is.read(this.buffer,0, this.nextEnd);
                this.nextEnd -= wasRead;
            }
        }
        
        
        if(wasRead > 0) {
            this.last = wasRead-1;
            this.pos = 0;
            return true;
        } else {
            this.last = -1;
            this.pos = 0;
            return false;
        }
    }
    
    /*
     * Returns the number of bytes still available in buffer
     */
    private int leftInBuffer(){
        return this.last - this.pos + 1;
    }
    
    
    /**
     * Sets delimiter character for filling the buffer. If the
     * character is set, then when filling the buffer, the reading is
     * stopped when the character is encountered even if the buffer is
     * not fully filled. Thus, this method can be used when the stream is
     * not closed after each transmission and transmissions are separated
     * with the given character. This way only one transmission, at most, is
     * read to the buffer and read()-method will not block for filling the buffer if
     * one transmission is available.
     */
    
    
    /**
     *  Reads an int from buffer. Returns -1 if there are no more left.
     *  @return int The next byte as int
     *  @throw IOException If reading is failed for unexpected reason.
     */
    public synchronized int read() throws IOException {
        //return this.is.read();
        if((this.leftInBuffer() < 1) && !this.fillBuffer()){
            return -1;
        }
        this.pos++;
        //System.out.print((char)this.buffer[this.pos - 1]);
        return this.buffer[this.pos - 1]& 0xff;
    }
    
    /**
     *  Reads the bytes to the given array. Returns number of bytes read.
     *  @param array The array to be read in
     *  @param off The offset for array
     *  @param len Number of bytes to be read.
     *  @throw IOException If reading fails for unexpected reason
     *  @throw IndexOutOfBoundsException If the array with offset
     *         is too small to contain requested bytes.
     */
    public synchronized int read(byte[] array, int off, int len) throws IOException{
        //return this.is.read(array,off,len);
        if(array.length < off + len) {
            throw new IndexOutOfBoundsException();
        }
        
        int avail = this.leftInBuffer();
        if(len == 0){
            return 0;
        }
        if(avail == 0){
            if(this.fillBuffer()){
                avail = this.leftInBuffer();
            } else {
                return -1;
            }
        }
        
        int readSoFar = 0;
        if(len <= avail){
            // all requested bytes available at the buffer
            System.arraycopy(this.buffer,this.pos,array,off,len);
            this.pos += len;
            return len;
        } else {
            // prepare to fill buffer, copy remaining bytes from buffer first
            try {
                System.arraycopy(this.buffer, this.pos, array, off, avail);
                this.pos += avail;
                readSoFar = avail;
            } catch (Exception e){
                System.out.println("Error, in arraycopy: pos:"+this.pos+" off:"+off+" avail:"+avail+" last:"+this.last);
                e.printStackTrace();
                return -1;
            }
        }
        // need to read more?
        if(readSoFar < len){
            readSoFar += this.is.read(array,off + readSoFar, (len-readSoFar));
            return readSoFar;
        }
        return readSoFar;
    }
    /*
    public static void main(String[] args){
        FileInputStream inputFile = null;
        FileInputStream inputFile2 = null;
        //FileOutputStream outputFile = null;
        try {
            inputFile = new FileInputStream(args[0]);
            inputFile2 = new FileInputStream(args[0]);
        } catch (Exception e){
            System.out.println("File not found...");
            return;
        }
     
        BufferedInputStream input = new BufferedInputStream(inputFile);
        //BufferedOutputStream output = new BufferedOutputStream(outputFile);
        //byte[] tempArray = new byte[5000];
        int read = 0;
        input.setNextEOS(2046);
        try {
            do {
                read = input.read();
                System.out.print((char)read);
                if(read != inputFile2.read()){
                    System.err.println("No match!");
                    break;
                }
                //read = input.read(tempArray);
                //output.write(tempArray, 0, read);
            } while(read > 0);
            //output.flush();
            //outputFile.close();
            inputFile.close();
            inputFile2.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
     */
}