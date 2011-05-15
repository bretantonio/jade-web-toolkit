/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Telecom

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
 * This JSA extension (also called JIA - JSA Institutional Agents) makes it
 * possible to make JSA agents reason about institutional concepts
 * (institutional truth, count as rules, institutional powers) and exchange and
 * interpret institutional speech acts (declarations, promises). Institutional
 * agents come with a default behaviour making them able to manage their
 * obligations, monitor those of other agents, learn new rules at runtime, take
 * their institutional powers into account when planning...
 * 
 * <p>
 * To have a try, the 'business' demo is provided in the jsademos package.
 * It is an automatic B2B-mediation platform, running on real-world scenario.
 * More details can be found in the doc/papers/AAMAS_2009_Submission.pdf file
 * of the distribution). To run this demo, launch the following command in the
 * JSA root directory:
 * <pre>
 * ant business compile run
 * </pre>
 * 
 */
package jade.semantics.ext.institutions;