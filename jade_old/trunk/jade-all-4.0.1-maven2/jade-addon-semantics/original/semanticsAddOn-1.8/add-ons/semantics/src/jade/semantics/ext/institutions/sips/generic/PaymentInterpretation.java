package jade.semantics.ext.institutions.sips.generic;

/*
 * created by Carole Adam, 8 April 2008
 * to trigger observers when a payment is done
 * so that the receiver's observers are triggered
 * by his new bank account
 */

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * SIP used to update the bank account of the receiver after a PAY action.
 * @author wdvh2120
 * @version date April 8, 2008
 *
 * TODO: should be in B2B demo, too specific
 */
public class PaymentInterpretation extends ActionDoneSIPAdapter {

	public PaymentInterpretation(SemanticCapabilities capabilities) {
		super(capabilities,
			new ActionExpressionNode(
				new MetaTermReferenceNode("someone"),
				SL.term("(PAY :amount ??amount :receiver ??receiver :reference ??reference)")));
	}
	
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			// update all observers waiting for money values 
			myCapabilities.getMyKBase().updateObservers(SL.formula("(has-money ??agent ??amount)"));
			return result;
		}
		
		return null;
	}

}
