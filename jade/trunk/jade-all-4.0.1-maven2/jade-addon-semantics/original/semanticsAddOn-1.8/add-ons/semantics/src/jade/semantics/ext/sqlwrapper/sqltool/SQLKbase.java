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

package jade.semantics.ext.sqlwrapper.sqltool;

import jade.semantics.ext.sqlwrapper.sqltool.action.OntologicalAction_AddSqlMapping;
import jade.semantics.ext.sqlwrapper.sqltool.action.OntologicalAction_AddSqlService;
import jade.semantics.ext.sqlwrapper.sqltool.action.OntologicalAction_AdminSqlService;
import jade.semantics.ext.sqlwrapper.sqltool.action.OntologicalAction_RemoveSqlMapping;
import jade.semantics.ext.sqlwrapper.sqltool.action.OntologicalAction_RemoveSqlService;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService;
import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.kbase.filters.KBFilter;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SQLKbase extends FilterKBaseImpl
{
    private Map<String,SQLService>  _serviceMap; 
    private List<PredicateNode>     _predicateList;
    private Map<String,KBFilter>    _filterMap; 
    
    
    public SQLKbase(SemanticCapabilities sc)
    {
        super(sc.getMyKBase());
        
        sc.getMySemanticActionTable().addSemanticAction(new OntologicalAction_AddSqlMapping(sc,this));
        sc.getMySemanticActionTable().addSemanticAction(new OntologicalAction_RemoveSqlMapping(sc,this));
        sc.getMySemanticActionTable().addSemanticAction(new OntologicalAction_AddSqlService(sc,this));
        sc.getMySemanticActionTable().addSemanticAction(new OntologicalAction_RemoveSqlService(sc,this));
        sc.getMySemanticActionTable().addSemanticAction(new OntologicalAction_AdminSqlService(sc,this));
        initialize_list();
    }

    /*
     * ----------------------------------------------------------
     */
    
    /* TODO: gestion des observers sur les predicat SQL*/
    
//    public void addObserver(final Observer obs)
//    {
//        super.getContinuous().addObserver(obs);
//        Formula[] formulas = obs.getObservedFormulas();
//        for (int i=0; i<formulas.length; i++) {
//            Set result = new SortedSetImpl();
//            getObserverTriggerPatterns(formulas[i], result);    
//            for (Iterator it = result.iterator(); it.hasNext();) {
//                obs.addFormula((Formula)it.next());
//            }
//        }
//      
//    }
//  
//    /**
//     * @inheritDoc
//     */
//    public void removeObserver(Finder finder) {
//        super.getContinuous().removeObserver(finder);
//        
//       
//    } 
//
//    /**
//     * @inheritDoc
//     */
//    public void removeObserver(Observer obs) {
//        super.getContinuous().removeObserver(obs);
//        
//       
//    }
    
    /*
     * ----------------------------------------------------------
     */

    private void 
    initialize_list()
    {
        this._serviceMap = new HashMap<String, SQLService> ();
        this._predicateList = new ArrayList<PredicateNode> ();
        this._filterMap = new HashMap<String, KBFilter> ();
    }
    
    /*
     * ----------------------------------------------------------
     */

    /**
     * Ajout un service dans la gestion d'instance
     * @param name nom du service
     * @param sqls class du service associer.
     */
    public void
    add_sqlService(String name, SQLService sqls)
    { this._serviceMap.put(name, sqls); }
    
    /**
     * @param name nom du service
     * @return la class service associer
     */
    public SQLService
    get_sqlService(String name)
    { return this._serviceMap.get(name); }

    /*
     * ----------------------------------------------------------
     */

    /**
     * Ajout un predicat a la list des predicat mappé
     */
    public void 
    add_sqlPredicate (PredicateNode slPredicat)
    { this._predicateList.add(slPredicat); }

    public boolean 
    is_in_sqlPredicate_list (PredicateNode slPredicat)
    { return this._predicateList.contains(slPredicat); }
    
    /**
     * ajout un filtre dans la gestion d'instance
     * @param name nom du filtre, de la forme "nom_predicat{A|Q}"
     * @param nb_metavar nombre de meta variable du predicat
     * @param f le filstre assicier
     */
    public void
    add_sqlFilter(String name, int nb_metavar, KBFilter f)
    { this._filterMap.put(name + nb_metavar, f); }
   
    /**
     * Supprimer les instance du filtre dans la KBase
     * @param slPredicat predicat associé
     */
    public void 
    remove_sql_mapping(PredicateNode slPredicat, SemanticCapabilities sc)
    {
        final PredicateNode slPred = slPredicat;
        MatchResult     metarefInPred;
        
        if (!is_in_sqlPredicate_list(slPredicat))
            return; 
        this._predicateList.remove(slPredicat);
        metarefInPred = SL.match(slPredicat, slPredicat);
        this.remove_sqlAFilter(slPredicat.as_symbol().toString(), metarefInPred.size(), sc);
        this.remove_sqlQFilter(slPredicat.as_symbol().toString(), metarefInPred.size(), sc);
        sc.getMyKBase().removeClosedPredicate(new Finder() { public boolean identify(Object object) { return slPred == object; } });
    }
    
    private void
    remove_sqlQFilter(String name, int nb_metavar, SemanticCapabilities sc)
    { 
        final KBFilter f;
        f = this._filterMap.remove(name + "Q" + nb_metavar); 
        if (f != null)
            ((FilterKBase)sc.getMyKBase()).removeKBQueryFilter(new Finder() {
                public boolean identify(Object object) { return f == object; } });
    }

    private void
    remove_sqlAFilter(String name, int nb_metavar, SemanticCapabilities sc)
    { 
        final KBFilter f;
        f = this._filterMap.remove(name + "A" + nb_metavar); 
        if (f != null)
            ((FilterKBase)sc.getMyKBase()).removeKBAssertFilter(new Finder() { public boolean identify(Object object) { return f == object; } });
    }
}

