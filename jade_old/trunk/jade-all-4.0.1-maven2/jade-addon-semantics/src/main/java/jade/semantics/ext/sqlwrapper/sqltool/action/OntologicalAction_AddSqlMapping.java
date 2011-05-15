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
import jade.semantics.ext.sqlwrapper.sqltool.filter.KBAssertFilter_SQLDefault;
import jade.semantics.ext.sqlwrapper.sqltool.filter.KBAssertFilter_SQLNull;
import jade.semantics.ext.sqlwrapper.sqltool.filter.KBQueryFilter_SQLDefault;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLNodeColumn;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLPredicat;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService.SQLSimpleType;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.filters.KBAssertFilter;
import jade.semantics.kbase.filters.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.FunctionalTerm;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.StringConstant;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSet;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

public class OntologicalAction_AddSqlMapping extends OntologicalAction
{

    private SemanticCapabilities _sc;
    private SQLKbase             _sqlkbase;
    private boolean              _is_math_pred;
    /*
     * CONSTRUCTEUR
     */
    public 
    OntologicalAction_AddSqlMapping(SemanticCapabilities sc, SQLKbase sqlkbase) 
    {
        super(sc,SQLTools.ADD_SQLMAPPING_ACTION_PATTERN, SL.TRUE, SL.TRUE);
        this._sc = sc; 
        this._sqlkbase = sqlkbase;
        this._is_math_pred = false;
    }

    /**
     * Ajoute les VarLink au SQLPredicat
     */
    private boolean 
    add_mapping(SQLPredicat sqlPredicat, TermSet set, PredicateNode slPredicat) 
    {
        Term tmp;
        Term inSet;
        MatchResult     metarefInPred;
        
        metarefInPred = SL.match(slPredicat, slPredicat);
        
        if (set == null)
            for (int i = 0; i < metarefInPred.size(); i++)
            {   
                sqlPredicat.add_sql_mapping(new SQLNodeColumn(((MetaTermReferenceNode)metarefInPred.get(i)).lx_name(),
                      slPredicat.as_symbol().toString()+ "." + ((MetaTermReferenceNode)metarefInPred.get(i)).lx_name()));
            }
        else if (set.size() != metarefInPred.size())
            return false;
        else
            for (int i = 0; i < set.size(); i++)
            {
                inSet = set.getTerm(i);
                if (inSet instanceof FunctionalTerm)
                {
                    FunctionalTermParamNode inSetFunc = (FunctionalTermParamNode) inSet.getSimplifiedTerm();
                    if (SL.match(inSetFunc, SQLTools.SQLMAPPING_MAPPING_PARAMETER_PATTERN) == null)
                        {
                        System.err.println("###Err: OntologicalAction_SQL.add_mapping : No Match SQLLINK_MAPPING_PARAMETER_PATTERN" +
                                "\n  " + SQLTools.SQLMAPPING_MAPPING_PARAMETER_PATTERN.toString() + "\n  " + inSetFunc.toString());
                        return false;
                        }
                    SQLNodeColumn colNode = new SQLNodeColumn(((StringConstant)inSetFunc.getParameter("metavar")).stringValue(),
                            ((StringConstant)inSetFunc.getParameter("sqlref")).stringValue());
                    tmp = inSetFunc.getParameter("type");
                    if (tmp != null && !colNode.set_type(((StringConstant)tmp).stringValue()))
                        {
                        return false;
                        }
                    tmp = inSetFunc.getParameter("math");
                    if (tmp != null)
                    {
                        this._is_math_pred = true;
                        if (!colNode.set_mat(((StringConstant)tmp).stringValue()))
                            return false;
                    }
                    sqlPredicat.add_sql_mapping(colNode);
                }
                else return false;
            }
        return true;
    }
    
    /**
     * Ajoute les TabLink au SQLPredicat
     */
    private boolean 
    add_innnerjoin(SQLPredicat sqlPredicat, TermSet set) 
    {
        Term inSet;
        
        for (int i = 0; i < set.size(); i++)
        {
            inSet = set.getTerm(i);
            if (inSet instanceof FunctionalTerm)
            {
                FunctionalTermParamNode inSetFunc = (FunctionalTermParamNode) inSet.getSimplifiedTerm();
                if (SL.match(inSetFunc, SQLTools.SQLMAPPING_INNERJOIN_PARAMETER_PATTERN) == null)
                    {
                    System.err.println("###Err: OntologicalAction_SQL.add_innerjoin : No Match SQLLINK_INNERJOIN_PARAMETER_PATTERN" +
                            "\n  " + SQLTools.SQLMAPPING_INNERJOIN_PARAMETER_PATTERN.toString() + "\n  " + inSetFunc.toString());
                    return false;
                    }
                sqlPredicat.add_sql_innerjoin(((StringConstant)inSetFunc.getParameter("primary")).stringValue(),
                        ((StringConstant)inSetFunc.getParameter("associat")).stringValue());
             
            }
            else return false;
        }
        return true;
    }
    
    /**
     * Ajoute les groupby au SQLPredicat
     */
    private boolean 
    add_groupby(SQLPredicat sqlPredicat, TermSet set) 
    {
        for (int i = 0; i < set.size(); i++)
        {
            sqlPredicat.add_sql_groupby(((StringConstant)set.getTerm(i)).stringValue());
        }
        return true;
    }
    
    
    /**
     * Set le variable de construction des tables
     */
    private boolean 
    set_createtable(SQLPredicat sqlPredicat, Constant tmp) 
    {
        String s = tmp.stringValue();
        if (s.compareTo("on") == 0 || s.compareTo("true") == 0)
        {
            sqlPredicat.set_create_table(true);
            return true;
        }
        else if (s.compareTo("off") == 0 || s.compareTo("false") == 0)
        {
            sqlPredicat.set_create_table(false);
            return true;
        }
        return false;
    }
    
    
    /**
     * Set le variable de fermeture du predicat
     */
    private boolean 
    set_closedtable(SQLPredicat sqlPredicat, Term tmp) 
    {
        FunctionalTermParamNode inFunc = (FunctionalTermParamNode) tmp.getSimplifiedTerm();
        if (SL.match(inFunc, SQLTools.SQLMAPPING_COLSEDTABLE_PARAMETER_PATTERN) == null)
            {
            System.err.println("###Err: OntologicalAction_SQL.set_closedtable : No Match SQLMAPPING_COLSEDTABLE_PARAMETER_PATTERN" +
                    "\n  " + SQLTools.SQLMAPPING_COLSEDTABLE_PARAMETER_PATTERN.toString() + "\n  " + inFunc.toString());
            return false;
            }
        SQLNodeColumn colNode;
        sqlPredicat.add_sql_innerjoin(((StringConstant)inFunc.getParameter("primary")).stringValue(),
                "Closed_" + ((PredicateNode)sqlPredicat.get_pattern()).as_symbol().toString() + ".id_predicat");
        colNode = new SQLNodeColumn(SQLTools.CLOSEDTABLE_DEFAULT_BELIEF_METAVAR,
                "Closed_" + ((PredicateNode)sqlPredicat.get_pattern()).as_symbol().toString()+ "." + SQLTools.CLOSEDTABLE_DEFAULT_BELIEF_METAVAR);
        colNode.set_type(SQLSimpleType.INT);
        sqlPredicat.add_sql_mapping(colNode);    
        sqlPredicat.set_closed_table(false);
        return true;
    }
    
    
    private boolean 
    creat_KB_filter(SQLService sqlservice, SQLPredicat sqlPredicat)
    {
        int             metaref_in_predicat;
        KBQueryFilter   qfilter;
        KBAssertFilter  afilter; 
        
        metaref_in_predicat = SL.match(sqlPredicat.get_pattern(), sqlPredicat.get_pattern()).size();

        sqlservice.add_associat_link(sqlPredicat.get_pattern().toString());
         
        qfilter = new KBQueryFilter_SQLDefault(sqlPredicat);
        ((FilterKBase)this._sc.getMyKBase()).addKBQueryFilter(qfilter); 
        this._sqlkbase.add_sqlFilter(((PredicateNode)sqlPredicat.get_pattern()).as_symbol().toString() + "Q", metaref_in_predicat, qfilter);

        if (!this._is_math_pred)
            afilter = new KBAssertFilter_SQLDefault(sqlPredicat);
        else
        {
            sqlPredicat.set_is_math(true);
            afilter = new KBAssertFilter_SQLNull(sqlPredicat);
        }
        ((FilterKBase)this._sc.getMyKBase()).addKBAssertFilter(afilter); 
        this._sqlkbase.add_sqlFilter(((PredicateNode)sqlPredicat.get_pattern()).as_symbol().toString() + "A", metaref_in_predicat, afilter);
        if (sqlPredicat.is_closed_table())
        {
            this._sc.getMyKBase().addClosedPredicate(sqlPredicat.get_pattern());
        }
        
        return true;
    }
    
    
    public void 
    perform(OntoActionBehaviour behaviour)
    {
        Term            tmp;
        String          servicename;
        TermSet         set;
       
        SQLService      sqlservice;
        StringConstant  formula;
        PredicateNode   slPredicat;
        SQLPredicat     sqlPredicat;
       
        
        try
        {
            switch (behaviour.getState()) 
            {
                case OntoActionBehaviour.START:
                    
                    formula = (StringConstant)getActionParameter("formula");
                    slPredicat = (PredicateNode)SL.formula(formula.stringValue());
                    
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
                    sqlPredicat = new SQLPredicat(sqlservice, slPredicat);

                    set = (TermSet)getActionParameter("mappings");
                    if (!add_mapping(sqlPredicat, set, slPredicat))
                    {   
                        System.err.println("#####Err: OntologicalAction_SQL.perform : Bad varlink parameter :" + slPredicat.toString());
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;                       
                    }
                    set = (TermSet)getActionParameter("innerjoins");
                    if (set != null && !add_innnerjoin(sqlPredicat, set))
                    {   
                        System.err.println("#####Err: OntologicalAction_SQL.perform : Bad tablink parameter "+ slPredicat.toString());
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;                       
                    }
                    tmp = getActionParameter("closedtable");
                    if (tmp != null)
                    {
                        if (this._is_math_pred)
                        {
                            System.err.println("#####Err: OntologicalAction_SQL.perform : closedtable can't used with :mat " + slPredicat.toString());
                            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                            break;                       
                        }
                        if (!set_closedtable(sqlPredicat, tmp))
                        {
                            System.err.println("#####Err: OntologicalAction_SQL.perform : Bad closedtable parameter " + slPredicat.toString());
                            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                            break;                       
                        }
                    }
                    tmp = getActionParameter("createtable");
                    if (tmp != null)
                    {
                        if (this._is_math_pred)
                        {
                            System.err.println("#####Err: OntologicalAction_SQL.perform : Createtable can't used with :mat " + slPredicat.toString());
                            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                            break;                       
                        }
                        if (!set_createtable(sqlPredicat, (Constant)tmp))
                        {
                            System.err.println("#####Err: OntologicalAction_SQL.perform : Bad createtable parameter " + slPredicat.toString());
                            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                            break;                       
                        }
                    }
                    set = (TermSet)getActionParameter("groupby");
                    if (set == null)
                    {
                        if (this._is_math_pred)
                        {
                            System.err.println("#####Err: OntologicalAction_SQL.perform : option :mat must be used with :groupby " + slPredicat.toString());
                            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                            break;                       
                        }
                    } else {
                        if (!this._is_math_pred || !add_groupby(sqlPredicat, set))
                        {
                            System.err.println("#####Err: OntologicalAction_SQL.perform : Bad groupby parameter " + slPredicat.toString());
                            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                            break;                       
                        }
                    }
                    if (!sqlPredicat.finalizeInit())
                    {   
                        System.err.println("#####Err: OntologicalAction_SQL.perform : Bad finalize SQLpredicate" + slPredicat.toString());
                        behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
                        break;                       
                    }

                    creat_KB_filter(sqlservice, sqlPredicat);

                    behaviour.setState(OntoActionBehaviour.SUCCESS);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            behaviour.setState(OntoActionBehaviour.FEASIBILITY_FAILURE);
        }
    }
}
    
   

