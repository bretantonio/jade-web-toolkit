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

import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.OntologyException;
import jade.content.schema.AggregateSchema;
import jade.content.schema.TermSchema;
import jade.webservice.utils.WSDLUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionType;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaTypeWriter;
import org.apache.axis.wsdl.toJava.Utils;

//#APIDOC_EXCLUDE_FILE

public class OperationParser {

	private List<HeaderInfo> explicitHeadersList;
	private Map<QName, HeaderInfo> implicitHeadersMap = new HashMap<QName, HeaderInfo>();

	private List<ParameterInfo> parametersList;

	private BeanOntology typeOnto;
	private ClassLoader classloader;
	
	
	public OperationParser(BindingOperation bindingOperation, Binding bindingPort, Emitter emitter, BeanOntology typeOnto, ClassLoader classloader) throws ClassNotFoundException, OntologyException {
		this.typeOnto = typeOnto;
		this.classloader = classloader;
		
		Operation operation = bindingOperation.getOperation();
		SymbolTable symbolTable = emitter.getSymbolTable();
		BindingEntry bEntry = symbolTable.getBindingEntry(bindingPort.getQName());
		Parameters parameters = (Parameters) bEntry.getParameters().get(operation);
		Map<QName, HeaderInfo> explicitInputWadeHeadersMap = new HashMap<QName, HeaderInfo>();

		explicitHeadersList = new ArrayList<HeaderInfo>();
		parametersList = new ArrayList<ParameterInfo>();
		
		if (parameters.returnParam != null) {
			parametersList.add(convertToParameterInfo(parameters.returnParam, emitter, bEntry, true));
		}

		Iterator<Parameter> iterator = parameters.list.iterator();
		int i = 0;
		Parameter param;
		while(iterator.hasNext()) {
			param = iterator.next();

			if (param.isInHeader() || param.isOutHeader()) {
				implicitHeadersMap.put(param.getQName(), convertToHeaderInfo(param, emitter, i));
			} else {
				// param belongs to operation parameters, it isn't in a header: skip it
				parametersList.add(convertToParameterInfo(param, emitter, bEntry));
			}
			i++;
		}

		Set<QName> inHeaders = new HashSet<QName>();
		Iterator<Object> iter;
		SOAPHeader sh;
		QName shqn;
		if (bindingOperation.getBindingInput() != null) {
			iter = bindingOperation.getBindingInput().getExtensibilityElements().iterator();
			while(iter.hasNext()) {
				Object o = iter.next();
				if (o instanceof SOAPHeader) {
					sh = (SOAPHeader) o;
					shqn = WSDLUtils.getSOAPHeaderQName(sh, emitter);
					if (!implicitHeadersMap.keySet().contains(shqn)) {
						explicitInputWadeHeadersMap.put(shqn, convertToHeaderInfo(sh, emitter, ParameterInfo.IN, -1));
						inHeaders.add(shqn);
					}
				}
			}
		}

		if (bindingOperation.getBindingOutput() != null) {
			iter = bindingOperation.getBindingOutput().getExtensibilityElements().iterator();
			while(iter.hasNext()) {
				Object o = iter.next();
				if (o instanceof SOAPHeader) {
					sh = (SOAPHeader) o;
					shqn = WSDLUtils.getSOAPHeaderQName(sh, emitter);
					if (!implicitHeadersMap.keySet().contains(shqn)) {
						if (inHeaders.contains(shqn)) {
							HeaderInfo wadeHeader = explicitInputWadeHeadersMap.remove(shqn);
							wadeHeader.setMode(ParameterInfo.INOUT);
							explicitHeadersList.add(wadeHeader);
						} else {
							explicitHeadersList.add(convertToHeaderInfo(sh, emitter, ParameterInfo.OUT, -1));
						}
					}
				}
			}
		}
		
		explicitHeadersList.addAll(explicitInputWadeHeadersMap.values());
	}
	
	private String extractDocumentation(Emitter emitter, String namespace, QName paramQName) {
		
//		<xsd:element name="diff">
//		  <xsd:annotation>
//				<xsd:documentation>comment diff</xsd:documentation>			<--- first level
//			</xsd:annotation>
//		  <xsd:complexType>
//		    <xsd:sequence>
//		      <xsd:element name="firstElement" type="xsd:float">
//		      	<xsd:annotation>
//		      		<xsd:documentation>comment diff.firstElement</xsd:documentation>	<--- second level
//		      	</xsd:annotation>
//		    	</xsd:element>
//		    </xsd:sequence>
//		  </xsd:complexType>
//		</xsd:element>
//
//		For de-wrapped params the localPart is in the form '>elementName>paramName' and namespace is null
//		In this case is necessary extract from localPart the root element name and the param element name 		
//		
//		For wrapped params and headers the paramQName is already correct		
		
		String comment = null;
		try {
			SymbolTable symbolTable = emitter.getSymbolTable();
			String paramLocalPart = paramQName.getLocalPart();

			// Get parameter root element qname
			QName elementQname = null;
			if (paramLocalPart.startsWith(">")) {
				int pos = paramLocalPart.indexOf('>', 1);
				if (pos > 1) {
					String operationElementName = paramLocalPart.substring(1, pos);
					elementQname = new QName(namespace, operationElementName);
				}
			} else {
				elementQname = paramQName;
			}
			
			if (elementQname != null) {
				// Get parameter root element 
				Element operationElement = symbolTable.getElement(elementQname);
				if (operationElement != null) {
				
					// Get first level comment 
					comment = SchemaUtils.getAnnotationDocumentation(operationElement.getNode());
		
					// Get second level comment (only for de-wrapped)
					if (operationElement.getRefType() != null && operationElement.getRefType().getContainedElements() != null) {
						for (Object obj : operationElement.getRefType().getContainedElements()) {
							ElementDecl paramElement = (ElementDecl)obj;
							if (paramElement.getQName().equals(paramQName)) {
								if (comment.length() > 0) {
									comment += ", ";
								}
								comment += paramElement.getDocumentation();
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return comment;
	}
	
	private ParameterInfo convertToParameterInfo(Parameter axisParam, Emitter emitter, BindingEntry bEntry) throws ClassNotFoundException, OntologyException {
		return convertToParameterInfo(axisParam, emitter, bEntry, false);
	}
	
	private ParameterInfo convertToParameterInfo(Parameter axisParam, Emitter emitter, BindingEntry bEntry, boolean returnType) throws ClassNotFoundException, OntologyException {
		String paramType = axisParam.getType().getName();
		if(!returnType && axisParam.getMode() != Parameter.IN) {
			Boolean holderIsNeeded = (Boolean) axisParam.getType().getDynamicVar(JavaTypeWriter.HOLDER_IS_NEEDED);
			if(holderIsNeeded != null && holderIsNeeded) {
				paramType = Utils.holder(axisParam, emitter);
			}
		}else {
	        // For base types that are nillable and are mapped to primitives,
	        // need to switch to the corresponding wrapper types.
	        if ((axisParam.isOmittable() && axisParam.getType().getDimensions().equals(""))
	            || (axisParam.getType() instanceof CollectionType
	                && ((CollectionType) axisParam.getType()).isWrapped())
	            || axisParam.getType().getUnderlTypeNillable()) {

	            paramType = Utils.getWrapperType(axisParam.getType());
	        }

		}
		ParameterInfo pi = new ParameterInfo(axisParam.getName());
		if (!returnType) {
			pi.setMode(convertAxisModeToParameterMode(axisParam.getMode()));
		} else {
			pi.setMode(ParameterInfo.RETURN);
		}
		paramType = JavaUtils.getLoadableClassName(paramType);
		pi.setTypeClass(getClassFromType(paramType));
		TermSchema schema = getSchemaFromType(paramType);
		pi.setSchema(schema);
		pi.setMandatory(!axisParam.isOmittable());
		pi.setDocumentation(extractDocumentation(emitter, bEntry.getQName().getNamespaceURI(), axisParam.getQName()));
		pi.setRegex(axisParam.getRegex());
		pi.setPermittedValues(axisParam.getPermittedValues());
		pi.setDefaultValue(axisParam.getDefaultValue());
		if (schema instanceof AggregateSchema) {
			Integer cardMin = axisParam.getMinOccurs();
			if (cardMin != null) {
				pi.setCardMin(cardMin);
			}
			Integer cardMax = axisParam.getMaxOccurs();
			if (cardMax != null) {
				pi.setCardMax(cardMax);
			}
		}
		
		return pi;
	}

	private HeaderInfo convertToHeaderInfo(SOAPHeader soapHeader, Emitter emitter, int headerMode, int signaturePosition) throws ClassNotFoundException, OntologyException {
		SymbolTable st = emitter.getSymbolTable();
		MessageEntry messageEntry = st.getMessageEntry(soapHeader.getMessage());
		Part part = messageEntry.getMessage().getPart(soapHeader.getPart());
		String name;
		String namespace;

		String type;
		QName qname;
		boolean mandatory = false;
		Element element;
		if (part.getTypeName() == null) {
			element = st.getElement(part.getElementName());
			qname = element.getQName();
			name = qname.getLocalPart();
			namespace = qname.getNamespaceURI();
			type = JavaUtils.getLoadableClassName(element.getName());
		} else {
			name = soapHeader.getPart();
			namespace = soapHeader.getNamespaceURI();
			qname = new QName(namespace, name);
			element = st.getElement(qname);
			type = JavaUtils.getLoadableClassName(st.getTypeEntry(part.getTypeName(), false).getName());
		}

		HeaderInfo hi = new HeaderInfo(name);
		hi.setNamespace(namespace);
		hi.setMode(headerMode);
		hi.setTypeClass(getClassFromType(type));
		hi.setSchema(getSchemaFromType(type));
		hi.setSignaturePosition(signaturePosition);
		hi.setDocumentation(extractDocumentation(emitter, null, qname));

		try {
			ElementDecl elementDecl = SchemaUtils.processChildElementNode(element.getNode(), st);
			elementDecl = SchemaUtils.getSanedElement(elementDecl);
			hi.setMandatory(!elementDecl.getMinOccursIs0());
			hi.setRegex(elementDecl.getRegex());
			hi.setPermittedValues(elementDecl.getPermittedValues());
			hi.setDefaultValue(elementDecl.getDefaultValue());
			Integer cardMin = elementDecl.getMinOccurs();
			if (cardMin != null) {
				hi.setCardMin(cardMin);
			}
			Integer cardMax = elementDecl.getMaxOccurs();
			if (cardMax != null) {
				hi.setCardMax(cardMax);
			}
		} catch(Throwable t) {
			// ENRICO: non si sa mai!
		}
		
		return hi;
	}
	
	private HeaderInfo convertToHeaderInfo(Parameter param, Emitter emitter, int signaturePosition) throws ClassNotFoundException, OntologyException {
		byte mode = param.getMode();
		String paramName = param.getQName().getLocalPart();
		String paramNamespace = param.getQName().getNamespaceURI();
		String paramType = JavaUtils.getLoadableClassName(param.getType().getName());

		if(mode != Parameter.IN) {
			Boolean holderIsNeeded = (Boolean) param.getType().getDynamicVar(JavaTypeWriter.HOLDER_IS_NEEDED);
			if(holderIsNeeded != null && holderIsNeeded) {
				paramType = Utils.holder(param, emitter);
			}
		}
		
		HeaderInfo hi = new HeaderInfo(paramName);
		hi.setNamespace(paramNamespace);
		hi.setMode(convertAxisModeToParameterMode(param.getMode()));
		hi.setTypeClass(getClassFromType(paramType));
		hi.setSchema(getSchemaFromType(paramType));
		hi.setSignaturePosition(signaturePosition);
		hi.setMandatory(!param.isOmittable());
		hi.setDocumentation(extractDocumentation(emitter, null, param.getQName()));
		hi.setRegex(param.getRegex());
		hi.setPermittedValues(param.getPermittedValues());
		hi.setDefaultValue(param.getDefaultValue());
		Integer cardMin = param.getMinOccurs();
		if (cardMin != null) {
			hi.setCardMin(cardMin);
		}
		Integer cardMax = param.getMaxOccurs();
		if (cardMax != null) {
			hi.setCardMax(cardMax);
		}
		
		return hi;
	}
	
	private TermSchema getSchemaFromType(String paramType) throws ClassNotFoundException, OntologyException {
		// Get type class 
		Class paramClass = getClassFromType(paramType);

		// Check if class is a holder -> get the schema of holder value
		Class holderValueClass = JavaUtils.getHolderValueType(paramClass);
		if (holderValueClass != null) {
			String holderValueType = JavaUtils.getLoadableClassName(holderValueClass.getName());
			return getSchemaFromType(holderValueType);
		}
		
		// Get schema
		TermSchema paramSchema;
		if (isArray(paramType)) {
			// Array
			String elementType = getArrayElementType(paramType);
			TermSchema elementSchema = getSchemaFromType(elementType);
			paramSchema = new AggregateSchema(BasicOntology.SEQUENCE, elementSchema);
		} else {
			// Non array
			paramSchema = getTypeSchema(paramClass);
		}
		return paramSchema;
	}

	private TermSchema getTypeSchema(Class typeClass) throws OntologyException {
		TermSchema typeSchema;
		// In Axis 1.x the wsdl type dateTime is associated to java.util.Calendar class
		// In jade dateTime is managed with DATE schema associated to Date class
		// Ignore typeClass and get DATE schema (see: convertAbsToObj and convertObjToAbs functions)
		if (Calendar.class.isAssignableFrom(typeClass)) {
			typeSchema = (TermSchema)BasicOntology.getInstance().getSchema(BasicOntology.DATE);
			
		} else {
			// Check if type schema is already present in ontology
			typeSchema = (TermSchema)typeOnto.getSchema(typeClass);
			if (typeSchema == null) {
				// Create a new schema
				typeOnto.add(typeClass);
				typeSchema = (TermSchema)typeOnto.getSchema(typeClass);
			}
		}
		return typeSchema;
	}

	private boolean isArray(String paramType) {
		String typeName = JavaUtils.getTextClassName(paramType);
		
		// Check if type is an array (xxx[])
		int bracketsPos = typeName.indexOf("[]");
		return (bracketsPos > 0);
	}

	private String getArrayElementType(String paramType) {
		String typeName = JavaUtils.getTextClassName(paramType);

		// Check if type is an array (xxx[])
		int bracketsPos = typeName.indexOf("[]");
		if (bracketsPos > 0) {
			// Extract element type
			return typeName.substring(0, bracketsPos);
		}
		return null;
	}
	
	private Class getClassFromType(String type) throws ClassNotFoundException {
		Class typeClass = getPrimitiveClass(type);
		if (typeClass == null) {
			typeClass = Class.forName(type, true, classloader);
		}
		return typeClass;
	}

    public Class getPrimitiveClass(String value) {
        Class clz = null;        
        if ("int".equals(value)) {
            clz = int.class;
        }
        if ("byte".equals(value)) {
            clz = byte.class;
        }
        if ("short".equals(value)) {
            clz = short.class;
        }
        if ("long".equals(value)) {
            clz = long.class;
        }
        if ("float".equals(value)) {
            clz = float.class;
        }
        if ("double".equals(value)) {
            clz = double.class;
        }
        if ("boolean".equals(value)) {
            clz = boolean.class;
        }
        if ("char".equals(value)) {
            clz = char.class;
        }
        return clz;
    }
	
	private int convertAxisModeToParameterMode(byte axisMode) {
		int parameterMode = ParameterInfo.UNDEFINED;
		switch(axisMode) {
			case Parameter.IN:
				parameterMode = ParameterInfo.IN;
				break;
			case Parameter.OUT:
				parameterMode = ParameterInfo.OUT;
				break;
			case Parameter.INOUT:
				parameterMode = ParameterInfo.INOUT;
				break;
		}
		return parameterMode;
	}

	public Collection<HeaderInfo> getImplicitHeaders() {
		return implicitHeadersMap.values();
	}

	public Collection<HeaderInfo> getExplicitHeaders() {
		return explicitHeadersList;
	}

	public List<ParameterInfo> getParameters() {
		return parametersList;
	}

}
