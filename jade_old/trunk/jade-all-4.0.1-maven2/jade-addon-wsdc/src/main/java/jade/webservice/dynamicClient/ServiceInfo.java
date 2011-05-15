/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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
package jade.webservice.dynamicClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.axis.client.Service;

/**
 * Descriptor of a service exposed by the web-service.
 * 
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class ServiceInfo {

	private String name;
	private String documentation;
	private Service locator;
	
	private Map<String, PortInfo> portsInfo = new HashMap<String, PortInfo>();
	
	ServiceInfo(String serviceName, Service locator) {
		this.name = serviceName;
		this.locator = locator;
	}

	/**
	 * Return the name of the service
	 * 
	 * @return the name of service
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the wsdl documentation associated at this service
	 * <p>
	 * <code>
	 *  &lt;wsdl:service name="ServiceName"&gt;<br>
	 *		&lt;wsdl:documentation&gt;service documentation&lt;/wsdl:documentation&gt;<br>
	 *	&lt;/wsdl:service&gt;<br>
	 * </code>
	 *   
	 * @return wsdl documentation
	 */
	public String getDocumentation() {
		return documentation;
	}

	void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	Service getLocator() {
		return locator;
	}

	/**
	 * Return the list of ports associated to this service
	 *  
	 * @return list of ports 
	 * 
	 * @see jade.webservice.dynamicClient.PortInfo
	 */
	public Set<String> getPortNames() {
		return portsInfo.keySet();
	}

	/**
	 * Return the information of specific port
	 *  
	 * @param portName name of the port
	 * @return port informations
	 * 
	 * @see jade.webservice.dynamicClient.PortInfo
	 */
	public PortInfo getPort(String portName) {
		if (portName == null && portsInfo.values().iterator().hasNext()) {
			return portsInfo.values().iterator().next();
		}
		
		return portsInfo.get(portName);
	}
	
	void putPort(String portName, PortInfo portInfo) {
		portsInfo.put(portName, portInfo);
	}
}
