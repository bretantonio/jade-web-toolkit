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
 * $Id: ToolAgentParameter.java,v 1.3 2005/08/24 08:28:35 medha Exp $
 *
 * Created on 2005/02/28, 21:35:26
 */

package jade.domain.toolagent;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Protege name: ToolAgentParameter
* @author ontology bean generator
 *@author Tim Niemueller
* @version 2005/02/28, 21:35:26
*/
public class ToolAgentParameter implements Concept {

    public ToolAgentParameter() {
    }
    
    public ToolAgentParameter(String type, String value) {
        this.type = type;
        this.value = value;
    }
    
    
   /**
   * Protege name: Value
   */
   private String value;
   public void setValue(String value) { 
    this.value=value;
   }
   public String getValue() {
     return this.value;
   }

   /**
   * Protege name: Type
   */
   private String type;
   public void setType(String value) { 
    this.type=value;
   }
   public String getType() {
     return this.type;
   }

}
