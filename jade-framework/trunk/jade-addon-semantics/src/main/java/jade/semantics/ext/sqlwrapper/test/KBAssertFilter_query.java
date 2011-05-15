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

package jade.semantics.ext.sqlwrapper.test;

import jade.semantics.kbase.filters.KBAssertFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;


public class KBAssertFilter_query extends KBAssertFilter
{
    /*
     * Private
     */
	private Formula        _assertpattern;
    
    /*
     * Constructeur
     */

    public KBAssertFilter_query()
		{
		 this._assertpattern = SL.formula("(B ??myself ??phy)");
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
                Formula[] pattern = new Formula[20] ;
                Formula f = applyResult.getFormula("phy").getSimplifiedFormula();
                pattern[0] = SL.formula("(test_simple ??x)");
                pattern[1] = SL.formula("(remove ??x)");
                pattern[2] = SL.formula("(type_int ??x)");
                pattern[3] = SL.formula("(type_string ??x)");
                pattern[4] = SL.formula("(type_real ??x)");
                pattern[5] = SL.formula("(type_date ??x)");
                pattern[6] = SL.formula("(test_join ??x)");
                pattern[7] = SL.formula("(user ??x ??y)");
                pattern[8] = SL.formula("(posede ??x ??y ??z)");
                pattern[9] = SL.formula("(nb_prenom_by_name ??x ??y)");
                
                for (int i = 0; i <=9; i++)
                    if (SL.match(pattern[i], f) != null)
                    {
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
