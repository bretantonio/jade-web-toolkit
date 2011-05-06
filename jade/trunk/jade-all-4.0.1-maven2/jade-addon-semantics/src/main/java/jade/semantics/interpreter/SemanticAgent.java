/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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
 * SemanticAgent.java
 * Created on 13 mai 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.Agent;

/**
 * This class provides an empty semantic agent, that is an empty JADE agent equiped
 * with a default instance of {@link SemanticCapabilities}. This class is to be
 * extended by developers when they need a semantic agent, which is not based on
 * an existing JADE agent.
 * 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/13 Revision: 1.0 
 */
public class SemanticAgent extends Agent {
	
	
    /**
     * SemanticCapabilities instance associated to this agent.
     */
    private SemanticCapabilities semanticCapabilities;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a SemanticAgent with a default SemanticCapabilities instance.
     */
    public SemanticAgent() {
        semanticCapabilities = new DefaultCapabilities();
    }
    
    /**
     * Creates a SemanticAgent with a given SemanticCapabilities instance.
     * 
     * @param capabilities The SemanticCapabilities instance of the created agent
     */
    public SemanticAgent(SemanticCapabilities capabilities) {
        semanticCapabilities = capabilities;
    }
    
    /*********************************************************************/
    /**                         METHODS                             **/
    /*********************************************************************/
    
    /**
     * Returns the SemanticCapabilities instance associated to this agent.
     * 
     * @return the SemanticCapabilities instance associated to this agent
     */
    public SemanticCapabilities getSemanticCapabilities() {
        return semanticCapabilities;
    } 

    /**
     * Sets the SemanticCapabilities instance associated to this agent.
     * 
     * @param capabilities the SemanticCapabilities instance to associate to
     *                     this agent.
     */
    public void setSemanticCapabilities(SemanticCapabilities capabilities) {
        this.semanticCapabilities = capabilities;
    } 

    /**
     * Returns a reference to this agent, cast as a JADE agent class
     * 
     * @deprecated This methods is useless as the SemanticAgent class now
     *             extends the Agent class.
     * @return a reference of type Agent to this agent
     */
    @Deprecated
	public Agent getAgent() {
        return this;
    } 
    
    /**
     * Setup method of this agent. It installs the specified SemanticCapabilities
     * instance on this agent. Do not forget to call it in inherited methods!
     */
    @Override
	public void setup() {
        super.setup();
        semanticCapabilities.install(this);
    }
    
} 
