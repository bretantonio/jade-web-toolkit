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
 * Provides the classes required to run the interpretation algorithm of JSA agents.
 * 
 * Besides the interpretation algorithm itself (implemented by the
 * {@link jade.semantics.interpreter.SemanticInterpreterBehaviour} class), this
 * package includes:
 * <ul>
 * 	<li>the basic classes to build a JSA-based semantic agent (see
 *      {@link jade.semantics.interpreter.SemanticAgent} and
 *      {@link jade.semantics.interpreter.SemanticCapabilities}),</li>
 *  <li>a set of generic interpretation rules, named "Semantic Interpretation
 *      Principles" (or SIPs), which can be extended for the application needs
 *      (see the {@link jade.semantics.interpreter.sips} package).</li>
 * </ul>
 */
package jade.semantics.interpreter;