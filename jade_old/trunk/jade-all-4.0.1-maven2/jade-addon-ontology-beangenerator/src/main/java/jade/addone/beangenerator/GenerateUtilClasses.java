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


import java.io.*;

/*
 Class to generate part of "generateclasses"
 */

public class GenerateUtilClasses {

  public GenerateUtilClasses() {

    String file = "ProtegeIntrospector";
    String content = readFile(file + ".txt");

    writeFile("src/jade/addone/beangenerator/Generate" + file + ".java", content);

  }

  public static String readFile(String theFile) {

    StringBuffer bf = new StringBuffer();
    try {

//      InputStream is =  ClassLoader.getSystemResourceAsStream(theFile);
      FileInputStream fis = new FileInputStream(theFile);

      BufferedReader in = new BufferedReader(new InputStreamReader(fis));

      String theLine = "";
      while ( ( (theLine = in.readLine()) != null)) {
        bf.append("  sb.append(\"" + theLine + "\\n\");\n");
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

  public static void main(String[] args) {
    new GenerateUtilClasses();
  }
}