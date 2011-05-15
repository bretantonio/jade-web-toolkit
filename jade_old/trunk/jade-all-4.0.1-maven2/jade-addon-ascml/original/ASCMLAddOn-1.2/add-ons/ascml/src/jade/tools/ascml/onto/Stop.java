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


package jade.tools.ascml.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Stop a model at an ascml
   * Protege name: Stop
* @author ontology bean generator
* @version 2005/12/7, 10:14:11
*/
public class Stop extends jade.content.onto.basic.Action implements AgentAction {

   /**
   * Defining the model by its full quallified name
   * Protege name: Models
   */
   private List models = new ArrayList();
   public void addModels(AbsModel elem) { 
     models.add(elem);
   }
   public boolean removeModels(AbsModel elem) {

     boolean result = models.remove(elem);
     return result;
   }
   public void clearAllModels() {

     models.clear();
   }
   public Iterator getAllModels() {return models.iterator(); }
   public List getModels() {return models; }
   public void setModels(List l) {models = l; }

}
