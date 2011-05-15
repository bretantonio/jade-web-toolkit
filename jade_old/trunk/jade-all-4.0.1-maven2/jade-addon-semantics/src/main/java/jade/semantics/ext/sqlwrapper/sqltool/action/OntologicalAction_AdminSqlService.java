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

package jade.semantics.ext.sqlwrapper.sqltool.action;

import jade.semantics.actions.OntologicalAction;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.ext.sqlwrapper.sqltool.SQLKbase;
import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.StringConstant;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSet;
import jade.semantics.lang.sl.tools.SL;

public class OntologicalAction_AdminSqlService extends OntologicalAction
{

    private SQLKbase             _sqlkbase;
    /*
     * CONSTRUCTEUR
     */
    public 
    OntologicalAction_AdminSqlService(SemanticCapabilities sc, SQLKbase sqlkbase) 
    {
        super(sc,SQLTools.ADMIN_SQLSERVICE_ACTION_PATTERN, SL.TRUE, SL.TRUE);
        this._sqlkbase = sqlkbase;
    }


    private boolean
    cleantable_param(Term tmp, SQLService sqlservice)
    {
        if (tmp == null)
        {
            return true;
        }
        else if (tmp instanceof StringConstant && 
                ((StringConstant)tmp).stringValue().compareTo("all") == 0)
        {
            sqlservice.clean_all_table();
        }
        else if (tmp instanceof TermSet)
        {
            TermSet sterm = (TermSet)tmp;
            for (int i = 0; i < sterm.size(); i++)
            {
            if (sterm.getTerm(i) instanceof StringConstant)
                sqlservice.clean_table(((StringConstant)sterm.getTerm(i)).stringValue());
            else
                return false; 
            }
        }
        else 
        {
            return false;
        }
        return true;
        
    }
    
    
    private boolean
    deletetable_param(Term tmp, SQLService sqlservice)
    {
        if (tmp == null)
        {
            return true;
        }
        else if (tmp instanceof StringConstant && 
                ((StringConstant)tmp).stringValue().compareTo("all") == 0)
        {
            sqlservice.delete_all_table();
        }
        else if (tmp instanceof TermSet)
        {
            TermSet sterm = (TermSet)tmp;
            for (int i = 0; i < sterm.size(); i++)
            {
            if (sterm.getTerm(i) instanceof StringConstant)
                sqlservice.delete_table(((StringConstant)sterm.getTerm(i)).stringValue());
            else
                return false; 
            }
        }
        else 
        {
            return false;
        }
        return true;
        
    }
    
    public void 
    perform(OntoActionBehaviour behaviour)
    {
        Term tmp;
        String servicename;
        SQLService sqlservice;
        
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
                        System.err.println("#####Err: OntologicalAction_SQL.perform : SQLService not existe :" + servicename);
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;  
                    }             
                    tmp = getActionParameter("cleantable");
                    if (!cleantable_param(tmp, sqlservice))
                    {
                        System.err.println("#####Err: OntologicalAction_SQL.perform : Bad Parametre cleantable");
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;  
                    }
                    tmp = getActionParameter("deletetable");
                    if (!deletetable_param(tmp, sqlservice))
                    {
                        System.err.println("#####Err: OntologicalAction_SQL.perform : Bad Parametre deletetable");
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;  
                    }                   
                    behaviour.setState(OntoActionBehaviour.SUCCESS);
                    break;
            }
        } catch (Exception e) {
            System.out.println("#Err:OntologicalAction_AdminSqlService");
            e.printStackTrace();
            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
        }
    }
}
    
   

