/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

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

package jade.semantics.ext.sqlwrapper.sqltool.filter;

import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLPredicat;
import jade.semantics.kbase.filters.KBAssertFilter;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;


public class KBAssertFilter_SQLDefault extends KBAssertFilter
{
    /*
     * Private
     */
	private Formula        _truepattern1;
    private Formula        _truepattern2;
    private Formula        _falsepattern1;
    private Formula        _falsepattern2;
	private SQLPredicat    _sql_predicat;
    
    /*
     * Constructeur
     */

    public KBAssertFilter_SQLDefault(SQLPredicat pred)
		{
		 this._truepattern1 = SL.formula("(B ??myself ??p)").instantiate("p", pred.get_pattern());
         this._falsepattern1 = SL.formula("(B ??myself (not ??p))").instantiate("p", pred.get_pattern());
         this._truepattern2 = SL.formula("(not (B ??myself (not ??p)))").instantiate("p", pred.get_pattern());
         this._falsepattern2 = SL.formula("(not (B ??myself ??p))").instantiate("p", pred.get_pattern());
         this._sql_predicat = pred;
        }
		

    /*
     * ----------------------------------------------------------
     */
    
    private final Formula
    bassert(MatchResult applyResult)
    {
        if (!this._sql_predicat.inform_metavar(applyResult)) 
            return new FalseNode();
        if (this._sql_predicat.assert_execute())
            return SL.TRUE;
        return new FalseNode();
    }
    
    private final Formula
    bremove(MatchResult applyResult)
    {
        if (!this._sql_predicat.inform_metavar(applyResult)) 
            return new FalseNode();
        if (this._sql_predicat.remove_execute())
                return SL.TRUE;
        return new FalseNode();
    }
    
    /*
     * ----------------------------------------------------------
     */

    private final Formula
    closed_bassert(MatchResult applyResult, boolean b)
    {
        MetaTermReferenceNode node = new MetaTermReferenceNode();
        node.lx_name(SQLTools.CLOSEDTABLE_DEFAULT_BELIEF_METAVAR);
        if (b)
            node.sm_value(new IntegerConstantNode(1L));
        else
            node.sm_value(new IntegerConstantNode(0L));
        applyResult.add(node);

        if (!this._sql_predicat.inform_metavar(applyResult)) 
            return new FalseNode();
        if (this._sql_predicat.assert_execute())
            return SL.TRUE;
        return new FalseNode();
    }
    
    private final boolean
    closed_bremove(MatchResult applyResult, boolean b)
    {
        MetaTermReferenceNode node = new MetaTermReferenceNode();
        node.lx_name(SQLTools.CLOSEDTABLE_DEFAULT_BELIEF_METAVAR);
        if (b)
            node.sm_value(new IntegerConstantNode(1L));
        else
            node.sm_value(new IntegerConstantNode(0L));
        applyResult.add(node);
        
        if (!this._sql_predicat.inform_metavar(applyResult)) 
            return false;
        this._sql_predicat.remove_execute();
        return true;
    }
    
    
    
    /*
     * ----------------------------------------------------------
     * ----------------------------------------------------------
     */

    
    
    
    
    
    /*
     * Fonction
     */
    public final Formula 
    apply(Formula formula) 
    {
        MatchResult applyResult;
        MatchResult applyResult2;
        try 
        {
            applyResult = SL.match(this._truepattern1, formula);
            if (applyResult != null) 
            {
 
                if (this._sql_predicat.is_closed_table())
                {
                    return this.bassert(applyResult);
                }
                else
                {
                    applyResult2 = SL.match(this._truepattern1, formula);
                    if(!this.closed_bremove(applyResult, false))
                        return new FalseNode();
                    return this.closed_bassert(applyResult2, true);
                }
            }
            applyResult = SL.match(this._truepattern2, formula);
            if (applyResult != null) 
            {
 
                if (this._sql_predicat.is_closed_table())
                {
                    return this.bassert(applyResult);
                }
                else
                {
                    if(!this.closed_bremove(applyResult, false))
                        return new FalseNode();
                    return SL.TRUE;
                }
            }            

            applyResult = SL.match(this._falsepattern1, formula);
            if (applyResult != null) 
            {

            	if (this._sql_predicat.is_closed_table())
                {
                    return this.bremove(applyResult);
                }
                else
                {
                    applyResult2 = SL.match(this._falsepattern1, formula);
                    if(!this.closed_bremove(applyResult, true))
                        return new FalseNode();
                    return this.closed_bassert(applyResult2, false);
                }
            }
            applyResult = SL.match(this._falsepattern2, formula);
            if (applyResult != null) 
            {

            	if (this._sql_predicat.is_closed_table())
                {
                    return this.bremove(applyResult);
                }
                else
                {
                    applyResult2 = SL.match(this._falsepattern1, formula);
                    if(!this.closed_bremove(applyResult, true))
                        return new FalseNode();
                    return SL.TRUE;
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return formula;
    } // End of beforeAssert/1
      
}
