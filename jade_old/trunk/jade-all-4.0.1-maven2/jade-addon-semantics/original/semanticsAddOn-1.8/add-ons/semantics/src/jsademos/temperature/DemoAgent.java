/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * DemoAgent.java
 * Created on 14 déc. 2004
 * Author : Vincent Pautret
 */
package jsademos.temperature;

import jade.core.Agent;

/**
 * Class that represents a classical jade agent. It is possible to specify 
 * contents and receivers for the messages to send. A text area shows the 
 * received messages. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class DemoAgent extends Agent 
{	
	
    /*********************************************************************/
    /**				 			INTERNALS	**/
    /*********************************************************************/
    /**
     * The associated gui
     */
    DemoAgentGui gui = null;

    /*********************************************************************/
    /**				 			METHODS		**/
    /*********************************************************************/
    /**
     * Setup of this agent
     */
    @Override
	public void setup() {
    	Object[] args = getArguments();
	if ( args == null || args.length < 1 ) {
	    System.err.println("ERROR : agent should be run like this <agent name>:DemoAgent\\(<file_name>\\)");
	    System.err.print("ERROR : current args are :");
	    for (int i=0; i<args.length; i++) {
		System.err.print(args[i]+" ");
	    }
	    System.err.println();
	    System.exit(1);
	} else {
	    System.err.println("setup of DemoAgent("+ args[0]+")");
	    gui = new DemoAgentGui(getName(), args[0].toString(), this, false,  false);
	}
    } // End of setup/0
    
} // End of class 
