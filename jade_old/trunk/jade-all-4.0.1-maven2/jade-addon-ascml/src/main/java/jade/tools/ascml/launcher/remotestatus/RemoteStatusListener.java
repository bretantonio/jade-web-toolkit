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


package jade.tools.ascml.launcher.remotestatus;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class RemoteStatusListener extends CyclicBehaviour {

    MessageTemplate template;
    AgentLauncher al;
    ContentManager cm;
    Codec sl; 
    Ontology onto;
    
    public RemoteStatusListener(AgentLauncher al) {
        super(al);
        this.al=al;   
        cm = new ContentManager();
        sl = new SLCodec();
        cm.registerLanguage(sl);
        onto = ASCMLOntology.getInstance();
        cm.registerOntology(onto);        
    }

    public synchronized void action() {
        MessageTemplate template = MessageTemplate.MatchOntology(ASCMLOntology.ONTOLOGY_NAME);
        template = MessageTemplate.and(template, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE));
        template = MessageTemplate.and(template, MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF));
        ACLMessage msg = al.receive(template);
        if (msg!=null) {
            try {
				Status s = al.getStatusFromInform(msg);
				String fqn = al.getFQNfromInform(msg);
				System.out.println("Status of "+ fqn +" is " + s);
                IAbstractRunnable absRunnable = al.getRepository().getRunnableManager().getRunnable(fqn);                        
                absRunnable.setStatus(s);
            } catch (Exception e) {
                System.err.println("Exception while receiving Message in ModelStatusListener:");
                e.printStackTrace();
				System.err.println("MSG was: "+msg.toString());
            }            
        }
        block();
        
    }

}
