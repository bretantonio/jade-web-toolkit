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
import jade.lang.acl.*;
import jade.core.*;
import java.lang.Integer;
import cascom.fipa.acl.ACLOutputStream;

/**
 *  Example class for using bit-efficient ACL encoder.
 */
public class DummyEncoder {        
    public static void main(String[] args) {
        int c = 0;
        ACLMessage aclMessage[] = new ACLMessage[100];
                /*
                 * We are reading from standard input
                 */
        BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));
        String line = null;
                /*
                 * Initialize the ACL OutputStream
                 */
        ACLOutputStream os = null;
        if (args.length!=0) {
            os = new ACLOutputStream(System.out,
                    new Integer(args[0]).intValue());
        } else {
            os = new ACLOutputStream(System.out);
        }
        StringACLCodec sc =new StringACLCodec(in, null);
        
        try {
            while(true) {
                ACLMessage m = sc.decode();
                byte [] __o = new byte[1800];
                m.setContentObject(__o);
                System.err.println("INFO"+m.toString());
                os.write(m);
                ++c;
            }
        } catch (Exception e) {
            
            System.err.println("-- Error: "+e);
        }
                                /*
                                 * Here we assume that whole message is
                                 * given in one line; stupid assumption,
                                 * but better than nothing...
                                 */
                                /*
                                 * If we succesfully parsed the message,
                                 * then we can write that to stdout
                                 * using bit-efficient encoding.
                                 */
        System.err.println("INFO"+c+" message(s) written to stdout");
    }
}
