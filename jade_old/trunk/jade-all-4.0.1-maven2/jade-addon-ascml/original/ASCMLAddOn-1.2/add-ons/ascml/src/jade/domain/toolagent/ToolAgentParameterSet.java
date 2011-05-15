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


package jade.domain.toolagent;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Protege name: ToolAgentParameterSet
* @author ontology bean generator
* @version 2005/02/28, 21:35:26
*/
public class ToolAgentParameterSet implements AgentAction {

   /**
   * Protege name: AgentID
   */
   private AID agentID;
   public void setAgentID(AID value) { 
    this.agentID=value;
   }
   public AID getAgentID() {
     return this.agentID;
   }

   /**
   * Protege name: Parameter
   */
   private List parameter = new ArrayList();
   public void addParameter(ToolAgentParameter elem) { 
     parameter.add(elem);
   }
   public boolean removeParameter(ToolAgentParameter elem) {

     boolean result = parameter.remove(elem);
     return result;
   }
   public void clearAllParameter() {

     parameter.clear();
   }
   public Iterator getAllParameter() {return parameter.iterator(); }
   public List getParameter() {return parameter; }
   public void setParameter(List l) {parameter = l; }

}
