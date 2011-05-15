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

import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLPredicat;
import jade.semantics.kbase.filters.KBAssertFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

public class KBAssertFilter_SQLNull extends KBAssertFilter
{
    /*
     * Private
     */
	private Formula        _assertpattern;
    private Formula        _removepattern;
    
    /*
     * Constructeur
     */

    public KBAssertFilter_SQLNull(SQLPredicat pred)
		{
		 this._assertpattern = SL.formula("(B ??myself ??p)")
                                     .instantiate("p", pred.get_pattern());
         this._removepattern = SL.formula("(B ??myself (not ??p))")
             .instantiate("p", pred.get_pattern());
        }
		
    /*
     * Fonction
     */
    public final Formula 
    apply(Formula formula) 
    {
        MatchResult applyResult;
        try 
        {
            applyResult = SL.match(this._assertpattern, formula);
            if (applyResult != null) 
            {
                return SL.TRUE;
            }
            applyResult = SL.match(this._removepattern, formula);
            if (applyResult != null) 
            {
                return SL.TRUE;
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return formula;
    } // End of beforeAssert/1
      
}
