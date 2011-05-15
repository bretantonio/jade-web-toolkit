
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

public abstract class BinaryLogicalFormula extends Formula
{
    public static Integer ID = new Integer(10012);
    @Override
	public int getClassID() {return ID.intValue();}
    static int _as_left_formula = 0;
    static int _as_right_formula = 1;

    public BinaryLogicalFormula(int capacity, Formula as_left_formula, Formula as_right_formula)  {
      super (capacity);
        as_left_formula(as_left_formula);
        as_right_formula(as_right_formula);
    }

    @Override
	public void copyValueOf(Node n, HashMap clones) {
        if (n instanceof BinaryLogicalFormula) {
            super.copyValueOf(n, clones);
            BinaryLogicalFormula tn = (BinaryLogicalFormula)n;
        }
    }

    @Override
	public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    static public int as_left_formula_ID = new String("as_left_formula").hashCode();
    public Formula as_left_formula() {return (Formula)_nodes[_as_left_formula];}
    public void as_left_formula(Formula s) {_nodes[_as_left_formula] = s;}
    static public int as_right_formula_ID = new String("as_right_formula").hashCode();
    public Formula as_right_formula() {return (Formula)_nodes[_as_right_formula];}
    public void as_right_formula(Formula s) {_nodes[_as_right_formula] = s;}

    @Override
	public boolean hasAttribute(int attrname) {
        if ( attrname == as_left_formula_ID) return true;
        if ( attrname == as_right_formula_ID) return true;
        return super.hasAttribute(attrname);
    }

    @Override
	public Object getAttribute(int attrname) {
        if ( attrname == as_left_formula_ID) return as_left_formula();
        if ( attrname == as_right_formula_ID) return as_right_formula();
        return super.getAttribute(attrname);
    }

    @Override
	public void setAttribute(int attrname, Object attrvalue) {
        if ( attrname == as_left_formula_ID) {as_left_formula((Formula)attrvalue);return;}
        if ( attrname == as_right_formula_ID) {as_right_formula((Formula)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}