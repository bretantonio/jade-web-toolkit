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
package nl.uva.psy.swi.beangenerator;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 *  This class performs various conversions, i.e. turning a slot name into the
 *  get method, getSlotName()
 *
 *@author     Jamie Lawrence - Media Lab Europe
 *@created    November 14, 2002
 */

public class NameConverter {

	/**
	 *  Get the name of the class. If there is a JADE name specified in Protege
	 *  this will be used, otherwise it will be the ProtegeName
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         The className value
	 */
	public String getClassName(Cls theCls) {
		String className = theCls.getName();
		if (!useJADENames) {
			return className;
		}

		// if there is a slot for the JADE name use this insteead of the Protege name
		if (jadeNameSlot != null) {
			Object temp = theCls.getOwnSlotValue(jadeNameSlot);
			if (temp != null) {
				className = (String) temp;
			}
		}
		return className;
	}

	/**
	 *  Get the name of the slot. If there is a JADE name specified in Protege
	 *  this will be used, otherwise it will be the ProtegeName
	 *
	 *@param  theSlot  Description of the Parameter
	 *@return          The slotName value
	 */
	public String getSlotName(Slot theSlot) {
		String slotName = theSlot.getName();
		if (!useJADENames) {
			return slotName;
		}

		// if there is a JADE name use this as the slot name
		if (jadeNameSlot != null) {
			Object temp = theSlot.getOwnSlotValue(jadeNameSlot);
			if (temp != null) {
				slotName = (String) temp;
			}
		}

		return slotName;
	}

	public String toAddMethod(Slot theSlot) {
		String slotName = ProtegeTools.toJavaStyleString(theSlot.getName());
		return "add" + ProtegeTools.firstUpper(slotName);
	}

	public String toClearAllMethod(Slot theSlot) {
		String slotName = ProtegeTools.toJavaStyleString(theSlot.getName());
		return "clearAll" + ProtegeTools.firstUpper(slotName);
	}

	/**
	 *  Get the String representing the directory to which all the files will be
	 *  written. If srcDir/packageName doesn't exist the directories will be
	 *  automatically created.
	 *
	 *@param  packageName  Description of the Parameter
	 *@param  srcDir       Description of the Parameter
	 *@return              Description of the Return Value
	 */
	public String toDir(String packageName, String srcDir) {
		StringBuffer buff = new StringBuffer(srcDir);
		buff.append("/");
		buff.append(packageName.replace('.', '/'));
		return buff.toString();
	}

	/**
	 *  Creates a fully qualified Java class name
	 *
	 *@param  packageName  Description of the Parameter
	 *@param  theCls       Description of the Parameter
	 *@return              Description of the Return Value
	 */
	public String toFullJavaClassName(String packageName, Cls theCls) {
		return packageName + "." + toJavaTypeName(theCls);
	}

	public String toGetAllMethod(Slot theSlot) {
		String slotName = ProtegeTools.toJavaStyleString(theSlot.getName());
		return "getAll" + ProtegeTools.firstUpper(slotName);
	}

	/**
	 *  Creates the get method used in the ontology bean class, i.e. getSlotName
	 *
	 *@param  theSlot  Description of the Parameter
	 *@return          Description of the Return Value
	 */
	public String toGetMethod(Slot theSlot) {
		String slotName = ProtegeTools.toJavaStyleString(theSlot.getName());
		return "get" + ProtegeTools.firstUpper(slotName);
	}

	/**
	 *  Create a class name of the form ClassName
	 *
	 *@param  theCls  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public String toJavaTypeName(Cls theCls) {
		String className = theCls.getName();
		return ProtegeTools.firstUpper(ProtegeTools.toJavaStyleString(className));
	}

	public String toJavaTypeName(String className) {
		String buff = ProtegeTools.firstUpper(ProtegeTools.toJavaStyleString(className));
		buff = buff.replace(' ', '_');
		buff = buff.replace('-', '_');
		buff = buff.replace(':', '_');
		return buff;
	}

	public String toOntologyClassName(String ontologyName) {
		StringBuffer buff = new StringBuffer(ontologyName);
		buff.append("Ontology");
		ProtegeTools.toJavaStyleString(buff);
		ProtegeTools.firstUpper(buff);
		return buff.toString();
	}

	public String toRemoveMethod(Slot theSlot) {
		String slotName = ProtegeTools.toJavaStyleString(theSlot.getName());
		return "remove" + ProtegeTools.firstUpper(slotName);
	}

	/**
	 *  Create a role constant used in the ontology file. If slotName is null it
	 *  will return CLASSNAME, otherwise CLASSNAME_SLOTNAME
	 *
	 *@param  theCls   Description of the Parameter
	 *@param  theSlot  Description of the Parameter
	 *@return          Description of the Return Value
	 */
	public String toRoleConstant(Cls theCls, Slot theSlot) {
		String className = getClassName(theCls);

		StringBuffer buff = new StringBuffer(className);
		ProtegeTools.toJavaString(buff);
		if (theSlot != null) {
			String slotName = getSlotName(theSlot);
			buff.append("_");
			buff.append(ProtegeTools.toJavaString(slotName));
		}
		//        buff.setCharAt(0, Character.toUpperCase(buff.charAt(0)));
		//        ProtegeTools.toJavaString(buff);

		return buff.toString().toUpperCase();
	}

	/**
	 *  Creates the name of the schema variable used in the ontology file
	 *
	 *@param  theCls   Description of the Parameter
	 *@param  theSlot  Description of the Parameter
	 *@return          Description of the Return Value
	 */
	public String toSchemaVariableName(Cls theCls, Slot theSlot) {
		String className = theCls.getName();

		StringBuffer buff = new StringBuffer(className);
		if (theSlot != null) {
			String slotName = getSlotName(theSlot);
			buff.append("_");
			buff.append(slotName);
		}
		buff.append("Schema");

		ProtegeTools.toJavaVariableStyleString(buff);

		return buff.toString();
	}

	/**
	 *  Creates the set method used in the ontology bean class, i.e. setSlotName
	 *
	 *@param  theSlot  Description of the Parameter
	 *@return          Description of the Return Value
	 */
	public String toSetMethod(Slot theSlot) {
		String slotName = ProtegeTools.toJavaStyleString(theSlot.getName());
		return "set" + ProtegeTools.firstUpper(slotName);
	}

	/**
	 *  Creates a new instance of NameConverter
	 *
	 *@param  itsKB         Description of the Parameter
	 *@param  useJADENames  Description of the Parameter
	 */
	public NameConverter(KnowledgeBase itsKB, boolean useJADENames) {
		this.itsKB = itsKB;
		this.useJADENames = useJADENames;

		updateConstantsNames(itsKB);
		// Get the JADE_CLASS and the definition of the slot, JADE_NAME etc
		Cls jadeClass = itsKB.getCls(JADE_CLASS);

		if (jadeClass == null)
			return;

		Collection jadeClassSlots = jadeClass.getDirectTemplateSlots();
		Iterator iter = jadeClassSlots.iterator();
		while (iter.hasNext()) {
			Slot slot = (Slot) iter.next();
			if (slot.getName().equals(JADE_NAME_SLOT)) {
				jadeNameSlot = slot;
			}
		}
	}

	private void updateConstantsNames(KnowledgeBase kb) {
		if (!(kb instanceof OWLModel))
			return;

		String prefix = ((OWLModel)kb).getNamespaceManager().getPrefix(OntologyBeanGeneratorTab.BEAN_GENERATOR_NAMESPACE);

		if (prefix == null)
			return;

		JADE_CLASS = prefix + ":" + "JADE-CLASS";
		JADE_NAME_SLOT = prefix + ":" + "JADE-NAME";
	}


	private KnowledgeBase itsKB;
	// references to the jade specific slots
	private Slot jadeNameSlot;
	private boolean useJADENames;
	// Constants for Protege
	private String JADE_CLASS = ":JADE-CLASS";
	private String JADE_NAME_SLOT = ":JADE-NAME";
	//private final String JADE_SLOT = ":JADE-SLOT";

}
