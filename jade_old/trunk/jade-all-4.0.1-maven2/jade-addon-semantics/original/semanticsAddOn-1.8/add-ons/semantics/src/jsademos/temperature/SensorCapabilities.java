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
 * SensorCapabilities.java
 * Created on 13 mai 2005
 * Author : Vincent Pautret
 */
package jsademos.temperature;

import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.interpreter.sips.adapters.BeliefTransferSIPAdapter;
import jade.semantics.interpreter.sips.adapters.CFPSIPAdapter;
import jade.semantics.kbase.ArrayListKBaseImpl;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.kbase.filters.std.NestedBeliefFilters;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * Capbilities of a sensor.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/13 Revision: 1.0
 */
public class SensorCapabilities extends DefaultCapabilities {
    /**
     * 
     */
    SingleNumValueDefinition temperatureDefinition = new SingleNumValueDefinition("temperature");
    
    /**
     * 
     */
    Formula bPattern;
    /** ****************************************************************** */
    /** CONSTRUCTOR * */
    /** ****************************************************************** */
    /**
     * Creates a new SensorCapabilities
     */
    public SensorCapabilities() {
        super();
        bPattern = SL.formula("(B ??agent ??phi)");
    } 
    
    /** ****************************************************************** */
    /** METHODS * */
    /** ****************************************************************** */
    
    @Override
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
    	SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();

    	// BELIEF TRANSFER SIPS
    	// Prevent the adoption of beliefs about temperature-related facts originated by external agents
    	table.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this,
    			temperatureDefinition.VALUE_X_PATTERN) {
    		@Override
			protected ArrayList doApply(MatchResult matchFormula, MatchResult matchAgent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
    			return refuseResult;
    		}
    	});

    	table.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this,
    			temperatureDefinition.VALUE_GT_X_PATTERN) {
    		@Override
			protected ArrayList doApply(MatchResult matchFormula, MatchResult matchAgent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
    			return refuseResult;
    		}
    	});
    	
    	table.addSemanticInterpretationPrinciple(new CFPSIPAdapter(this));
    	
    	return table;
}

    /**
     * setup StandardCustomization
     */
    @Override
	public StandardCustomization setupStandardCustomization() {
        return new StandardCustomizationAdapter() {
			// Sets the color to yellow when it receives a subscribe
			@Override
			public void notifySubscribe(Term subscriber, Formula obsverved, Formula goal) {
				((SensorAgent)getAgent()).setSubscribed(true);
			}
            //Sets the color to gray when it receives an unsubscribe
			@Override
			public void notifyUnsubscribe(Term subscriber, Formula obsverved, Formula goal) {
				((SensorAgent)getAgent()).setSubscribed(false);
			}
			
		};
    } 
    
    /**
     * 
     */
    @Override
	public KBase setupKbase() {
        FilterKBase kb = (FilterKBase)super.setupKbase();
        
        kb.addFiltersDefinition(temperatureDefinition);
        kb.addFiltersDefinition(new NestedBeliefFilters() {
			/* (non-Javadoc)
        	 * @see jade.semantics.kbase.filters.NestedBeliefFilters#newInstance()
        	 */
        	@Override
			public KBase newInstance(Term agent) {
				FilterKBase result = new FilterKBaseImpl(new ArrayListKBaseImpl(agent));
        		result.addFiltersDefinition(new SingleNumValueDefinition("temperature"));
        		return result;
        	} 
        });
		return kb;
    } 
	
} 
