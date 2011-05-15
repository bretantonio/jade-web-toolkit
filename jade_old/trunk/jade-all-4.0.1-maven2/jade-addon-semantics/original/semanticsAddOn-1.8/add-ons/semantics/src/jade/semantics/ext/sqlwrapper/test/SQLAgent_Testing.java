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

import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.interpreter.SemanticAgent;


public class SQLAgent_Testing extends SemanticAgent
{
	
   
	//---------------------------------------------------------------
	//                      PUBLIC
	//---------------------------------------------------------------
	
	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public SQLAgent_Testing() 
	{
		super();
//        setSemanticCapabilities(new SQLCapabilities());
	}

	
	//---------------------------------------------------------------
	//                      ADD SEMANTIC CAPABILITIE 
	//---------------------------------------------------------------
//	public class SQLCapabilities extends DefaultCapabilities
//	{
//        protected SemanticActionTable setupSemanticActions() 
//        {
//            SemanticActionTable table;
//            
//            table = super.setupSemanticActions();
//            return table;
//        } /* !setupSemanticAction */
//
//        
//        
//        protected KBase setupKbase() 
//        {
//            FilterKBase res_kbase;
//            res_kbase = (FilterKBase)super.setupKbase();
// 
//            return res_kbase;
//        } 
//        
//        public void install(Agent agent)
//        {
//            super.install(agent);
//            SQLTools.Install_SQL_Kbase(this);
//        }
//        
//	}
	
	//---------------------------------------------------------------
	//                      SETUP
	//---------------------------------------------------------------

     
    
	public void setup() 
	{
		super.setup();
        SQLTools.Install_SQL_Kbase(this);


        SQLTools.createSQLService(getSemanticCapabilities(),
                "(ADD_SQL_SERVICE" +
                " :name myservice" +
                " :driver com.mysql.jdbc.Driver" +
                " :path jdbc:mysql://localhost/testsql" +
                " :user root" +
                " :pass \"\"" +
                ")");

    
        SQLTools.AdminSQLService(getSemanticCapabilities(),
                "(ADMIN_SQL_SERVICE" +
                " :sqlservice myservice" +
                " :deletetable all" +
                ")");
        
    }
	
}
