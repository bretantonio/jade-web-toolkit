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
import java.util.Date;

/**
 * BinDate implements Bitefficient representation of "DateTimeToken"
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BinDate extends BinRep implements ACLConstants {
    private static final byte T_POS = 8;
    private static final int MAX_DATE_LEN = 32;
    
    private static char plus = '+';
    private static char minus = '-';
    
    /**
     * Converts ASCII representation of Date to bit-efficient
     * representation.
     *
     * @param s String containing ASCII representation of date
     *
     * @return Bit-efficient representation of supplied date.
     */
    public byte[] toBin(String s) {
        byte[] b = new byte[MAX_DATE_LEN];
        byte d;
        int x = s.length(), j = 0;
        if (s.charAt(T_POS) != 'T' && s.charAt(T_POS) != 't')
            return null;
        
        /* First year, month, & day */
        for (int i = 0; i < 8; i+=2) {
            d = (byte)(encode(s.charAt(i)) << 4);
            d |= (encode(s.charAt(i+1)) & 0x0f);
            b[j++] = d;
        }
        
        /* Then Hours, Minutes, Seconds, and Milliseconds */
        for (int i = 9; i < 17; i+=2) {
            d = (byte)(encode(s.charAt(i)) << 4);
            d |= (encode(s.charAt(i+1)) & 0x0f);
            b[j++] = d;
        }
        b[j++] = (byte)(encode(s.charAt(17)) << 4);
        return b;
    }
    /**
     *
     */
    private char[] c = new char[ACL_DATE_LEN*2+2];
    
    /**
     * Converts bit-efficient Date to String.
     *
     * @param b Bit-efficient date
     * @return String containing ASCII representation of supplied date.
     */
    public String fromBin(byte[] b) {
        int i = 0, j = 0;
        for (i = 0; i < ACL_DATE_LEN; ++i) {
            if (j == T_POS) c[j++] = 'T';
            c[j] = (char)decode((b[i]>>4)&0x0f);
            c[j+1] = (char)decode(b[i]&0x0f);
            j += 2;
        }
        c[j]=0;
        return (new String(c).trim());
    }
    /**
     * Checks whether there's type designator in Date String
     * @param s String date to check
     * @return true if there's type designator present, false otherwise
     */
    public static boolean containsTypeDg(String s) {
        char a = s.charAt(s.length()-1);
        return ((a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z'));
    }
    
    /**
     * Calculate correct BinDateTimeToken identifier:<br>
     * 0x20: Absolute time				<br>
     * 0x21: Relative time (+)			<br>
     * 0x22: Relative time (-)			<br>
     * 0x20: Absolute time w/ TypeDesignator 	<br>
     * 0x21: Relative time (+) w/ TypeDesignator 	<br>
     * 0x22: Relative time (-) w/ TypeDesignator 	<br>
     */
    public static byte getDateFieldId(String s) {
        int tmp = 0;
        
        if (s.charAt(0) == plus) tmp = 1;
        else if (s.charAt(0) == minus) tmp = 2;
        tmp += containsTypeDg(s) ? 4 : tmp;
        return (byte)(ACL_ABS_DATE_FOLLOWS + tmp);
    }
}
