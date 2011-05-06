/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France T�l�com

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

package jade.semantics.ext.sqlwrapper.sqltool.action;

import jade.semantics.actions.OntologicalAction;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.ext.sqlwrapper.sqltool.SQLKbase;
import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.StringConstant;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

public class OntologicalAction_RemoveSqlMapping extends OntologicalAction
{
    
   private SQLKbase             _sqlkbase;
   private SemanticCapabilities _sc;
    /*
     * CONSTRUCTEUR
     */
    public 
    OntologicalAction_RemoveSqlMapping(SemanticCapabilities sc, SQLKbase sqlkbase) 
    {
        super(sc,SQLTools.REMOVE_SQLMAPPING_ACTION_PATTERN, SL.TRUE, SL.TRUE);
        this._sc = sc; 
        this._sqlkbase = sqlkbase;
    }


    public void 
    perform(OntoActionBehaviour behaviour)
    {
        String          formula;
        Term            tmp;
        String          servicename;
        SQLService      sqlservice;
        final PredicateNode   slPredicat;
        
        try
        {
            switch (behaviour.getState()) 
            {
                case OntoActionBehaviour.START:
   
                    
                    tmp = getActionParameter("sqlservice");
                    if (tmp == null) servicename = "myservice";
                    else servicename = ((Constant)tmp).stringValue();
                    sqlservice = this._sqlkbase.get_sqlService(servicename);
                    if (sqlservice == null)
                    {
                        System.err.println("#####Err: OntologicalAction_SQLRemove.perform : SQLService not existe :" + servicename);
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;  
                    }
                    
                    formula = ((StringConstant)getActionParameter("formula")).stringValue();
                    slPredicat = (PredicateNode)SL.formula(formula);
                    this._sqlkbase.remove_sql_mapping(slPredicat, this._sc);
                    
                    behaviour.setState(OntoActionBehaviour.SUCCESS);
                    break;
            }
//        } catch (Cast Exception e) {
            
        } catch (Exception e) {
            e.printStackTrace();
            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
        }
    }
}
    
   

