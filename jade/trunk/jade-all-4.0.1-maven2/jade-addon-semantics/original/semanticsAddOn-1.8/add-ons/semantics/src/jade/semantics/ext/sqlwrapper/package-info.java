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

/**
 * This JSA extension makes it possible to connect a JSA belief base with a SQL
 * database. The principle is to map some SL formula patterns to SQL tables.
 * This piece of software then automatically translates into proper SQL statements
 * (and executes them of course!) assertions and queries of these patterns into
 * the JSA belief base.
 * 
 * <p>
 * This extension may be used mainly in two ways:
 * <ul>
 *   <li>take benefit from the persistence of SQL databases to store externally
 *       some believes (so that they can be reused later or by other agents),</li>
 *   <li>complete the belief base of a JSA-agent with some already existing data
 *       picked from a database (e.g. a legacy system in a company).</li>
 * </ul>
 * </p>
 *  
 * <p>
 * To have a try and understand how to use this extension, please have a look to
 * the comments of the {@link SQLAgent_Tuto} file. You can also try it by launching
 * the following command (do not forget to properly configure the Java classpath):
 * <pre>
 * java jade.Boot -name test -gui -nomtp sql:jade.semantics.ext.sqlwrapper.SQLAgent_Tuto
 * </pre>
 * When starting, the demo outputs (on System.err) a number of errors, which
 * are normal :) and explained in the comments of the {@link SQLAgent_Tuto} file
 * to illustrate how the software works.
 * Then, you can go to the sql agent's GUI, select the "KBase" tab and use the "Assert" and
 * "Query" buttons to test some assertions and queries of the mapped predicates
 * defined in the {@link SQLAgent_Tuto} file, and then look to the content of
 * your SQL tables to check their contents (e.g. query "(user ??x ??y)" to see all
 * the pairs of first/last names defined in the SQL database, or assert "(user john
 * smith)" to add a new pair).
 * 
 * <br><b>Important note:</b></br>
 * You will have to also install a MySQL database locally on your computer (see
 * for example <a href="http://www.easyphp.org/">EasyPHP</a>) and to include a
 * <a href="http://www.mysql.com/products/connector/j/">JDBC driver for MySQL</a>
 * in your classpath (e.g. we are using the 3.1.14 version). 
 * </p>
 *  
 * This software package is given in its current state and could not be reviewed
 * for a complete english translation and documentation.
 */
package jade.semantics.ext.sqlwrapper;