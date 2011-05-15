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

/*
 * données et retourne les réponses sous forme de String.
 */

import jade.semantics.ext.sqlwrapper.sqltool.SQLKbase;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class is use for create a connection and converse with a BDD.
 * @author Julien Schluth - France Telecom
 *
 */
public class SQLService 
{
	private Connection     con;
	private Statement      stmt;
    private boolean        _etat = false;


/*
 * ----------------------------------------------------------------
 */
    
    /**
     * Cette class Permer de cree une connection a une BDD 
     * Elle possaide les fonctions de base pour l'utilisation de la BDD
     * @param driver Le nom du driver utilisé pour la connection SQL
     * @param path le chemain de connection a la BDD
     * @param user 
     * @param password
     */
	public 
    SQLService(String driver, String path, String user, String password, SemanticCapabilities sc) 
	{
        this._sc = sc;
        this._associat_formula = new ArrayList<String>();
        
		try 
		{ 
			Class.forName(driver).newInstance();
			System.err.println("Chargement du driver : ok");
		}
		catch (Exception e) {
			System.err.println("###Err: SQLService: Chargement impossible.\n" + e);
		}
		try 
		{
			con = DriverManager.getConnection(path, user, password);
			stmt = con.createStatement();
			System.err.println("Connection a la BDD : ok ");
            this._etat = true;
		}
		catch(SQLException e) 
		{
			System.err.println("###Err: SQLService: Connexion impossible.");
			while (e != null) 
			{
				System.err.println("Message: " + e.getMessage());
				System.err.println("Etat: " + e.getSQLState());
				System.err.println("Code Erreur: " + e.getErrorCode() + "\n");
				e = e.getNextException();
			}
		}
	}
	 

	public boolean 
    get_etat() {return this._etat;}
    
    /**
     * Close the connection with BDD
     */
    public void 
    close() 
    {
        try 
        {
            stmt.close();
            con.close();
        }
        catch(SQLException e) 
        {
            System.err.println("###Err1: SQLService: SQLException: " + e.getMessage() + "\n");
        }    
    } 

/*
 * -----------------------------------------------------------
 */
    
    /**
     * Execute a commande
     * @param command
     * @return
     */
    public void 
    exec(String command) 
    {    
        try 
        {
           // System.out.println("-->" + command);
            stmt.execute(command);
        }
        catch(SQLException e) 
        {
            System.err.println("##Err2:: SQLException: " + command);
            System.err.println("    ----> " + e.getMessage());
        }
    }

    
    /**
     * 
     * @param command commande SQL a executer
     * @param metaref MetaTermResult qui seront completer dans le list de res
     * @return list des resulta de la requette
     */
    public QueryResult 
    execQuery(String command, MatchResult metaref)
    {
    	QueryResult  res = new QueryResult();
        MatchResult         tmp_res;
        
        try 
        {
            ResultSet           rs; 
            ResultSetMetaData   resultSchema;
            int                 nbCol;
            SQLSimpleType[]               type_tab = new SQLSimpleType[255];
            
            //System.out.println("---> " + command);
            rs = stmt.executeQuery(command);
            resultSchema = rs.getMetaData();
            nbCol = resultSchema.getColumnCount();
            
            for (int i = 0; i < nbCol; i++)
                type_tab[i] = this.get_sl_type(resultSchema.getColumnType(i+1));
            
            if (nbCol != metaref.size())
                System.err.println("###Err: SQLService.execQuery : Bad size result");
            
            while (rs.next()) 
            {
                tmp_res = (MatchResult)metaref.getClone();
                for (int i = 0; i < nbCol; i++) 
                {
                    if (type_tab[i] == SQLSimpleType.INT)
                        ((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new IntegerConstantNode(new Long(rs.getLong(i + 1))));
                    if (type_tab[i] == SQLSimpleType.REAL)
                        ((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new RealConstantNode(new Double(rs.getDouble(i + 1))));
                    if (type_tab[i] == SQLSimpleType.DATETIME)
                        ((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new DateTimeConstantNode( rs.getDate(i + 1)));
                    if (type_tab[i] == SQLSimpleType.STRING)
                        ((MetaTermReferenceNode)tmp_res.get(i)).sm_value(SL.string( rs.getString(i + 1)));
                }
                res.add(tmp_res);
            }
        }
        catch(SQLException e) 
        {
            System.err.println("###Err3: SQLService: SQLException: " + e.getMessage());
        }
        return res;
    }
    
    
    /**
     * 
     * @param command commande SQL a executer
     * @param metaref MetaTermResult qui seront completer dans le list de res
     * @return list des resulta de la requette
     */
    public QueryResult 
    execQuery(String command, Map<MetaTermReferenceNode, MetaTermReferenceNode> select_column_list, List<SQLNodeColumn> l_col)
    {
        int                 nbCol;
        ResultSet           rs; 
        ResultSetMetaData   resultSchema;
        SQLSimpleType[]     sql_type_tab = new SQLSimpleType[255];
        SQLSimpleType[]     node_type_tab = new SQLSimpleType[255];
        MatchResult         meta_term_ref = new MatchResult();
        MatchResult         tmp_res;
//        QueryResult         res = new QueryResult();
        QueryResult         res = QueryResult.UNKNOWN; // NEW VL

        int index = 0;
        for (MetaTermReferenceNode metaTerm : select_column_list.keySet())
        {    
            metaTerm.sm_value(null);
            meta_term_ref.add(index, select_column_list.get(metaTerm));
            node_type_tab[index] = SQLNodeColumn.type_of_col(metaTerm.lx_name(), l_col);
            index++;
        }
        try 
        {
            rs = stmt.executeQuery(command);
            resultSchema = rs.getMetaData();
            nbCol = resultSchema.getColumnCount();
            
            for (int i = 0; i < nbCol; i++)
                sql_type_tab[i] = this.get_sl_type(resultSchema.getColumnType(i+1));
            
            if (nbCol != meta_term_ref.size() && meta_term_ref.size() > 0)
                System.err.println("###Err: SQLService.execQuery : Bad size result");
            
            if (meta_term_ref.size() > 0) {
            	while (rs.next()) 
            	{
            		tmp_res = (MatchResult)meta_term_ref.getClone();
            		for (int i = 0; i < nbCol; i++) 
            		{
            			if (sql_type_tab[i] == SQLSimpleType.INT)
            				((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new IntegerConstantNode(new Long(rs.getLong(i + 1))));
            			else if (sql_type_tab[i] == SQLSimpleType.REAL)
            				((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new RealConstantNode(new Double(rs.getDouble(i + 1))));
            			else if (sql_type_tab[i] == SQLSimpleType.DATETIME)
            				((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new DateTimeConstantNode( rs.getDate(i + 1)));
            			else if (sql_type_tab[i] == SQLSimpleType.BYTE)
            				((MetaTermReferenceNode)tmp_res.get(i)).sm_value(new ByteConstantNode( rs.getBytes(i + 1)));
            			else if (sql_type_tab[i] == SQLSimpleType.STRING)
            			{
            				if (rs.getString(i + 1).length() == 0)
            					((MetaTermReferenceNode)tmp_res.get(i)).sm_value(SL.string(""));
            				else if (node_type_tab[i] == SQLSimpleType.SLTERM)
            					((MetaTermReferenceNode)tmp_res.get(i)).sm_value(SL.term(rs.getString(i + 1)));
            				else if (rs.getString(i + 1).indexOf(" ") == -1)
            					((MetaTermReferenceNode)tmp_res.get(i)).sm_value(SL.word( rs.getString(i + 1)));
            				else
            					((MetaTermReferenceNode)tmp_res.get(i)).sm_value(SL.word( rs.getString(i + 1)));
            			}
            		}
            		if (res == null) { // NEW VL
            			res = new QueryResult();
            		}
            		res.add(tmp_res);
            	}
            }
            else if (rs.next()) {
            	res = QueryResult.KNOWN;
            }
        }
        catch(SQLException e) 
        {
            System.err.println("###Err4: SQLService: SQLException: " + e.getMessage());
            return QueryResult.UNKNOWN; // NEW VL
        }
        return res;
    }
    
    
    /**
     * @param command commande SQL a executer
     * @return true si relusta non vide, false sinon
     */
    public boolean 
    execQueryNotEmpti(String command)
    {
        ResultSet           rs; 

        try 
        {
            rs = stmt.executeQuery(command);
            return rs.first();
        }
        catch(SQLException e) 
        {
            System.err.println("###Err5: SQLService: SQLException: " + e.getMessage());
        }
        return false;
    }
    
/*
 * ----------------------------------------------------------------
 */
 
    /**
     * test l'existance d'un Table dans la BDD
     * @param nomTable
     * @return true if table existe
     */
    public boolean 
    existeTable(String nomTable) 
    {
        boolean existe;

        try 
        {
            DatabaseMetaData dmd = this.con.getMetaData();
            ResultSet tables = dmd.getTables(this.con.getCatalog(),null,nomTable,null);
            existe = tables.next();
            tables.close();
            return existe;
        }
        catch (Exception e) 
        {
            System.err.println("###Err: SQLService: Recuperation des infos Table.\n" + e);
        }
        return false;
     }
    
    /**
     * test l'existance d'un column dans une table. 
     * @param nomTable
     * @param nomColumn
     * @return true if table existe.
     */
    public boolean 
    existeColumn(String nomTable, String nomColumn) 
    {
        boolean existe;

        try 
        {
            DatabaseMetaData dmd = this.con.getMetaData();
            ResultSet col = dmd.getColumns(this.con.getCatalog(),null,nomTable, nomColumn);
            existe = col.next();
            col.close();
            return existe;
        }
        catch (Exception e) 
        {
            System.err.println("###Err: SQLService: Recuperation des infos Table.\n" + e);
        }
        return false;
     }

    
    
    private String
    ColumnInfoString(SQLNodeColumn colNode)
    {
        String res = "";

        res += colNode.get_sql_column_name();
        res += " " + SQLSimpleTypeInSQL(colNode.get_type());
        res += " NOT NULL";
        if (colNode.is_PrimaryKey())
            res += " PRIMARY KEY AUTO_INCREMENT";
        else
            res += " " + SQLSimpleDefaultInSQL(colNode.get_type());

        return res;
    }
    
    
    
    /**
     * Ajout la table dans la BDD si elle n'existe pas 
     * Ajout la colone dans la table si elle n'existe pas
     * @param colNode
     */
    public void
    testAndCreatColumn(SQLNodeColumn colNode)
    {
        if (!existeTable(colNode.get_sql_table_name()))
            {
            this.exec("CREATE TABLE " + colNode.get_sql_table_name() + 
                    " (" + ColumnInfoString(colNode) + ");");
            }
        else if (!existeColumn(colNode.get_sql_table_name(), colNode.get_sql_column_name()))
            {
            this.exec("ALTER TABLE " + colNode.get_sql_table_name() + 
                    " ADD " + ColumnInfoString(colNode) + ";");
            }
    }

    
    /*
     * --------------------------------------------------------
     */    
    
    
    static public String requette_for_select_all_table_in_bdd = "SHOW TABLES;";

    
    public void
    clean_table(String tab)
    {
        this.exec("TRUNCATE `" + tab +"`;");
    }
    
    public void 
    delete_table(String tab)
    {
        this.exec("DROP TABLE `" + tab +"`;");
    }
    
    
    public void 
    clean_all_table()
    {
        String[] tables = new String[255];
        try 
        {
            ResultSet           rs; 
            rs = stmt.executeQuery(this.requette_for_select_all_table_in_bdd);
            int i = 0;
            while (rs.next()) 
                tables[i++] = rs.getString(1);
            int nb = i;
            for (i = 0; i < nb; i++)
                clean_table(tables[i]); 
        }
        catch (Exception e) 
        {
            System.err.println("###Err: SQLService: clean_all_table.\n" + e);
        }   
    }
    
    public void 
    delete_all_table()
    {
        String[] tables = new String[255];
        try 
        {
            ResultSet           rs; 
            rs = stmt.executeQuery(this.requette_for_select_all_table_in_bdd);
            int i = 0;
            while (rs.next()) 
                tables[i++] = rs.getString(1);
            int nb = i;
            for (i = 0; i < nb; i++)
                delete_table(tables[i]);
        }
        catch (Exception e) 
        {
            System.err.println("###Err: SQLService: clean_all_table.\n" + e);
        }   
    }
    
    /*
     * --------------------------------------------------------
     */    
             
    
    private boolean 
    checkColumnType(SQLNodeColumn colNode)
    {
        try 
        {
            if (colNode.get_type() == SQLSimpleType.SLTERM)
                return true;
            
            ResultSet rs = stmt.executeQuery(
                    "SELECT " + colNode.get_sql_column_name() + " FROM " + colNode.get_sql_table_name() + ";");
            if (rs.getMetaData().getColumnCount() != 1)
                return false;
            if (SQLSimpleTypeCompart(colNode.get_type(),get_sl_type(rs.getMetaData().getColumnType(1))))
            {
                return true;
            } 
        }
        catch (Exception e) 
        {
            System.err.println("###Err: SQLService.checkColumnType \n" + e);
        }
        return false;
    }
    
    
    
    /**
     * test l'existance d'un column dans une table. 
     * @param nomTable
     * @param nomColumn
     * @return true if table existe.
     */
    public boolean 
    checkColumn(SQLNodeColumn colNode) 
    {
        boolean ok = true;

        try 
        {
            DatabaseMetaData dmd = this.con.getMetaData();
            ResultSet col = dmd.getColumns(this.con.getCatalog(),null,colNode.get_sql_table_name(), colNode.get_sql_column_name());
            if (!col.next())
                return true;
            if (!checkColumnType(colNode))
            {
                System.err.println("###Err: SQLService.checkColumn : Bad type Column : " + colNode.get_mapping_name() + " != " + SQLSimpleTypeInSQL(colNode.get_type()));
                return false;
            }
            if (colNode.is_PrimaryKey())
            {
                ResultSet clefs = dmd.getPrimaryKeys(this.con.getCatalog(),null,colNode.get_sql_table_name());
                ok = false;
                while(clefs.next())
                {
                   if (colNode.get_sql_column_name().compareTo(clefs.getString("COLUMN_NAME")) == 0)
                       ok = true;
                }
                if (!ok)
                    {
                    System.err.println("###Err: SQLService.checkColumn: Column : " 
                            + colNode.get_mapping_name() + " not is PrimaryKey ");
                    return false;
                    }
            }
            col.close();
            return true;
        }
        catch (Exception e) 
        {
            System.err.println("###Err: SQLService.CheckColumn \n" + e);
        }
        return false;
     }
    
  
/*
 * ----------------------------------------------------------------
 */
        
        private SemanticCapabilities _sc;
        private List<String>   _associat_formula;
        
        public void 
        add_associat_link(String s)
        {
            this._associat_formula.add(s);
        }

        public void 
        remove_all_SQLLink(SQLKbase sqlkbase)
        {
            PredicateNode   slPredicat;
            for (String s : this._associat_formula)
            {
                slPredicat = (PredicateNode)SL.formula(s);
                sqlkbase.remove_sql_mapping(slPredicat, this._sc);
            }
            this._associat_formula.clear();
        }
    
    
    
    /*
     * ----------------------------------------------------------------
     */

    public static enum SQLSimpleType 
    {
        INT,
        REAL,
        BYTE,
        STRING,
        DATETIME,
        SLTERM
    };

    private static boolean
    SQLSimpleTypeCompart(SQLSimpleType t1, SQLSimpleType t2)
    {
        if (t1 == t2) return true;
        if (t1 == SQLSimpleType.STRING && t2 == SQLSimpleType.SLTERM) return true;
        if (t1 == SQLSimpleType.SLTERM && t2 == SQLSimpleType.STRING) return true;
        
        return false;
    }
    
    private static String
    SQLSimpleTypeInSQL(SQLSimpleType t)
    {
        if (t == SQLSimpleType.INT)
            return "INTEGER";
        else if (t == SQLSimpleType.REAL)
            return "DOUBLE";
        else if (t == SQLSimpleType.BYTE)
            return "BINARY(512)";
        else if (t == SQLSimpleType.STRING ||
                t == SQLSimpleType.SLTERM)
            return "VARCHAR(255)";
        else if (t == SQLSimpleType.DATETIME)
            return "DATETIME";
        else return "VARCHAR(255)";
    }

    private static String
    SQLSimpleDefaultInSQL(SQLSimpleType t)
    {
        if (t == SQLSimpleType.INT)
            return "DEFAULT 0";
        else if (t == SQLSimpleType.REAL)
            return "DEFAULT 0";
        else if (t == SQLSimpleType.BYTE)
            return "DEFAULT \"\"";
        else if (t == SQLSimpleType.STRING)
            return "DEFAULT \"\"";
        else if (t == SQLSimpleType.DATETIME)
            return "DEFAULT \"0000-00-00 00:00\"";
        else if (t == SQLSimpleType.SLTERM)
            return "DEFAULT \"\"";
        return "DEFAULT \"\"";
    }
    
    private static SQLSimpleType
    get_sl_type(int sql_type)
    {
        switch(sql_type)
        {
           //IntegerConstantNode
            case java.sql.Types.BIGINT:
            case java.sql.Types.INTEGER: 
            case java.sql.Types.TINYINT: 
                return SQLSimpleType.INT;
            //RealConstantNode
            case java.sql.Types.DOUBLE: 
            case java.sql.Types.FLOAT: 
            case java.sql.Types.REAL: 
            case java.sql.Types.NUMERIC: 
            case java.sql.Types.DECIMAL: 
                return SQLSimpleType.REAL;
            //ByteConstantNode
            case java.sql.Types.BIT:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY: 
            case java.sql.Types.LONGVARBINARY: 
            case java.sql.Types.BLOB:
                return SQLSimpleType.BYTE;
            //StringConstantNode
            case java.sql.Types.VARCHAR: 
            case java.sql.Types.CHAR:
            case java.sql.Types.LONGVARCHAR: 
            case java.sql.Types.CLOB:
                return SQLSimpleType.STRING;
            //DateTimeConstantNode
            case java.sql.Types.DATE:
            case java.sql.Types.TIME: 
            case java.sql.Types.TIMESTAMP:
                return SQLSimpleType.DATETIME;
            // True/False
            case java.sql.Types.NULL: 
                System.err.println("====> java.sql.Types.NULL");
            case java.sql.Types.BOOLEAN: 
            case java.sql.Types.ARRAY:
            case java.sql.Types.DATALINK: 
            case java.sql.Types.DISTINCT: 
            case java.sql.Types.JAVA_OBJECT: 
            case java.sql.Types.OTHER: 
            case java.sql.Types.REF: 
            case java.sql.Types.SMALLINT: 
            case java.sql.Types.STRUCT:
                return SQLSimpleType.STRING;
        }
        return SQLSimpleType.STRING;
    }

    
    
    

}              