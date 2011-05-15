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
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.ReflectiveIntrospector;
import jade.content.onto.UngroundedException;
import jade.content.schema.ObjectSchema;

import java.lang.reflect.Method;

/**
 *@author     Jamie Lawrence - Media Lab Europe
 *@created    November 14, 2002
 */
public class ProtegeIntrospector extends ReflectiveIntrospector {

  public void setOntology(ProtegeTools.ProtegeOntology onto) {
    this.protegeOntology = onto;
  }

  /**
   *  Translate an object of a class representing an element in an ontology into
   *  a proper abstract descriptor
   *
   *@param  obj                 The Object to be translated
   *@param  schema              The schema for the ontological element this
   *      object is an instance of.
   *@param  javaClass           The class of the Object to be translated
   *@param  referenceOnto       The reference ontology in the context of this
   *      translation.
   *@return                     The Abstract descriptor produced by the
   *      translation
   *@throws  OntologyException  If some error occurs during the translation
   */
  public AbsObject externalise(Object obj, ObjectSchema schema, Class javaClass, Ontology referenceOnto)
     throws OntologyException {
    String slotName = "<unknown>";
    try {
      AbsObject abs = schema.newInstance();
      String[] names = schema.getNames();

      // Loop on slots
      for (int i = 0; i < names.length; ++i) {
        slotName = names[i];
        ObjectSchema slotSchema = schema.getSchema(slotName);
        String transLatedName = translateName(slotName, javaClass);
        if (transLatedName != null) {
          String methodName = "get" + transLatedName;
           // Retrieve the accessor method from the class and call it
          Method getMethod = findMethodCaseInsensitive(methodName, javaClass);
//          this.setOntology(referenceOnto);
          AbsObject value = (AbsObject)this.invokeAccessorMethod(getMethod, obj);
          if (value != null) {
            AbsHelper.setAttribute(abs, slotName, value);
          }
        }
      }

      return abs;
    } catch (OntologyException oe) {
      throw oe;
    } catch (Throwable t) {
      throw new OntologyException("Schema and Java class do not match for the slot:" + slotName + " and class: " + javaClass.getClass().toString(), t);
    }
  }

  /**
   *  Translate an abstract descriptor into an object of a proper class
   *  representing an element in an ontology
   *
   *@param  abs                   The abstract descriptor to be translated
   *@param  schema                The schema for the ontological element this
   *      abstract descriptor is an instance of.
   *@param  javaClass             The class of the Object to be produced by the
   *      translation
   *@param  referenceOnto         The reference ontology in the context of this
   *      translation.
   *@return                       The Java object produced by the translation
   *@throws  UngroundedException  If the abstract descriptor to be translated
   *      contains a variable
   *@throws  OntologyException    If some error occurs during the translation
   */
  public Object internalise(AbsObject abs, ObjectSchema schema, Class javaClass, Ontology referenceOnto)
     throws UngroundedException, OntologyException {

    try {
      Object obj = javaClass.newInstance();
      String[] names = schema.getNames();

      // LOOP on slots
      for (int i = 0; i < names.length; ++i) {
        String slotName = names[i];
        AbsObject value = abs.getAbsObject(slotName);
        if (value != null) {
          ObjectSchema slotSchema = schema.getSchema(slotName);

          String methodName = "set" + translateName(slotName, javaClass);
          // Retrieve the modifier method from the class and call it
          Method setMethod = findMethodCaseInsensitive(methodName, javaClass);
          this.invokeSetterMethod(setMethod, obj, value);
        }
      }

      return obj;
    } catch (OntologyException oe) {
      throw oe;
    } catch (InstantiationException ie) {
      throw new OntologyException("Class " + javaClass + " can't be instantiated", ie);
    } catch (IllegalAccessException iae) {
      throw new OntologyException("Class " + javaClass + " does not have an accessible constructor", iae);
    } catch (Throwable t) {
      throw new OntologyException("Schema and Java class do not match", t);
    }
  }

  protected String translateName(String slotName) {
    return ProtegeTools.firstUpper(ProtegeTools.toJavaStyleString(slotName));
    /*
     *  StringBuffer buff = new StringBuffer(slotName);
     *  ProtegeTools.firstUpper(buff);
     *  ProtegeTools.toJavaString(buff);
     *  return buff.toString();
     */
  }

  private String translateName(String jadeName, Class javaClass) {
    //System.out.print("Class: " + javaClass.getName());
    //System.out.print(", jadeName: " + jadeName);
    SlotHolder jadeSlot = new SlotHolder(javaClass.getName(), jadeName);
    if (jadeSlot == null) {
      return null;
    }
    SlotHolder methodKey = protegeOntology.getSlotNameFromJADEName(jadeSlot);
    if (methodKey == null) {
      return null;
    }
    //System.out.println(", methodKey: " + methodKey.slotName);
    return translateName(methodKey.slotName);
  }
  private ProtegeTools.ProtegeOntology protegeOntology;
}
