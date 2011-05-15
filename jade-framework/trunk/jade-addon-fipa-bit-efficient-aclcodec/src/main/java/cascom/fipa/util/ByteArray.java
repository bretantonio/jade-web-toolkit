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
/**
 * Dynamic byte array is an implementation of an array which
 * automatically grows whenever needed.
 *
 * @author Heikki Helin, Mikko Laukkanen, Ahti Syreeni
 */
public class ByteArray {
    /**
     * Data holder
     */
    private byte[] data;
    private int alloc;
    private int used;
    /** How much to increase the array in the case of overflow */
    private static final int grow_size = 50;
    /** Default size for the array */
    private static final int def_initsize = 100;
    /**
     * Initializes the ByteArray with size 100
     */
    public ByteArray() { initialize(def_initsize); }
        
    /**
     *  Initializes the ByteArray with given array
     */
    public ByteArray(byte[] array){
        data = array;
        alloc = array.length;
        used = array.length;
    }
        
    /**
     * Initialized the ByteArray with given size
     * @param init_size Initial size for the array.
     */
    public ByteArray(int init_size) { initialize(init_size); }
    
    /**
     * Clear this array
     */
    public void reset() { used = 0; }
    
    /**
     * Returns the lenght of this array
     *
     * @return the length of this array
     */
    public int length() { return (int) used; }
    
    /**
     * Returns this array
     *
     * @return content of this array as byte array
     */
    public byte[] get() {
        byte[] ret = new byte[used];
        System.arraycopy(data, 0, ret, 0, used);
        return ret;
    }
    
    
    /**
     * Returns the byte at specified index
     * @return Byte at specified index
     * @throw ArrayIndexOfBoundsException if the argument is less than 0 or
     *        more than current size of the byte array
     */
    public byte indexAt(int i){
        return data[i];
    }
    
    
    /**
     * Add a byte to this array.
     *
     * @param b byte to add
     * @return content of this array
     */
    public ByteArray add(byte b) {
        data[used++] = b;
        if (used == alloc) grow(grow_size);
        return this;
    }
    
    /**
     * Add a byte to this array in a specified position.
     *
     * @param b byte to add
     * @param pos position
     * @return content of this array
     */
    public ByteArray addToPos(byte b, int pos) {
        if (pos >= alloc) grow(pos+1);
        if (pos > used) used = pos+1;
        data[pos] = b;
        return this;
    }
    
    /**
     * Insert a whole byte array to given position. Nothing will be erased.
     * Position must be 0 or greater. Position can be greater than the
     * current end of the array.
     *
     * @param bytes Array of bytes to be inserted
     * @param pos Insert position.
     * @return content of this array
     *
     */
    public ByteArray insertArrayToPos(byte[] bytes, int pos){
        int used2 = used;
        if(pos >= used) {  // zero values will cause extra size
            grow(pos + bytes.length);
            used2 = pos + bytes.length;
        } else {   // no extra zero values
            if(bytes.length + used > alloc) {
                grow(used + bytes.length);
            }
            used2 = used + bytes.length;
        }
        
        //move the old data
        for(int i = used-1; i >= pos; i--){
            data[i + bytes.length] = data[i];
        }
        //insert new data
        System.arraycopy(bytes,0,data,pos,bytes.length);
        used = used2;
        return this;
    }
    
    
    /**
     * Add an array of bytes to this array
     *
     * @param b Byte array to add
     * @param len Lenght of byte array to add.
     * @return content of this array
     */
    public ByteArray add(byte[] b, int len) {
        if (used+len >= alloc) grow(len+grow_size);
        System.arraycopy(b,0,data,used,len);
        used += len;
        return this;
    }
    
    /**
     * Add a ByteArray to this array
     *
     * @param b ByteArray to add
     * @return content of this array
     */
    public ByteArray add(ByteArray b) { return add(b.get(), b.length()); }
    private void grow(int x) {
        alloc += x;
        byte [] n = new byte[alloc];
        System.arraycopy(data, 0, n, 0, used);
        data = n;
    }
    private void initialize(int sz) {
        data = new byte[sz];
        alloc = sz;
        used = 0;
    }
    
    /**
     * Grows this buffer up to new given size. With this
     * method is possible to grow the size of the 
     * undelying buffer to new size in advance so the
     * buffer will not be grown every time when size
     * exceeds the current buffer size. 
     *
     * This method should be used if the final size of 
     * byte array is known in advance.
     *
     * @author Ahti Syreeni - TeliaSonera
     */
    public void growToSize(int newSize){
        if(newSize > this.data.length){
            this.grow(newSize);
        }
    }
}
