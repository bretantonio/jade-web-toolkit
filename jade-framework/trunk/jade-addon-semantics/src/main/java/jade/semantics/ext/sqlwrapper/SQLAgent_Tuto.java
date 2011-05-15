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

import jade.core.behaviours.WakerBehaviour;
import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService;
import jade.semantics.interpreter.SemanticAgent;
import jsademos.DemoAgentGui;



public class SQLAgent_Tuto extends SemanticAgent 
{
	DemoAgentGui gui;
	    
	//---------------------------------------------------------------
	//                      agent's SETUP
	//---------------------------------------------------------------
	public void setup() 
	{
		super.setup();
		SQLTools.Install_SQL_Kbase(this); // Install an SQL wrapper on the semantic agent
		gui = new DemoAgentGui(getName(), null, this, true, true, null);
		
        /*
         * --- CREATION OF SQL SERVICES ----------------------------------------
         *
         *  An "SQL Service" is the basic object that interfaces a semantic agent
         *  with an SQL database. It relies on a JDBC driver.
         *  This basic object is then used by SQL mappings (see further) to
         *  implement the query and assertion of SL predicates into an SQL
         *  database.
         */
   
        /* 
         * Creation of a SQLService (here, it is connected to a MySQL database)
         *    - the "name" parameter specifies an internal identifier to refer
         *      further to this SQLService (in particular in SQL mappings)
         *    - the "dricer" parameter specifies the Java class of the JDBC driver
         *      to use (of course, it must be in the classpath)  
         *    - the "path" parameter specifies the URI to the SQL server (like here)
         *      or to the SQL database (see the next ADD_SQL_SERVICE example below),
         *      in the format expected by the driver
         */
        SQLTools.createSQLService(getSemanticCapabilities(),
                "(ADD_SQL_SERVICE" +
                " :name bddadmin" +
                " :driver com.mysql.jdbc.Driver" +
                " :path jdbc:mysql://localhost/" +
                " :user root" +
                " :pass \"\"" +
                ")");
        
        /*
         * Disconnection and removal of a SQL service. 
         * All SQL mappings associated to this service are also removed.
         */
        SQLTools.removeSQLService(getSemanticCapabilities(),
                "(REMOVE_SQL_SERVICE " +
                " :name bddadmin" +
                ")");
        
        
        /* 
         *    - By default (if not specified), the "name" parameter equals to
         *      "myservice"
         *    - here the "path" parameters specifies a database (/testsql) and not
         *      a SQL server
         */
        SQLTools.createSQLService(getSemanticCapabilities(),
                "(ADD_SQL_SERVICE" +
                " :name myservice" +
                " :driver com.mysql.jdbc.Driver" +
                " :path jdbc:mysql://localhost/testsql" +
                " :user root" +
                " :pass \"\"" +
                ")");

        
       
        /*
         * --- ADMINISTRATION OF SQL SERVICES ----------------------------------
         *
         * The "ADMIN_SQL_SERVICE" action makes it possible to run 2 basic admin
         * commands on the DB:
         *  - clean some tables (use the "cleantable" parameter), that is, run
         *    the "truncate table" command on some tables
         *  - delete some tables (use the "deletetable" parameter), taht is, run
         *    the "drop table" command on some tables
         *  In both cases, the list of tables to truncate or drop is given as an
         *  SL set (e.g. (set table1 table2)). The constant "all" may also be used,
         *  in this case, all tables of the DB are truncated or dropped.
         *  
         *  The common "sqlservice" parameter specifies which SQL service to use.
         */
        
         /* 
         * !!WARNING!! when using the value "all", the SQL wrappers must be able
         *             to select all tables of a DB, which is not a standard SQL
         *             command. The following attribute must therefore specify
         *             the correct command to use.
         *             (here, it is specified for a MySQL database)
         */
        SQLService.requette_for_select_all_table_in_bdd = "SHOW TABLES;";
        
        /*
         * Clean (i.e. truncate) le tables "toto" and "titi"
         */
        SQLTools.AdminSQLService(getSemanticCapabilities(),
                "(ADMIN_SQL_SERVICE" +
                " :sqlservice myservice" +
                " :cleantable (set toto titi)" +
                ")");
        /*
         * Clean (i.e. truncate) all tables of the DB.
         * Note that if the "sqlservice" parameter is unspecified, then the
         * "myservice" SQL service is used by default. However, it is warmly
         * recommended to always specify this parameter!
         */
        SQLTools.AdminSQLService(getSemanticCapabilities(),
                "(ADMIN_SQL_SERVICE" +
                " :cleantable all" +
                ")");
        
        /*
         * Remove (i.e. drop) the tables "titi" and "toto"
         */
        SQLTools.AdminSQLService(getSemanticCapabilities(),
                "(ADMIN_SQL_SERVICE" +
                " :sqlservice myservice" +
                " :deletetable (set toto titi)" +
                ")");
        
        /*
         * Remove (i.e. drop) all tables of the DB
         */
        SQLTools.AdminSQLService(getSemanticCapabilities(),
                "(ADMIN_SQL_SERVICE" +
                " :sqlservice myservice" +
                " :deletetable all" +
                ")");
        
        
        /*
         * --- SQL MAPPING - BASIC USE -----------------------------------------
         * 
         * SQL mappings are objects that make it possible to link a pattern of SL
         * predicate to a corresponding SQL representation, in order to handle
         * assertions or queries of this predicate directly into a SQL DB (instead
         * of the usual JSA belief base).
         * 
         * Here, we start with simple SQL mapping specifications.
         */
        
        /* 
         * Creation of a mapping between a predicate pattern - here: (err_table_not_exists ??x ??y) -
         * and a corresponding table
         *    - the pattern of prediate to map to a DB is specified by the "formula"
         *      parameter. !!WARNING!! the value of this parameter must be an SL
         *      string, which represents a predicate. Do not forget the opening and
         *      closing backslashed quote marks ("\"")
         *    - by default, the SQL table corresponding to an SL predicate pattern
         *    is the table with the same name as the predicate (here "err_table_not_exists")
         *    and which has at least columns with the same names as the meta-references
         *    that occur in the predicate pattern (here "x" and "y")
         *    
         * !!ERROR!! here an error occurs because the table "err_table_not_exists"
         *           does not exist in the DB.   
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :formula \"(err_table_not_exists ??x ??y)\" " +
                ")");

        
        /* 
         * It is of course possible to specify which DB an SQL mapping is associated
         * to, by using the "sqlservice" parameter. If not specified, the SQL mapping
         * refers by default to the "myservice" SQL service. It is warmly recommended
         * to always specify the SQL service a SQL mapping refers to.
         *
         * Thus, it is possible to manage various SL predicates with various SQL
         * DB (possibly on various SQL servers).
         * 
         * !!ERROR!! here an error occurs because the specified "aservice" SQL
         *           service has not been defined.
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :sqlservice aservice" +
                " :formula \"(err_service_not_exists ??x ??y)\" " +
                ")");

        
        /*
         * The "createtable" parameter makes it possible to specify if the SQL
         * tables (with proper names and columns) that support the defined SQL
         * mapping must be automatically created in the DB. To do so, the
         * parameter value must be "on", or "off" otherwise (this is the default
         * value).
         * 
         * This parameter is particularly interesting to store predicates into
         * a DB without reusing existing tables.
         * 
         * Here the "err_table_not_exists" error no longer occurs, because a table
         * "firstname" has been automatically created (with a column "x") in the DB
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(firstname ??x)\" " +
                ")");
        
        /* 
         * The "mappings" parameter is the basic one to specify a mapping between
         * the meta-references occuring in the predicate pattern and SQL tables/columns.
         * It is specified as an SL set of "MAPPING" functional terms, where each
         * "MAPPING" term has the following parameters:
         *  - "metavar": to specify the name of the meta-reference (of the predicate
         *               pattern) to map to an SQL table/column
         *  - "sqlref":  the SQL table/column (in the format <table_name>.<column_name>)
         *               mapped to the previous meta-reference
         * 
         * By default, if no "mappings" parameter is specified, each meta-reference
         * of the predicate pattern is mapped to <predicate_name>.<metaref_name>.
         * 
         * Here, the meta-reference "foo" is mapped to the column "x" of the
         * "lastname" table
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(name ??foo)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar foo :sqlref lastname.x)" +
                    ")" +
                ")");
        
        /*
         * Of course, it is possible to define 2 predicates that are mapped to the
         * same data of a DB.
         * Here, (lastname ??x) has the same mapping as the previous (name ??foo) predicate
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :formula \"(lastname ??x)\" " +
                ")");

        /*
         * It is also possible to remove a previously defined SQL mapping.
         * In this case, the "formula" parameter must specify the predicate
         * pattern, the mapping of which has to be removed (all associated "MAPPING"
         * structures are removed).
         * 
         * Note that the mappings are identified with respect to a predicate
         * pattern, which is not sensitive to the name of the meta-references.
         * Thus, you can use different meta-reference names in the ADD_SQL_MAPPING
         * and the corresponding REMOVE_SQL_MAPPING actions.
         * 
         * Here, we remove the SQL MAPPING previously defined on the "(name ??foo)"
         * pattern
         */
        SQLTools.removeSQLmapping(getSemanticCapabilities(), 
                "(REMOVE_SQL_MAPPING" +
                    " :formula \"(name ??bar)\" " +
                ")");

        
        /*
         * When the "mappings" parameter is specified, a "MAPPING" structure must
         * be specified for each meta-reference of the predicate pattern.
         * 
         * !!ERROR!! here an error occurs because a mapping definition for the
         *           "bar" meta-reference is missing
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(err_varlink_not_complete ??foo ??bar)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar foo :sqlref lastname.x)" +
                    ")" +
                ")");
        

        
        /*
         * --- SQL MAPPING - MAPPING TYPES -------------------------------------
         *
         * The optional "type" parameter of the "MAPPING" structures makes it
         * possible to specify the type (among the FIPA-SL data types) of
         * the values of the corresponding mapped meta-reference.
         * This mechanism allows for constraining the values of the predicate
         * and so optimizing their storage into the DB.
         * 
         * Possible values for the "type" parameter are: "INT", "REAL", "BYTE",
         * "STRING", "DATETIME" and "SLTERM".
         * If the "type" parameter is not specified, its value is determined
         * automatically by the SQL type of the column of the corresponding table.
         * If the table is automatically created ("createtable" parameter set to
         * "on"), then the default used type is "SLTERM".
         * 
         * If the "type" parameter is specified, the SQL type of the table column
         * must be compliant with the specified SL type. In this case, if the
         * "createtable" parameter is "on" (i.e. the SQL table is automatically
         * created), then the columns of the created table will have specialised
         * SQL types (otherwise, columns of created tables used a string type
         * by default)
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(car ??x ??y)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref car.name)" +
                    "(MAPPING :metavar y :sqlref car.maxspeed :type INT)" +
                    ")" +
                ")");
        
        /*
         * !!ERROR!! here an error occurs because the data type specified for the
         *           mapping of the "y" meta-reference is not compliant with the
         *           SQL type of the corresponding column table.
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(err_typage ??y)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar y :sqlref car.maxspeed :type STRING)" +
                    ")" +
                ")");
  
        
        /*
         * !!WARNING!! to properly use the "DATETIME" SL type, the "datetime_format"
         *             variable on the SQL server must be set to "%Y-%m-%d %H:%i:%s"
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(datetest ??nom ??date)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar nom :sqlref datetest.nom)" +
                    "(MAPPING :metavar date :sqlref datetest.date :type DATETIME)" +
                    ")" +
                ")"); 


        /*
         * Using the "SLTERM" SL type makes it possible to use any SL term as
         * a value of the predicate parameter (this is the default setting if
         * the corresponding SQL table is automatically created).  
         * 
         * !!WARNING!! the matching function with values of type "SLTERM" has
         *             still some bugs.
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(test_slterm ??name ??term)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar name :sqlref test_type_slterm.nom)" +
                    "(MAPPING :metavar term :sqlref test_type_slterm.term :type SLTERM)" +
                    ")" +
                ")"); 
        
        /*
         * --- SQL MAPPING - INNERJOINS ----------------------------------------
         *
         * When defining SQL mappings, it is possible to specify Join operations
         * between SQL tables, using the "innerjoins" parameter. The value of
         * this parameter must be an SL set of "INNERJOIN" functional terms, with
         * the following parameters:
         *  - "primary": specifies the column that must be linked to another column
         *               in the format <table_name>.<column_name>. This column
         *               must be a primary key of the table
         *  - "associat": specifies the column to be linked to the primary one in
         *                the format <table_name>.<column_name>
         * 
         * In the following example, the table "lastname" contains a list of last names,
         * identified with the primary key "lastname.id" (the actual values are stored in the
         * "lastname.x" column). The table "firstname" contains a list of first names,
         * identified with the primary key "firstname.id" (the actual values are stored in the
         * "firstname.x" column).
         * Then the table "user" joins the previous 2 tables by linking the "firstname.id"
         * column to the "user.firstname_id" one and the "lastname.id" column to the
         * "user.lastname_id" one.
         * The "(user ??x ??y)" predicate then retrieves the pairs of first/last
         * name values, using the previously defined SQL innerjoin.
         * 
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(user ??x ??y)\"" +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref firstname.x)" +
                    "(MAPPING :metavar y :sqlref lastname.x)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary firstname.id :associat user.firstname_id)" +
                    "(INNERJOIN :primary lastname.id    :associat user.lastname_id)" +
                    ")" +
                ")");        


        /* 
         * !!ERROR!! here an error occurs because the "car.maxspeed" is not
         *           a primary key in the SQL DB
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(err_column_not_primary_key ??x ??y)\"" +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref firstname.x)" +
                    "(MAPPING :metavar y :sqlref lastname.x)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary car.maxspeed :associat err.maxspeed)" +
                    "(INNERJOIN :primary lastname.id          :associat err.lastname_id)" +
                    ")" +
                ")"); 
        
        
        /* 
         * !!ERROR!! here an error occurs because the "INNERJOIN" clauses must
         *           represent a complete SQL innerjoin. Here the "lastname" table,
         *           whose "x" column is needed in the 2nd "MAPPING" structure,
         *           is not linked to the "innerjoins" definition
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(err_tree_not_complet ??x ??y)\"" +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref firstname.x)" +
                    "(MAPPING :metavar y :sqlref lastname.x)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary firstname.id :associat user.firstname_id)" +
                    ")" +
                ")");

        /* 
         * !!ERROR!! here an error occurs because none of the "INNERJOIN" clauses
         *           must be useless in the innerjoin definition. Here the link
         *           with the "err" table (in the last "INNERJOIN" clause) is
         *           useless in the defined innerjoin
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(err_tablink_not_used ??x ??y)\"" +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref firstname.x)" +
                    "(MAPPING :metavar y :sqlref lastname.x)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary firstname.id :associat user.firstname_id)" +
                    "(INNERJOIN :primary lastname.id    :associat user.lastname_id)" +
                    "(INNERJOIN :primary err.id    :associat user.err_id)" +
                    ")" +
                ")");
        
        
        /* 
         * It is of course possible to define more complex innerjoin involving
         * more than 2 products of tables. The resulting "innerjoin" definition
         * just has to be complete, as explained above.
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" + 
                " :formula \"(owns ??x ??y ??z)\"" +
                " :mappings (set " + 
                    "(MAPPING :metavar x :sqlref firstname.x)" +
                    "(MAPPING :metavar y :sqlref lastname.x)" +
                    "(MAPPING :metavar z :sqlref car.name)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary firstname.id  :associat user.firstname_id)" +
                    "(INNERJOIN :primary lastname.id     :associat user.lastname_id)" +
                    "(INNERJOIN :primary user.id    :associat owns.user_id)" +
                    "(INNERJOIN :primary car.id :associat owns.car_id)" +
                    ")" +
                ")");
        

        /*
         * --- SQL MAPPING - MATH PREDICATES -----------------------------------
         * 
         * SQL math functions make it possible to perform some computations on
         * the data of a table. To use a SQL math function, specify it in the
         * optional "math" parameter of a "MAPPING" structure.
         * 
         * When using SQL math functions, some restrictions apply:
         *  - the "createtable" parameter cannot be "on"
         *  - the specified predicate pattern cannot be asserted into the semantic
         *    agent's belief base. This pattern can only be queried.
         *    
         * Usable SQL math functions are:
         *  - COUNT, AVG, MIN, MAX, STDDEV, SUM, VARIANCE, BIT_AND, BIT_OR, BIT_XOR
         * !!WARNING!! some DB do not handle all of them
         * 
         * !!ERROR!! Possible errors resulting from the use of SQL math functions:
         *  - the use of the "createtable" parameter is invalid 
         *  - the "groupby" parameter is mandatory
         *  - the columns of the "groupby" parameter must be linked to the specified
         *    innerjoin
         */
 
        /*
         * In the following example, the SQL mapping structure is basically the same as
         * the previously specified one for the "(user ??x ??y)" predicate pattern.
         * The data are grouped wrt. to the "lastname.x" column and are counted
         * (COUNT SQL math function), so that the number of first names is counted
         * for a given last name.
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :formula \"(nb_firstname_by_lastname ??nb ??name)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar nb :sqlref firstname.x :math COUNT)" +
                    "(MAPPING :metavar name :sqlref lastname.x)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary firstname.id :associat user.firstname_id)" +
                    "(INNERJOIN :primary lastname.id :associat user.lastname_id)" +
                    ")" +
                " :groupby (set " + 
                    " lastname.x" + 
                    ")" +
                ")");     
        
        /*
         * To compute the COUNT(*) SQL function, a "groupby" including all
         * columns of the request must be specified, and the "math" parameter
         * (with the COUNT function) must be specified with respect to one of
         * the columns (here the "firstname.x" column)
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :formula \"(count_user ??nb)\" " +
                " :mappings (set " + 
                    "(MAPPING :metavar nb :sqlref firstname.x :math COUNT)" +
                    ")" +
                " :innerjoins (set " +
                    "(INNERJOIN :primary firstname.id :associat user.firstname_id)" +
                    "(INNERJOIN :primary lastname.id :associat user.lastname_id)" +
                    ")" +
                " :groupby (set " + 
                    " lastname.x" + 
                    " firstname.x" +
                    ")" +
                ")");     
        
        
        /*
         * --- SQL MAPPING - CLOSED PREDICATES ---------------------------------
         * 
         * By default, all predicates that are mapped to an SQL database are
         * not considered as being closed, so that if a queried value is not in the
         * DB, then (not (B ??myself <predicate_value>)) is true, as for regular
         * SL predicates (i.e. predicates that are stored in the usual belief
         * base).
         * 
         * When specifying the "closedtable" parameter in an SQL mapping, the
         * defined predicate is considered as being closed, so that if a queried value
         * is not in the DB, then it is considered as being implicitly false, that
         * is, (B ??myself (not <predicate_value>)) is true.
         * 
         * To do so, the "closedtable" parameter must specify a value of the form
         * (CLOSEDTABLE :primary <closedtable_name>.<primary_key_column>)
         * When the "createtable" mode is switched on, if the primary column
         * specified for the closed table does not exist, then it is automatically
         * created.
         * 
         */
        SQLTools.createSQLmapping(getSemanticCapabilities(), 
                "(ADD_SQL_MAPPING" +
                " :createtable on" +
                " :formula \"(clpredicat ??x)\" " +
                " :closedtable (CLOSEDTABLE :primary clpredicat.id)" +
                ")");     
        
        /*
         * --- DATA ASSERTION --------------------------------------------------
         */
        
        /*
         * All needed SQL services and mappings have been defined within the
         * setup() method of the agent. As all these operations are implemented
         * as regular semantic actions performed by the agent, they actually
         * install on the agent the proper behaviour to perform them. So we have
         * to wait for the termination of all these behaviours before performing
         * assertion or query operations on the belief base.
         * 
         * The following assertions are therefore wrapped into a waker behaviour.
         */
        this.addBehaviour(new WakerBehaviour(this, 10000)
        {   
            private void interpret(String s) 
            { ((SemanticAgent)this.myAgent).getSemanticCapabilities().interpret(s); }
            
            public void onWake()
            {
                /*
                 * The following assertions into the semantic agent's belief
                 * base will use the previously defined SQL mappings, and so
                 * will actually insert some data into the SQL DB
                 */
                this.interpret("(firstname pluto)");
                this.interpret("(firstname pinocchio)");
                this.interpret("(user mickey mouse)");
                this.interpret("(user minie mouse)");
                this.interpret("(user pluto disney)");
                this.interpret("(user donald duck)");
                this.interpret("(user daisy duck)");
                this.interpret("(owns riri picsou \"Audi A2\")");
                this.interpret("(owns fifi picsou \"Renault Clio II\")");
                this.interpret("(owns loulou picsou \"Mercedes-Benz Classe A\")");
                this.interpret("(car \"Citroën C5\" 150)");

                /* !!ERROR!! here an error occurs because the "err" SL term has not the expected type (INT) */
                this.interpret("(car \"Citroën C5\" err)");
          
//                /* Asserting a date value as a string with the correct SQL format */
//                this.interpret("(datetest test1 \"2007-01-08 10:51:42\")");
                /* Asserting a date value an SL datetime */
                this.interpret("(datetest test2 20070208T123541000z)");
               
               
               /* Asserting SL terms */
               this.interpret("(test_slterm test1 (f ?x))");
               this.interpret("(test_slterm test2 (f ?x ?y))");
               
            };
        });
    
        
    
    
    }
	
}
