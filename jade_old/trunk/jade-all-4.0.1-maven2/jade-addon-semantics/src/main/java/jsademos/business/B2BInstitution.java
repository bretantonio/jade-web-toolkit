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


package jsademos.business;

import jade.core.AID;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;



public class B2BInstitution extends BusinessAgent {

	/********************************
	 * STATIC CONFIG OF AGENT NAMES *
	 ********************************/

	public static Term ENTERPRISE_A_AGENT = SL.term("(agent-identifier :name buyer@test)");
	public static Term ENTERPRISE_B_AGENT = SL.term("(agent-identifier :name seller@test)");
	public static Term BANK_A_AGENT = SL.term("(agent-identifier :name bankAagent@test)");
	public static Term BANK_B_AGENT = SL.term("(agent-identifier :name bankBagent@test)");
	public static Term ENTERPRISE_A = SL.term("(agent-identifier :name enterpriseA@test)");
	public static Term ENTERPRISE_B = SL.term("(agent-identifier :name enterpriseB@test)");
	public static Term BANK_A = SL.term("(agent-identifier :name bankA@test)");
	public static Term BANK_B = SL.term("(agent-identifier :name bankB@test)");
	
	public static String ENTERPRISE_A_AGENT_NAME = "buyer";
	public static String INTERMEDIARY_NAME = "intermediary";
	public static String ENTERPRISE_B_AGENT_NAME = "seller";
	public static String BANK_A_AGENT_NAME = "bankAagent";
	
	@Override
	public void setup() {
		// specific method for the agent representing the institution
		super.setupInstitution(new B2BInstitutionCapabilities((String)getArguments()[0]),(String)getArguments()[0]);

		String institution = (String)getArguments()[0];
		if (institution.equals("b2b")) {
			Formula test = new InstitutionalFactNode(SL.term("b2b"),new BelieveNode(Tools.AID2Term(new AID("seller",AID.ISLOCALNAME)),new NotNode(SL.formula("phi"))));
			getSemanticCapabilities().getMyKBase().assertFormula(test);
			Formula test2 = new InstitutionalFactNode(SL.term("b2b"),new BelieveNode(Tools.AID2Term(new AID("seller",AID.ISLOCALNAME)),new NotNode(SL.formula("psi"))));
			getSemanticCapabilities().getMyKBase().assertFormula(test2);
		}
		else if (institution.equals("bbb")) {
			Formula test2 = new InstitutionalFactNode(SL.term("bbb"),new BelieveNode(Tools.AID2Term(new AID("seller",AID.ISLOCALNAME)),new NotNode(SL.formula("zeta"))));
			getSemanticCapabilities().getMyKBase().assertFormula(test2);
		}
	}


}


class B2BInstitutionCapabilities extends BusinessCapabilities {

	public B2BInstitutionCapabilities(String institutionName) {
		super(institutionName);
	}

	@Override
	protected KBase setupKbase() {
		KBase kbase = super.setupKbase();
		return kbase;
	}
	
	@Override
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
		return table;
	}
	
}