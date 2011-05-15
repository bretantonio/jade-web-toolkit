package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/*
 * created by Carole Adam, 29 November 2007
 * Split action sequences to prevent ActionFeaturesSIP to throw
 * NullPointerException when trying to get to actor of an action.
 */

public class SplitActionSequence extends SemanticInterpretationPrinciple {

	public SplitActionSequence(SemanticCapabilities capabilities) {
        super(capabilities,
        	  new DoneNode(new SequenceActionExpressionNode(new MetaTermReferenceNode("left_action"),
        			  new MetaTermReferenceNode("right_action")),
        			  SL.TRUE),
        	  SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
    }
	
	@Override
	public ArrayList apply(SemanticRepresentation sr)
		throws SemanticInterpretationPrincipleException {
    	if (sr.getSLRepresentation() instanceof DoneNode) {
    		ActionExpression sequence = (ActionExpression)((DoneNode)sr.getSLRepresentation()).as_action();
    		if (sequence instanceof SequenceActionExpressionNode) {
    			ActionExpression leftAction = (ActionExpression)((SequenceActionExpressionNode)sequence).as_left_action();
    			ActionExpression rightAction = (ActionExpression)((SequenceActionExpressionNode)sequence).as_right_action();
                ArrayList listOfSR = new ArrayList();
                listOfSR.add(new SemanticRepresentation(new DoneNode(leftAction,SL.TRUE), sr));
                listOfSR.add(new SemanticRepresentation(new DoneNode(rightAction,SL.TRUE),sr));
                return listOfSR;
    		}
    	}
        return null;
    }

}
