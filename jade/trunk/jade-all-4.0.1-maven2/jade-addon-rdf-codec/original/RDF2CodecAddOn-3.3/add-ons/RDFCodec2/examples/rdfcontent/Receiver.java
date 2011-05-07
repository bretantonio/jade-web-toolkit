/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package examples.rdfcontent;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jade.util.Logger;

import jade.content.*;
import jade.content.lang.rdf.*;
import jade.content.abs.*;
import jade.content.onto.*;
import jade.content.lang.*;
import jade.content.onto.basic.*;

import examples.rdfcontent.ontology.*;

public class Receiver extends Agent {
  // This agent speaks the RDF language
  private Codec          codec       = new RDFCodec();
  // This agent understands terms about people relationships
  private Ontology   ontology    = PeopleOntology.getInstance();
  
  //logging
  private static Logger logger = Logger.getMyLogger(Receiver.class.getName());

  
  protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	
	 if(logger.isLoggable(Logger.INFO))
	 	logger.log(Logger.INFO, "[" + getLocalName() + "] Waiting for a message...");
	 	
		addBehaviour(new ReceiverBehaviour(this));
  }
  
  
  /**
     Inner class ReceiverBehaviour
   */
  class ReceiverBehaviour extends SimpleBehaviour {
		private boolean finished = false;
	
		public ReceiverBehaviour(Agent a) { super(a); }
	
		public boolean done() { return finished; }
	
		public void action() {
	    ACLMessage msg = receive();
	    if (msg != null) {
				try {
			    ContentElement ce = myAgent.getContentManager().extractContent(msg);
			    if(ce instanceof FatherOf) {
			    	FatherOf fo = (FatherOf) ce;
				    if(logger.isLoggable(Logger.INFO))
				    	logger.log(Logger.INFO,"["+getLocalName()+"] "+fo.getFather().getName()+" is the father of ");
				    Iterator it = fo.getChildren().iterator();
				    while (it.hasNext()) {
				    	Person p = (Person) it.next();
			    		System.out.print(p.getName()+" ");
				    }
				    finished = true;
					}
				} 
				catch(Exception e) { 
					if(logger.isLoggable(Logger.WARNING))
						logger.log(Logger.WARNING,"Error in extracting message");
					e.printStackTrace(); 
				}
			}
			else {
				block();
			}
		}
	}
}
