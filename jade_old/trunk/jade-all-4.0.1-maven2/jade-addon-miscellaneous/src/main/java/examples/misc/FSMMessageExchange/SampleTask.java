/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2003 TILAB S.p.A. 

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

package examples.misc.FSMMessageExchange;

import jade.core.Agent;
import jade.core.behaviours.*; 
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

/**
 * <br> SampleTask just prints on stdout that is has been executed.
 * @author Fabio Bellifemine - TILAB
 * @version $Date: 2004-07-19 18:20:54 +0200 (lun, 19 lug 2004) $ $Revision: 438 $
 **/
public class SampleTask extends OneShotBehaviour {
    
    private static Logger logger = Logger.getMyLogger(SampleTask.class.getName());
    
    public void action() {
	if(logger.isLoggable(Logger.INFO))
		logger.log(Logger.INFO,myAgent.getLocalName()+" executed SampleTask");
    }
}

