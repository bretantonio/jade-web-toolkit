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

import jade.util.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
/***********************************************************************/

/**
 * This class defines an agent tag to be used in any Servlet/JSP container.
 *
 * Usage:
 * <pre>
 *  <jade:useAgent id="snooper" classname="examples.jsp.Snooper" 
 *   container="mycontainer" type="jade.wrapper.Agent" scope="application"/>
 * </pre>
 *
 * @author Olivier Fourdrinoy
 * @author Daniel Le Berre
 */
public class UseAgentTag extends BodyTagSupport {

     String container;	     
     jade.wrapper.AgentController ac;
     
     //logger object
     private static Logger logger = Logger.getMyLogger(UseAgentTag.class.getName());

     // Java Bean set properties method. 
     public void setContainer(String val) {
          this.container = val;
     }

    private String id;
    private String scope = "page";
    private String classname;
    private String type = "jade.wrapper.Agent";
    private boolean init = false;
    private Object beanObject;

    public void setId(String val) {
	this.id = val;
    }
    
    public void setScope(String val) {
	this.scope = val;
    }

    public void setClassname(String val) {
	this.classname = val;
    }

    public void setType(String val) {
	this.type = val;
    }

    public void setInit(boolean val) {
	this.init = val;
    }

    
    public int doStartTag() throws JspException { 
	// DLB 23/5/02: added scope to pageContext.setAttribute()

	String clas = (this.classname == null)? type : this.classname;
	
	String scop;
	int scopid;
	
	if ((scope==null)||scope.equals("page")) {
	    scop = PageContext.PAGE;
	    scopid = PageContext.PAGE_SCOPE;
	} else if (scope.equals("request")) {
	    scop = PageContext.REQUEST;
	    scopid = PageContext.REQUEST_SCOPE;
	} else if (scope.equals("session")) {
	    scop = PageContext.SESSION;
	    scopid = PageContext.SESSION_SCOPE;
	} else if (scope.equals("application")) {
	    scop = PageContext.APPLICATION;
	    scopid = PageContext.APPLICATION_SCOPE;
	} else {
	    throw new JspException ("useAgent: wrong scope attribute: "+scope);
	}
	try {
		
	    // useBean idiosyncrasies.
	    if (type == null) type = clas;
	    
	    // locate the object.
	    beanObject = pageContext.getAttribute(id,scopid);
	    
	    if (beanObject != null) {
		if (!Class.forName(type).isInstance(beanObject))
		    throw new JspException("ClassCastException: " +
					   id + "  " + clas+" "+type);
		return SKIP_BODY;
	    }
	    
	    synchronized(pageContext.findAttribute(scop)) {
		beanObject = createBean();
		pageContext.setAttribute(id, beanObject, scopid);
		setInit(true);
	    }
	    
	    // Check if the body needs to be evaluated.
	    if (init == false) return SKIP_BODY;
	    else return EVAL_BODY_TAG;
	} catch (Exception ex) {
	    throw new JspException(ex.getMessage());
	}
    }
    
    public int doAfterBody() {
	return SKIP_BODY;
    }
    
    /***********************************************************************/
    
    protected Object createBean() throws JspException {
	// créer un bean uniquement si classname est spécifié.
	// sinon le bean doit etre placé dans la portée (scope ).
	
	try {
	    if(logger.isLoggable(Logger.INFO))
	    	logger.log(Logger.INFO,"Creating new Agent "+id);
	    jade.wrapper.AgentContainer ac = 
		(jade.wrapper.AgentContainer) pageContext.getAttribute(container, PageContext.APPLICATION_SCOPE);
	    if (ac==null) {
		throw new JspException ("useAgent: container "+container+" not found");
	    }
	    jade.wrapper.AgentController agent = ac.createNewAgent(id,classname, new Object[0]);
	    if (agent==null) {
		throw new JspException ("useAgent: agent"+id+" cannot be created");
	    }
	    agent.start();
	    return agent;
	} catch (Exception ex) {
	    throw new JspException ("useAgent: class " + classname + " non trouvée."+ex);
	}
    }

}


