
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


//-----------------------------------------------------
// This file has been automatically produced by a tool.
//-----------------------------------------------------

package jade.semantics.lang.sl.grammar;

import java.util.HashMap;
//#PJAVA_EXCLUDE_END
/*#PJAVA_INCLUDE_BEGIN
import jade.util.leap.Collection;
import jade.util.leap.Comparable;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import jade.util.leap.HashMap;
#PJAVA_INCLUDE_END*/

public class ActionContentExpressionNode extends ContentExpression
{
    public static Integer ID = new Integer(2);
    @Override
	public final int getClassID() {return ID.intValue();}
    static int _as_action_expression = 0;

    public ActionContentExpressionNode(ActionExpression as_action_expression)  {
        super(1);
        as_action_expression(as_action_expression);
        initNode();
    }

    public ActionContentExpressionNode() {
        super(1);
        as_action_expression(null);
        initNode();
    }

    @Override
	public void accept(Visitor visitor) {visitor.visitActionContentExpressionNode(this);}

    @Override
	public Node getClone(HashMap clones) {
        Node clone = new ActionContentExpressionNode(null);
        clone.copyValueOf(this, clones);
        return clone;
    }

    @Override
	public void copyValueOf(Node n, HashMap clones) {
        if (n instanceof ActionContentExpressionNode) {
            super.copyValueOf(n, clones);
            ActionContentExpressionNode tn = (ActionContentExpressionNode)n;
        }
        initNode();
    }


    @Override
	public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    static public int as_action_expression_ID = new String("as_action_expression").hashCode();
    public ActionExpression as_action_expression() {return (ActionExpression)_nodes[_as_action_expression];}
    public void as_action_expression(ActionExpression s) {_nodes[_as_action_expression] = s;}

    @Override
	public boolean hasAttribute(int attrname) {
        if ( attrname == as_action_expression_ID) return true;
        return super.hasAttribute(attrname);
    }

    @Override
	public Object getAttribute(int attrname) {
        if ( attrname == as_action_expression_ID) return as_action_expression();
        return super.getAttribute(attrname);
    }

    @Override
	public void setAttribute(int attrname, Object attrvalue) {
        if ( attrname == as_action_expression_ID) {as_action_expression((ActionExpression)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}