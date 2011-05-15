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
package jade.webservice.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaServiceWriter;
import org.apache.axis.wsdl.toJava.Utils;

//#APIDOC_EXCLUDE_FILE

public final class WSDLUtils {

	public static String buildPortNameJavaId(SymbolTable st, Port port) {
		String portName;
		Binding b = port.getBinding();
		BindingEntry be = st.getBindingEntry(b.getQName());
		portName = (String) be.getDynamicVar(JavaServiceWriter.PORT_NAME + ":" + port.getName());
		if(portName == null) {
			portName = port.getName();
		}
		if(!JavaUtils.isJavaId(portName)) {
			portName = Utils.xmlNameToJavaClass(portName);
		}
		return portName;
	}
	
	public static Collection<Port> getPorts(ServiceEntry serviceEntry) {
		return serviceEntry.getService().getPorts().values();
	}
	
	public static List<ServiceEntry> getServices(Parser wsdlParser) {
		List<ServiceEntry> services = new ArrayList<ServiceEntry>();
		HashMap map = wsdlParser.getSymbolTable().getHashMap();
		Iterator iterator = map.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			QName key = (QName) entry.getKey();
			Vector v = (Vector) entry.getValue();

			for(int i = 0; i < v.size(); ++i) {
				SymTabEntry symTabEntry = (SymTabEntry) v.elementAt(i);

				if(ServiceEntry.class.isInstance(symTabEntry)) {
					services.add((ServiceEntry) symTabEntry);
				}
			}
		}
		return services;
	}

	public static List<BindingOperation> getOperations(Port servicePort) {
		return servicePort.getBinding().getBindingOperations();
	}

	public static QName getSOAPHeaderQName(SOAPHeader soapHeader, Emitter emitter) {
		QName result;
		SymbolTable st = emitter.getSymbolTable();
		MessageEntry messageEntry = st.getMessageEntry(soapHeader.getMessage());
		Part part = messageEntry.getMessage().getPart(soapHeader.getPart());

		String type;
		if (part.getTypeName() == null) {
			Element element = st.getElement(part.getElementName());
			result = element.getQName();
		} else {
			String name;
			String namespace;
			name = soapHeader.getPart();
			namespace = soapHeader.getNamespaceURI();
			result = new QName(namespace, name);
		}
		return result;
	}
	
}
