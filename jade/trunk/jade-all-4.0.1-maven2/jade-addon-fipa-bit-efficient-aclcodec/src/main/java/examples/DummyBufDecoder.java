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
package examples;

import java.io.*;
import java.util.*;
import jade.lang.acl.*;
import java.lang.Integer;
import cascom.fipa.acl.ACLDecoder;
import cascom.fipa.util.ByteArray;

/**
 *  Example class for using bit-efficient ACL codec.
 */
public class DummyBufDecoder {
    
    public static void main(String[] args) {
                /*
                 * Initialize Bit-efficient ACL input stream.
                 */
        ACLDecoder ad;
        ByteArray ba = new ByteArray();
        if (args.length!=0) {
            ad = new ACLDecoder(new Integer(args[0]).intValue());
        } else {
            ad = new ACLDecoder();
        }
        int c = 0;
        try {
            byte b = 0;
            while (b != -1) {
                b = (byte)System.in.read();
                if (b != -1)
                    ba.add(b);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        try {
            while (true) {
                                /*
                                 * Read a bit-efficiently coded message
                                 */
                ACLMessage m = ad.readMsg(ba.get());
                                /*
                                 * And dump it to stdout
                                 */
                System.out.println(m.toString());
                ++c;
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("INFO"+c+ " message(s) parsed");
    }
}
