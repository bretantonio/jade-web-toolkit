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
package examples.dynamicClient;

import jade.content.abs.AbsConcept;
import jade.webservice.dynamicClient.DynamicClient;
import jade.webservice.dynamicClient.OperationInfo;
import jade.webservice.dynamicClient.ParameterInfo;
import jade.webservice.dynamicClient.PortInfo;
import jade.webservice.dynamicClient.ServiceInfo;
import jade.webservice.dynamicClient.WSData;

import java.net.URI;
import java.util.Set;

/**
 * Example of use of DynamicClient to invoke the operations 
 * offered by the webservice MathFunctions and
 * obtain informations on the wsdl
 */
public class MathClient {

	public static void main(String[] args) {
		try {

			// Get an instance of DynamicClient
			DynamicClient dc = new DynamicClient();

			// Initialize DynamicClient for MathFunctions webservice by file
			dc.initClient(new URI("file:./MathFunctions.wsdl"));

			// Get the DynamicClient for MathFunctions webservice by url
			// dc.initClient(new URI("http://localhost:2000/axis/services/MathFunctionsPort?wsdl"));

			
			// Example of invocation of an operation (sum) with primitive input/output parameters only
			// ---------------------------------------------------------------------------------------
			WSData input = new WSData();
			input.setParameter("firstElement", 5);
			input.setParameter("secondElement", 3);
			// Invoke the sum operation
			WSData output = dc.invoke("sum", input);
			float sum = output.getParameterFloat("sumReturn");
			
			
			// Example of invocation of an operation (sumComplex) with structured input/output parameters
			// We use Abstract Descriptors (AbsConcept) to represent them.
			// ------------------------------------------------------------------------------------------
			input = new WSData();
			// Create an abstract descriptor representing a Complex number
			AbsConcept absComplex1 = new AbsConcept("Complex");
			absComplex1.set("real", 4);
			absComplex1.set("imaginary", 5);
			// Create another abstract descriptor representing another Complex number
			AbsConcept absComplex2 = new AbsConcept("Complex");
			absComplex2.set("real", 1);
			absComplex2.set("imaginary", 3);
			input.setParameter("firstComplexElement", absComplex1);
			input.setParameter("secondComplexElement", absComplex2);
			// Invoke the sumComplex operation
			output = dc.invoke("sumComplex", input);
			AbsConcept sumComplex = (AbsConcept) output.getParameter("sumComplexReturn");
			float real = sumComplex.getFloat("real");
			float imaginary = sumComplex.getFloat("imaginary");
			
			
			// Example to get wsdl informations
			// --------------------------------
			// Get the list of service names 
			Set<String> serviceNames = dc.getServiceNames();
			
			// Get a specific service informations
			ServiceInfo serviceInfo = dc.getService("MathFunctionsService");
			System.out.println(serviceInfo.getName()+": "+serviceInfo.getDocumentation());
			
			// Get the list of port names for service 
			Set<String> portNames = serviceInfo.getPortNames();
			
			// Get a specific port informations
			PortInfo portInfo = serviceInfo.getPort("MathFunctionsPort");
			System.out.println(portInfo.getName()+": "+portInfo.getDocumentation());
			
			// Get the list of operation names for service 
			Set<String> opNames = portInfo.getOperationNames();

			// Get a specific operation informations
			OperationInfo opInfo = portInfo.getOperation("sum");
			System.out.println(opInfo.getName()+": "+opInfo.getDocumentation());

			// Get input parameters informations
			for (String inParamName : opInfo.getInputParameterNames()) {
				ParameterInfo inParInfo = opInfo.getInputParameter(inParamName);
				System.out.println(inParamName+": "+inParInfo.getDocumentation());
			}

			// Get output parameters informations
			for (String outParamName : opInfo.getOutputParameterNames()) {
				ParameterInfo outParInfo = opInfo.getOutputParameter(outParamName);
				System.out.println(outParamName+": "+outParInfo.getDocumentation());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
