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

import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FunctionalTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

public class SQLTools
{

	// Note: an SQL wrapper may only be installed on a Semantic Agent using
	// a subclass of DefaultCapabilities, otherwise, a ClassCastException is
	// thrown
    public static void Install_SQL_Kbase(SemanticAgent a)
    {
    	DefaultCapabilities sc = (DefaultCapabilities)a.getSemanticCapabilities();
    	sc.setKBase(new SQLKbase(sc));
    }
    
/*
 * ----------------------------------------------------------
 */
    
    private static ActionExpression ACTION_PATTERN = (ActionExpression)
    SL.term("(action ??sender ??action)");

    private static Formula ME_ACTION_PATTERN = 
    SL.formula("(I ??myself (done (action ??myself ??action)))");
  
/*
 * ----------------------------------------------------------
 */
    public static FunctionalTerm 
    ADD_SQLSERVICE_ACTION_PATTERN = (FunctionalTerm)SL.term(
            "(ADD_SQL_SERVICE" + 
                " :name ??name" +
                " :driver ??driver" +
                " :path ??path" +
                " :user ??user" +
                " :pass ??pass" +
                ")").getSimplifiedTerm(); 

            
    public static FunctionalTerm
    REMOVE_SQLSERVICE_ACTION_PATTERN = (FunctionalTerm)SL.term(
            "(REMOVE_SQL_SERVICE " +
                " :name ??name" +
                ")").getSimplifiedTerm(); 
    
 
    public static FunctionalTerm
    ADMIN_SQLSERVICE_ACTION_PATTERN = (FunctionalTerm)SL.term(
            "(ADMIN_SQL_SERVICE " +
                " (::? :sqlservice ??sqlservice)" +
                " (::? :cleantable ??cleantable)" +
                " (::? :deletetable ??deletetable)" +
                ")").getSimplifiedTerm(); 
    
    
    
   public static FunctionalTerm 
   ADD_SQLMAPPING_ACTION_PATTERN = (FunctionalTerm)SL.term(
            "(ADD_SQL_MAPPING" +
                " (::? :sqlservice ??service)" + //default = myservice
                " (::? :createtable ??createtable)" + //default = off
                " (::? :closedtable ??closedtable)" + //default = off
                " :formula ??formula" +
                " (::? :mappings ??mappings)" +
                " (::? :innerjoins ??innerjoins)" +
                " (::? :groupby ??groupby)" +
                ")").getSimplifiedTerm(); 
              
                
   public static FunctionalTerm
   SQLMAPPING_MAPPING_PARAMETER_PATTERN = (FunctionalTerm)SL.term(
           "(MAPPING " +
                " :metavar ??metavar" +
                " :sqlref ??sqlref" +
                " (::? :type ??type)" + 
                " (::? :math ??math)" +
                ")").getSimplifiedTerm(); 
    
   public static FunctionalTerm
   SQLMAPPING_INNERJOIN_PARAMETER_PATTERN = (FunctionalTerm)SL.term(
           "(INNERJOIN " +
                " :associat ??associat" +
                " :primary ??primary" +
                ")").getSimplifiedTerm(); 
   
   
   public static FunctionalTerm
   SQLMAPPING_COLSEDTABLE_PARAMETER_PATTERN = (FunctionalTerm)SL.term(
           "(CLOSEDTABLE " +
                " :primary ??primary" +
                ")").getSimplifiedTerm(); 
   public static String CLOSEDTABLE_DEFAULT_BELIEF_METAVAR = "belief_value";
   
    public static FunctionalTerm
    REMOVE_SQLMAPPING_ACTION_PATTERN = (FunctionalTerm)SL.term(
            "(REMOVE_SQL_MAPPING " +
                " (::? :sqlservice ??service)" +
                " :formula ??formula" +
                ")").getSimplifiedTerm(); 
 
    
    /*
     * ----------------------------------------------------------
     */

    private static void
    actionInterpreteMe(SemanticCapabilities sc, Term action)
    {
        try 
        {
            Formula addsqllink= (Formula)SL
                    .instantiate(ME_ACTION_PATTERN,
                            "action", action);
            sc.interpret(addsqllink);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private static void
    actionRequestAuther(SemanticCapabilities sc, Term action, Term agent)
    {
        try 
        {
            ActionExpression addsqllink= (ActionExpression)SL
                    .instantiate(ACTION_PATTERN,
                            "sender", agent,
                            "action", action);
            sc.request(addsqllink, agent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void
    execaction(SemanticCapabilities sc, Term sqlLink_Action, Term agent)
    {
        if (agent == null)
            actionInterpreteMe(sc, sqlLink_Action);
        else 
            actionRequestAuther(sc,sqlLink_Action, agent);
    }
    
    /*
     * ----------------------------------------------------------
     */

    public static void
    createSQLmapping(SemanticCapabilities sc, String sqlLink_Action)
    {
        createSQLmapping(sc,null, sqlLink_Action);
    }

    public static void
    createSQLmapping(SemanticCapabilities sc, Term agent, String sqlLink_Action)
    {
        try
        {
            Term sqllink = SL.term(sqlLink_Action).getSimplifiedTerm();
            if (SL.match(sqllink, ADD_SQLMAPPING_ACTION_PATTERN) != null)
                execaction(sc,sqllink,agent);
            else
                System.err.println("###Err: SQLTools.CreateSQLlink: Bad SQLmapping_Action matching : \n" +
                        "       " + sqllink.toString() );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("###Err: SQLTools.CreateSQLlink: Bad SQLmappin_Action Syntaxe");
        }
    }
    
    /*
     * ----------------------------------------------------------
     */
    
    public static void
    createSQLService(SemanticCapabilities sc, String sqlService_Action)
    {
        createSQLService(sc,null, sqlService_Action);
    }
    
    
    public static void
    createSQLService(SemanticCapabilities sc, Term agent, String sqlService_Action)
    {
        try
        {
            Term sqllink = SL.term(sqlService_Action).getSimplifiedTerm();
            if (SL.match(sqllink, ADD_SQLSERVICE_ACTION_PATTERN) != null)
                execaction(sc, sqllink, agent);
            else
                System.err.println("###Err: SQLTools.CreateSQLService: Bad SQLLink_Action matching : \n" +
                        "       " + sqllink.toString() );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("###Err: SQLTools.CreateSQLService: Bad SQLLink_Action Syntaxe");
        }
    }
    
    /*
     * ----------------------------------------------------------
     */

    public static void
    removeSQLmapping(SemanticCapabilities sc, String sqlLink_Action)
    {
        removeSQLmapping(sc,null, sqlLink_Action);
    }

    public static void
    removeSQLmapping(SemanticCapabilities sc, Term agent, String sqlLink_Action)
    {
        try
        {
            Term sqllink = SL.term(sqlLink_Action).getSimplifiedTerm();
            if (SL.match(sqllink, REMOVE_SQLMAPPING_ACTION_PATTERN) != null)
                execaction(sc,sqllink,agent);
            else
                System.err.println("###Err: SQLTools.removeSQLmapping: Bad removeSQLmapping_Action matching : \n" +
                        "       " + sqllink.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("###Err: SQLTools.removeSQLmapping Bad removeSQLmapping_Action Syntaxe");
        }
    }
    
    
    /*
     * ----------------------------------------------------------
     */

    public static void
    removeSQLService(SemanticCapabilities sc, String sqlLink_Action)
    {
        removeSQLService(sc,null, sqlLink_Action);
    }

    public static void
    removeSQLService(SemanticCapabilities sc, Term agent, String sqlLink_Action)
    {
        try
        {
            Term sqllink = SL.term(sqlLink_Action).getSimplifiedTerm();
            if (SL.match(sqllink, REMOVE_SQLSERVICE_ACTION_PATTERN) != null)
                execaction(sc,sqllink,agent);
            else
                System.err.println("###Err: SQLTools.removeSQLservice: Bad removeSQLService_Action matching : \n" +
                        "       " + sqllink.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("###Err: SQLTools.removeSQLservice: Bad removeSQLService_Action Syntaxe");
        }
    }
    
    /*
     * ----------------------------------------------------------
     */

    public static void
    AdminSQLService(SemanticCapabilities sc, String sqlLink_Action)
    {
        AdminSQLService(sc,null, sqlLink_Action);
    }

    public static void
    AdminSQLService(SemanticCapabilities sc, Term agent, String sqlLink_Action)
    {
        try
        {
            Term sqllink = SL.term(sqlLink_Action).getSimplifiedTerm();
            if (SL.match(sqllink, ADMIN_SQLSERVICE_ACTION_PATTERN) != null)
                execaction(sc,sqllink,agent);
            else
                System.err.println("###Err: SQLTools.adminSQLservice: Bad adminSQLService_Action matching : \n" +
                        "       " + sqllink.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("###Err: SQLTools.adminSQLservice: Bad adminSQLService_Action Syntaxe");
        }
    }
}

