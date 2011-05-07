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
import cascom.fipa.acl.ACLInputStream;

/**
 *  Example class for using bit-efficient ACL decoder.
 */
public class DummyDecoder {        
    public static void main(String[] args) {
                /*
                 * Initialize Bit-efficient ACL input stream.
                 */
        ACLInputStream in = null;
        if (args.length!=0) {
            in = new ACLInputStream(System.in, new Integer(args[0]).intValue());
        } else {
            in = new ACLInputStream(System.in);
        }
        int c = 0;
        try {
            while (true) {
                                /*
                                 * Read a bit-efficiently coded message
                                 */
                ACLMessage m = in.readMsg();
                                /*
                                 * And dump it to stdout
                                 */
                System.out.println(m.toString());
                ++c;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        System.err.println("INFO"+c+ " message(s) parsed");
    }
}
