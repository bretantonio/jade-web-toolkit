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


package jade.semantics.ext.institutions.sips.generic;

/*
 * Class SplitInstitutionalFact
 * Created by Carole Adam, November 2007
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.util.leap.ArrayList;

/**
 * This SIP intercepts InstitutionalFacts encapsulating AndNodes
 * and split them into the corresponding atomic InstitutionalFacts.
 * 
 * created by Carole Adam, November 2007
 * @author wdvh2120
 */

public class SplitInstitutionalFact extends SemanticInterpretationPrinciple {

	private final boolean DEBUG = false;
	
	/**
	 * Constructor of the principle
	 * @param capabilities capabilities of the owner (the agent) of this 
	 * semantic interpretation principle
	 */
	public SplitInstitutionalFact(InstitutionalCapabilities capabilities) {
		super(capabilities,
				new InstitutionalFactNode(
						new MetaTermReferenceNode("institution"),
						new AndNode(
								new MetaFormulaReferenceNode("left"),
								new MetaFormulaReferenceNode("right"))), 
								SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
	}

	@Override
	public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {

		MatchResult mr = pattern.match(sr.getSLRepresentation());
		if (mr != null) {
			InstitutionTools.printTraceMessage("SplitInstAnd, sr="+sr, DEBUG);
			InstitutionTools.printTraceMessage("SplitInstAnd, pattern="+pattern, DEBUG);
			InstitutionTools.printTraceMessage("SplitInstAnd, match="+mr, DEBUG);
			
			if (sr.getSLRepresentation() instanceof BelieveNode) {
				Term agent = ((BelieveNode)sr.getSLRepresentation()).as_agent();
				Formula institutionalFact = ((BelieveNode)sr.getSLRepresentation()).as_formula();
				if (institutionalFact instanceof InstitutionalFactNode) {
					Term institution = ((InstitutionalFactNode)institutionalFact).as_institution();
					Formula institutionalContent = ((InstitutionalFactNode)institutionalFact).as_fact();
					if (institutionalContent instanceof AndNode) {
						ListOfFormula andLeaves = ((AndNode)institutionalContent).getLeaves();
						ArrayList listOfSR = new ArrayList();
						// to force the leaves to be interpreted by all SIP
						sr.setSemanticInterpretationPrincipleIndex(0);
						for (int i=0; i<andLeaves.size(); i++) {
							listOfSR.add(new SemanticRepresentation(
									new BelieveNode(agent,new InstitutionalFactNode(
											institution,
											andLeaves.element(i))), 
											sr));
						}
						return listOfSR;
					}//end if   
				}}}
			return null;
		}

	}
