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

package jade.semantics.ext.sqlwrapper.sqltool.imp;

import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Utiliser cette classe pour definir un predicat ratacher a une vue SQL
 * @author Julien Schluth - France Telecom
 *
 */
public class SQLPredicat
{
/*
 * Private Var
 */
    private SQLService                              _sql_service;
    private Formula                                 _pattern; 
    private boolean                                 _create_table;
    private boolean                                 _closed_table;
    private boolean                                 _is_math;
    
    private ArrayList<SQLNodeColumn>                _sql_list_column;
    private ArrayList<SQLNodeTable>                 _sql_list_table;
    private ArrayList<String>                       _sql_list_groupby;
    private SQLNodeTable                            _sql_link_racine;
    
    private Map<MetaTermReferenceNode, MetaTermReferenceNode>   _select_column_list;
    private Map<MetaTermReferenceNode, Constant>                _where_column_list;
    private Map<MetaTermReferenceNode, Term>                    _tmp_select_list;
 

    
    
/*
 * -------------------------------------------------------------------
 */    

    /**
     * Initalisatino du SQLPredicat
     * @param sqls ServiceSQL connecter a la BDD
     */
    private void
    requette_init(SQLService sqls)
    {
        if (sqls == null)
            System.err.println("###Err: SqlPredicat: SQLService == NULL");

        this._sql_service = sqls;
        //this._sql_var_link = new HashMap<String, String>();
        this._sql_list_column = new ArrayList<SQLNodeColumn>();
        this._sql_list_table = new ArrayList<SQLNodeTable>();
        this._sql_list_groupby = new ArrayList<String>();
        this._select_column_list = new HashMap<MetaTermReferenceNode, MetaTermReferenceNode>();
        this._where_column_list = new HashMap<MetaTermReferenceNode, Constant>();
        this._tmp_select_list = new HashMap<MetaTermReferenceNode, Term>();

        this._is_math = false;
        this._create_table = false;
        this._closed_table = true;
    }
    
    /**
     * Constructeur
     * @param sqls Service que le predicat utilisera pour excuter la requette
     * @param pattern Patern du predicat
     */
    public SQLPredicat(SQLService sqls, Formula pattern)
    {
        this._pattern = pattern; 
        this.requette_init(sqls);
    }

    public SQLPredicat(SQLService sqls, String spattern)
    {
        Formula pattern = SL.formula(spattern);
        this._pattern = pattern; 
        this.requette_init(sqls);
    }
    
   
/*
 * -------------------------------------------------------------------
 */    

    /**
     * Definie si le SQLpredicat est un predicat mathematique . 
     */
    public void    
    set_is_math(boolean b)
    { this._is_math = b; }
    
    /**
     * Definie si le SQLpredicat cree les table dans la bass de donnée ou non (defaus: False)
     */
    public void 
    set_create_table(boolean b)
    { 
        this._create_table = b; 
    }
    
    /**
     * Definie si le SQLpredicat est une predicat ouvert ou ferme (defaus: False)
     */
    public void 
    set_closed_table(boolean b)
    { this._closed_table = b; }
  
    public boolean
    is_closed_table()
    { return this._closed_table; }
    
     /**
      * Ajout d'une liaison entre une MetaTerm.name et une Table.Champ
      */
     public void 
     add_sql_mapping(SQLNodeColumn colNode)
     { 
         this._sql_list_column.add(colNode);
     }
    
    /** 
     * Ajout d'une Fonction de JOIN entre 2 table SQL
     */
    public void 
    add_sql_innerjoin(String tab1, String tab2)
    { 
        this._sql_list_table.add(new SQLNodeTable(tab1,tab2));
    }

    /** 
     * Ajout d'une Fonction de GROUPBY
     */
    public void 
    add_sql_groupby(String tab1)
    { 
        this._sql_list_groupby.add(tab1);
    }

    
    
/*
 * ---------------------------------------------
 */    

    /**
     * Permet de completer la liste des Noued represantant une Table.
     * Elle complete avec les tables induite par les liason 
     */
    private void 
    Completed_list_tableNode()
    {
        LinkedList<SQLNodeTable> tmp_list_linknode = new LinkedList<SQLNodeTable>();
        boolean                 test;

        for (SQLNodeTable tmp : this._sql_list_table)
        {
            test = false;
            for (SQLNodeTable tmp2 : this._sql_list_table)
            {
                if (tmp.get_fatherName() != null && tmp != tmp2 && tmp.get_fatherName().compareTo(tmp2.get_tabName()) == 0)
                    test = true;
            }
            if (test == false)
                for (SQLNodeTable tmp2 : tmp_list_linknode)
                    if (tmp.get_fatherName() != null && tmp != tmp2 && tmp.get_fatherName().compareTo(tmp2.get_tabName()) == 0)
                        test = true;
            if (test == false)
                tmp_list_linknode.add(new SQLNodeTable(tmp.get_fatherName()));
        }
        this._sql_list_table.addAll(tmp_list_linknode);
    }
    
    /**
     * Construi l'arboraisance des Tables permetant de represanter les liasons entre elle.
     * @return
     */
    private SQLNodeTable
    creat_mappingComplexeArbo()
    {
        SQLNodeTable             res = null;
        
        this.Completed_list_tableNode();
        
        for (SQLNodeTable tmp : this._sql_list_table)
        {
            for (SQLNodeTable tmp2 : this._sql_list_table)
            {
                if (tmp != tmp2)
                {
                    if (tmp.get_fatherName() != null && tmp.get_fatherName().compareTo(tmp2.get_tabName()) == 0)
                        tmp.set_fatherTable(tmp2);
                    if (tmp2.get_fatherName() != null && tmp.get_tabName().compareTo(tmp2.get_fatherName()) == 0)
                        tmp.add_childTable(tmp2);
                }
            }
        }
        
        res = this._sql_list_table.get(0);
        while (res.get_fatherTable() != null)
            res = res.get_fatherTable();

        return res;
    }

    /**
     * Construi un Node represantant une table unique utiliser par le predicat
     * @return
     */
    private SQLNodeTable
    creat_mappingNormalArbo()
    {
        SQLNodeTable             res = null;
        String                  tmp;

        tmp = this._sql_list_column.get(0).get_mapping_name();
        tmp = tmp.substring(0, tmp.indexOf('.'));
        res = new SQLNodeTable(tmp);
        
        return res;
    }
    
    /**
     * Creation de l'arboresance represantant les table et les laisons entre les tables.
     *
     */
    private boolean 
    creat_mappingArbo()
    {
        if (this._sql_list_table.size() != 0)
            this._sql_link_racine = creat_mappingComplexeArbo();
        else 
            this._sql_link_racine = creat_mappingNormalArbo();
        

        for (SQLNodeColumn tmp : this._sql_list_column)
        {
            if (!this._sql_link_racine.accept_varRef(tmp))
            {
                System.err.println("##Err:SQLPredicat.Creat_linkArbo : Bad var Ref Link: " + tmp.get_mapping_name());
                return false;
            }
        }
        for (String tmp : this._sql_list_groupby)
        {
            if (!this._sql_link_racine.accept_varRef(new SQLNodeColumn(tmp)))
            {
                System.err.println("##Err:SQLPredicat.Creat_linkArbo : Bad group Ref Link: " + tmp);
                return false;
            }
        }
        for (SQLNodeTable tmp : this._sql_list_table)
        {
            if (tmp.get_usedcount() <= 0 && tmp.get_childcount() <= 0)
            {
                System.err.println("##Err:SQLPredicat.Creat_linkArbo : Table Not Used : " + tmp.get_tabName());
                return false;
            }
        }
        return true;
    }
    
    /**
     * Cette fonction permet finaliser le predicat
     * @return
     */
    public boolean
    finalizeInit()
    {
        if (!this.creat_mappingArbo()) return false;
        if (!this._sql_link_racine.testCheckBDD(this._sql_service))
            return false;
        if (this._create_table) 
            this._sql_link_racine.synchonisedBDD(this._sql_service);
        if (!this._sql_link_racine.testSynchoBDD(this._sql_service))
            return false;
        return true;
    }
    
/*
 * ----------------------------------
 */    
    
    public Formula              
    get_pattern() { return _pattern; }
        
    /**
     * Permet de clean la requette interne du SQLPredicat
     * A utiliser avant chaque nouvelle construction de requette
     */
    private void
    requette_clean()
    {
        this._select_column_list.clear();
        this._where_column_list.clear();
        this._tmp_select_list.clear();
    }
   
    /**
     * Lors de la construction d'une nouvelle requette utiliser cette 
     * fonction pour rensegier le SqlPredicat que le MetaTerm ref est en 
     * Selection sur le MetaTerm out  
     * @param ref metaTerm interne
     * @param out metaTerm externe
     */
    private void
    add_select_var(MetaTermReferenceNode ref, MetaTermReferenceNode out)
    { this._select_column_list.put(ref,out); }

    /**
     * Lors de la construction d'une nouvelle requette utiliser cette 
     * fonction pour indiquer le le MetaTerm ref a pour valeur def
     * @param ref
     * @param def
     */
    private void
    add_where_var(MetaTermReferenceNode ref, Constant def)
    { this._where_column_list.put(ref,def); }

    /** 
     * Lors de la construction d'un nouvelle requette et l'utilisation d'un
     * mapping de type: SLTERM  la list _tmp_select_list contient les metavariables
     * temporaire qui permete d'aller cherche les TERM dans la BDD.
     */
    private void
    add_tmp_select_var(MetaTermReferenceNode ref, Term def)
    { this._tmp_select_list.put(ref,def); }

    
    private Term
    is_a_tmp_select_var(MetaTermReferenceNode ref)
    {
        for(MetaTermReferenceNode tmp : this._tmp_select_list.keySet())   
        {
            if (tmp.lx_name().compareTo(ref.lx_name()) == 0)
                return this._tmp_select_list.get(tmp);
        }
        return null;
    }
    
    /**
     * Cette fonction permet de definir les metavariables utiliser pour la nouvelle requette
     * @param metavar_res Contien les infos sur les nouvelles meta variable
     * @return true if not Error
     */
    public boolean
    inform_metavar(MatchResult metavar_res)
    {
        this.requette_clean();
        int nb_var = this._sql_list_column.size(); 

        for (int i = 1; i < nb_var + 1; i++)
            {
                if (metavar_res.get(i) instanceof MetaTermReferenceNode)
                {
                    MetaTermReferenceNode tested_metavar = (MetaTermReferenceNode) metavar_res.get(i);
                    if (tested_metavar.sm_value() instanceof MetaTermReferenceNode)
                        this.add_select_var(tested_metavar, (MetaTermReferenceNode)(tested_metavar.sm_value()));
                    else 
                    {
                        if (SQLNodeColumn.type_of_col(tested_metavar.lx_name(), this._sql_list_column) == SQLService.SQLSimpleType.SLTERM)
                            this.add_where_var(tested_metavar, SL.string(((Term)tested_metavar.sm_value()).toString()));
                        else if (tested_metavar.sm_value() instanceof Constant)
                            this.add_where_var(tested_metavar, (Constant)(tested_metavar.sm_value()));
                        else if (tested_metavar.sm_value() instanceof Term)
                            this.add_where_var(tested_metavar, SL.string(((Term)tested_metavar.sm_value()).toString()));
                        else
                        {
                            System.err.println("###Err:SQLPredicat.inform_metavar : Bad MetaTerm sm_value class: "
                                    + tested_metavar.sm_value().getClass().toString());
                            return false;
                        }
                    }    
                }
                else
                {
                    System.err.println("###Err:SQLPredicat.inform_metavar Bad MetaTerm: " + metavar_res.toString());
                    return false;
                }
            }
        return true;
    }
  
    
    /**
     * Cette fonction permet de definir les metavariables utiliser pour la nouvelle requette
     * @param metavar_res Contien les infos sur les nouvelles meta variable
     * @return true if not Error
     */
    public boolean
    inform_metavar_for_query(MatchResult metavar_res)
    {
        MetaTermReferenceNode tested_metavar;
        MetaTermReferenceNode tmp_metavar;
        Term                  slterm;
        MatchResult           sl_metaref;
        int nb_var;
        
        this.requette_clean();
        nb_var = this._sql_list_column.size(); 

        for (int i = 1; i < nb_var + 1; i++)
            {
                if (metavar_res.get(i) instanceof MetaTermReferenceNode)
                {
                    tested_metavar = (MetaTermReferenceNode) metavar_res.get(i);
                    if (tested_metavar.sm_value() instanceof MetaTermReferenceNode)
                    {
                        this.add_select_var(tested_metavar, (MetaTermReferenceNode)(tested_metavar.sm_value()));
                    }
                    else if (SQLNodeColumn.type_of_col(tested_metavar.lx_name(), this._sql_list_column) == SQLService.SQLSimpleType.SLTERM)
                    {
                        slterm = (Term)tested_metavar.sm_value();
                        sl_metaref = SL.match(slterm, slterm);
                        if (sl_metaref.size() > 0)
                        {
                              tmp_metavar = new MetaTermReferenceNode("__tmp_slterm_" + tested_metavar.lx_name());
                              this.add_tmp_select_var(tmp_metavar, slterm);
                              this.add_select_var(tested_metavar, tmp_metavar);
                        }
                        else 
                        {
                            this.add_where_var(tested_metavar, SL.string(((Term)tested_metavar.sm_value()).toString()));
                        }
                    }
                    else if (tested_metavar.sm_value() instanceof Constant)
                    {
                        this.add_where_var(tested_metavar, (Constant)(tested_metavar.sm_value()));
                    }
                    else
                    {
                        System.err.println("###Err:SQLPredicat.inform_metavar : Bad MetaTerm sm_value class: "
                                + tested_metavar.sm_value().getClass().toString());
                        return false;
                    }
                }
                else
                {
                    System.err.println("###Err:SQLPredicat.inform_metavar Bad MetaTerm: " + metavar_res.toString());
                    return false;
                }
            }
        return true;
    }    
    
    
/*
 * ----------------------------------
 */    
    
    private SQLNodeColumn
    Find_SQLlink_of_MetaTerm(MetaTermReferenceNode metaTerm)
    {
        for (SQLNodeColumn tmp: this._sql_list_column)
        {
            if (tmp.get_metavar_name().compareTo(metaTerm.lx_name()) == 0)
                return tmp;
        }
        System.err.println("###Err:SQLPredicat.Find_SQLlink_of_MetaTerm : not Foud metaTermReferance");
        return null;
    }
    
    
    /**
     * @return La requette SELECT SQL correpondant au SQLPerdicat construit.
     */
    private String
    get_selectRequette()
    {
        String          req = new String();
        boolean         check_separator;
        SQLNodeColumn   colnode; 
        
        req += "SELECT ";
        check_separator = false;
        if (this._select_column_list.isEmpty()) {
        	req += "*";
        }
        else {
        	for (MetaTermReferenceNode metaTerm : this._select_column_list.keySet())
        	{
        		if ((colnode = Find_SQLlink_of_MetaTerm(metaTerm)) == null)
        			return "";
        		if (check_separator) req += ", "; else check_separator = true;
        		if (!colnode.is_math())
        			req += colnode.get_mapping_name(); 
        		else 
        			req += colnode.get_math() + "(" + colnode.get_mapping_name() + ")";
        	}
        }
        req += " FROM " + this._sql_link_racine.toFromTerm();
        
        req += " WHERE 1";
        for (MetaTermReferenceNode metaTerm : this._where_column_list.keySet())
        {
            if ((colnode = Find_SQLlink_of_MetaTerm(metaTerm)) == null)
                return "";
            if (!colnode.is_math())
            {
                req += " AND "; 
                req += colnode.get_mapping_name();
                req += " = '";
                req += this._where_column_list.get(metaTerm).stringValue() + "'";
            }
        } 
        if (this._is_math)
        {
            req += " GROUP BY ";
            check_separator = false;
            for (String tmp : this._sql_list_groupby)
            {
                if (check_separator) req += ", "; else check_separator = true;
                req += tmp;
            }
            req += " HAVING 1";
            for (MetaTermReferenceNode metaTerm : this._where_column_list.keySet())
            {
                if ((colnode = Find_SQLlink_of_MetaTerm(metaTerm)) == null)
                    return "";
                if (colnode.is_math())
                {
                    req += " AND "; 
                    req += colnode.get_mapping_name();
                    req += " = '";
                    req += this._where_column_list.get(metaTerm).stringValue() + "'";
                }
            }     
        }
        req += ";";
        return req;
    }

    
    /**
     * Cette fonction a pour role de remarchter le contenue des metavariable
     * temporel, utiliser dans le mapping avec un SLTERM.
     * @return une liste de match results ou les variable temporel on etais remplacer
     * par ce qu'il faux. les match result ou le match et bad son ignoré.
     */
    private QueryResult
    rematch_term_in_listofmatchresults(QueryResult in_lmr)
    {
    	QueryResult             out_lmr; 
        MatchResult             one_res;
        MatchResult             join_res;
        MetaTermReferenceNode   tmp_metaterm;
        Term                    tmp_metaterm_pattern;
        boolean                 res_ok;
        
        out_lmr = new QueryResult();
        for (int i = 0; i < in_lmr.size(); i++)
        {
            one_res = (MatchResult)in_lmr.getResult(i);
            res_ok = false;
            for(int j = 0; j < one_res.size(); j++)
            {
                tmp_metaterm = (MetaTermReferenceNode)one_res.get(0);
                if ((tmp_metaterm_pattern = this.is_a_tmp_select_var(tmp_metaterm)) != null)
                {
                    res_ok = true;
                    one_res.remove(tmp_metaterm);
                    join_res = SL.match(tmp_metaterm_pattern,tmp_metaterm.sm_value());
                    if (join_res == null)
                    {
                        res_ok = false;
                    }
                    else
                    {
                        if (one_res.join(join_res) != null)
                            one_res = one_res.join(join_res);
                        else
                        res_ok = false;
                    }
                }
            }
            if (res_ok)
                out_lmr.add(one_res);
        }
        return out_lmr;
        
    }
    
    /**
     * Prepar et execute la requette !
     * @return une list des resulta de la requette !
     */
    public QueryResult
    query_execute()
    {
    	QueryResult      requette_res; 

        
        if (this._sql_service == null)
            System.err.println("###Err: SqlPredicat: SQLService == NULL");
        else
        {
            requette_res = this._sql_service.execQuery(this.get_selectRequette(), this._select_column_list, this._sql_list_column);
            if (this._tmp_select_list.size() == 0)
                {
                return requette_res;
                }
            return rematch_term_in_listofmatchresults(requette_res);
        }
//        return new QueryResult();
        return QueryResult.UNKNOWN; // NEW VL
    }
  
    /**
     * Execute l'assert des données informé
     * @return true if exec is OK
     */
    public boolean 
    assert_execute()
    {
        boolean ok;
        if (this._select_column_list.size() != 0)
            System.err.println("###Err:SQLPredicat.assert_execute: Bad Attribute List");
        for (MetaTermReferenceNode metaTerm : this._where_column_list.keySet())
        {
            ok = false;
            for (SQLNodeColumn tmp : this._sql_list_column)
            {
                if (tmp.get_metavar_name().compareTo(metaTerm.lx_name()) == 0)
                {
                    if (!tmp.set_tmp_content(this._where_column_list.get(metaTerm)))
                    {
                        System.err.println("###Err:SQLPredicat.assert_execute : type of MetaTerm: ?" + metaTerm.lx_name() + " in: " + this.get_pattern().toString());
                        return false; 
                    }
                    ok = true;
                }
            }
            if (!ok)
                {
                System.err.println("###Err:SQLPredicat.assert_execute : Not Referancede MetaTerm:" + metaTerm.lx_name());
                return false;
                }
        } 
        return this._sql_link_racine.assert_inSQL(this._sql_service);
    }
    
    /**
     * Execute la suppretion des données informé
     * @return true if exec is OK
     */
    public boolean 
    remove_execute()
    {
        if (this._select_column_list.size() != 0)
            System.err.println("###Err:SQLPredicat.assert_execute: Bad Attribute List");
        for (MetaTermReferenceNode metaTerm : this._where_column_list.keySet())
        {
            for (SQLNodeColumn tmp : this._sql_list_column)
            {
                if (tmp.get_metavar_name().compareTo(metaTerm.lx_name()) == 0)
                {
                    tmp.set_tmp_content(this._where_column_list.get(metaTerm));
                    this._sql_link_racine.reference_varRef(tmp);
                }
            }
        } 
        return this._sql_link_racine.remove_inSQL(this._sql_service);
    }
    

/*
 * Debug 
 */   
    public String 
    toString()
    {
        String res; 
        
        res = "### " + this._pattern.toString() + " ###\n";
        if (this._is_math == true)
            res = "## is_math = true \n";
        if (this._closed_table == true)
            res = "## closed = true \n";
            
        res += this._sql_link_racine; 
        return res;
    }
    
}

