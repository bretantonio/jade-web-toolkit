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
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.MatchResult;

import java.util.ArrayList;
import java.util.List;


/**
 * cette class et une simple structur qui permet de cree la hierarchie 
 * des liason entre les tables. 
 * @author Julien Schluth - France Telecom
 *
 */
public class SQLNodeTable
{
    private String                  _tabName;
    private String                  _tabLinkName;
    private String                  _fatherLinkName;
    private Constant                _tmpLinkConstant;

    
    private SQLNodeTable             _fatherTable;
    private List<SQLNodeTable>       _childTable;
    private List<SQLNodeColumn>      _usedColumn;    
    
    
    
/*
 * -----------------------------------------------------
 */
    
    private void init_SQLLinkNode()
    {
        this._childTable = new ArrayList<SQLNodeTable>();
        this._usedColumn = new ArrayList<SQLNodeColumn>();
        
//        this._usedColumn = new HashMap<String, Constant>();
        this._fatherTable = null;
    }
    
    /**
     * Constructeur d'un node sans pere.
     * Represante generalement un neoud racine dans le hierarchi des laisons des table
     * @param name non de la table
     */
    public SQLNodeTable (String name)
    {
        this._tabName = name.toUpperCase();
        this._tabLinkName = null;
        this._fatherLinkName = null;
        this.init_SQLLinkNode();
    }

    /**
     * Constructeur d'un node ayant une laison avec un pere
     * @param link1 indicateur du champ sur la table 1 .Generalement ID de la ligne dans la table. PRIVATE KEY 
     * @param link2 indicateur du champ de la liason dans la table 2
     */
    public SQLNodeTable (String link1,String link2)
    {
        this._tabName = link1.substring(0,link1.indexOf('.')).toUpperCase();
        this._tabLinkName = link1.toUpperCase();
        this._fatherLinkName = link2.toUpperCase();
        this.init_SQLLinkNode();
    }
    

/*
 * -----------------------------------------------------
 */

    /**
     * Ajoute un fils dans la table
     * @param table la SQLTableNode fils a ajouter
     */
    public void 
    add_childTable(SQLNodeTable node) { this._childTable.add(node);}
 
    /**
     * @param table la SQLTableNode de la table pere
     */
    public void 
    set_fatherTable(SQLNodeTable table) { _fatherTable = table; }


/*
 * -----------------------------------------------------
 */
        
    /**
     * @return Le Nom de la table
     */
    public String 
    get_tabName() { return _tabName; }
 
    /**
     * @return Le nombre colone utiliser dans la table
     */
    public int 
    get_usedcount() { return _usedColumn.size(); }

    /**
     * @return Le nombre de fils de la table
     */
    public int 
    get_childcount() { return _childTable.size(); }
    
    /**
     * @return Le SQLTableNode Pere de la table
     */
    public SQLNodeTable 
    get_fatherTable() { return _fatherTable; }
    
    /**
     * @return Le Nom de la table pere
     */
    public String 
    get_fatherName() 
    { if (_fatherLinkName == null) return null;
    else return _fatherLinkName.substring(0,_fatherLinkName.indexOf('.')); }

    /**
     * @return Le Nom de la colone servant de Link avec le pere
     */
    private String
    get_ColumsLinkName() 
    {   if (_tabLinkName == null) return null;
    else return _tabLinkName.substring(_tabLinkName.indexOf('.') + 1); }
    
    /**
     * @return Le Nom de la colone servant de Link avec la table, dans la table pere
     */
    private String 
    get_fatherLinkName() 
    { if (_fatherLinkName == null) return null;
    else return _fatherLinkName.substring(_fatherLinkName.indexOf('.') + 1 ); }

    /**
     * @return Le Constante temporel de laison entre la table et la table pere,
     *  utiliser dans la requette courante 
     */
    private Constant
    get_tmpLinkConstant() { return this._tmpLinkConstant; }
        
        
/*
 * -----------------------------------------------------
 */ 
    /**
     * test si la colonne et referancée dans l'abre
     */
    public boolean 
    reference_varRef(SQLNodeColumn colNode)
    {
        if (this._tabName.compareTo(colNode.get_sql_table_name()) == 0)
        {
            if (this._usedColumn.contains(colNode))
                return true;
            else
                return false;
        }
        for (SQLNodeTable tmp : this._childTable)
            if (tmp.reference_varRef(colNode))
                return true;
        return false;
    }
        
    /**
     * ajoute une referance a la colonne dans l'abre
     */
    public boolean 
    accept_varRef(SQLNodeColumn colnode)
    {
        if (this._tabName.compareTo(colnode.get_sql_table_name()) == 0)
            {
            this._usedColumn.add(colnode);
            return true;
            }
        for (SQLNodeTable tmp : this._childTable)
        {
            if (tmp.accept_varRef(colnode) == true)
                return true;
        }
        return false;
    }
        
/*
 * -----------------------------------------------------
 */
    
    /**
     * Test si les colums de la table dans la BDD son valide par 
     * raport au information dans l'appli
     */
    public boolean 
    testCheckBDD(SQLService sqlS)
    {
        SQLNodeColumn colNode;
        
        if (this.get_ColumsLinkName() != null)
            if(sqlS.existeColumn(this.get_tabName(), this.get_ColumsLinkName()))
            {
                colNode = new SQLNodeColumn(this.get_tabName() + "." + this.get_ColumsLinkName());
                colNode.set_is_PrimaryKey(true);
                colNode.set_type(SQLService.SQLSimpleType.INT);
                if(!sqlS.checkColumn(colNode)) return false;
            }
        for (SQLNodeColumn tmp : this._usedColumn)
            if (sqlS.existeColumn(this.get_tabName(), tmp.get_sql_column_name()))
                if(!sqlS.checkColumn(tmp)) return false;
        for (SQLNodeTable tmp : this._childTable)
            if (sqlS.existeColumn(this.get_tabName(), tmp.get_fatherLinkName()))
            {
                colNode = new SQLNodeColumn(this.get_tabName() + "." + tmp.get_fatherLinkName());
                colNode.set_type(SQLService.SQLSimpleType.INT);
                if(!sqlS.checkColumn(colNode)) return false;
            }
        for (SQLNodeTable tmp : this._childTable)
            if (!tmp.testCheckBDD(sqlS))
                return false;
        return true;
    }
    
    /**
     * test si les tables de la BDD correponde au table de l'apli
     */
    public boolean 
    testSynchoBDD(SQLService sqlS)
    {
        
        if (this.get_ColumsLinkName() != null)
            if(!sqlS.existeColumn(this.get_tabName(), this.get_ColumsLinkName()))
            {
                System.err.println("###Err: SQLTableNode.testSyncroBDD : Not existe Table or Column : " 
                        + this.get_tabName() + "."+ this.get_ColumsLinkName());
                return false;
            }
        for (SQLNodeColumn tmp : this._usedColumn)
            if (!sqlS.existeColumn(this.get_tabName(), tmp.get_sql_column_name()))
            {
                System.err.println("###Err: SQLTableNode.testSyncroBDD : Not existe Table or Column : "
                        + this.get_tabName() + "." + tmp.get_sql_column_name());
                return false;
            }
        for (SQLNodeTable tmp : this._childTable)
            if (!sqlS.existeColumn(this.get_tabName(), tmp.get_fatherLinkName()))
            {
                System.err.println("###Err: SQLTableNode.testSyncroBDD : Not existe Table or Column : "
                        + this.get_tabName() + "." + tmp.get_fatherLinkName());
                return false;
            }
        for (SQLNodeTable tmp : this._childTable)
            if (!tmp.testSynchoBDD(sqlS))
                return false;
        
        return true;
    }
    
    /**
     * Creation des table dans la BDD
     */
    public void 
    synchonisedBDD(SQLService sqlS)
    {
       SQLNodeColumn colNode;

       if (this.get_ColumsLinkName() != null)
       {
           colNode = new SQLNodeColumn(this.get_tabName() + "." + this.get_ColumsLinkName());
           colNode.set_is_PrimaryKey(true);
           colNode.set_type(SQLService.SQLSimpleType.INT);
           sqlS.testAndCreatColumn(colNode);
       }
       for (SQLNodeColumn tmp : this._usedColumn)
           sqlS.testAndCreatColumn(tmp);
       for (SQLNodeTable tmp : this._childTable)
       {
           colNode = new SQLNodeColumn(this.get_tabName() + "." + tmp.get_fatherLinkName());
           colNode.set_type(SQLService.SQLSimpleType.INT);
           sqlS.testAndCreatColumn(colNode);
       }
       for (SQLNodeTable tmp : this._childTable)
           tmp.synchonisedBDD(sqlS);
    }


/*
 * -----------------------------------------------------
 */
    
    /**
     * Parcour en profondeur permetant de cree la partie FROM de la requette SELECT 
     */
    public String toFromTerm()
    {
        String res = new String();
    
        if (this._fatherTable != null)
            res += "JOIN ";
        res += this._tabName;
        if (this._fatherTable != null)
            res += " ON " + this._tabLinkName + " = " + this._fatherLinkName;
        res += " ";
        for (SQLNodeTable tmp : this._childTable)
            res += tmp.toFromTerm();
        return res;
    }
        

    public void
    clear_varRef()
    {
        for (SQLNodeTable tmp : this._childTable)
            tmp.clear_varRef();
        for (SQLNodeColumn tmp : this._usedColumn)
            tmp.set_tmp_content(null);
    }

    
/*
 * -----------------------------------------------------
 */
 
    private String
    select_LinkContant()
    {
        String res = new String();
        
        res += "SELECT DISTINCT "; 
        if (this.get_ColumsLinkName() == null)
            res += "*";
        else 
            res += this.get_ColumsLinkName(); 
        res += " FROM ";
        res += this.get_tabName(); 
        res += " WHERE 1";

        for (SQLNodeColumn tmp : this._usedColumn)
        {
            if (tmp.get_tmp_content() == null)
                System.err.println("##Err:SQLNodeTable.select_LinkContant : Bad used Column refer");
            else
                res += " AND " + tmp.get_sql_column_name() + " = '" + tmp.get_tmp_content_stringValue() + "'";
        }
        for (SQLNodeTable tmp : this._childTable)
            res += " AND " + tmp.get_fatherLinkName() + " = '" + tmp.get_tmpLinkConstant().stringValue() + "'"; 
        res += ";";
        
        return res;
    }
    
    private String
    insert_LinkContant()
    {
        String  res = new String();
        boolean first;
        
        res += "INSERT INTO " + this.get_tabName() + " (";

        first = true;
        
        for (SQLNodeColumn tmp : this._usedColumn)
        {
            if (tmp.get_tmp_content() == null)
                System.err.println("##Err:SQLNodeTable.insert_LinkContant : Bad used Column refer 1");
            else
            {
                if (first) first = false; else res += ", "; 
                res += tmp.get_sql_column_name();
            }
        }
        for (SQLNodeTable tmp : this._childTable)
            {
            if (first) first = false; else res += ", "; 
            res += tmp.get_fatherLinkName(); 
            }

        res += ") VALUES (";
        first = true;
        for (SQLNodeColumn tmp : this._usedColumn)
        {
            if (tmp.get_tmp_content() == null)
                System.err.println("##Err:SQLNodeTable.insert_LinkContant : Bad used Column refer 2");
            else
            {   
                if (first) first = false; else res += ", ";
                res += "'" + tmp.get_tmp_content_stringValue() + "'";
            }
        }
       for (SQLNodeTable tmp : this._childTable)
        {
            if (first) first = false; else res += ", ";
            res += "'" + tmp.get_tmpLinkConstant().stringValue() + "'"; 
        }

        res += ");";
        
        return res; 
    }
    
    
    public boolean 
    assert_inSQL(SQLService sqlservice)
    {
        MatchResult         tmp_matchres;
        QueryResult         tmp_res;

        try
        {
            for (SQLNodeTable child : this._childTable)
                if (!child.assert_inSQL(sqlservice)) return false;
            
            if (this.get_ColumsLinkName() !=  null)
            {
                tmp_matchres = new MatchResult();
                tmp_matchres.add(0, new MetaTermReferenceNode("id"));
                tmp_res = sqlservice.execQuery(select_LinkContant(), tmp_matchres);
                if (tmp_res.size() == 0)
                {
                    sqlservice.exec(this.insert_LinkContant());
                    tmp_res = sqlservice.execQuery(this.select_LinkContant(), tmp_matchres);
                    if (tmp_res.size() == 0) return false;
                }
                this._tmpLinkConstant = (Constant)((MatchResult)tmp_res.getResult(0)).getTerm("id");
                return true;
            }
            else 
            {
                if (!sqlservice.execQueryNotEmpti(this.select_LinkContant()))
                    sqlservice.exec(this.insert_LinkContant());
            }
         
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return false;
    }
    
/*
 * -----------------------------------------------------
 */    
    
    
    private String
    delete_LinkContant()
    {
        String res = new String();
        
        res += "DELETE FROM "; 
        res += this.get_tabName(); 
        res += " WHERE 1";

        
        for (SQLNodeColumn tmp : this._usedColumn)
        {
            if (tmp.get_tmp_content() == null)
                System.err.println("##Err:SQLNodeTable.delete_LinkContant : Bad used Column refer");
            else
                res += " AND " + tmp.get_sql_column_name() + " = '" + tmp.get_tmp_content_stringValue() + "'";
        }
       for (SQLNodeTable tmp : this._childTable)
            res += " AND " + tmp.get_fatherLinkName() + " = '" + tmp.get_tmpLinkConstant().stringValue() + "'"; 
        res += ";";
        
        return res;
    }
    

    public boolean 
    remove_inSQL(SQLService sqlservice)
    {
        MatchResult         tmp_matchres;
        QueryResult         tmp_res;

        try
        {
            for (SQLNodeTable child : this._childTable)
                if (!child.assert_inSQL(sqlservice)) return false;
            
            if (this.get_ColumsLinkName() !=  null)
            {
                tmp_matchres = new MatchResult();
                tmp_matchres.add(0, new MetaTermReferenceNode("id"));
                tmp_res = sqlservice.execQuery(select_LinkContant(), tmp_matchres);
                if (tmp_res.size() == 0) return false;
                this._tmpLinkConstant = (Constant)((MatchResult)tmp_res.getResult(0)).getTerm("id");
                return true;
            }
            else 
            {
                if (sqlservice.execQueryNotEmpti(this.select_LinkContant()))
                    sqlservice.exec(this.delete_LinkContant());
            }
         
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return false;
    }
    
    

    
/*
 * ----------------------------------------------------------------
 */

    static int nb_to_string_tab = 0;

    public String toString()
    {
        String res = new String();
    
        for (int i = 0; i < this.nb_to_string_tab; i++)
            res += "  ";
        res += "[" + this._tabName + "]";
        if (this._fatherTable != null)
            res += " ON '" + this._tabLinkName + "' = '" + this._fatherLinkName + "'";
        res += "\n";

        for (SQLNodeColumn tmp : this._usedColumn)
          {
          for (int i = 0; i < this.nb_to_string_tab; i++)
              res += "  ";
          res += " |" + tmp.get_sql_column_name() + "\n";
          }
        this.nb_to_string_tab++;
        
        for (SQLNodeTable tmp : this._childTable)
            res += tmp.toString();
        
        this.nb_to_string_tab--;
        return res;
    }
    
}

