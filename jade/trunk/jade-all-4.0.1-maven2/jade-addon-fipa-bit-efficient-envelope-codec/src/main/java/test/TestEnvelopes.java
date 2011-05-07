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
import java.util.Date;
import java.util.Calendar;
import jade.util.leap.List;
import jade.util.leap.Iterator;

import jade.lang.acl.*;
import jade.core.AID;
import jade.lang.acl.ISO8601;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ReceivedObject;

/**
 * Class containing test ACL message envelopes. This is a 
 * test class for testing purposes.
 * 
 * @author Ahti Syreeni - TeliaSonera
 */

public class TestEnvelopes {
    
    private String receiverAidAddress ="reveiver@131.177.37.133:1099/JADE";
    private AID receiverAid = new AID(receiverAidAddress, true);
    
    private String toAidAddress ="to@131.177.37.133:1099/JADE";
    private AID toAid = new AID(toAidAddress, true);

    private String fromAidAddress ="from@131.177.37.133:1099/JADE";
    private AID fromAid = new AID(fromAidAddress, true);
    
    private String roBy = "http://foo.csedkjf.com/by";
    private String roFrom = "http://foo.csedkjf.com/from";
    private String roID = "139";
    private String roVia = "http://foo.csedkjf.com/via";
    
    /** 
     *  Constructs the SimpleEnvelope.
     */
    public TestEnvelopes() {
    }
    
    /**
     *  Returns simple envelope without ReceivingObject for testing purposes
     *
     *  @author Ahti Syreeni
     */    
    public Envelope getSimpleEnvelopeWithoutRO(){
        Envelope env = new Envelope();
        env.addIntendedReceiver(this.receiverAid);
        env.addTo(this.toAid);
	env.setAclRepresentation("fipa.acl.rep.xml.std");
        env.setComments("Comment");
        env.setDate(Calendar.getInstance().getTime());
        env.setFrom(this.fromAid);
        env.setPayloadEncoding("US-ASCII");
        return env;
    }
    
    /**
     *
     *
     */
    public Envelope getSimpleEnvelopeWithRO(){                        
		Envelope e = new Envelope();
		/*
		 * TO
		 */
		AID to_resolver = new AID("resolver@bar.com",true);
		to_resolver.addAddresses("http://bar.com/acc1");
		to_resolver.addAddresses("http://bar.com/acc2");
		to_resolver.addAddresses("http://bar.com/acc3");
		AID to = new AID("receiver@foo.com", true);
		to.addAddresses("http://foo.com/acc");
		to.addResolvers(to_resolver);
		e.addTo(to);
		/*
		 * FROM
		 */
		AID from = new AID("sender@bar.com", true);
		from.addAddresses("http://bar.com/acc");
		AID from_resolver = new AID("resolver@foobar.com", true);
		from_resolver.addAddresses("http://foobar.com/acc1");
		from_resolver.addAddresses("http://foobar.com/acc2");
		from_resolver.addAddresses("http://foobar.com/acc3");
		from.addResolvers(from_resolver);
		e.setFrom(from);
		/*
		 * COMMENTS
		 */
		e.setComments("comment");
		/*
		 * ACL-REP
		 */
		e.setAclRepresentation("fipa.acl.rep.xml.std");
		/*
		 * Payload-encoding
		 */
		e.setPayloadEncoding("US-ASCII");
		/*
		 * DATE
		 */
		Date dd = Calendar.getInstance().getTime();
		e.setDate(dd);



		AID idr = new AID("intendedreceiver@bar.com", true);
		idr.addAddresses("http://foobar.com/acc1");
		idr.addAddresses("http://foobar.com/acc2");
		idr.addAddresses("http://foobar.com/acc3");


		AID idr_r1 = new AID("resolver@foobar.com", true);
		idr_r1.addAddresses("http://foobar.com/acc1");
		idr_r1.addAddresses("http://foobar.com/acc2");
		idr_r1.addAddresses("http://foobar.com/acc3");
		AID idr_r2 = new AID("resolver@foobar.com", true);
		idr_r2.addAddresses("http://foobar.com/acc1");
		idr_r2.addAddresses("http://foobar.com/acc2");
		idr_r2.addAddresses("http://foobar.com/acc3");
		idr_r1.addResolvers(idr_r2);
		idr.addResolvers(idr_r1);
		e.addIntendedReceiver(idr);

                /*
		 * Received 
		 */
                
		ReceivedObject ro = new ReceivedObject();
		ro.setBy("http://foo.com/acc");
		ro.setFrom("http://foobar.com/acc");
		ro.setDate(dd);
		ro.setId("123456789");
		ro.setVia("http://bar.com/acc");
		e.setReceived(ro);       
                return e;        
    }
    
    
    /**
     * Returns envelope with 20 stamps
     */
    public Envelope getStampedEnvelope(){ 
        Envelope e = this.getSimpleEnvelopeWithRO();

        ReceivedObject ro = new ReceivedObject();
        ro.setBy("http://foo.com/acc");
        ro.setFrom("http://foobar.com/acc");
        ro.setDate(Calendar.getInstance().getTime());
        ro.setId("123456789");
        ro.setVia("http://bar.com/acc");

        for(int i=0; i < 20; i++){
           e.addStamp(ro);            
        }
        return e;
    }
    
    /**
     *  Returns a envelope with very, very long comment.
     *  This will cause the envelope to be encoded as JumboEnvelope.
     */
    public Envelope getLongEnvelope(){
        Envelope e = this.getSimpleEnvelopeWithRO();
        StringBuffer sb = new StringBuffer(2000);
        for(int i=0; i < (65550 / 5); i++){
            sb.append(" "+i);
        }
        e.setComments(sb.toString());
        return e;
    }
    
    /**
     *  Return envelope with user defined properties
     */
    public Envelope getPropEnvelope(){
        Envelope e = this.getSimpleEnvelopeWithoutRO();
        e.addProperties(new Property("X-SONERA-PROP1",(Object)"value1"));
        e.addProperties(new Property("X-SONERA-PROP2",(Object)"value2"));
        e.addProperties(new Property("X-SONERA-PROPinvalid",(Object)new Property()));
        return e;
    }

    
    /**
     *  Returns envelope with long properties, user defined properties and 
     *  stamps.
     */
    public Envelope getComplexEnvelope(){
        // jumbo envelope
        Envelope e = this.getLongEnvelope();
        // some user defined properties
        e.addProperties(new Property("X-SONERA-PROP1",(Object)"Very nice value 1"));
        e.addProperties(new Property("X-SONERA-PROP2",(Object)"Even better value 2"));
        
        // some stamps
        ReceivedObject ro = new ReceivedObject();
        ro.setBy("http://foo.com/accstart");
        ro.setFrom("http://foobar.com/from");
        ro.setDate(Calendar.getInstance().getTime());
        ro.setId("123456789g");
        ro.setVia("http://bar.com/via");
        for(int i=0; i < 10; i++){
           e.addStamp(ro);            
        }
        
        return e;
    }

    /*
     *  Prints the envelope as string
     */
    public static String envAsString(Envelope env){
        String out = new String("(Envelope ");
	Iterator it = env.getAllTo();
	if (it.hasNext()) {
	    out += "\n :to (sequence ";
            Iterator it2 = it;
	    while (it2.hasNext()){ 
		out = out+" "+it2.next().toString();
            }
	    out = out + ") ";
	}
	if (env.getFrom() != null) out += "\n :from " + env.getFrom().toString();
	if (env.getAclRepresentation() != null) out += "\n :acl-representation " + env.getAclRepresentation(); 
	if (env.getComments() != null) out += "\n :comments " + env.getComments(); 
	if (env.getPayloadEncoding() != null) out += "\n :payload-encoding " + env.getPayloadEncoding();
	if (env.getPayloadLength() != null) out += "\n :payload-length " + env.getPayloadLength().toString(); 
	if (env.getDate() != null)  out += "\n :date " + env.getDate().toString();
	it = env.getAllIntendedReceiver();
	if (it.hasNext()) {
	    out += "\n :intended-receiver (sequence ";
	    while(it.hasNext()){                
		out += " "+ it.next().toString();
            }
	    out += ") ";
	}        
	ReceivedObject[] ro = env.getStamps();

        if (ro.length > 0 ) {
	    out += "\n :received-object (sequence ";
	    for (int j=0; j<ro.length; j++) {
	    	if (ro[j] != null) {
		    out += " "+ ro[j].toString(); 
	    	}
	    }
	    out = out + ") ";
	}

        if (env.getAllProperties().hasNext()) {
	    out += "\n :properties (set";
            it = env.getAllProperties();
	    while(it.hasNext()) {
                Object next = it.next();
                if(next.getClass().isInstance((Object)" ")) {
                    out += (String)next;
                } else {
                    out += next.toString();
                }
	    }
	    out = out + ")";
	}        
        return out;
    }
}
