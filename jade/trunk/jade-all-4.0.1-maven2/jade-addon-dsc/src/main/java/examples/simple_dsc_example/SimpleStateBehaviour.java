/*****************************************************************
"DistilledStateChartBehaviour" is a work based on the library "HSMBehaviour"
(authors: G. Caire, R. Delucchi, M. Griss, R. Kessler, B. Remick).
Changed files: "HSMBehaviour.java", "HSMEvent.java", "HSMPerformativeTransition.java",
"HSMTemplateTransition.java", "HSMTransition.java".
Last change date: 18/06/2010
Copyright (C) 2010 G. Fortino, F. Rango

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation;
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*****************************************************************/

package examples.simple_dsc_example;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/**
 * @author G. Fortino, F. Rango
 */
public class SimpleStateBehaviour extends Behaviour {

	private Agent myAgent;

	public SimpleStateBehaviour(Agent anAgent, String aName) {
		super(anAgent);
		this.myAgent = anAgent;
		setBehaviourName(aName);
	}

	public void action() {}

	public boolean done() {
		return true;
	}

}

