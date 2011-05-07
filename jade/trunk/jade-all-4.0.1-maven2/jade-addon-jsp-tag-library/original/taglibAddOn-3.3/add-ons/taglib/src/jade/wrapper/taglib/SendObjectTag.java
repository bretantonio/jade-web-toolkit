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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class SendObjectTag extends BodyTagSupport {

    private String id;
    private boolean blocking=false;

    public String getId() {
        return id;
    }

    public void setId(String val) {
        this.id = val;
    }

    public boolean getBlocking() {
        return blocking;
    }

    public void setBlocking(boolean val) {
        this.blocking = val;
    }

    public int doStartTag() {
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspException {
        try {
            System.out.println("prepare to send object"+id);
	    int scope = pageContext.getAttributesScope(id);
	    if (scope==0) {
		throw new JspException ("sendObject: id "+id+" not found");
	    }
	    jade.wrapper.AgentController agent =
		(jade.wrapper.AgentController) pageContext.getAttribute(id,scope);
	    if (agent==null) {
		throw new JspException ("sendObject: id "+id+" not found");
	    }
	    agent.putO2AObject(bodyContent.getString(),blocking);
        } catch (Exception ex) {
            throw new JspException("Could set pageContext attribute");
        }
        return SKIP_BODY;
    }

}
