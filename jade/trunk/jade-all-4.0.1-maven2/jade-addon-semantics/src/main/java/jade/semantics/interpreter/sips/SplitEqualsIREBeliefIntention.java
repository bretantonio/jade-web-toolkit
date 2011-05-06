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

package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.ListOfVariable;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSet;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

public class SplitEqualsIREBeliefIntention extends SplitBeliefIntention {

	private Formula equalsIREPattern = SL.formula("(= ??ire ??term)");
	
	public SplitEqualsIREBeliefIntention(SemanticCapabilities capabilities) {
		super(capabilities);
	}
	
	@Override
	public ArrayList doApply(Formula formulaToBelieve, Term sender, SemanticRepresentation inputSR)
	throws SemanticInterpretationPrincipleException
	{
		MatchResult matchResult = equalsIREPattern.match(formulaToBelieve);
		if (matchResult != null) {
            IdentifyingExpression ire = (IdentifyingExpression)matchResult.term("ire");
            Term term = matchResult.term("term");
            TermSet set = (term instanceof TermSet ? (TermSet)term : new TermSetNode(new ListOfTerm(new Term[] {term})));

            // Applicable to all AnyNode and IotaNode, all AllNode with a non-empty set, all SomeNode with a set
            if ((ire instanceof AnyNode || ire instanceof IotaNode || term instanceof TermSetNode)
            		&& (!(ire instanceof AllNode) || set.size() > 0)) {
            	// Compute the formula and the term patterns
                ListOfVariable quantifiedVariables = new ListOfVariable();
                Formula formulaPattern = null;
                Term termPattern = null;
                if (ire.as_term().childrenOfKind(VariableNode.class, quantifiedVariables)) {
                    formulaPattern = (Formula)SL.toPattern(ire.as_formula(), quantifiedVariables, null);
                    termPattern = (Term)SL.toPattern(ire.as_term(), quantifiedVariables, null);
                }             
            	// Produce a closure SR in case of AllNode or IotaNode
                ArrayList result = new ArrayList();
                if (ire instanceof AllNode || ire instanceof IotaNode) {
                	produceSR(result, new EqualsNode(
                			new AllNode(ire.as_term(), ire.as_formula()),
                			new TermSetNode(new ListOfTerm())),
                			sender, inputSR);
                }
                // Produce one SR per element of the set
                for(int i = 0; i < set.size() && termPattern != null; i++) {
                	Formula toBelieve = formulaPattern;
                	MatchResult termMatchResult = termPattern.match(set.getTerm(i));
                	for (int j = 0; j < quantifiedVariables.size(); j++) {
                		String varName = quantifiedVariables.element(j).lx_name();
                		try {
							toBelieve = toBelieve.instantiate(varName, termMatchResult.getTerm(varName));
						} catch (WrongTypeException e) {
							e.printStackTrace();
							throw new SemanticInterpretationPrincipleException();
						}
                	}
                	produceSR(result, toBelieve, sender, inputSR);
                }
                return result;
            }

		}
		return null;
	}

    /**
     * Produce a new SR of the form (B myself ??toBelieve) into the given list.
     * The attributes of the given inputSR are attached to the produced SR.
     * @param list the list where to add the produced SR. 
     * @param inputSR the consumed SR, from which to use attributes
     * @param toBelieve the formula to believe, used to generate the formula attached to the produced SR
     * @throws WrongTypeException 
     */
    private void produceSR(ArrayList list, Formula toBelieve, Term sender, SemanticRepresentation inputSR) {
    	list.add(new SemanticRepresentation(
    			(Formula)SL.instantiate(pattern,
    					"sender", sender,
    					"goal", new BelieveNode(myCapabilities.getAgentName(), toBelieve)),
    					inputSR));
    }
}
