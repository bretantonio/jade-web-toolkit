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

/*
 *  @author     Jamie Lawrence - Media Lab Europe
 *  @author  Chris van Aart - Acklin, University of Amsterdam
 *  @created    November 14, 2002
 */
public class ProtegeTools {

  public static void firstDown(StringBuffer buff) {
    buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));
  }

  public static String firstDown(String str) {
    StringBuffer buff = new StringBuffer(str);
    firstDown(buff);
    return buff.toString();
  }

  public static void firstUpper(StringBuffer buff) {
    buff.setCharAt(0, Character.toUpperCase(buff.charAt(0)));
  }

  public static String firstUpper(String str) {
    StringBuffer buff = new StringBuffer(str);
    firstUpper(buff);
    return buff.toString();
  }
  /**
   *  Convert Strings to a valid Java identifiers by replacing ' ' and '-' and ':' with
   *  an underscore '_'
   *
   *@param  buff  Description of the Parameter
   */
  public static void toJavaString(StringBuffer buff) {
    for (int i = 0; i < buff.length(); i++) {
      char c = buff.charAt(i);
      switch (c) {
      case ' ':
        buff.setCharAt(i, '_');
        break;
      case '-':
        buff.setCharAt(i, '_');
        break;
      case ':':
    	  buff.setCharAt(i, '_');
          break;
      default:
      }
    }
  }

  public static String toJavaString(String key) {
    StringBuffer buff = new StringBuffer(key);
    toJavaString(buff);
    return buff.toString();
  }

  public static void toJavaStyleString(StringBuffer buff) {
    // remove spaces, dashes etc
    toJavaString(buff);
    // capitalise letters after a '_'


    /*    for (int i = 0; i < buff.length(); i++) {
          char c = buff.charAt(i);
          switch (c) {
          case '_':
          buff.deleteCharAt(i);
          // after deletion, char i+1 is at position i
          buff.setCharAt(i, Character.toUpperCase(buff.charAt(i)));
          break;
          default:
          }
          }
    */
  }

  public static String toJavaStyleString(String key) {
    StringBuffer buff = new StringBuffer(key);
    toJavaStyleString(buff);
    return buff.toString();
  }

  public static String toJavaVariableStyleString(String var) {
    StringBuffer buff = new StringBuffer(var);
    toJavaVariableStyleString(buff);
    return buff.toString();
  }

  public static void toJavaVariableStyleString(StringBuffer buff) {
    // remove incompatible characters
    toJavaStyleString(buff);

    // conform to Java variable style:
    // MyHAP->myHAP, AIDSchema -> aidSchema, Dir->dir, HomeDir->homeDir

    // put down 1st char
    buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));
    // walk along and put down i char if i+1 is uppercase
    boolean pastFirstWork = false;
    for (int i = 0; i < buff.length(); i++) {
      // i+1 doesn't exist
      if (i == buff.length() - 1) {
        break;
      }
      // i and i+1 are lowercase so we should stop processing now
      if (Character.isLowerCase(buff.charAt(i)) && Character.isLowerCase(buff.charAt(i + 1))) {
        break;
      }

      if (Character.isUpperCase(buff.charAt(i + 1))) {
        buff.setCharAt(i, Character.toLowerCase(buff.charAt(i)));
      }
    }

  }

  public interface ProtegeOntology {
    //        public String getJADENameFromSlotName(SlotHolder slot);
    public SlotHolder getSlotNameFromJADEName(SlotHolder jadeSlot);
    //        public String getClassNameFromJADEName(String jadeName);
    //        public String getJADENameFromClassName(String className);
  }

}

