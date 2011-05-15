/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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

package test.security.messaging;

import test.common.*;
import jade.core.Agent;

/**
 * @author Jerome Picault - Motorola Labs
 * @author Nicolas Lhuillier - Motorola Labs
 */
public class RemoteSecurityTesterAgent extends TesterAgent {
	
  public static final String MTP_KEY = "MTP";
  public static String MTP;
  public static final String CLASSPATH_KEY = "classpath";
  public static String CLASSPATH;
  
  protected TestGroup getTestGroup() {		
		TestGroup tg = new TestGroup("test/security/messaging/remoteSecurityTestsList.xml") {
        
        public void initialize(Agent a) throws TestException {
          MTP = (String)getArgument(MTP_KEY);
          CLASSPATH = (String)getArgument(CLASSPATH_KEY);
        }
      };
    tg.specifyArgument(MTP_KEY,    "MTP class (for \"remote\" config.) ", "");
    tg.specifyArgument(CLASSPATH_KEY, "Additional classpath for MTP","");
    return tg;
  }

}
