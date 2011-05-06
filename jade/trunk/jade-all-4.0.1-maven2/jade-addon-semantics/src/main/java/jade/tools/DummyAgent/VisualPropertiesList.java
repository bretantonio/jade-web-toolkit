/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * VisualPropertiesList2.java
 * Created on 5 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;




import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class VisualPropertiesList extends VisualStringList
{
    /**
  @serial
  */
  private Properties userDefinedSlots;
    
    VisualPropertiesList(Properties content, Component owner)
    {
        super(new ArrayList().iterator(),owner);
        
        this.userDefinedSlots = content;
        
        Enumeration e = content.propertyNames();
    
        ArrayList list = new ArrayList();
        while(e.hasMoreElements())
            list.add(e.nextElement());
        
        resetContent(list.iterator());
    }
    
    
    
    @Override
	protected Object editElement(Object el, boolean isEditable)
    {
        SingleProperty p;
    String out = null;
    
        if (el == null)
            p = new SingleProperty("", "");
        else 
            p = new SingleProperty((String)el,userDefinedSlots.getProperty((String)el));
        UserPropertyGui gui = new UserPropertyGui(owner);
        p = gui.ShowProperty(p,isEditable);

        if(p != null)
        {
            out = p.getKey();
            userDefinedSlots.setProperty(out,p.getValue());
         } 
         
        return out; 
        
    }
    
    // This method have been overridden because in this case 
    //is necessary to update the properties 
    @Override
	protected void removeElement(Object el)
    {
        super.removeElement(el);
        userDefinedSlots.remove(el);
    }
    
    /**
    * This method must be used to retry the actual properties. 
    * The method getContent of the super class only returns the keys.
    */
    
    public Properties getContentProperties()
    {
        return userDefinedSlots;
    }
    
    /**
    * This method must be used after a resetContent on this list to update the properties.
    */
    
    public void setContentProperties(Properties p)
    {
        this.userDefinedSlots = p;
    }
    
}
