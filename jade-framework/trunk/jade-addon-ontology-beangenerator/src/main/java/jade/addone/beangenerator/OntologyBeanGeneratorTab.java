/**
 *  JADE - Java Agent DEvelopment Framework is a framework to develop
 *  multi-agent systems in compliance with the FIPA specifications. Copyright
 *  (C) 2002 TILAB S.p.A. This file is donated by Acklin B.V. to the JADE
 *  project. GNU Lesser General Public License This library is free software;
 *  you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public License as published by the Free Software Foundation, version
 *  2.1 of the License. This library is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 *  General Public License for more details. You should have received a copy of
 *  the GNU Lesser General Public License along with this library; if not, write
 *  to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 *  MA 02111-1307, USA. **************************************************************
 */
package jade.addone.beangenerator;

//Java
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.InstancesTab;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 *  Main class which handles the Ontology generation
 *
 *@author     Chris van Aart - Acklin, University of Amsterdam
 *@author     Jamie Lawrence - Media Lab Europe
 *@created    November 14, 2002
 */
public class OntologyBeanGeneratorTab extends InstancesTab {

	/**
	 *  Get the cardinality of this slot and build the appropriate String
	 *  Ultimately there are two possible outputs: For singule slots its either
	 *  ObjectSchema.OPTIONAL or ObjectSchema.MANDATORY. For multiple slots its
	 *  min, max where min is a number and max can be a number or
	 *  ObjectSchema.UNLIMITED.
	 *
	 *@param  theSlot
	 *@return          the string specifing the cardinality parameters
	 */
	private String getCardinality(Slot theSlot) {
		String optionality = null;
		String maxString = null;
		int min = theSlot.getMinimumCardinality();
		int max = theSlot.getMaximumCardinality();

		// specify the optionality
		if (min == 0) {
			optionality = "ObjectSchema.OPTIONAL";
		}
		else {
			optionality = "ObjectSchema.MANDATORY";
		}

		// maximum cardinality (either specify the max or infinite)
		if (max >= 1) {
			maxString = Integer.toString(max);
		}
		else {
			maxString = "ObjectSchema.UNLIMITED";
		}

		// assemble the strings
		StringBuffer schemaParams = new StringBuffer();
		if (max == 1) {
			schemaParams.append(optionality);
		}
		else {
			// attributes with ranges
			schemaParams.append(min);
			schemaParams.append(", ");
			schemaParams.append(maxString);
		}

		return schemaParams.toString();
	}

	private String getDocumentation(edu.stanford.smi.protege.model.Frame frame) {
		// Add any documentation in Protege to this class
		StringBuffer documentation = new StringBuffer();
		Iterator iter = frame.getDocumentation().iterator();
		while (iter.hasNext()) {
			documentation.append("   * ");
			documentation.append(iter.next().toString());
			documentation.append("\n");
		}
		documentation.append("* Protege name: " + frame.getName());
		return documentation.toString();
	}

	/**
	 *  If there is a Java superclass specified in Protege (i.e.
	 *  java.lang.Exception) this is returned, otherwise null
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         The javaSuperClass value
	 */
	private String getJavaSuperClass(Cls theCls) {
		String javaSuperClassName = null;
		if (javaSuperClassSlot != null) {
			Object temp = theCls.getOwnSlotValue(javaSuperClassSlot);
			if (temp != null) {
				javaSuperClassName = (String) temp;
			}
		}
		return javaSuperClassName;
	}

	/**
	 *  Determine the real super class. Discard all super classes up the hierarchy
	 *  marked as ignored except JADE primitive classes, and return the "real"
	 *  super class, i.e. the super class used in the generated ontology
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         The realSuperClass value
	 */
	private Cls getRealSuperClass(Cls theCls) {
		Cls superClass = theCls;
		do {
			superClass = (Cls) superClass.getDirectSuperclasses().iterator().next();
			if (isJADEPrimitive(superClass) || !isIgnored(superClass)) {
				break;
			}
		}
		while (superClass.getDirectSuperclassCount() > 0);

		return superClass;
	}

	/**
	 *  Builds the inheritence string to be used in the Java class. The super
	 *  class will either be defined as that Java class specified in Protege, a
	 *  class from the JADE ontology (Action/Predicate), a class from the Ontology
	 *  or just a system class (i.e. there is no inheritence)
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         The superClass value
	 */
	private String getSuperClass(Cls theCls) {
		StringBuffer result = new StringBuffer("");
		// Walk the inheritence hierarchy of the class
		Cls superClass = getRealSuperClass(theCls);

		String superClassName = fixNamespacePrefix(superClass.getName());

		superClassName = converter.toJavaTypeName(superClassName);

		// Implementing one of the JADE primitives
		if (superClassName.equals("Concept") || superClassName.equals("AgentAction") || superClassName.equals("Predicate")) {
			// Java specific inheritance
			String javaBaseClass = getJavaSuperClass(theCls);
			if (javaBaseClass != null) {
				result.append(" extends ");
				result.append(javaBaseClass);
			}
			result.append(" implements ");
			result.append(superClassName);
		}
		else {
			// Extending an existing class in the ontology
			// FIXME: Should overwrite the java specific inheritence???
			//        if(result.length() == 0){
			result.append(" extends ");
			result.append(superClassName);
		}

		// It's extending a system class, so forget it!
		if (superClassName.charAt(0) == ':') {
			result = new StringBuffer("");
		}

		return result.toString();
	}

	/**
	 * If the argument string (a class name) has the AbstractJadeOntology prefix than remove it
	 * (This applies only if ontology is in OWL)
	 * @param clsName - the superclass name
	 * @return a String without the namespace prefix, if the case; otherwise the original string
	 */
	private String fixNamespacePrefix(String clsName) {
		if (!(getKnowledgeBase() instanceof OWLModel))
			return clsName;

		String prefix = ((OWLModel)getKnowledgeBase()).getNamespaceManager().getPrefix(BEAN_GENERATOR_NAMESPACE);

		if (prefix == null)
			return clsName;

		if (clsName.startsWith(prefix)) {
			String shortSuperclsName = clsName.substring(prefix.length()+1);
			return shortSuperclsName;
		}
		return clsName;
	}

	/**
	 *  This determines whether a given class in Protege has been ignored by the
	 *  user. Ignored classes are not generated as beans, do not appear in the
	 *  generate Ontology. Subclasses of ignored classes are generated but will
	 *  inherit from their super-super class
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         The ignored value
	 */
	private boolean isIgnored(Cls theCls) {
		Boolean ignored = new Boolean(false);
		// by default it is not ignored
		if (jadeIgnoredSlot != null) {
			Object temp = theCls.getOwnSlotValue(jadeIgnoredSlot);
			if (temp != null) {
				ignored = (Boolean) temp;
			}
		}
		return ignored.booleanValue();
	}

	/**
	 *  This determines whether the class presented is a JADE class, i.e.
	 *  Predicate, AgentAction, Concept, AID
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         The jADEPrimitive value
	 */
	private boolean isJADEPrimitive(Cls theCls) {
		String className = theCls.getName();
		boolean primitive = false;

		//treat frames ontologies
		if (className.equalsIgnoreCase(AID) || className.equalsIgnoreCase(CONCEPT) ||
				className.equalsIgnoreCase(AGENTACTION) ||
				className.equalsIgnoreCase(PREDICATE)) {
			primitive = true;
		}

		//treat OWL ontologies
		if (getKnowledgeBase() instanceof OWLModel) {
			try {
				if (((RDFResource)theCls).getNamespace().equals(BEAN_GENERATOR_NAMESPACE))
					primitive = true;
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}

		return primitive;
	}

	private void addClass(Cls theCls, String collectionName) {
		/*
    ontologyHeader.insert(0, "    public static final String " +
    					converter.toRoleConstant(theCls, null) + "=\"" +
                        theCls.getName() + "\";\n");
		 */
		ontologyHeader.insert(0, "    public static final String " +
				converter.toRoleConstant(theCls, null) + "=\"" +
				converter.toJavaTypeName(theCls.getName()) + "\";\n");


		if (collectionName.equals("AID")) {
			collectionName = "Concept";
		}

		//        ontologyHeader.append(theTab);
		ontologyHeader.append("    " + collectionName + "Schema ");
		ontologyHeader.append(converter.toSchemaVariableName(theCls, null));
		ontologyHeader.append(" = new ");
		ontologyHeader.append(collectionName);
		ontologyHeader.append("Schema(" + converter.toRoleConstant(theCls, null) +
		");\n");
		// Add a facet to check that (if present) the serialID is > 0
		// The following line is commented as the NumberInRangeFacet is not yet available
		//itemSchema.addFacet(ITEM_SERIALID, new NumberInRangeFacet(0, NumberInRangeFacet.GREATER));
		//add(itemSchema, Item.class);
	}

	/**
	 *  Calculate inheritence for the Ontology file
	 *
	 *@param  theCls         The feature to be added to the Inheritance attribute
	 *@exception  Exception  Description of the Exception
	 */
	private void addInheritance(Cls theCls) throws Exception {
		// determine the real super class.  Discard all super classes up the
		// hierarchy marked as ignored except JADE primitive classes
		Cls superClass = getRealSuperClass(theCls);

		// We cannot inherit from Predicate subclasses in JADE.  This test traverses
		// the inheritence hierarchy of the super class.  If any of these super-super
		// class are the Predicate class then e can throw an exception.
		Cls superSuperClass = superClass;
		do {
			superSuperClass = (Cls) superSuperClass.getDirectSuperclasses().iterator().next();
			if (superSuperClass.getName().equalsIgnoreCase(PREDICATE)) {
				throw new Exception(
						"In JADE it is not possible to inherit from Predicates.  Unable to add class " +
						theCls.getName());
			}
		}
		while (superSuperClass.getDirectSuperclassCount() > 0);

		// ignore inheritence from the basic bean classes
		if (isJADEPrimitive(superClass)) {
			return;
		}

		// Convert the class names to variable names
		String superClassSchema = converter.toSchemaVariableName(superClass, null);
		String classSchema = converter.toSchemaVariableName(theCls, null);

		//        className = OntologyBeanGeneratorUtil.firstDown(className);
		ontologyInheritance.append("    " + classSchema + ".addSuperSchema(" +
				superClassSchema +
		");\n");
	}

	/**
	 *  For each slot passed in, this adds a mapping from JADE names to Protege
	 *  names and vice versa. This will be used by the Protege introspector at
	 *  runtime.
	 *
	 *@param  theCls   The feature to be added to the JADEMapping attribute
	 *@param  theSlot  The feature to be added to the JADEMapping attribute
	 */
	private void addJADEMapping(Cls theCls, Slot theSlot) {

		// Create a fully qualified Java class name
		String fullClassName = converter.toFullJavaClassName(packageName, theCls);
		String slotName = converter.getSlotName(theSlot);

		// If a JADE Name exists use this, otherwise use the Protege name
		//        if(temp != null){
		//            slotName = (String) temp;
		//        } else {
		//           String SlotName = ProtegeTools.toJavaString(theSlot.getName());
		//            slotName = OntologyBeanGeneratorUtil.firstUpper(SlotName);
		//        }
		slotName = "\"" + slotName + "\"";

		// append this line to the stringubffer which will be written to the Ontology file
		nameMapping.append("    storeSlotName(" + slotName + ", \"" + fullClassName +
				"\", \"" + theSlot.getName() + "\");  \n");
	}

	/**
	 *  Add a role to the Ontology file
	 *
	 *@param  theCls   The feature to be added to the Role attribute
	 *@param  theSlot  The feature to be added to the Role attribute
	 */
	private void addRole(Cls theCls, Slot theSlot) {
		try {
			// addd the Role constant (towards the top of the ontology file)
			addRoleNameConstant(theCls, theSlot);

			// define the role schema and and the name mapping
			addRoleToSchema(theCls, theSlot);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Something went wrong with Slot: " +
					theSlot.getName() + " of class: " +
					theCls.getName(), "error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void addRoleNameConstant(Cls theCls, Slot theSlot) throws Exception {
		String slotName = converter.getSlotName(theSlot);
		slotName = ProtegeTools.toJavaString(slotName); //added by J.Picault
		//System.out.println("slotName: " + slotName);
		if ( (slotName.length() < 1) || (slotName.charAt(0) == ':')) {
			throw new Exception("Invalid slot name");
		}
		// used to inform caller of an invalid name

		String roleName = converter.toRoleConstant(theCls, theSlot);

		// Append to the Ontology Header
		ontologyHeader.insert(0,
				"    public static final String " + roleName + "=\"" +
				slotName + "\";\n");
	}

	/**
	 *  Add the slot to the class schema. This consists of a line such as this
	 *  dFAgentDescriptionSchema.add(DFAGENTDESCRIPTION_AGENT_NAME,
	 *  (ConceptSchema)getSchema(BasicOntology.AID), ObjectSchema.MANDATORY);
	 *  which can be broken down into <ClassSchema>.add(<SlotNameConstant>, (
	 *  <SlotTypeSchema>)getSchema(<Ontology>), <Cardinality>); The purpose of
	 *  this method is to build this string by determining the class schema, slot
	 *  name, slot type's schema, slot type's ontology, and the slots cardinality
	 *
	 *@param  theCls   The feature to be added to the RoleToSchema attribute
	 *@param  theSlot  The feature to be added to the RoleToSchema attribute
	 */
	private void addRoleToSchema(Cls theCls, Slot theSlot) {
		// The constant used to access the slot name in the ontology
		String roleName = converter.toRoleConstant(theCls, theSlot);
		// The name of the schema to add the roles to
		String classSchemaVariableName = converter.toSchemaVariableName(theCls, null);

		// Used to represent (<SlotTypeSchema>)getSchema(<Ontology>)
		String termSchemaString = null;
		// Used to represent the <SlotTypeSchema>
		String termString = null;

		// Repsents the type of this slot
		ValueType valueType = theSlot.getValueType();
		// The name of the class in an ontology corresponding to the SlotTypeSchema
		String ontologyType = OntologyBeanGeneratorUtil.getOntologyType(valueType);

		// if the type is a system class, ignore
		if (valueType.toString().charAt(0) == ':') {
			return;
		}
		
//		Collection c= theSlot.getAllowedClses();
		
		Collection c = new ArrayList();		
		 
		if (theSlot.getValueType().equals(ValueType.INSTANCE))
			c = theCls.getTemplateSlotAllowedClses(theSlot);
		else if (theSlot.getValueType().equals(ValueType.CLS))
			c = theCls.getTemplateSlotAllowedParents(theSlot); 
		
		if (c.isEmpty()) {
			// no allowed classes specified, default to String
			//ontologyType = "BasicOntology.STRING";
			termString = "(TermSchema)";
		}

		if (c.size() == 1) {
			// one allowed class

			// Get the name of the class
			// REMOVE:
			//ontologyType = converter.toSchemaVariableName(((Cls)c.iterator().next()), null);
			Cls allowedCls = (Cls) c.iterator().next();
			ontologyType = allowedCls.getName();
			
			KnowledgeBase kb = getKnowledgeBase();
			
			// if this is a JADE primative class, use the Basic Ontology
			/*if (ontologyType.equalsIgnoreCase("Concept") ||
					ontologyType.equalsIgnoreCase("AID") ||
					ontologyType.equalsIgnoreCase("AgentAction") ||
					ontologyType.equalsIgnoreCase("Predicate"))*/
			
			if (allowedCls.equals(kb.getCls(CONCEPT))) {
				ontologyType = "BasicOntology.STRING";
				termString = "(TermSchema)";
				termSchemaString = "new ConceptSchema(\"Concept\")";								
			} else if (allowedCls.equals(kb.getCls(AGENTACTION))) {
				ontologyType = "BasicOntology.STRING";
				termString = "(TermSchema)";
				termSchemaString = "new AgentActionSchema(\"AgentAction\")";				
			} else if (allowedCls.equals(kb.getCls(PREDICATE))) {
				ontologyType = "BasicOntology.STRING";
				termString = "(TermSchema)";
				termSchemaString = "new PredicateSchema(\"Predicate\")";				
			} else if (allowedCls.equals(kb.getCls(AID))) {
				termString = "(ConceptSchema)";
				ontologyType = fixNamespacePrefix(ontologyType);					
				ontologyType = "BasicOntology." + ontologyType.toUpperCase();			
			} else {
				// else, use the schema for the class
				
				// ignore all system classes
				if (allowedCls.isSystem() || allowedCls.getName().startsWith(":")) {
					System.out.println("Ignore ontology type: " + allowedCls + "... Returning");
					return;
				}
				
				termString = "(TermSchema)";
				// FIXME: what wrong?
				ontologyType = converter.toSchemaVariableName(allowedCls, null);
				termSchemaString = ontologyType;
			}
		}

		if (c.size() > 1 ) {
			// multiple classes allowed, default to Concept
			ontologyType = "BasicOntology.STRING";
			termString = "(TermSchema)";
			termSchemaString = "new ConceptSchema(\"Concept\")";
		}

		//if (theSlot.getOwnSlotAllowsMultipleValues(theSlot)) {
		//		termString = "(TermSchema)";
		//		ontologyType = "BasicOntology.SET";
		//      termSchemaString = "(ConceptSchema)" + ontologyType;
		//}

		// ignore all system classes
		if (ontologyType.length() >0 && ontologyType.toString().charAt(0) == ':') {
			System.out.println("ingnore ontology type!, returning");
			return;
		}
	
		if (termSchemaString == null) {
			if (termString == null) {
				termSchemaString = ontologyType;
			}
			else {
				termSchemaString = termString + "getSchema(" + ontologyType + ")";
			}
			//termSchemaString = ProtegeTools.toJavaString(termSchemaString);
		}

		
		//if ontologyType was not computed yet for instance or class slots
		if ((theSlot.getValueType().equals(ValueType.INSTANCE) || theSlot.getValueType().equals(ValueType.INSTANCE)) &&  ontologyType.length() < 1) {
			ontologyType = "BasicOntology.STRING";
			termString = "(TermSchema)";
			termSchemaString = "new ConceptSchema(\"Concept\")";
		}
		
		// Build the String which specifies the cardinality and optionality
		// of this slot
		String roleParams = getCardinality(theSlot);

		// add this role to the class schema
		//        ontologyBody.append(theTab);
		ontologyBody.append("    " + classSchemaVariableName);
		ontologyBody.append(".add(" + roleName + ", " + termSchemaString + ", " + roleParams + ");");
		ontologyBody.append("\n");

		// Check if this is an unnamed (anonymous) slot
		if (jadeUnnamedSlot != null) {
			Object temp2 = theSlot.getOwnSlotValue(jadeUnnamedSlot);
			if (temp2 != null) {
				Boolean anonymous = (Boolean) temp2;
				if (anonymous.booleanValue()) {
					// this is anonymous, so tell JADE to parse by order not name
					ontologyBody.append("\n   " + classSchemaVariableName +	".setEncodingByOrder(true);");
				}
			}
		}
	}

	//////////////////////////////////////////////////////////// File Generation Methods

	/**
	 *  Create the bean file
	 *
	 *@param  theCls       Description of the Parameter
	 *@param  f            Description of the Parameter
	 *@param  superClass   Description of the Parameter
	 *@param  isSuperBean  Description of the Parameter
	 *@return              Description of the Return Value
	 */
	private PrintStream createBean(Cls theCls, File f, String superClass,
			boolean isSuperBean) {
		PrintStream ps;
		String theName = converter.toJavaTypeName(theCls.getName());

		try {
			FileOutputStream fos = new java.io.FileOutputStream(f);
			OutputStream bos = new BufferedOutputStream(fos);
			ps = new PrintStream(bos);

			// package line
			ps.println("package " + packageName + ";");
			ps.println();
			if (isSuperBean) {
				if (fullBeanSupport) {
					ps.println("import java.io.Serializable;");
					ps.println("import java.beans.PropertyChangeSupport;");
					ps.println("import java.beans.PropertyChangeListener;");
				}
				ps.println();
			}
			if (microSupport) {
				ps.println("import jade.content.abs.*;");
				ps.println("import jade.content.onto.*;");
			}
			ps.println("import jade.content.*;");
			ps.println("import jade.util.leap.*;");
			ps.println("import jade.core.*;");
			ps.println();
			ps.println("/**");
			ps.println(getDocumentation(theCls));
			ps.println("* @author ontology bean generator");
			ps.println("* @version " + OntologyBeanGeneratorUtil.getDate());
			ps.println("*/");
			// class line
			if (isSuperBean) {
				ps.print("public class " + theName + superClass);
				if (fullBeanSupport) {
					ps.println(", Serializable {");
					ps.println("   // bean stuff");
					ps.println(
					"   protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);\n");
					ps.println(
					"   public void addPropertyChangeListener(PropertyChangeListener pcl) {");
					ps.println("     pcs.addPropertyChangeListener(pcl);\n" + "   }\n");
					ps.println(
					"   public void removePropertyChangeListener(PropertyChangeListener pcl) {");
					ps.println("     pcs.removePropertyChangeListener(pcl);\n" + "   }\n");
				}
				else if (microSupport) {
					ps.println(", Introspectable {");
				}
				else {
					ps.println(" {");
				}
			}
			else {
				ps.println("public class " + theName + superClass + "{ ");
			}
			ps.println();

			// Now add any extra methods specified in Protege
			if (javaJavaCodeSlot != null) {
				Object codeVal = theCls.getOwnSlotValue(javaJavaCodeSlot);
				if (codeVal != null) {
					ps.println("//////////////////////////// User code");
					ps.println( (String) codeVal);
				}
			}

			return ps;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  Create the start of the Ontology file
	 *
	 *@return    Description of the Return Value
	 */
	private PrintStream createOntologyFile() {
		PrintStream ps;
		try {
			StringBuffer fileName = new StringBuffer(converter.toDir(packageName,
					dirName));
			fileName.append("/");
			fileName.append(converter.toOntologyClassName(ontologyName));
			fileName.append(".java");

			File f = new File(fileName.toString());
			ontologyBeanGeneratorPanel.statusTextArea.append(
					"created ontology file: " +
					fileName.toString() +
			"\n");

			FileOutputStream fos = new java.io.FileOutputStream(f);
			OutputStream bos = new BufferedOutputStream(fos);
			ps = new PrintStream(bos);
			ps.println("// file: " + f.getName() +
			" generated by ontology bean generator.  DO NOT EDIT, UNLESS YOU ARE REALLY SURE WHAT YOU ARE DOING!");

			// package line
			packageName = (String) ontologyBeanGeneratorPanel.packageComboBox.
			getSelectedItem();
			ps.println("package " + packageName + ";");
			ps.println();

			// imports
			ps.println("import jade.content.onto.*;");
			ps.println("import jade.content.schema.*;");

			ps.println("import jade.util.leap.HashMap;");
			ps.println("import jade.content.lang.Codec;");
			ps.println("import jade.core.CaseInsensitiveString;");

			if (!microSupport && useJADENames) {
				ps.println("import nl.uva.psy.swi.beangenerator.ProtegeIntrospector;");
				ps.println("import nl.uva.psy.swi.beangenerator.SlotHolder;");
				ps.println("import nl.uva.psy.swi.beangenerator.ProtegeTools;");
			}

			ps.println();
			ps.println("/** file: " + f.getName());
			ps.println(" * @author ontology bean generator");
			ps.println(" * @version " + OntologyBeanGeneratorUtil.getDate());
			ps.println(" */");

			// class line
			ps.print("public class " + converter.toOntologyClassName(ontologyName) +
			" extends jade.content.onto.Ontology ");
			if (!microSupport && useJADENames) {
				ps.println("implements ProtegeTools.ProtegeOntology {");
				ps.println("   /**");
				ps.println("    * These hashmap store a mapping from jade names to either protege names of SlotHolder ");
				ps.println("    * containing the protege names.  And vice versa");
				ps.println("    */  ");
				ps.println("   private HashMap jadeToProtege;");

				ps.println();
			}
			else {
				ps.println(" {");
			}

			ps.println("  //NAME");
			ps.println("  public static final String ONTOLOGY_NAME = \"" +
					ontologyName + "\";");
			ps.println("  // The singleton instance of this ontology");
			if (microSupport) {
				ps.println(
				"  private static MicroIntrospector introspect = new MicroIntrospector();");
			}
			else {
				if (useJADENames) {
					ps.println(
					"  private static ProtegeIntrospector introspect = new ProtegeIntrospector();");
				}
				else {
					ps.println("  private static ReflectiveIntrospector introspect = new ReflectiveIntrospector();");
				}
			}

			ps.println("  private static Ontology theInstance = new " +
					converter.toOntologyClassName(ontologyName) + "();");
			ps.println("  public static Ontology getInstance() {");
			ps.println("     return theInstance;");
			ps.println("  }");
			ps.println();

			if (!microSupport && useJADENames) {
				ps.println("   // ProtegeOntology methods");

				ps.println(
				"   public SlotHolder getSlotNameFromJADEName(SlotHolder jadeSlot) {");
				ps.println("     return (SlotHolder) jadeToProtege.get(jadeSlot);");
				ps.println("   }");
				ps.println();

				ps.println();
				ps.println("   // storing the information");
				ps.println("   private void storeSlotName(String jadeName, String javaClassName, String slotName){");

				ps.println("       jadeToProtege.put(new SlotHolder(javaClassName, jadeName), new SlotHolder(javaClassName, slotName));");
				ps.println("   }");
				ps.println();
			}
			ps.println();

			ps.println("   // VOCABULARY");
			ontologyHeader.append("\n  /**\n   * Constructor\n  */");
			ontologyHeader.append("\n  private " +
					converter.toOntologyClassName(ontologyName) +
			"(){ \n");
			//      ontologyHeader.append(
			//        "    super(ONTOLOGY_NAME, BasicOntology.getInstance(), introspect);\n");
			ontologyHeader.append(
			"    super(ONTOLOGY_NAME, BasicOntology.getInstance());\n");

			if (!microSupport && useJADENames) {
				ontologyHeader.append("    introspect.setOntology(this);\n");
				ontologyHeader.append("    jadeToProtege = new HashMap();\n");
			}

			//            ontologyHeader.append("    protegeToJade = new HashMap();\n\n");
			ontologyHeader.append("    " + "try { \n");

			return ps;
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "error",
					JOptionPane.ERROR_MESSAGE);
			ontologyBeanGeneratorPanel.statusTextArea.append(e.getMessage() + "\n");
		}

		return null;
	}

	/**
	 *  Create a slot within the Bean. i.e. private attributes and get/set methods
	 *
	 *@param  ps       Description of the Parameter
	 *@param  theCls   Description of the Parameter
	 *@param  theSlot  Description of the Parameter
	 *@param  extern   Description of the Parameter
	 *@param  intern   Description of the Parameter
	 */
	private void createSlot(PrintStream ps, Cls theCls, Slot theSlot,
			StringBuffer extern,
			StringBuffer intern) {
		boolean multipleSetters = false;
		String theVarName = ProtegeTools.toJavaVariableStyleString(theSlot.getName());
		String absSlotType = null;

		// skip system slots
		if (theVarName.charAt(0) == ':') {
			ontologyBeanGeneratorPanel.statusTextArea.append("     skipping " +
					theVarName + "\n");
			return;
		}

		String theType = "String";
		ValueType valueType = theSlot.getValueType();
		theType = OntologyBeanGeneratorUtil.getJavaSlotType(valueType);

		// again, skip this slot if the type is a system class
		if (valueType.toString().charAt(0) == ':') {
			ontologyBeanGeneratorPanel.statusTextArea.append("     skipping " +
					theType + "\n");
			return;
		}

		// if the slot type is INSTANCE or CLASS
		if (valueType.equals(ValueType.CLS) || valueType.equals(ValueType.INSTANCE)) {
			//Collection c = theSlot.getAllowedCls();
			
			Collection c = new ArrayList();
			if (valueType.equals(ValueType.INSTANCE))
				c = theCls.getTemplateSlotAllowedClses(theSlot);
			else if (valueType.equals(ValueType.CLS))
				c = theCls.getTemplateSlotAllowedParents(theSlot);
			
			// if there are no or multiple allowed types specified, the slot type is Object
			if (c.isEmpty() || (c.size() > 1)) {
				theType = "Object";
			}

			if (c.size() == 1) {
				theType = ( (Cls) c.iterator().next()).getName();
				theType = fixNamespacePrefix(theType);
				
				if (theType.charAt(0) == ':') {
					ontologyBeanGeneratorPanel.statusTextArea.append("     skipping " +
							theType + "\n");
					return;
				}

				// FIXME: Can we manipulate an object rather than Strings?  No?
				theType = ProtegeTools.toJavaString(theType);
				theType = ProtegeTools.firstUpper(theType);
			}
		}

		//single cardinality
		if (!theSlot.getOwnSlotAllowsMultipleValues(theSlot)) {
			// if it's a primative type make it lower case, otherwise capitalise the first letter
			if (theType.equalsIgnoreCase("boolean") ||
					theType.equalsIgnoreCase("int") ||
					theType.equalsIgnoreCase("float")) {
				theType = theType.toLowerCase();
				absSlotType = theVarName;
			}
			else {
				theType = ProtegeTools.toJavaString(theType);
				theType = ProtegeTools.firstUpper(theType);
			}

			// Add any documentation in Protege to this slot
			ps.println("   /**");
			ps.println(getDocumentation(theSlot));
			ps.println("   */");

			// create the private variable for the slot
			ps.println("   private " + theType + " " + theVarName + ";");
			ontologyBeanGeneratorPanel.statusTextArea.append("     " + theType + " " +
					theVarName + "\n");

			//create setter
			if (!multipleSetters) {
				//single setter
				ps.println("   public void " + converter.toSetMethod(theSlot) + "(" +
						theType + " value) { ");
				if (fullBeanSupport) {
					ps.print("     pcs.firePropertyChange(\"" + theVarName + "\", ");

					if (theType.equals("int") ||
							theType.equals("boolean")) {
						ps.print("this." + theVarName);
						ps.println(", value);");
					}
					else if (theType.equals("float")) {
						ps.print("\"\"+this." + theVarName);
						ps.println(", \"\"+value);");

					}
					else {
						ps.print("(this." + theVarName + "==null?new " + theType +
								"():this." + theVarName + ")");
						ps.println(", value);");
					}

				}
				ps.println("    this." + theVarName + "=value;\n" + "   }");
			}

			//create getter
			ps.println("   public " + theType + " " + converter.toGetMethod(theSlot) +
			"() {");
			ps.println("     return this." + theVarName + ";\n" + "   }");
			ps.println();
		}
		else {
			// multiple cardinality

			String oldStr = "     List oldList = this." + theVarName + ";";
			String propertyStr = "";
			if (fullBeanSupport) {
				propertyStr = "     pcs.firePropertyChange(\"" + theVarName +
				"\", oldList, this." + theVarName + ");";
			}
			ontologyBeanGeneratorPanel.statusTextArea.append("Collection " +
					theVarName + "\n");

			// Add any documentation in Protege to this slot
			ps.println("   /**");
			ps.println(getDocumentation(theSlot));
			ps.println("   */");

			ps.println("   private List " + theVarName + " = new ArrayList();");

			//create adder
			ps.println("   public void " + converter.toAddMethod(theSlot) + "(" +
					theType +
			" elem) { ");
			ps.println(oldStr);
			ps.println("     " + theVarName + ".add(elem);");
			if (propertyStr != "") {
				ps.println(propertyStr);
			}
			ps.println("   }");

			//create remover
			ps.println("   public boolean " + converter.toRemoveMethod(theSlot) + "(" +
					theType + " elem) {");
			ps.println(oldStr);
			ps.println("     boolean result = " + theVarName + ".remove(elem);");
			if (propertyStr != "") {
				ps.println(propertyStr);
			}

			ps.println("     return result;");
			ps.println("   }");

			//clearer
			ps.println("   public void " + converter.toClearAllMethod(theSlot) +
			"() {");
			ps.println("" + oldStr);
			ps.println("     " + theVarName + ".clear();");
			if (propertyStr != "") {
				ps.println(propertyStr);
			}
			ps.println("   }");

			//all getter
			ps.println("   public Iterator " + converter.toGetAllMethod(theSlot) +
					"() {return " + theVarName + ".iterator(); }");
			ps.println("   public List " + converter.toGetMethod(theSlot) +
					"() {return " + theVarName + "; }");
			ps.println("   public void " + converter.toSetMethod(theSlot) +
					"(List l) {" + theVarName + " = l; }");

			ps.println();
			theType = "List";
		}

		// generate the statements within the internalise and externalise methods
		extern.append("      abs.set(");

		// Ontology name
		extern.append(converter.toOntologyClassName(ontologyName));
		extern.append(".");

		// Slot constant in the Ontology
		extern.append(converter.toRoleConstant(theCls, theSlot));

		// Type of slot
		extern.append(", ");

		// varies on the type
		if (absSlotType == null) {
			extern.append("(AbsTerm) ");

			//        extern.append(", (AbsPredicate) ");
			//        extern.append(", (AbsObject) ");
			extern.append("onto.fromObject(");
			extern.append(converter.toGetMethod(theSlot));
			extern.append("())");
		}
		else {
			extern.append(absSlotType);
		}

		extern.append(");\n");

		//        setActor((AID) onto.toObject(absAction.getAbsObject(BasicOntology.ACTION_ACTOR)));
		//        setAction((Concept) onto.toObject(absAction.getAbsObject(BasicOntology.ACTION_ACTION)));
		intern.append("      " + theVarName);
		if (theType.equalsIgnoreCase("boolean") || theType.equalsIgnoreCase("int") ||
				theType.equalsIgnoreCase("float")) {
			intern.append(" = abs.get");
			if (theType.equalsIgnoreCase("int")) {
				theType = "integer";
			}
			intern.append(ProtegeTools.firstUpper(theType));
			intern.append("(");
			intern.append(converter.toOntologyClassName(ontologyName));
			intern.append(".");
			intern.append(converter.toRoleConstant(theCls, theSlot));
			intern.append(");\n");
		}
		else {
			intern.append(" = (");
			intern.append(theType);
			intern.append(")");
			intern.append("onto.toObject(abs.getAbsObject");
			intern.append("(");
			intern.append(converter.toOntologyClassName(ontologyName));
			intern.append(".");
			intern.append(converter.toRoleConstant(theCls, theSlot));
			intern.append("));\n");
		}
	}

	/**
	 *  Main entry point from the GUI. Called when the user presses the generate
	 *  button
	 */
	void doIt() {
		// reset all global fields, fetch properties and JADE specific slot definitions
		reset();

		// create the Ontology file
		PrintStream ontologyFile;
		if (ontologyGeneration) {
			ontologyFile = this.createOntologyFile();
			if (ontologyFile == null) {
				return;
			}
		}
		else {
			ontologyFile = null;
		}

		// Get the classes and all subclasses from the knowledge base
		Cls conceptCls = itsKB.getCls(CONCEPT);
		Cls AIDCls = itsKB.getCls(AID);
		Cls agtactCls = itsKB.getCls(AGENTACTION);
		Cls predClS = itsKB.getCls(PREDICATE);

		Collection conceptCol =  new HashSet(conceptCls.getSubclasses());
		Collection aidCol = new HashSet(AIDCls.getSubclasses());
		Collection agtactClsCol = new HashSet(agtactCls.getSubclasses());
		Collection predCol = new HashSet(predClS.getSubclasses());

		// filter the classes.  i.e. remove duplicates, system and ignored classes
		filterClasses(conceptCol, aidCol, agtactClsCol, predCol);

		// setup the JProgressBar
		int max = conceptCol.size() + aidCol.size() + agtactClsCol.size() +
		predCol.size();
		ontologyBeanGeneratorPanel.progress.setMaximum(max);
		ontologyBeanGeneratorPanel.progress.setMinimum(0);

		// Generate each type of entity, Concepts, Actions, AIDs, and Predicates
		generateByCollection(conceptCol, JAVA_CONCEPT_CLASS_NAME);
		generateByCollection(agtactClsCol, JAVA_AGENTACTION_CLASS_NAME);
		generateByCollection(aidCol, JAVA_AID_CLASS_NAME);
		generateByCollection(predCol, JAVA_PREDICATE_CLASS_NAME);

		// Output the Ontology file
		if (ontologyGeneration) {
			ontologyFile.println(this.ontologyHeader);
			ontologyFile.println(this.ontologyInit);
			ontologyFile.println("    // adding fields");
			ontologyFile.println(this.ontologyBody);
			ontologyFile.println("    // adding name mappings");
			ontologyFile.println(this.nameMapping);
			ontologyFile.println("    // adding inheritance");
			ontologyFile.println(this.ontologyInheritance);
			ontologyFile.println(
			"   }catch (java.lang.Exception e) {e.printStackTrace();}");
			ontologyFile.println("  }");
			ontologyFile.println("  }");
			ontologyFile.flush();
			ontologyFile.close();
		}
		// save project annotation
		this.saveProjectAnnotation();
	}

	/**
	 *  Remove all system classes, JADE classes and classes ignored by the user
	 *
	 *@param  conceptCol    Description of the Parameter
	 *@param  aidCol        Description of the Parameter
	 *@param  agtactClsCol  Description of the Parameter
	 *@param  predCol       Description of the Parameter
	 */
	private void filterClasses(Collection conceptCol, Collection aidCol,
			Collection agtactClsCol, Collection predCol) {
		// ensure we don't regenerate the classes Concept, AID, AgentAction
		// as they also inherit from Concept
		if (itsKB.getCls(CONCEPT) == null) {
			System.out.println("no Concepts found");
		}
		else {
			//System.out.println("removing: " + itsKB.getCls(CONCEPT).getClass());
			conceptCol.remove(itsKB.getCls(CONCEPT));
		}
		if (itsKB.getCls(AID) != null) {
			conceptCol.remove(itsKB.getCls(AID));
		}
		if (itsKB.getCls(AGENTACTION) != null) {
			conceptCol.remove(itsKB.getCls(AGENTACTION));

			// remove all agent actions from the concepts
		}
		Iterator agtactClsItor = agtactClsCol.iterator();
		while (agtactClsItor.hasNext()) {
			conceptCol.remove(agtactClsItor.next());
		}

		// remove all the AIDs from the concepts
		Iterator aidColItor = aidCol.iterator();
		while (aidColItor.hasNext()) {
			conceptCol.remove(aidColItor.next());
		}

		// for each collection, remove classes which are marked ignored
		removeIgnoredClasses(conceptCol);
		removeIgnoredClasses(aidCol);
		removeIgnoredClasses(agtactClsCol);
		removeIgnoredClasses(predCol);
	}

	private void finishUpRole(Cls theCls) {
		// modified by J.Picault
		if (!microSupport)
			ontologyHeader.append("    add(" + converter.toSchemaVariableName(theCls, null) +
					", " + this.packageName + "." +
					converter.toJavaTypeName(theCls) + ".class);\n");
		else  {
			ontologyHeader.append("    add(" + converter.toSchemaVariableName(theCls, null) + ", Class.forName(\"" + this.packageName + "." + converter.toJavaTypeName(theCls) + "\"));\n");
		}

	}

	private void format(File source) {
		try {
			//  jalopy.setInput(source);
			//  jalopy.setOutput(source);
			//  jalopy.format();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *  Generate each type; Concwept, AgentAction, AID and Predicate
	 *
	 *@param  theClasses      Description of the Parameter
	 *@param  collectionName  Description of the Parameter
	 */
	private void generateByCollection(Collection theClasses,
			String collectionName) {
		ontologyHeader.append("\n    // adding " + collectionName + "(s)\n");

		Iterator classIter = theClasses.iterator();

		while (classIter.hasNext()) {
			Cls theCls = (Cls) classIter.next();

			// if it's not a system class
			if (theCls.getName().charAt(0) != ':') {
				//className = OntologyBeanGeneratorUtil.firstUpper(className);
				String superClassString = getSuperClass(theCls);

				boolean isSuperBean = ( (superClassString.indexOf("implements") >= 0)
						? true : false);
				try {
					addInheritance(theCls);
				}
				catch (Exception e) {
					e.printStackTrace();
					break;
				}

				// Add the class definition
				addClass(theCls, collectionName);

				// Create the externalise and internalise methods for J2ME support
				StringBuffer extern = new StringBuffer();
				StringBuffer intern = new StringBuffer();

				if (microSupport) {
					extern.append(
					"  public void externalise(AbsObject absObj, Ontology onto) throws OntologyException {\n");
					extern.append("    try {\n");

					// cast the AbsObject to the correct type
					extern.append("      Abs");
					extern.append(collectionName);
					// Abs.. Predicate, Abs.. Concept, Abs.. AgentAction
					extern.append(" abs = (Abs");
					extern.append(collectionName);
					// Abs.. Predicate, Abs.. Concept, Abs.. AgentAction
					extern.append(") absObj;\n");

					intern.append(
					"  public void internalise(AbsObject absObj, Ontology onto) throws UngroundedException, OntologyException {\n");
					intern.append("    try {\n");

					// cast the AbsObject to the correct type
					intern.append("      Abs");
					intern.append(collectionName);
					// Abs.. Predicate, Abs.. Concept, Abs.. AgentAction
					intern.append(" abs = (Abs");
					intern.append(collectionName);
					// Abs.. Predicate, Abs.. Concept, Abs.. AgentAction
					intern.append(") absObj;\n");
				}

				PrintStream ps = null;
				try {
					File file = null;
					if (beanGeneration) {
						StringBuffer fileName = new StringBuffer(converter.toDir(
								packageName,
								dirName));
						fileName.append("/");
						fileName.append(converter.toJavaTypeName(theCls));
						fileName.append(".java");
						file = new File(fileName.toString());
						ps = createBean(theCls, file, superClassString, isSuperBean);

						ontologyBeanGeneratorPanel.statusTextArea.append(
								" created file: " + converter.toJavaTypeName(theCls) +
						".java\n");
						ontologyBeanGeneratorPanel.statusTextArea.append(
						"  added slots:\n");
					}

					Collection theSlots = new HashSet(OntologyBeanGeneratorUtil.
							getDirectTemplateSlots(
									theCls));
					Iterator slotIter = theSlots.iterator();

					// For each slot
					while (slotIter.hasNext()) {
						Slot theSlot = (Slot) slotIter.next();
						if (beanGeneration) {
							// create the slot methods within the bean
							createSlot(ps, theCls, theSlot, extern, intern);
						}

						addRole(theCls, theSlot);
						if (!microSupport && useJADENames) {
							addJADEMapping(theCls, theSlot);
						}
					}

					// finish up the internalise/externalise methods
					if (microSupport) {
						extern.append("     } catch (ClassCastException cce) {\n");
						extern.append(
						"       throw new OntologyException(\"Error externalising ");
						extern.append(converter.toJavaTypeName(theCls));
						extern.append("\");\n");
						extern.append("     }\n");
						extern.append("   }\n\n");

						intern.append("     } catch (ClassCastException cce) {\n");
						intern.append(
						"       throw new OntologyException(\"Error internalising ");
						intern.append(converter.toJavaTypeName(theCls));
						intern.append("\");\n");
						intern.append("     }\n");
						intern.append("   }\n\n");

						ps.print(extern.toString());
						ps.print(intern.toString());
					}

					finishUpRole(theCls);
					if (beanGeneration) {
						ps.println("}");
						ps.flush();
						ps.close();

						// format the bean
						if (applyFormatting) {
							format(file);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			// end if
			// update the progress bar
			ontologyBeanGeneratorPanel.progress.setValue(ontologyBeanGeneratorPanel.
					progress.getValue() + 1);
			ontologyBeanGeneratorPanel.progress.repaint();
		}
		// end while
	}

	// the formatter
	//    private Jalopy jalopy;

	/**
	 *  Initialisation code. Only called once during start up like a constructor
	 */
	public void initialize() {
		updateConstantsNames(getKnowledgeBase());
		// initialize the tab text
		ImageIcon beanIcon = new ImageIcon(this.getClass()
				.getResource("graphics/bean.gif"));
		this.setIcon(beanIcon);
		setLabel("Ontology Bean Generator");
		setFont(new java.awt.Font("SansSerif", 3, 12));

		itsKB = this.getKnowledgeBase();
		ontologyBeanGeneratorPanel = new OntologyBeanGeneratorPanel(this);
		add(ontologyBeanGeneratorPanel);

		if (!isSimpleJADEAbstractOntIncluded(getKnowledgeBase())) {
			String errorMsg;
			if (getKnowledgeBase() instanceof OWLModel)
				errorMsg = "Project should import OWLSimpleJADEAbstractOntology.owl";
			else
				errorMsg = "Project should include SimpleJADEAbstractOntology.pprj";

			Log.getLogger().warning(errorMsg);

			JOptionPane.showMessageDialog(this, errorMsg, "Missing include/import", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Cls projectAnno = itsKB.getCls(PROJECTANNOTATION);
		if (projectAnno == null)
			return;

		if (projectAnno.getInstanceCount() == 0) {
			this.projectAnnoInstance = projectAnno.createDirectInstance("project annotation");
		}
		else {
			this.projectAnnoInstance = (Instance) projectAnno.getInstances().iterator().next();
			this.retrieveProjectAnnotation();
		}
	}

	private void removeIgnoredClasses(Collection col) {
		Iterator iter = col.iterator();
		while (iter.hasNext()) {
			Cls theCls = (Cls) iter.next();
			if (isIgnored(theCls)) {
				iter.remove();
			}
		}
	}

	/**
	 *  Reset all global fields and read options
	 */
	private void reset() {
		ontologyHeader = new StringBuffer();
		ontologyInit = new StringBuffer();
		ontologyBody = new StringBuffer();
		ontologyInheritance = new StringBuffer();
		nameMapping = new StringBuffer();

		ontologyName = (String) ontologyBeanGeneratorPanel.ontologyComboBox.
		getSelectedItem();
		ontologyBeanGeneratorPanel.progress.setValue(0);

		// Get the JADE_CLASS and the definition of the slot, JADE_NAME etc

		Cls jadeClass = itsKB.getCls(JADE_CLASS);

		if (jadeClass != null) {
			try {
				Collection jadeClassSlots = jadeClass.getDirectTemplateSlots();
				Iterator iter = jadeClassSlots.iterator();
				while (iter.hasNext()) {
					Slot slot = (Slot) iter.next();
					if (slot.getName().equals(JADE_JAVA_BASE_CLASS_SLOT)) {
						javaSuperClassSlot = slot;
					} else if (slot.getName().equals(JADE_JAVA_CODE_SLOT)) {
						javaJavaCodeSlot = slot;
					} else if (slot.getName().equals(JADE_IGNORED_SLOT)) {
						jadeIgnoredSlot = slot;
					} else if (slot.getName().equals(PROTEGE_DOC)) {
						protegeDoc = slot;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Collection jadeClassSlots = jadeClass.getDirectTemplateSlots();
				Iterator iter = jadeClassSlots.iterator();
				while (iter.hasNext()) {
					Slot slot = (Slot) iter.next();
					if (slot.getName().equals(JADE_JAVA_BASE_CLASS_SLOT)) {
						javaSuperClassSlot = slot;
					} else if (slot.getName().equals(JADE_JAVA_CODE_SLOT)) {
						javaJavaCodeSlot = slot;
					} else if (slot.getName().equals(JADE_IGNORED_SLOT)) {
						jadeIgnoredSlot = slot;
					} else if (slot.getName().equals(PROTEGE_DOC)) {
						protegeDoc = slot;
					}
				}

				// Get the JADE_SLOT class and the definition of the slots,
				// JADE_UNNAMED_SLOT
				// and JADE_JAVA_BASE_CLASS
				Cls jadeSlotClass = itsKB.getCls(JADE_SLOT);
				jadeClassSlots = jadeSlotClass.getDirectTemplateSlots();
				iter = jadeClassSlots.iterator();
				while (iter.hasNext()) {
					Slot slot = (Slot) iter.next();
					if (slot.getName().equals(JADE_UNNAMED_SLOT)) {
						jadeUnnamedSlot = slot;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Get generation options from the GUI Panel
		microSupport = false;
		fullBeanSupport = false;
		if (ontologyBeanGeneratorPanel.microSupport.isSelected()) {
			microSupport = true;
		}
		else if (ontologyBeanGeneratorPanel.fullBeanSupport.isSelected()) {
			fullBeanSupport = true;
		}

		dirName = (String) ontologyBeanGeneratorPanel.locationComboBox.
		getSelectedItem();
		packageName = (String) ontologyBeanGeneratorPanel.packageComboBox.
		getSelectedItem();
		beanGeneration = this.ontologyBeanGeneratorPanel.beansCheckBox.isSelected();
		ontologyGeneration = this.ontologyBeanGeneratorPanel.jadeCheckBox.
		isSelected();
		useJADENames = this.ontologyBeanGeneratorPanel.useJadeNamesCheckBox.
		isSelected();
		applyFormatting = this.ontologyBeanGeneratorPanel.doFormatting.getModel()
		.isSelected();

		// The converter between Cls/Slot and various Strings
		converter = new NameConverter(itsKB, useJADENames);

		// if the output dir doesn't exist, create it and all its parents
		File outputDir = new File(converter.toDir(packageName, dirName));
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		// create the Jalopy formatter and options
		if (applyFormatting) {
			//  jalopy = new Jalopy();
			//  jalopy.setBackup(false);
			//  jalopy.setForce(true);
			//  try {
			//      jalopy.setPreferences(
			//             this.ontologyBeanGeneratorPanel.jalopyFile.getText());
			// } catch (Exception e) {
			//     e.printStackTrace();
			// }
		}
	}

	void retrieveProjectAnnotation() {
		try {

			Slot packageSlot = itsKB.getSlot(this.PACKAGE_SLOT);
			Slot ontologySlot = itsKB.getSlot(this.ONTOLOGYNAME_SLOT);
			Slot locationSlot = itsKB.getSlot(this.LOCATION_SLOT);
			Slot supportSlot = itsKB.getSlot(this.SUPPORT_SLOT);
			String packageName = (String) projectAnnoInstance.getOwnSlotValue(
					packageSlot);
			String ontologyName = (String) projectAnnoInstance.getOwnSlotValue(
					ontologySlot);
			String locationName = (String) projectAnnoInstance.getOwnSlotValue(
					locationSlot);
			String supportLine = (String) projectAnnoInstance.getOwnSlotValue(
					supportSlot);
			if (packageName != null) {
				this.ontologyBeanGeneratorPanel.packageBoxModel.addElement(packageName);
				this.ontologyBeanGeneratorPanel.packageComboBox.setSelectedItem(
						packageName);
			}
			if (ontologyName != null) {
				this.ontologyBeanGeneratorPanel.ontologyBoxModel.addElement(
						ontologyName);
				this.ontologyBeanGeneratorPanel.ontologyComboBox.setSelectedItem(
						ontologyName);
			}
			if (locationName != null) {
				this.ontologyBeanGeneratorPanel.locationBoxModel.addElement(
						locationName);
				this.ontologyBeanGeneratorPanel.locationComboBox.setSelectedItem(
						locationName);
			}

			ontologyBeanGeneratorPanel.support.setSelected(ontologyBeanGeneratorPanel.
					standardSupport.getModel(), true);
			ontologyBeanGeneratorPanel.exampleTextArea.setText(Examples.
					getStandardExample());
			if (supportLine != null) {
				if (supportLine.equalsIgnoreCase("j2me")) {
					ontologyBeanGeneratorPanel.support.setSelected(
							ontologyBeanGeneratorPanel.microSupport.getModel(), true);
					ontologyBeanGeneratorPanel.exampleTextArea.setText(Examples.
							getMicroExample());
				}
				if (supportLine.equalsIgnoreCase("j2se")) {
					ontologyBeanGeneratorPanel.support.setSelected(
							ontologyBeanGeneratorPanel.standardSupport.getModel(), true);
					ontologyBeanGeneratorPanel.exampleTextArea.setText(Examples.
							getStandardExample());
				}
				if (supportLine.equalsIgnoreCase("javabeans")) {
					ontologyBeanGeneratorPanel.support.setSelected(
							ontologyBeanGeneratorPanel.fullBeanSupport.getModel(), true);
					ontologyBeanGeneratorPanel.exampleTextArea.setText(Examples.
							getFullExample());
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	void saveProjectAnnotation() {
		try {
			Slot packageSlot = itsKB.getSlot(this.PACKAGE_SLOT);
			Slot ontologySlot = itsKB.getSlot(this.ONTOLOGYNAME_SLOT);
			Slot locationSlot = itsKB.getSlot(this.LOCATION_SLOT);
			Slot supportSlot = itsKB.getSlot(this.SUPPORT_SLOT);
			String packageName = (String)this.ontologyBeanGeneratorPanel.
			packageComboBox.getSelectedItem();
			String ontologyName = (String)this.ontologyBeanGeneratorPanel.
			ontologyComboBox.getSelectedItem();
			String locationName = (String)this.ontologyBeanGeneratorPanel.
			locationComboBox.getSelectedItem();
			String supportLine = "j2se";

			if (ontologyBeanGeneratorPanel.microSupport.isSelected()) {
				supportLine = "j2me";
			}
			if (ontologyBeanGeneratorPanel.fullBeanSupport.isSelected()) {
				supportLine = "javabeans";
			}

			if (projectAnnoInstance == null)
				return;

			if (packageName != null) {
				this.projectAnnoInstance.setOwnSlotValue(packageSlot, packageName);
			}
			if (ontologyName != null) {
				this.projectAnnoInstance.setOwnSlotValue(ontologySlot, ontologyName);
			}
			if (locationName != null) {
				this.projectAnnoInstance.setOwnSlotValue(locationSlot, locationName);
			}
			this.projectAnnoInstance.setOwnSlotValue(supportSlot, supportLine);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean applyFormatting;
	private boolean beanGeneration;
	private NameConverter converter;
	private String dirName;
	private boolean fullBeanSupport;
	// the knowledge base and project references
	private KnowledgeBase itsKB;
	private Project itsProject;
	// references to the jade specific slots
	private Slot jadeIgnoredSlot;
	private Slot jadeUnnamedSlot;
	private Slot javaJavaCodeSlot;
	private Slot javaSuperClassSlot;
	// options, taken from the GUI
	private boolean microSupport;
	private StringBuffer nameMapping;
	private OntologyBeanGeneratorPanel ontologyBeanGeneratorPanel;
	private StringBuffer ontologyBody;
	private boolean ontologyGeneration;
	// Components of the generated ontology file
	private StringBuffer ontologyHeader;
	private StringBuffer ontologyInheritance;
	private StringBuffer ontologyInit;
	private String ontologyName;
	// The package where the generated classes will be
	private String packageName = "";

	Instance projectAnnoInstance;
	private Slot protegeDoc;
	private boolean useJADENames;
	// Constants for Protege
	private String JADE_CLASS = ":JADE-CLASS";

	private String JADE_IGNORED_SLOT = ":JADE-IGNORE";

	private String JADE_JAVA_BASE_CLASS_SLOT = ":JADE-JAVA-BASE-CLASS";

	private String JADE_JAVA_CODE_SLOT = ":JADE-JAVA-CODE";

	private String JADE_NAME_SLOT = ":JADE-NAME";

	private String JADE_SLOT = ":JADE-SLOT";

	private String JADE_UNNAMED_SLOT = ":JADE-UNNAMED";

	private String PROTEGE_DOC = ":DOCUMENTATION";

	private String AGENTACTION = "AgentAction";

	private String JAVA_AGENTACTION_CLASS_NAME = "AgentAction";

	private String AID = "AID";

	private String JAVA_AID_CLASS_NAME = "AID";

	private String CONCEPT = "Concept";

	private String JAVA_CONCEPT_CLASS_NAME = "Concept";  	

	private String LOCATION_SLOT = ":LOCATION";

	private String ONTOLOGYNAME_SLOT = ":ONTOLOGYNAME";

	private String PACKAGE_SLOT = ":PACKAGE";

	private String PREDICATE = "Predicate";

	private String JAVA_PREDICATE_CLASS_NAME = "Predicate";

	private String PROJECTANNOTATION = ":PROJECT-ANNOTATION";

	private String SUPPORT_SLOT = ":SUPPORT";

	public static final String BEAN_GENERATOR_NAMESPACE = "http://jade.cselt.it/beangenerator#";

	/*
	 *  private JComponent createDirectInstancesList() {
	 *  itsDirectInstancesList = new DirectInstancesList(getProject());
	 *  Collection selection = clsTree.getSelection();
	 *  itsDirectInstancesList.setClses(selection);
	 *  itsDirectInstancesList.initializeSelection();
	 *  itsDirectInstancesList.setEnabled(false);
	 *  return itsDirectInstancesList;
	 *  }
	 */
	/*
	 *  private void transmitSelection() {
	 *  if (itsDirectInstancesList == null) {
	 *  return;
	 *  }
	 *  Collection selection = clsTree.getSelection();
	 *  itsDirectInstancesList.setClses(selection);
	 *  itsDirectInstancesList.initializeSelection();
	 *  }
	 */



	private void updateConstantsNames(KnowledgeBase kb) {
		if (!(kb instanceof OWLModel))
			return;

		String prefix = ((OWLModel)kb).getNamespaceManager().getPrefix(BEAN_GENERATOR_NAMESPACE);

		if (prefix == null)
			return;

		JADE_CLASS = prefix + ":" + "JADE-CLASS";
		JADE_IGNORED_SLOT = prefix + ":" + "JADE-IGNORE";
		JADE_JAVA_BASE_CLASS_SLOT = prefix + ":" + "JADE-JAVA-BASE-CLASS";
		JADE_JAVA_CODE_SLOT = prefix + ":" + "JADE-JAVA-CODE";
		JADE_NAME_SLOT = prefix + ":" + "JADE-NAME";
		JADE_SLOT = prefix + ":" + "JADE-SLOT";
		JADE_UNNAMED_SLOT = prefix + ":" + "JADE-UNNAMED";
		PROTEGE_DOC = prefix + ":" + "DOCUMENTATION";
		AGENTACTION = prefix + ":" + "AgentAction";
		AID = prefix + ":" + "AID";
		CONCEPT = prefix + ":" + "Concept";
		LOCATION_SLOT = prefix + ":" + "LOCATION";
		ONTOLOGYNAME_SLOT = prefix + ":" + "ONTOLOGYNAME";
		PACKAGE_SLOT = prefix + ":" + "PACKAGE";
		PREDICATE = prefix + ":" + "Predicate";
		PROJECTANNOTATION = prefix + ":" + "PROJECT-ANNOTATION";
		SUPPORT_SLOT = prefix + ":" + "SUPPORT";
	}


	/**
	 * Ugly method to find out if Jade Ontology is included, or necessary concepts have been defined
	 * @param kb
	 * @return true -if included, false - otherwise
	 */
	private static boolean isSimpleJADEAbstractOntIncluded(KnowledgeBase kb) {	  
		String CONCEPT_NAME = "Concept"; 
		String PREDICATE_NAME = "Predicate";

		if (!(kb instanceof OWLModel))
			return kb.getCls(CONCEPT_NAME) != null && kb.getCls(PREDICATE_NAME) != null;

		String prefix = ((OWLModel)kb).getNamespaceManager().getPrefix(BEAN_GENERATOR_NAMESPACE);

		if (prefix != null) {
			CONCEPT_NAME = prefix + ":" + CONCEPT_NAME;
			PREDICATE_NAME = prefix + ":" + PREDICATE_NAME;
		}

		return kb.getCls(CONCEPT_NAME) != null && kb.getCls(PREDICATE_NAME) != null;
	}


	public static boolean isSuitable(Project p, Collection errors) {	  
		if (!isSimpleJADEAbstractOntIncluded(p.getKnowledgeBase())) {
			if (p.getKnowledgeBase() instanceof OWLModel)
				errors.add("Project should import OWLSimpleJADEAbstractOntology.owl");
			else
				errors.add("Project should include SimpleJADEAbstractOntology.pprj");			  
			return false;
		}

		return true;
	}
	
}
//***EOF***
