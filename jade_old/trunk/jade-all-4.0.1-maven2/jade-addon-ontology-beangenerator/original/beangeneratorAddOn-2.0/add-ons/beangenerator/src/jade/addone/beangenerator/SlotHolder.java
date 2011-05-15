/******************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2002 TILAB S.p.A.
 *
 * This file is donated by Acklin B.V. to the JADE project.
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

/*
 *  @author     Jamie Lawrence - Media Lab Europe
 *  @created    November 14, 2002
 */
public class SlotHolder {

  public boolean equals(Object o) {
    if (o instanceof SlotHolder) {
      SlotHolder other = (SlotHolder) o;
      if (other.className.equalsIgnoreCase(className) && other.slotName.equalsIgnoreCase(slotName)) {
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    int retValue;

    retValue = new String(className + "_" + slotName).toLowerCase().hashCode();
    return retValue;
  }

  public SlotHolder(String className, String slotName) {
    this.className = className;
    this.slotName = slotName;
  }
  public String className;
  public String slotName;
}
