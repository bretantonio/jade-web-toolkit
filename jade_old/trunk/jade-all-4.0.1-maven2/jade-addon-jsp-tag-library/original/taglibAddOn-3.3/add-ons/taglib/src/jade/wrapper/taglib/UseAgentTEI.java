/*****************************************************************
Copyright (C) 2002 - the authors donate the copyright to the 
originator/manager of the JADE Open source Project

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
package jade.wrapper.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class UseAgentTEI extends TagExtraInfo {
    
    public VariableInfo[] getVariableInfo(TagData data) {
	String type = data.getAttributeString("type");
	if (type==null) {
	    type = "jade.wrapper.AgentController";
	}
        return new VariableInfo[] 
            {
                new VariableInfo(data.getAttributeString("id"),
                                 type,
                                 true,
                                 VariableInfo.AT_BEGIN)
		    };
    }
}
