/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France Télécom

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

/**
 * Provides the classes to handle semantically defined actions.
 * 
 * In particular, this package includes:
 * <ul>
 * 	<li>the SemanticActionTable structure used by the JSA engine to access all
 *      defined actions,</li>
 *  <li>an implementation for each communicative act of the FIPA-ACL library,</li>
 *  <li>an implementation for each action expression operator of the FIPA-SL
 *      language (namely the alternative and sequence operators),</li>
 *  <li>the {@link jade.semantics.actions.OntologicalAction} class (to be
 *      extended by developers) to implement application-specific actions.</li>
 *  </ul>
 */
package jade.semantics.actions;