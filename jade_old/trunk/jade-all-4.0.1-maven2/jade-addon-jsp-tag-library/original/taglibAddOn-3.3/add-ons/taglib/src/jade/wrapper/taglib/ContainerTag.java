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

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.Logger;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This class defines a container tag to be used in any Servlet/JSP container.
 * 
 * Note that due to security reasons, only containers (not platforms) can be
 * used inside JSP pages.
 *
 * Usage:
 * <pre>
 *  <jade:useAgent id="mycontainer" host="myhost" port="1099" 
 *                                  params="optional string parameters"/>
 * </pre>
 *
 * @author Olivier Fourdrinoy
 * @author Daniel Le Berre
 */

public class ContainerTag extends TagSupport {

     String id;	
     String host="localhost";
     int port=1099;
     String params;

     Object beanObject;

     boolean init = false;
     
     private static Logger logger = Logger.getMyLogger(ContainerTag.class.getName());


     public void setId(String val) {
          this.id = val;
     }

     public void setHost(String val) {
          this.host = val;
     }

     public void setPort(int val) {
          this.port = val;
     }

     public void setParams(String val) {
          this.params = val;
     }

     public void setInit(boolean val) {
          this.init = val;
     }

     public int doStartTag() throws JspException {
 
	 beanObject = pageContext.getAttribute(id, PageContext.APPLICATION_SCOPE);
	 
	 if (beanObject != null) {
	     
	     try {
		 if (!Class.forName("jade.wrapper.AgentContainer").isInstance(beanObject))
		     throw new JspException("ClassCastException: " + id );
	     } catch (Exception ex) {
		 throw new JspException (ex.toString());
	     }
	     return SKIP_BODY;
	     
        }
	 synchronized((ServletContext)
		      pageContext.findAttribute(PageContext.APPLICATION)) {
	     beanObject = createBean();
	     if(logger.isLoggable(Logger.INFO))
	     	logger.log(Logger.INFO," saving container for id "+id+" "+beanObject);
	     pageContext.setAttribute(id, beanObject,PageContext.APPLICATION_SCOPE);
	     setInit(true);
	 }		
	 return SKIP_BODY;
     }
	

    private Object createBean() throws JspException {
       	if(logger.isLoggable(Logger.INFO))
       		logger.log(Logger.INFO,"container jade created");
	jade.core.Runtime rt = jade.core.Runtime.instance();
	Profile p = new ProfileImpl(host,port,id);
	Object o =  rt.createAgentContainer(p);
	if (o==null) {
	    throw new JspException("Unable to create container "+id+" on "+host+" port "+port);
	}
	return o;
    }
}
