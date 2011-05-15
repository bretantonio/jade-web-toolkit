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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Descriptor of a port exposed by the web-service.
 * 
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class PortInfo {

	private String name;
	private String documentation;
	private Map<String, OperationInfo> operationsInfo = new HashMap<String, OperationInfo>();
	private Method stubMethod;
	
	PortInfo(String portName, Method stubMethod) {
		this.name = portName;
		this.stubMethod = stubMethod;
	}

	/**
	 * Return the name of the port
	 * 
	 * @return the name of port
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the wsdl documentation associated at this port
	 * <p>
	 * <code>
	 *  &lt;wsdl:port name="PortName"&gt;<br>
	 *		&lt;wsdl:documentation&gt;port documentation&lt;/wsdl:documentation&gt;<br>
	 *	&lt;/wsdl:port&gt;<br>
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
	
	/**
	 * Return the list of operations associated to this port
	 *  
	 * @return list of operations 
	 * 
	 * @see jade.webservice.dynamicClient.OperationInfo
	 */
	public Set<String> getOperationNames() {
		return operationsInfo.keySet();
	}

	/**
	 * Return the information of specific operation
	 *  
	 * @param operationName name of the operation
	 * @return operation informations
	 * 
	 * @see jade.webservice.dynamicClient.OperationInfo
	 */
	public OperationInfo getOperation(String operationName) {
		return operationsInfo.get(operationName);
	}
	
	void putOperation(String operationName, OperationInfo operationInfo) {
		operationsInfo.put(operationName, operationInfo);
	}

	Method getStubMethod() {
		return stubMethod;
	}

}
