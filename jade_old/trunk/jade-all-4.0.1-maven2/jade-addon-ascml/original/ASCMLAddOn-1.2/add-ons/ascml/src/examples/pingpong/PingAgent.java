/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package examples.pingpong;

import jade.core.Agent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.core.behaviours.CyclicBehaviour;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;


public class PingAgent
        extends Agent {
    
    public final static String PING = "Ping";
    public final static String QUIT = "Quit";
    
    protected AID oponentAID = null;
    protected Integer pingCount = new Integer(10);
    protected int delay = 0;
    protected boolean verbose = false;
    protected DFAgentDescription dfd;
    
    protected void setup() {
        try {
            // Can't get the parametervalue, no matter what
            Object[] args = this.getArguments();
            if ((args != null) && (args.length > 0)){
                for(int i=0; i < args.length; i++) {
                    if((args[i] != null) && (!((String)args[i]).trim().endsWith("="))) {
                        if(((String)args[i]).startsWith("pingCount=")) {
                            pingCount = Integer.parseInt(((String)args[i]).substring(((String)args[i]).indexOf("=")+1));
                        } else if(((String)args[i]).startsWith("delay=")) {
                            delay = Integer.parseInt(((String)args[i]).substring(((String)args[i]).indexOf("=")+1));
                        } else if(((String)args[i]).startsWith("verbose=")) {
                            verbose = (Integer.parseInt(((String)args[i]).substring(((String)args[i]).indexOf("=")+1)) > 0);
                        }
                    }
                }
                
            }
            // create the agent descrption of itself
            // why registering ???
            ServiceDescription sd = new ServiceDescription();
            sd.setType( "PingPong" );
            sd.setName( "PingPongServiceDescription" );
            dfd = new DFAgentDescription();
            dfd.setName( getAID() );
            dfd.addServices( sd );
            DFService.register( this, dfd );
            
            // notify the host that this agent has arrived at the party
            /*ACLMessage ping = new ACLMessage( ACLMessage.INFORM );
            ping.setContent( PingAgent.PING );
            ping.addReceiver( oponentAID );
            ping.setDefaultEnvelope();
            send( ping );
             */
            // add a Behaviour to process incoming messages
            System.out.println("Ping agent waiting for message from oponent");
            addBehaviour( new CyclicBehaviour( this ) {
                public void action() {
                    // listen if a greetings message arrives
                    ACLMessage msg = receive( MessageTemplate.MatchPerformative( ACLMessage.INFORM ) );
                    
                    if (msg != null) {
                        if (PingAgent.QUIT.equals( msg.getContent() )) {
                            quitGame();
                        } else if ( msg.getContent().startsWith( PongAgent.PONG )) {
                            //System.out.println("Ping <---" + msg.getContent().substring(5) + "--- Pong");
                            ping( msg );
                        } else {
                            System.out.println( "Ping received unexpected message: " + msg +"\n" + msg.getContent() );
                        }
                    } else {
                        // if no message is arrived, block the behaviour
                        block();
                    }
                }
            } );
        } catch (Exception e) {
            System.out.println( "Exception in PingAgent: " + e );
            e.printStackTrace();
        }
        
    }
    
    
    protected void quitGame() {
        System.out.println("Game over");
        try {
            DFService.deregister(this, dfd);
        } catch (FIPAException fe) {
            System.err.println("PingAgent failed to unregister from df");
        }
    
        doDelete();
    }
    
    protected void ping( ACLMessage msg ) {
        if(verbose) {
            System.out.println(PingAgent.PING);
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            System.err.println("Ping Agent interrupted");
        }
        ACLMessage ping = msg.createReply();
        if ( this.pingCount == 0) {
            ping.setContent( PingAgent.QUIT );
        } else {
            ping.setContent( PingAgent.PING + " " + pingCount.toString() );
            --this.pingCount;
        }
        send( ping );
        
    }
}