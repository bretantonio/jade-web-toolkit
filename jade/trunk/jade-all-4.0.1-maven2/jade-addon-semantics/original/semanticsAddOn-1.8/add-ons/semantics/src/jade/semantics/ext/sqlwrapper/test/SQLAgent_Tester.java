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

import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

import java.text.SimpleDateFormat;
import java.util.Date;



public class SQLAgent_Tester extends SemanticAgent
{
	
   
	//---------------------------------------------------------------
	//                      PUBLIC
	//---------------------------------------------------------------
	
	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public SQLAgent_Tester() 
	{
		setSemanticCapabilities(new SQLCapabilities());
	}

	
	//---------------------------------------------------------------
	//                      ADD SEMANTIC CAPABILITIE 
	//---------------------------------------------------------------
	public class SQLCapabilities extends DefaultCapabilities
	{
        
//         protected SemanticActionTable setupSemanticActions() 
//            {
//                SemanticActionTable table;
//                
//                table = super.setupSemanticActions();
//             
////                SQLKbase tmp = new SQLKbase(this);
////                
////                table.addSemanticAction(new OntologicalAction_AddSqlMapping(this,tmp));
////                table.addSemanticAction(new OntologicalAction_RemoveSqlMapping(this,tmp));
////                table.addSemanticAction(new OntologicalAction_AddSqlService(this,tmp));
////                table.addSemanticAction(new OntologicalAction_RemoveSqlService(this,tmp));
////                table.addSemanticAction(new OntologicalAction_AdminSqlService(this,tmp));
//
//                return table;
//            } /* !setupSemanticAction */

            
            
		protected KBase setupKbase() 
		{
			FilterKBase res_kbase;
			res_kbase = (FilterKBase)super.setupKbase();

            res_kbase.addKBAssertFilter(new KBAssertFilter_query());
            return res_kbase;
		} 
		
		protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() 
		{
			SemanticInterpretationPrincipleTable res_table;
			
			res_table = super.setupSemanticInterpretationPrinciples();
            
			res_table.addSemanticInterpretationPrinciple(new SIP_TestActionNotDone(this));
            res_table.addSemanticInterpretationPrinciple(new SIP_TestActionDone(this));
			res_table.addSemanticInterpretationPrinciple(new SIP_TestQuerry(this));
			return res_table;
		} 
        
//        public void install(Agent agent)
//        {
//            super.install(agent);
//            SQLTools.Install_SQL_Kbase(this);
//        }

	} /* !Class BookSellerCapabilities */
	
	//---------------------------------------------------------------
	//                      SETUP
	//---------------------------------------------------------------

     
    
	public void setup() 
	{
		super.setup();
        SQLTools.Install_SQL_Kbase(this);


        this.addBehaviour(new WakerBehaviour(this,2000)
        {   
            private Term Testing_agent = Tools.AID2Term(new AID("AgentTesting", AID.ISLOCALNAME));

            private SemanticCapabilities get_sc()
            { return  ((SemanticAgent)this.myAgent).getSemanticCapabilities(); }
            
            private void done_assert(String tmp)
            {
                Term phi = SL.term(tmp).getSimplifiedTerm();
                Formula f = SL.formula("(action_done ??phi)").instantiate("phi", phi);
                this.get_sc().getMyKBase().assertFormula(f);
            }
            
            private void notdone_assert(String tmp)
            {
                Term phi = SL.term(tmp).getSimplifiedTerm();
                Formula f = SL.formula("(action_not_done ??phi)").instantiate("phi", phi);
                this.get_sc().getMyKBase().assertFormula(f);
            }
            
            private void 
            testing_action(boolean is_action_done_or_not, String action)
            {
                if (is_action_done_or_not == true)
                    done_assert(action);
                else
                    notdone_assert(action);
                SQLTools.createSQLmapping(this.get_sc(), Testing_agent, action);
            }
            
            private void 
            testing_remove_action(boolean is_action_done_or_not, String action)
            {
                if (is_action_done_or_not == true)
                    done_assert(action);
                else
                    notdone_assert(action);
                SQLTools.removeSQLmapping(this.get_sc(), Testing_agent, action);
            }
            
            
            public void onWake()
            {
                
                System.err.close();
                
                System.out.println("### Phase 1 ### (test des actions)");

/*
 * --- NOTATION SIMPLE -------------------------------------------------------------
 */
                /* Creation d'une nouvelle table 1 atrib */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(test_simple ??x)\" " +
                        ")");

                /* ajout d'un atrib a une table */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(test_simple ??x ??y)\" " +
                        ")");

                
                /* Creation d'une nouvelle table 1 atrib */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(nom ??x)\" " +
                        ")");

                /* Map sur une table existante */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :formula \"(remove ??foo)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar foo :sqlref nom.x)" +
                            ")" +
                        ")");
                
                /* Suppretion d'un mapping */
                testing_remove_action(true,
                        "(REMOVE_SQL_MAPPING" +
                            " :formula \"(remove ??bar)\" " +
                        ")");


                /* Map avec une table qui n'existe pas */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :formula \"(err_table_not_existe ??x ??y)\" " +
                        ")");
                
                /* Mappings incomplet */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_mapping_not_complet ??foo ??bar)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar foo :sqlref nom.x)" +
                            ")" +
                        ")");
                
                /* Mappings sur quelque chose inexistant */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_mapping_to_complet ??foo ??bar)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar foo :sqlref err.x)" +
                            "(MAPPING :metavar bar :sqlref err.y)" +
                            "(MAPPING :metavar err :sqlref err.z)" +
                            ")" +
                        ")");
                
/*
 * --- TYPAGE -------------------------------------------------------------
 */

                /* Creation d'un type int */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_int ??x)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_int.x :type INT)" +
                            ")" +
                        ")");

                /* Creation d'un type String */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_string ??x)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_string.x :type STRING)" +
                            ")" +
                        ")");

                /* Creation d'un type Real */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_real ??x)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_real.x :type REAL)" +
                            ")" +
                        ")");

                /* Creation d'un type DATE */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_date ??x)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_date.x :type DATETIME)" +
                            ")" +
                        ")");

                /* Creation d'un type TERM */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_slterm ??x ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_slterm.x)" +
                            "(MAPPING :metavar y :sqlref type_slterm.y :type SLTERM)" +
                            ")" +
                        ")");
                /* Creation d'un type TERM */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_slterm_2 ??x ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_slterm.x)" +
                            "(MAPPING :metavar y :sqlref type_slterm.y :type SLTERM)" +
                            ")" +
                        ")");

                
                /* Creation d'un type TERM */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_slterm2 ??x ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_slterm2.x :type SLTERM)" +
                            "(MAPPING :metavar y :sqlref type_slterm2.y :type SLTERM)" +
                            ")" +
                        ")");
                /* Creation d'un type TERM */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_slterm2_2 ??x ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_slterm2.x :type SLTERM)" +
                            "(MAPPING :metavar y :sqlref type_slterm2.y :type SLTERM)" +
                            ")" +
                        ")");
                /* Creation d'un type TERM */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(type_slterm2_3 ??x ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref type_slterm2.x :type SLTERM)" +
                            "(MAPPING :metavar y :sqlref type_slterm2.y :type SLTERM)" +
                            ")" +
                        ")");                
                
                /* Err type no match */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_int.x :type STRING)" +
                            ")" +
                        ")");
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_int.x :type REAL)" +
                            ")" +
                        ")");
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_int.x :type DATETIME)" +
                            ")" +
                        ")");
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_string.x :type INT)" +
                            ")" +
                        ")");
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_string.x :type REAL)" +
                            ")" +
                        ")");
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_string.x :type DATETIME)" +
                            ")" +
                        ")");
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_date.x :type STRING)" +
                            ")" +
                        ")");                
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_date.x :type INT)" +
                            ")" +
                        ")");                
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_date.x :type REAL)" +
                            ")" +
                        ")");
                /* type inconue */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_typage ??y)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar y :sqlref type_err.x :type ERR)" +
                            ")" +
                        ")");
                

      
/*
 * --- LES JOINS  -------------------------------------------------------------
 */

                /* join 2 table */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(test_join ??x ??y)\"" +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref test_join1.x)" +
                            "(MAPPING :metavar y :sqlref test_join2.x)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary test_join1.id :associat test_join2.join_id)" + 
                            ")" +
                        ")");        
                
                /* join 3 table */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(user ??x ??y)\"" +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref prenom.x)" +
                            "(MAPPING :metavar y :sqlref nom.x)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary prenom.id :associat user.prenom_id)" + 
                            "(INNERJOIN :primary nom.id    :associat user.nom_id)" + 
                            ")" +
                        ")");        

                
                /* Complex Join */
                testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(posede ??x ??y ??z)\"" +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref prenom.x)" +
                            "(MAPPING :metavar y :sqlref nom.x)" +
                            "(MAPPING :metavar z :sqlref voiture.nom)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary prenom.id  :associat user.prenom_id)" +
                            "(INNERJOIN :primary nom.id     :associat user.nom_id)" +
                            "(INNERJOIN :primary user.id    :associat posede.user_id)" +
                            "(INNERJOIN :primary voiture.id :associat posede.voiture_id)" +
                            ")" +
                        ")");
   
                
                /* Err: Si le champs fils existe et n'ai pas une clef primaire . */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_column_not_primary_key ??x ??y)\"" +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref prenom.x)" +
                            "(MAPPING :metavar y :sqlref nom.x)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary voiture.vittmax :associat err.vittmax)" +
                            "(INNERJOIN :primary nom.id          :associat err.nom_id)" +
                            ")" +
                        ")"); 
                
                /* 
                 * \!/ ERR: L'ensemble des laisons + variable doit former un arbre, 
                 * chaque variable doit etre lier avec les autres
                 */
                testing_action(false,
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_tree_not_complet ??x ??y)\"" +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref prenom.x)" +
                            "(MAPPING :metavar y :sqlref nom.x)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                            ")" +
                        ")");

                /* 
                 * \!/ ERR: il ne peux y avoire des laison qui ne serve a rein ! 
                 */
                testing_action(false, 
                        "(ADD_SQL_MAPPING" +
                        " :createtable on" +
                        " :formula \"(err_tablink_not_used ??x ??y)\"" +
                        " :mappings (set " + 
                            "(MAPPING :metavar x :sqlref prenom.x)" +
                            "(MAPPING :metavar y :sqlref nom.x)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                            "(INNERJOIN :primary nom.id    :associat user.nom_id)" +
                            "(INNERJOIN :primary err.id    :associat user.err_id)" +
                            ")" +
                        ")");
                
                
             

/*
 * --- LES PREDICATS MATHEMATIQUE -------------------------------------------------------------
 */
                  testing_action(true,
                        "(ADD_SQL_MAPPING" +
                        " :formula \"(nb_prenom_by_name ??name ??nb)\" " +
                        " :mappings (set " + 
                            "(MAPPING :metavar name :sqlref prenom.x)" +
                            "(MAPPING :metavar nb :sqlref nom.x :math COUNT)" +
                            ")" +
                        " :innerjoins (set " +
                            "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                            "(INNERJOIN :primary nom.id    :associat user.nom_id)" +
                            ")" +
                        " :groupby (set " + 
                            " prenom.x" + 
                            ")" +
                        ")");     
                
                  /*  ERR: Le parametre :createtable est invalide avec l'utilisation des fonctions math */
                  testing_action(false,
                          "(ADD_SQL_MAPPING" +
                          " :createtable on" +
                          " :formula \"(err_createtable_with_math ??nb ??name)\" " +
                          " :mappings (set " + 
                              "(MAPPING :metavar nb :sqlref prenom.x :math COUNT)" +
                              "(MAPPING :metavar name :sqlref nom.x)" +
                              ")" +
                          " :innerjoins (set " +
                              "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                              "(INNERJOIN :primary nom.id    :associat user.nom_id)" +
                              ")" +
                          " :groupby (set " + 
                              " nom.x" + 
                              ")" +
                          ")");     

                  /*  Le parametre :groupby doit etre utilisée avec les fonctions maths */
                  testing_action(false,
                          "(ADD_SQL_MAPPING" +
                          " :formula \"(err_groupe_with_no_math ??nb ??name)\" " +
                          " :mappings (set " + 
                              "(MAPPING :metavar nb :sqlref prenom.x)" +
                              "(MAPPING :metavar name :sqlref nom.x)" +
                              ")" +
                          " :innerjoins (set " +
                              "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                              "(INNERJOIN :primary nom.id    :associat user.nom_id)" +
                              ")" +
                          " :groupby (set " + 
                              " nom.x" + 
                              ")" +
                          ")"); 

                  /*  ERR: Le parametre :groupby doit etre utilisée avec les fonctions maths */
                  testing_action(false,
                          "(ADD_SQL_MAPPING" +
                          " :formula \"(err_math_with_no_groupby ??nb ??name)\" " +
                          " :mappings (set " + 
                              "(MAPPING :metavar nb :sqlref prenom.x :math COUNT)" +
                              "(MAPPING :metavar name :sqlref nom.x)" +
                              ")" +
                          " :innerjoins (set " +
                              "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                              "(INNERJOIN :primary nom.id    :associat user.nom_id)" +
                              ")" +
                          ")"); 

                  
                  /*  ERR : arbres */
                  testing_action(false,
                          "(ADD_SQL_MAPPING" +
                          " :formula \"(err_group_tree ??nb)\" " +
                          " :mappings (set " + 
                              "(MAPPING :metavar nb :sqlref prenom.x :math COUNT)" +
                              ")" +
                          " :innerjoins (set " +
                              "(INNERJOIN :primary prenom.id :associat user.prenom_id)" +
                              ")" +
                          " :groupby (set " + 
                              " nom.x" + 
                              ")" +
                          ")");     
                
/*
 * --- LES PREDICATS OUVERT -------------------------------------------------------------
 */                  

                  testing_action(true, 
                          "(ADD_SQL_MAPPING" +
                          " :createtable on" +
                          " :formula \"(clpredicat ??x)\" " +
                          " :closedtable (CLOSEDTABLE :primary clpredicat.id)" +
                          ")");   
                  
            }
        });
        
/*
 * --- ASSERTION ET QUERRY -------------------------------------------------------------
 */                 
        
        this.addBehaviour(new WakerBehaviour(this,10000)
        {   
            private Term Testing_agent = Tools.AID2Term(new AID("AgentTesting", AID.ISLOCALNAME));

            private SemanticCapabilities get_sc()
            { return  ((SemanticAgent)this.myAgent).getSemanticCapabilities(); }
            
            private void add(Formula f1) 
            { 
                Formula f2 = SL.formula("(assert_ok " + f1.toString() + ")");
                this.get_sc().getMyKBase().assertFormula(f2);
            }
            private void add(String tmp) 
            { 
                Formula f1 = SL.formula(tmp);
                add(f1);
            }

            private void informe_add(Formula f1) 
            { 
                f1 = f1.getSimplifiedFormula();
                this.get_sc().inform(f1, Testing_agent); 
                add(f1);
            }
            private void informe_add(String tmp) 
            { 
                Formula f1 = SL.formula(tmp);
                informe_add(f1);
            }
            
            private void informe(Formula f1) 
            { 
                f1 = f1.getSimplifiedFormula();
                this.get_sc().inform(f1, Testing_agent); 
            }
            private void informe(String tmp) 
            { 
                Formula f1 = SL.formula(tmp);
                informe(f1);
            }

            private void querry_ref(String tmp) 
            { 
                IdentifyingExpression phi = (IdentifyingExpression)SL.term(tmp);
                this.get_sc().queryRef(phi, Testing_agent); 
            }  
            
            
            public void onWake()
            {
                System.out.println("### Phase 2 ### (assertion est querry)");

                informe_add("(test_simple toto)");
                informe_add("(test_simple 15)");
                informe_add("(test_simple 16.1)");
                informe_add("(test_simple titi er)");
                informe_add("(test_simple toto 15)");
                add("(test_simple titi)");
                add("(test_simple toto \"\")");
                add("(test_simple 15 \"\")");
                add("(test_simple 16.1 \"\")");
                querry_ref("(some (test_simple ?x)(test_simple ?x))");
                querry_ref("(some (test_simple ?x ?y)(test_simple ?x ?y))");

                informe_add("(remove not_a_name)");
                querry_ref("(some (remove ?x)(remove ?x))");
                
                informe_add("(type_int 10)");
                informe("(type_int toto)");
                informe("(type_int 12.4)");
                querry_ref("(some (type_int ?x)(type_int ?x))");
                
                informe_add("(type_string sss)");
                informe_add("(type_string \"ss s\")");
                informe("(type_string 10)");
                querry_ref("(some (type_string ?x)(type_string ?x))");
            
                informe_add("(type_real 10.4)");
                informe("(type_real ds)");
                informe("(type_real 12)");
                querry_ref("(some (type_real ?x)(type_real ?x))");
            
                try {
                    Date tmpd = new SimpleDateFormat("yyyy-MM-dd").parse("2007-02-08");
                    informe_add(SL.formula("(type_date ??date)")
                            .instantiate("date", new DateTimeConstantNode(tmpd)));
                } catch (Exception e) {}                      
                querry_ref("(some (type_date ?x)(type_date ?x))");
            
                informe_add("(type_slterm a (foo mickey))");
                informe_add("(type_slterm a (foo pluto))");
                informe_add("(type_slterm a (foo minie 10))");
                querry_ref("(some (type_slterm_1 ?a)(type_slterm a ?a))");
                add("(type_slterm_2 a (foo mickey))");
                add("(type_slterm_2 a (foo pluto))");
                querry_ref("(some (type_slterm_2 ?a)(type_slterm_2 a (foo ?a)))");
                
                informe_add("(type_slterm2 (foo mickey)(bar mickey 2))");
                informe_add("(type_slterm2 (foo pluto)(bar mickey 3))");
                informe_add("(type_slterm2 (foo minie)(bar minie 1))");
                querry_ref("(some (type_slterm2_1 ?a ?b ?c)(type_slterm2 (foo ?a)(bar ?b ?c)))");
                add("(type_slterm2_2 (foo mickey)(bar mickey 2))");
                querry_ref("(some (type_slterm2_2 ?a)(type_slterm2_2 (foo mickey) ?a))");
                add("(type_slterm2_3 (foo mickey)(bar mickey 2))");
                add("(type_slterm2_3 (foo minie)(bar minie 1))");
                querry_ref("(some (type_slterm2_3 ?a ?b)(type_slterm2_3 (foo ?a)(bar ?a ?b)))");
                
                
                informe_add("(test_join x y)");
                informe_add("(test_join x z)");
                querry_ref("(some (test_join ?x ?y)(test_join ?x ?y))");
                
                informe_add("(user x y)");
                informe_add("(user t v)");
                add("(user a b)");
                add("(user a d)");
                informe_add("(posede a b c)");
                informe_add("(posede a d c)");
                querry_ref("(some (user ?x ?y)(user ?x ?y))");
                querry_ref("(some (posede ?x ?y ?z)(posede ?x ?y ?z))");
            
                add("(nb_prenom_by_name x 1)");
                add("(nb_prenom_by_name t 1)");
                add("(nb_prenom_by_name a 2)");
                querry_ref("(some (nb_prenom_by_name ?x ?y)(nb_prenom_by_name ?x ?y))");
            
                informe("(clpredicat ok)");add("(clpredicat ok)");
                informe("(not (clpredicat not_ok))");//add("(not (clpredicat not_ok))");
                informe("(clpredicat toto)");
                informe("(not (clpredicat toto))");//add("(not (clpredicat toto))");
                informe("(not (clpredicat titi))");
                informe("(clpredicat titi)");add("(clpredicat titi)");
                //querry_ref("(some (not_clpredicat ?x)(not (clpredicat ?x)))");
                querry_ref("(some (clpredicat ?x)(clpredicat ?x))");

            }
        });

        this.addBehaviour(new WakerBehaviour(this,20000)
        {   
            private SemanticCapabilities get_sc()
            { return  ((SemanticAgent)this.myAgent).getSemanticCapabilities(); }
            
            public void onWake()
            {
                System.out.println("### Phase 3 ### (formule manquante)");

                QueryResult mr_l;
                
                mr_l = this.get_sc().getMyKBase().query(SL.formula("(assert_ok ??phy)"));
                if (mr_l != null)
                {
                    for (int i = 0; i < mr_l.size(); i++)
                    {
                        try {
                        System.out.println("#KO# :" + ((MatchResult)mr_l.getResult(i)).getTerm("phy").toString());
                        } catch (Exception e) {}
                    }
                }
                else 
                {
                    System.out.println("[[OK]]");
                }
            }
        });
   

       
    
    }
	
}
