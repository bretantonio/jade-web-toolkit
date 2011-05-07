/******************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2002 TILAB S.p.A.
 *
 * This file is donated by Y'All B.V. to the JADE project.
 *
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * ***************************************************************/
package jade.addone.beangenerator;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import javax.swing.event.*;

import edu.stanford.smi.protege.action.*;
import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/*
 *@author     Chris van Aart - Y'All, University of Amsterdam
 *@author     Jamie Lawrence - Media Lab Europe
 *@created    November 14, 2002
 */

public class OntologyBeanGeneratorUtil {

  protected static String getJavaSlotType(ValueType valueType) {
    String theType = "";
    if (valueType.equals(ValueType.BOOLEAN)) {
      return "boolean";
    }

    //valueType.CLS
    if (valueType.equals(ValueType.CLS)) {
      return "Object";
    }

    //valueType.FLOAT
    if (valueType.equals(ValueType.FLOAT)) {
      return "Float";
    }

    //valueType.INTEGER
    if (valueType.equals(ValueType.INTEGER)) {
      return "int";
    }

    //valueType.STRING
    if (valueType.equals(ValueType.STRING)) {
      return "String";
    }

    //valueType.SYMBOL
    if (valueType.equals(ValueType.SYMBOL)) {
      return "String";
    }

    //valueType.ANY
    if (valueType.equals(ValueType.ANY)) {
      return "Ontology.STRING_TYPE";
    }

    if (valueType.equals(ValueType.ANY) || valueType.equals(ValueType.SYMBOL)) {
      return "String";
    }

    theType = valueType.getJavaType().toString();
    theType = ProtegeTools.toJavaString(theType);
    // REMOVE:
//        theType.replace(' ', '_');
//        theType = theType.replace('-', '_');
    theType = ProtegeTools.firstUpper(theType);
    return theType;
  }

  protected static String getDate() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/d, HH:mm:ss",
        Locale.getDefault());
    Date currentDate = new Date();
    return formatter.format(currentDate);
  }

  protected static String getOntologyType(ValueType valueType) {
    String ontologyType = "";
    //valueType.BOOLEAN
    if (valueType.equals(ValueType.BOOLEAN)) {
      ontologyType = "BasicOntology.BOOLEAN";
    }

    //valueType.CLS
    if (valueType.equals(ValueType.CLS)) {
      ontologyType = "BasicOntology.SET";
    }

    //valueType.FLOAT
    if (valueType.equals(ValueType.FLOAT)) {
      ontologyType = "BasicOntology.FLOAT";
    }

    //valueType.INTEGER
    if (valueType.equals(ValueType.INTEGER)) {
      ontologyType = "BasicOntology.INTEGER";
    }

    //valueType.STRING
    if (valueType.equals(ValueType.STRING)) {
      ontologyType = "BasicOntology.STRING";
    }

    //valueType.SYMBOL
    if (valueType.equals(ValueType.SYMBOL)) {
      ontologyType = "BasicOntology.STRING";
    }

    //valueType.ANY
    if (valueType.equals(ValueType.ANY)) {
      ontologyType = "BasicOntology.STRING";
    }
    return ontologyType;
  }

  protected static Collection getDirectTemplateSlots(Cls theCls) {

    Collection c = new HashSet(theCls.getTemplateSlots());
    Iterator itor = c.iterator();
    while (itor.hasNext()) {
      Slot slot = (Slot) itor.next();
      slot.getName();

      if (!theCls.hasDirectTemplateSlot(slot)) {
        c.remove(slot);
        itor = c.iterator();
      }
    }
    return c;
  }

  protected static Collection getClassProperties(Cls theCls) {

    Collection c = new HashSet(theCls.getDirectTemplateSlots());
    Iterator itor = c.iterator();
    while (itor.hasNext()) {
      Slot slot = (Slot) itor.next();
      String name = slot.getName();

      if (name.indexOf(":") >= 0) {
        c.remove(slot);
        itor = c.iterator();
      }
    }
    return c;
  }

  protected static String getFacetString(Slot theSlot) {
    String str = "    // facets of " + theSlot.getName() + ": ";
    if (theSlot.getValueType().equals(ValueType.INTEGER) ||
        theSlot.getValueType().equals(ValueType.FLOAT)) {
      if (theSlot.getMinimumValue() != null) {
        str = str + "getMinimumValue " + theSlot.getMinimumValue() + ", ";
      }

      if (theSlot.getMaximumValue() != null) {
        str = str + "getMaximumValue " + theSlot.getMaximumValue() + ", ";
      }

    }

    if (theSlot.getAllowedValues().size() > 0) {
      if (theSlot.getMinimumCardinality() > 0) {
        str = str + "MinimumCardinality " + theSlot.getMinimumCardinality() +
            ",";
      }

      if (theSlot.getMaximumCardinality() > 0) {
        str = str + "MaximumCardinality " + theSlot.getMaximumCardinality() +
            ", ";
      }

    }
    if (theSlot.getAllowedClses().size() > 0) {
      str = str + "getAllowedClses: ";
      Iterator it = theSlot.getAllowedClses().iterator();
      while (it.hasNext()) {
        str = str + (Cls) it.next() + ", ";
      }

    }
    if (theSlot.getAllowedParents().size() > 0) {
      str = str + ", getAllowedParents: ";
      Iterator it = theSlot.getAllowedParents().iterator();
      while (it.hasNext()) {
        str = str + it.next().toString() + ", ";
      }

    }
    if (theSlot.getAllowedValues().size() > 0) {
      str = str + ", getAllowedValues: ";
      Iterator it = theSlot.getAllowedValues().iterator();
      while (it.hasNext()) {
        str = str + it.next().toString() + ", ";
      }

    }
    return str;
  }

  /*    protected static String firstUpper(String str) {
          String first = str.substring(0, 1);
          first = first.toUpperCase();
          String newStr = first + str.substring(1, str.length());
          return newStr;
      }
      protected static String firstDown(String str) {
          String first = str.substring(0, 1);
          first = first.toLowerCase();
          String newStr = first + str.substring(1, str.length());
          return newStr;
      }
   */

  static void createHelpClasses(String dir, String packageName) {
    GenerateProtegeTools.Generate(packageName, dir);
    GenerateProtegeIntrospector.Generate(packageName, dir);
    GenerateSlotHolder.Generate(packageName, dir);
  }

  public static String readFile(String theFile) {

    StringBuffer bf = new StringBuffer();
    try {

//      InputStream is =  ClassLoader.getSystemResourceAsStream(theFile);
      FileInputStream fis = new FileInputStream(theFile);

      BufferedReader in = new BufferedReader(new InputStreamReader(fis));

      boolean content = false;
      String theLine = "";
      while ( ( (theLine = in.readLine()) != null)) {
        bf.append(theLine + "\n");
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return bf.toString();
  }

  public static void writeFile(String theFile, String content) {

    try {
      BufferedWriter out = new BufferedWriter(new java.io.FileWriter(theFile));
      out.write(content);
      out.flush();
      out.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
//  ***EOF***