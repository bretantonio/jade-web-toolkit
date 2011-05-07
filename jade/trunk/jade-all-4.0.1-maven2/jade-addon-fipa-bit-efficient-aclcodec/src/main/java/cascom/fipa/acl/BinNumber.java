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
import java.lang.Integer;
import java.lang.Float;

/**
 * Bitefficient number. Convert numbers to bit-efficient representation and
 * vice versa.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BinNumber extends BinRep {
    private static final byte TT_INT = (byte)0x00;
    private static final byte TT_FLOAT = (byte)0x01;
    private Integer iNumber;
    private Float fNumber;
    private ByteArray ba = new ByteArray(32);
    byte tag;
    
    public BinNumber() {}
    /**
     * Initialize BinNumber from Integer
     */
    public BinNumber(Integer i) {
        iNumber = i;
        tag = TT_INT;
    }
    
    /**
     * Initialize BinNumber from Float
     */
    public BinNumber(Float f) {
        fNumber = f;
        tag = TT_FLOAT;
    }
    
    /**
     * Initialize BinNumber from encoded byte array
     */
    public BinNumber(byte[] b) {
        String s = fromBin(b);
        tag = TT_INT;
        for (int i = 0; i < s.length() && tag == TT_INT; ++i) {
            if (s.charAt(i) == 'e' || s.charAt(i) == '.')
                tag = TT_FLOAT;
        }
        if (tag == TT_INT) iNumber = new Integer(Integer.parseInt(s.trim()));
        else
            try {
                fNumber = new Float(Float.parseFloat(s));
            } catch (NumberFormatException e) {
                System.out.println("-- ERROR: could not parse string "+s+" to float in class sonera.fipa.acl.BinNumber");
            }
    }
    
    
    public Object value() {
        if(tag==TT_INT){
            return (Object)iNumber;
        } else {
            //System.err.println("Error: floats not implemented!");
            return (Object)fNumber;
        }
    }
    
    
    /**
     * Converts String representation to ByteArray
     */
    public ByteArray toBin(ByteArray b) {
        int x = b.length();
        byte d;
        byte[] bx = b.get();
        ba.reset();
        for (int i = 0; i < x; i+=2) {
            d = (byte) (encode(bx[i]) << 4);
            if ((i+1)<x) d |= (encode(bx[i+1])&0x0f);
            else d |= 0x00;
            ba.add(d);
        }
        if ((x % 2) == 0) { // Even, additional 0x00 to the end.
            ba.add((byte) 0x00);
        }
        return ba;
    }
    
    /**
     * Converts String representation to ByteArray
     */
    public ByteArray toBin(String s) {
        byte[] bx = s.getBytes();
        int x =  bx.length;
        byte d;
        ByteArray ba = new ByteArray(bx.length);
        for (int i = 0; i < x; i+=2) {
            d = (byte) (encode(bx[i]) << 4);
            if ((i+1)<x) d |= (encode(bx[i+1])&0x0f);
            else d |= 0x00;
            ba.add(d);
        }
        if ((x % 2) == 0) { // Even, additional 0x00 to the end.
            ba.add((byte) 0x00);
        }
        return ba;
            /*
                int x = s.length(), j = 0;
                byte d;
                ba.reset();
                for (int i = 0; i < x; i+=2) {
                        d = (byte)(encode(s.charAt(i)) << 4);
                        if ((i+1) < x) {
                                d |= (encode(s.charAt(i+1)) & 0x0f);
                        } else d |= 0x00;
                        ba.add(d);
                }
                if ((x % 2) == 0) { // Even, additional 0x00 to the end.
                        ba.add ((byte) 0x00);
                }
                return ba;
             */
    }
        
    public String fromBin(byte[] b) {
        char[] c = new char[64];
        int i = 0, j = 0;
        while((b[i]&0x0f) != 0x00 ) {
            c[j] = (char)decode((b[i]>>4)&0x0f);
            c[j+1] = (char)decode(b[i]&0x0f);
            j += 2;
            ++i;
        }
        if (b[i]!=0x00) c[j] = (char)decode((b[i]>>4)&0x0f);
        return (new String(c).trim());
    }
        
    public ByteArray fromBin(ByteArray bb) {
        ba.reset();
        byte b[] = bb.get();
        int i = 0;
        while((b[i]&0x0f) != 0x00) {
            ba.add(decode((b[i]>>4)&0x0f));
            ba.add(decode((b[i]&0x0f)));
            ++i;
        }
        if (b[i]!=0x00) {
            ba.add(decode((b[i]>>4)&0x0f));
        }
        return ba;
    }
}
