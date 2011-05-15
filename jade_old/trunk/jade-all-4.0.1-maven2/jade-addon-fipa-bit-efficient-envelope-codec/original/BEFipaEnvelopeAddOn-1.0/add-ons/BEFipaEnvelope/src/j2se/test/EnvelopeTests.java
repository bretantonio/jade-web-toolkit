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
package test;

import cascom.fipa.util.ByteArray;
import cascom.fipa.acl.ACLConstants;
import cascom.fipa.acl.BinNumber;
import cascom.fipa.acl.BinDate;
import cascom.fipa.acl.BEParseException;


import java.io.*;
import java.util.*;
import java.util.Date;
import java.util.Calendar;
import jade.util.leap.List;
import jade.util.leap.Properties;

import jade.lang.acl.*;
import jade.core.AID;
import jade.lang.acl.ISO8601;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;
import test.*;
import cascom.fipa.envelope.EnvelopeEncoder;
import cascom.fipa.envelope.EnvelopeDecoder;

/**
 * Example of endcoding and decoding bit-efficint envelopes. This example
 * encodes a simple Envelope and decodes it back. The original envelope is
 * written to file env-original.txt and the decoded version to env-decoded.txt.
 * The content of file should be same.
 *
 * @author Ahti Syreeni - TeliaSonera
 */

public class EnvelopeTests {
    /**
     * Encodes given envelope and decodes back. Writes
     * the original, be-efficient encoded and decoded envelopes to separate files.
     */
    public static void main(String args[]) {
        TestEnvelopes envs =  new TestEnvelopes();
        EnvelopeTests ets = new EnvelopeTests();
        
        if(ets.doDumpToFileTest(envs.getSimpleEnvelopeWithoutRO(), "test-withoutRO-orig.txt", "test-withoutRO-BE.txt", "test-withoutRO-decoded.txt") &&
                ets.doDumpToFileTest(envs.getSimpleEnvelopeWithRO(), "test-withRO-orig.txt", "test-withRO-BE.txt", "test-withRO-decoded.txt") &&
                ets.doDumpToFileTest(envs.getStampedEnvelope(), "test-stamped-orig.txt", "test-stamped-BE.txt", "test-stamped-decoded.txt") &&
                ets.doDumpToFileTest(envs.getPropEnvelope(), "test-props-orig.txt", "test-props-BE.txt", "test-props-decoded.txt") &&
                ets.doDumpToFileTest(envs.getComplexEnvelope(), "test-complex-orig.txt", "test-complex-BE.txt", "test-complex-decoded.txt") &&
                ets.doDumpToFileTest(envs.getLongEnvelope(), "test-jumbo-orig.txt", "test-jumbo-BE.txt", "test-jumbo-decoded.txt")
                ){
            System.out.println("Tests succeeded. You can check the output from textfiles.");
        } else {
            System.out.println("Tests not succeeded");
        }
    }
    
    
    private boolean doDumpToFileTest(Envelope e, String originalFile, String beCodedFile, String decodedFile){
        EnvelopeEncoder enc = new EnvelopeEncoder();
        ByteArray ba = null;
        try {
            ba = enc.encode(e);
        } catch (Exception ex) {
            System.err.println("Error while encoding envelope: "+ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        EnvelopeDecoder dec = new EnvelopeDecoder();
        Envelope decoded = null;
        
        byte[] temp = ba.get();
        try {
            decoded = dec.getEnvelope(temp);
        } catch (Exception ex) {
            System.err.println("Error while decoding envelope: "+ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        File outputFile = new File(originalFile);
        File outputFile2 = new File(decodedFile);
        File outputFile3 = new File(beCodedFile);
        writeEnv(e,outputFile);
        writeEnv(decoded,outputFile2);
        this.writeByteArrayToFile(temp, outputFile3);
        
        return true;
    }
    
    private void writeEnv(Envelope env, File file){
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(TestEnvelopes.envAsString(env));
            fw.flush();
            fw.close();
        } catch (Exception e){
            System.out.println("printing "+file.getName()+" was unsucessfull");
        }
    }
    private boolean writeByteArrayToFile(byte[] bytes, File file){
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.write(bytes);
            ps.flush();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error writing to file "+file.getName() + "  ,reason: "+e.getMessage());
            return false;
        }
        return true;
    }
}