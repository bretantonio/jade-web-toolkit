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
/**
 * Conversion between ASCII numbers and bit-efficient numbers.
 * The coding is based on following rules:
 * <tt><br>
 * '0' = 0001 (0x01) <br>
 * '1' = 0010 (0x02) <br>
 * '2' = 0011 (0x03) <br>
 * '3' = 0100 (0x04) <br>
 * '4' = 0101 (0x05) <br>
 * '5' = 0110 (0x06) <br>
 * '6' = 0111 (0x07) <br>
 * '7' = 1000 (0x08) <br>
 * '8' = 1001 (0x09) <br>
 * '9' = 1010 (0x0a) <br>
 * '+' = 1100 (0x0c) <br>
 * 'e' = 1101 (0x0d) <br>
 * '-' = 1110 (0x0e) <br>
 * '.' = 1111 (0x0f) <br>
 * </tt>
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BinRep {
    final static char[] ncodes = {
        ' ', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', '?', '+', 'e', '-', '.'
    };
    /**
     * Converts one ASCII number to bit-efficient representation.
     * The ASCII number to convert must be such number that conversion
     * can be done (check the list above). If the ASCII number is not
     * in given range, the result of this method is undefined (i.e.,
     * no validity checks are done).
     *
     * @param i ASCII number to convert
     * @return Corresponding bit-efficient number
     */
    protected static byte encode(int i) {
        return (byte)((i != 'e' && i != 'E') ? (i+1)&0x0f : 0x0d);
    }
    /**
     * Converts bit-efficient number to ASCII. The number to convert
     * must be valid bit-efficient number (i.e., number between
     * 0x00-0x0f). No validity checks are done.
     *
     * @param i bit-efficient number to convert
     * @return Corresponding ASCII number
     */
    protected static byte decode(int i) {
        return (byte)ncodes[i];
    }
}
