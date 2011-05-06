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

import jade.semantics.ext.sqlwrapper.sqltool.imp.SQLService.SQLSimpleType;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.StringConstant;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class SQLNodeColumn
{
    private String                      _var_name;
    private String                      _link_name;
    private String                      _math;
    private SQLService.SQLSimpleType    _type; 
    private boolean                     _is_PrimaryKey;
        
    private Term                    _tmp_content;

    /*
     * --------------------------------------------------------
     */
    
    public static SQLSimpleType
    type_of_col(String metavar_name, List<SQLNodeColumn> l_col)
    {
        
        for (SQLNodeColumn tmp : l_col)
        {
            if (tmp.get_metavar_name().compareTo(metavar_name) == 0)
                return tmp.get_type();
        }
        return null;
    }
    
    /*
     * --------------------------------------------------------
     */

    private void 
    Init()
    {
        this._type = SQLSimpleType.SLTERM;
        this._is_PrimaryKey = false;
        this._math = "";
    }
    
    public 
    SQLNodeColumn(String link_name)
    {
        this._var_name = "";
        this._link_name = link_name.toUpperCase();
        this.Init();
    }
    

    public 
    SQLNodeColumn(String var_name, String link_name)
    {
        this._var_name = var_name;
        this._link_name = link_name.toUpperCase();
        this.Init();
    }
    
    
    
    /*
     * --------------------------------------------------------
     */

    public String get_mapping_name()
    { return _link_name; }

    public String get_sql_column_name()
    { return _link_name.substring(_link_name.indexOf('.') + 1); }

    public String get_sql_table_name()
    { return _link_name.substring(0, _link_name.indexOf('.')); }

    
    public String get_metavar_name()
    { return _var_name; }

    public SQLService.SQLSimpleType get_type()
    { return _type; }

    public String get_math()
    { return this._math; }
    
    public boolean is_PrimaryKey()
    { return this._is_PrimaryKey; }
    
    public boolean is_math()
    { return this._math.compareTo("") != 0; }


    public Term get_tmp_content()
    { return this._tmp_content; }
    
    public String get_tmp_content_stringValue()
    { 
        if (_tmp_content instanceof IntegerConstantNode ||
            _tmp_content instanceof RealConstantNode ||
            _tmp_content instanceof StringConstant
            )
        {
         return ((Constant)_tmp_content).stringValue();   
        } 
        else if (_tmp_content instanceof DateTimeConstantNode)
        {
            DateTimeConstantNode tmp = (DateTimeConstantNode) _tmp_content;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return dateFormat.format(tmp.lx_value());
        }
        else if (_tmp_content instanceof Term)
        {
            return _tmp_content.toString();
        }
        return ""; 
    }

    
    /*
     * --------------------------------------------------------
     */
    
    public void set_type(SQLService.SQLSimpleType _type)
    { this._type = _type; }

    public boolean set_type(String s)
    { 
        boolean ok = false;
        if (s.compareTo("INT") == 0)
            {
            this._type = SQLService.SQLSimpleType.INT;
            ok = true;
            }
        else if (s.compareTo("REAL") == 0)
            {
            this._type = SQLService.SQLSimpleType.REAL;
            ok = true;
            }
        else if (s.compareTo("BYTE") == 0)
            {
            this._type = SQLService.SQLSimpleType.BYTE;
            ok = true;
            }
        else if (s.compareTo("STRING") == 0)
            {
            this._type = SQLService.SQLSimpleType.STRING;
            ok = true;
            }
        else if (s.compareTo("DATETIME") == 0)
            {
            this._type = SQLService.SQLSimpleType.DATETIME;
            ok = true;
            }
        else if (s.compareTo("SLTERM") == 0)
            {
            this._type = SQLService.SQLSimpleType.SLTERM;
            ok = true;
            }
        return ok;
    }

    public boolean set_mat(String s)
    { 
        if (s.compareTo("COUNT") == 0 
            || s.compareTo("AVG") == 0 
            || s.compareTo("MIN") == 0 
            || s.compareTo("MAX") == 0 
            || s.compareTo("STDDEV") == 0 
            || s.compareTo("SUM") == 0 
            || s.compareTo("VARIANCE") == 0 
            || s.compareTo("BIT_AND") == 0 
            || s.compareTo("BIT_OR") == 0 
            || s.compareTo("BIT_XOR") == 0 
//            || s.compareTo("GROUP_CONCAT") == 0 
                    )
        {
            this._math = s; 
            return true;
        }
        return false;
    }
    
    public boolean set_tmp_content(Constant c)
    { 
        if ((this._type == SQLSimpleType.INT && !(c instanceof IntegerConstantNode)) || 
            (this._type == SQLSimpleType.REAL && !(c instanceof RealConstantNode)) ||
            (this._type == SQLSimpleType.DATETIME && !(c instanceof DateTimeConstantNode)) ||
            (this._type == SQLSimpleType.STRING && (!(c instanceof StringConstantNode) && !(c instanceof WordConstantNode))) ||
            (this._type == SQLSimpleType.SLTERM && !(c instanceof StringConstantNode)) ||
            (this._type == SQLSimpleType.BYTE   && !(c instanceof ByteConstantNode)) 
           )
            return false;
        this._tmp_content = c;
        return true;
    }

    public void set_is_PrimaryKey(boolean b)
    { this._is_PrimaryKey = b; }
    
    /*
     * --------------------------------------------------------
     */

    public String 
    toString()
    {
        String res = "";
        res += "[" + this.get_metavar_name() + "] ";
        res += this.get_mapping_name() + " ";
        if (this.is_PrimaryKey())
            res += "PKey";
        
        return res;
    }
    
}

