
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

public class ListOfContentExpression extends ListOfNodes
{
    public static Integer ID = new Integer(10002);
    @Override
	public int getClassID() {return ID.intValue();}
    public ListOfContentExpression(ContentExpression[] nodes) {
        super(nodes);
    }

    public ListOfContentExpression() {
        super();
    }

    public ContentExpression element(int i) {return (ContentExpression)super.get(i);}
    public void append(ContentExpression e) {add(e);}
    public void prepend(ContentExpression e) {add(0,e);}
    public ContentExpression last() {return (ContentExpression)super.getLast();}
    public ContentExpression first() {return (ContentExpression)super.getFirst();}
    @Override
	public void accept(Visitor visitor) {visitor.visitListOfContentExpression(this);}
    @Override
	public Node getClone(HashMap clones) {
        Node clone = new ListOfContentExpression();
        clone.copyValueOf(this, clones);
        return clone;
    }

    @Override
	public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
}