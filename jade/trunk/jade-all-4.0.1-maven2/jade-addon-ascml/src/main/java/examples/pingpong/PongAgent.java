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


/*
 * $Id: PongAgent.java,v 1.13 2005/08/24 08:28:34 medha Exp $
 *
 */

package examples.pingpong;

import jade.core.Agent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.DFService;

public class PongAgent extends Agent {
    
    private class InitialPongBehaviour extends OneShotBehaviour {
        
        InitialPongBehaviour( Agent a ) {
            super(a);
        }
        
        public void action() {
            // We have an oponent, let's pong him... whatever
            ACLMessage pong = new ACLMessage( ACLMessage.INFORM );
            pong.setContent( PongAgent.PONG + "GO");
            pong.addReceiver( oponentAID );
            pong.setDefaultEnvelope();
            pong.setConversationId("PingPongGame");
            send( pong );
        }
    }
    
    private class PongBehaviour extends CyclicBehaviour {
        
        PongBehaviour( Agent a ) {
            super(a);
        }
        public void action() {
            ACLMessage msg = receive( MessageTemplate.MatchPerformative( ACLMessage.INFORM ) );
            
            if (msg != null) {
                if (PingAgent.QUIT.equals( msg.getContent() )) {
                    quitGame(msg);
                } else if(msg.getContent().startsWith(PingAgent.PING))  {
                    //System.out.println("Ping ---" + msg.getContent().substring(5) + "---> Pong");
                    pong( msg );
                } else {
                    System.out.println( "Pong received unexpected message: " + msg );
                }
            } else {
                // if no message is arrived, block the behaviour
                block();
            }
        }
    }
    
    public final static String PONG = "Pong";
    public final static String QUIT = "Quit";
    
    protected AID oponentAID = null;
    protected AID dfName = null;
    protected int delay = 0;
    protected boolean verbose = false;
    
    protected void setup() {
        try {
            Object[] args = this.getArguments();
            if ((args != null) && (args.length > 0)){
                for(int i=0; i < args.length; i++) {
                    if((args[i] != null) && (!((String)args[i]).trim().endsWith("="))) {
                        if(((String)args[i]).trim().startsWith("df=")) {
                            dfName = new AID(((String)args[i]).substring(((String)args[i]).indexOf("=")+1),AID.ISGUID);
                        } else if(((String)args[i]).startsWith("transport=")) {
                            dfName.addAddresses(((String)args[i]).substring(((String)args[i]).indexOf("=")+1));
                        } else if(((String)args[i]).startsWith("delay=")) {
                            delay = Integer.parseInt(((String)args[i]).substring(((String)args[i]).indexOf("=")+1));
                        } else if(((String)args[i]).startsWith("verbose=")) {
                            verbose = (Integer.parseInt(((String)args[i]).substring(((String)args[i]).indexOf("=")+1)) > 0);
                        }
                    }
                }
                
            }
            if(dfName == null) {
                dfName = super.getDefaultDF(); // hack !!! df to search has to be specified
            }
            System.out.println("\n\n"+dfName+"\n\n");
            // look for a Ping-Agent-Service at the DF
            ServiceDescription pingpongService = new ServiceDescription();
            pingpongService.setType("PingPong");
            pingpongService.setName("PingPongServiceDescription");
            DFAgentDescription dfAgentDesc = new DFAgentDescription();
            dfAgentDesc.addServices(pingpongService);
            
            SearchConstraints constraints = new SearchConstraints();
            constraints.setMaxResults(new Long(5));
            int timeout = 60000;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                System.err.println("Pong Agent interrupted while sleeping.");
            }
            while (oponentAID == null) {
                System.err.println("Pong player waiting for opponent.");
                DFAgentDescription[] oponentAgent = DFService.searchUntilFound(this, dfName, dfAgentDesc, constraints, timeout);
                if (oponentAgent != null)
                    oponentAID = oponentAgent[0].getName();
            }
            
            addBehaviour(new InitialPongBehaviour(this));
            
            // add a Behaviour to process incoming messages
            addBehaviour( new PongBehaviour( this ) );
        } catch (Exception e) {
            System.out.println( "Exception in PongAgent: " + e );
            e.printStackTrace();
        }
        
    }
    
    
    protected void quitGame(ACLMessage msg) {
        System.out.println("Game ended... telling Opponent");
        ACLMessage quit = msg.createReply();
        quit.setContent( PingAgent.QUIT );
        send( quit );
        doDelete();
    }
    
    
    protected void pong( ACLMessage msg ) {
        if(verbose) {
            System.out.println(PongAgent.PONG);
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            System.err.println("Pong Agent interrupted while sleeping before sending message.");
        }
        ACLMessage pong = msg.createReply();
        pong.setContent( PongAgent.PONG + msg.getContent().substring(4));
        send( pong );
        
    }
}