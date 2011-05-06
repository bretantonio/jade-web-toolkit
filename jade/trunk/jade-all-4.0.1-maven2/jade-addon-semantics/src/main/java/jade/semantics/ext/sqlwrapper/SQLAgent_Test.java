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

package jade.semantics.ext.sqlwrapper;

import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.interpreter.SemanticAgent;


public class SQLAgent_Test extends SemanticAgent
{
	
   
	//---------------------------------------------------------------
	//                      PUBLIC
	//---------------------------------------------------------------
	
	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public SQLAgent_Test() 
	{
		super();
//        setSemanticCapabilities(new SQLCapabilities());
	}

	
	//---------------------------------------------------------------
	//                      ADD SEMANTIC CAPABILITIE 
	//---------------------------------------------------------------
//	public class SQLCapabilities extends DefaultCapabilities 
//	{
//        public void install(Agent agent)
//        {
//            super.install(agent);
//            SQLTools.Install_SQL_Kbase(this);
//        }
//        
//	} /* !Class BookSellerCapabilities */
	
	//---------------------------------------------------------------
	//                      SETUP
	//---------------------------------------------------------------

     
    
	public void setup() 
	{
		super.setup();
        SQLTools.Install_SQL_Kbase(this);
        
        /*
         * --- CREATION DES SERVICE -------------------------------------------------------------
         */
   
        SQLTools.createSQLService(getSemanticCapabilities(),
                "(ADD_SQL_SERVICE" +
                " :name myservice" +
                " :driver com.mysql.jdbc.Driver" +
                " :path jdbc:mysql://localhost/test2" +
                " :user root" +
                " :pass \"\"" +
                ")");

        
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable off" +
                " :formula \"(user2 ??nom ??prenom)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar nom :sqlref user.nom)" +
                    "(MAPPING :metavar prenom :sqlref user.prenom)" +
                    ")" +
                ")");
        
        
        
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :formula \"(user ??nom ??prenom)\" " +
                ")");
        
        
    /*
     * 
     * 
     * */    
        SQLTools.AdminSQLService(getSemanticCapabilities(),
                "(ADMIN_SQL_SERVICE" +
                " :sqlservice myservice" +
                " :deletetable (set voiture possede)" +
                ")");
        
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(voiture ??x ??y)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref voiture.nom)" +
                    "(MAPPING :metavar y :sqlref voiture.vittmax :type INT)" +
                    ")" +
                ")");
        
        
        /*
        * ici : posede 
        *         - voiture [nom]
        *         - user [nom][prenom]
        */
       SQLTools.createSQLmapping(getSemanticCapabilities(), 
               "(ADD_SQL_MAPPING" +
               " :createtable on" + 
               " :formula \"(possede ??u_nom ??u_prenom ??v_nom)\"" +
               " :mappings (set " + 
                   "(MAPPING :metavar u_nom :sqlref user.nom)" +
                   "(MAPPING :metavar u_prenom :sqlref user.prenom)" +
                   "(MAPPING :metavar v_nom :sqlref voiture.nom)" +
                   ")" +
               " :innerjoins (set " +
                   "(INNERJOIN :primary user.id    :associat possede.user_id)" +
                   "(INNERJOIN :primary voiture.id :associat possede.voiture_id)" +
                   ")" +
               ")");
        
    /**/
       
       SQLTools.createSQLmapping(getSemanticCapabilities(), 
               "(ADD_SQL_MAPPING" +
               " :formula \"(count_user_name ??nom ??nb)\" " +
               " :mappings (set " + 
                   "(MAPPING :metavar nom :sqlref user.nom)" +
                   "(MAPPING :metavar nb :sqlref user.prenom :math COUNT)" +
                   ")" +
               " :groupby (set " + 
                   " user.nom" + 
                   ")" +
               ")");   
       
       
       SQLTools.createSQLmapping(getSemanticCapabilities(), 
               "(ADD_SQL_MAPPING" +
               " :formula \"(count_car_name ??nom ??prenom ??nb)\" " +
               " :mappings (set " + 
                   "(MAPPING :metavar nom :sqlref user.nom)" +
                   "(MAPPING :metavar prenom :sqlref user.prenom)" +
                   "(MAPPING :metavar nb :sqlref voiture.nom :math COUNT)" +
                   ")" +
               " :innerjoins (set " +
                   "(INNERJOIN :primary user.id    :associat possede.user_id)" +
                   "(INNERJOIN :primary voiture.id :associat possede.voiture_id)" +
                   ")" +                   
               " :groupby (set " + 
                   " user.nom" + 
                   " user.prenom" +
                   ")" +
               ")");   
       
       /**/
       
       
    }
	
}
