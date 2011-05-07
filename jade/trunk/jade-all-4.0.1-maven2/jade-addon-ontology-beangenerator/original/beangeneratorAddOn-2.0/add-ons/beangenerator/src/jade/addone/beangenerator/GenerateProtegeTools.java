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

/**
 *@author     C.J. van Aart - Y'All B.V.
 *@created    July 17, 2004
 *@version    1.0
 */


public class GenerateProtegeTools {

  public static void Generate(String packageName, String dir) {
    StringBuffer sb = new StringBuffer("package " + packageName + ";\n\n");
    sb.append("/* This is file is generated by the ontology bean generator.  \nDO NOT EDIT, UNLESS YOU ARE REALLY REALLY SURE WHAT YOU ARE DOING! */\n\n");
    sb.append("/** file: ProtegeTools\n");
    sb.append(" * @author ontology bean generator (Y'All BV) \n");
    sb.append(" * @version " + OntologyBeanGeneratorUtil.getDate() + "\n");
    sb.append(" */\n");

    sb.append("\n");
    sb.append("import jade.util.leap.Collection;\n");
    sb.append("import jade.util.leap.Iterator;");
    sb.append("\n");
    sb.append("\n");
    sb.append("public class ProtegeTools {\n");
    sb.append("\n");
    sb.append("  public static void firstDown(StringBuffer buff) {\n");
    sb.append("    buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public static String firstDown(String str) {\n");
    sb.append("    StringBuffer buff = new StringBuffer(str);\n");
    sb.append("    firstDown(buff);\n");
    sb.append("    return buff.toString();\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public static void firstUpper(StringBuffer buff) {\n");
    sb.append("    buff.setCharAt(0, Character.toUpperCase(buff.charAt(0)));\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public static String firstUpper(String str) {\n");
    sb.append("    StringBuffer buff = new StringBuffer(str);\n");
    sb.append("    firstUpper(buff);\n");
    sb.append("    return buff.toString();\n");
    sb.append("  }\n");
    sb.append("  /**\n");
    sb.append("   *  Convert Strings to a valid Java identifiers by replacing ' ' and '-' with\n");
    sb.append("   *  an underscore '_'\n");
    sb.append("   *\n");
    sb.append("   *@param  buff  Description of the Parameter\n");
    sb.append("   */\n");
    sb.append("  public static void toJavaString(StringBuffer buff) {\n");
    sb.append("    for (int i = 0; i < buff.length(); i++) {\n");
    sb.append("      char c = buff.charAt(i);\n");
    sb.append("      switch (c) {\n");
    sb.append("          case ' ':\n");
    sb.append("            buff.setCharAt(i, '_');\n");
    sb.append("            break;\n");
    sb.append("          case '-':\n");
    sb.append("            buff.setCharAt(i, '_');\n");
    sb.append("            break;\n");
    sb.append("          default:\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public static String toJavaString(String key) {\n");
    sb.append("    StringBuffer buff = new StringBuffer(key);\n");
    sb.append("    toJavaString(buff);\n");
    sb.append("    return buff.toString();\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public static void toJavaStyleString(StringBuffer buff) {\n");
    sb.append("    // remove spaces, dashes etc\n");
    sb.append("    toJavaString(buff);\n");
    sb.append("    // capitalise letters after a '_'\n");
    sb.append("\n");
    sb.append("\n");
    sb.append("/*    for (int i = 0; i < buff.length(); i++) {\n");
    sb.append("      char c = buff.charAt(i);\n");
    sb.append("      switch (c) {\n");
    sb.append("          case '_':\n");
    sb.append("            buff.deleteCharAt(i);\n");
    sb.append("            // after deletion, char i+1 is at position i\n");
    sb.append(
        "            buff.setCharAt(i, Character.toUpperCase(buff.charAt(i)));\n");
    sb.append("            break;\n");
    sb.append("          default:\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("*/\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public static String toJavaStyleString(String key) {\n");
    sb.append("    StringBuffer buff = new StringBuffer(key);\n");
    sb.append("    toJavaStyleString(buff);\n");
    sb.append("    return buff.toString();\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append(
        "  public static String toJavaVariableStyleString(String var) {\n");
    sb.append("    StringBuffer buff = new StringBuffer(var);\n");
    sb.append("    toJavaVariableStyleString(buff);\n");
    sb.append("    return buff.toString();\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append(
        "  public static void toJavaVariableStyleString(StringBuffer buff) {\n");
    sb.append("    // remove incompatible characters\n");
    sb.append("    toJavaStyleString(buff);\n");
    sb.append("\n");
    sb.append("    // conform to Java variable style:\n");
    sb.append(
        "    // MyHAP->myHAP, AIDSchema -> aidSchema, Dir->dir, HomeDir->homeDir\n");
    sb.append("\n");
    sb.append("    // put down 1st char\n");
    sb.append("    buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));\n");
    sb.append("    // walk along and put down i char if i+1 is uppercase\n");
    sb.append("    boolean pastFirstWork = false;\n");
    sb.append("    for (int i = 0; i < buff.length(); i++) {\n");
    sb.append("      // i+1 doesn't exist\n");
    sb.append("      if (i == buff.length() - 1) {\n");
    sb.append("        break;\n");
    sb.append("      }\n");
    sb.append(
        "      // i and i+1 are lowercase so we should stop processing now\n");
    sb.append("      if (Character.isLowerCase(buff.charAt(i)) && Character.isLowerCase(buff.charAt(i + 1))) {\n");
    sb.append("        break;\n");
    sb.append("      }\n");
    sb.append("\n");
    sb.append("      if (Character.isUpperCase(buff.charAt(i + 1))) {\n");
    sb.append(
        "        buff.setCharAt(i, Character.toLowerCase(buff.charAt(i)));\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public interface ProtegeOntology {\n");
    sb.append(
        "    //        public String getJADENameFromSlotName(SlotHolder slot);\n");
    sb.append(
        "    public SlotHolder getSlotNameFromJADEName(SlotHolder jadeSlot);\n");
    sb.append(
        "    //        public String getClassNameFromJADEName(String jadeName);\n");
    sb.append(
        "    //        public String getJADENameFromClassName(String className);\n");
    sb.append("  }\n");
    sb.append("\n");

    sb.append("  public static void copyCollection(final Collection from, final Collection to) {\n");
    sb.append("    for (Iterator i = from.iterator(); i.hasNext(); ) {\n");
    sb.append("      to.add(i.next());\n");
    sb.append("     }\n");
    sb.append("    }\n");
    sb.append("}\n");

    OntologyBeanGeneratorUtil.writeFile(dir + "/ProtegeTools.java", sb.toString());
  }

}