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

package examples.meeting_dsc_example;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.*;
import jade.domain.mobility.*;
import jade.wrapper.*;
import java.io.*;
import java.util.*;

/**
 * @author G. Fortino, F. Rango
 */
public class MeetingParticipant extends Agent {

	protected void setup() {
		try {
			getContentManager().registerLanguage(new SLCodec(),
					FIPANames.ContentLanguage.FIPA_SL0);
			getContentManager()
					.registerOntology(MobilityOntology.getInstance());
			getContentManager().registerOntology(
					JADEManagementOntology.getInstance());
			MeetingParticipantDSCBehaviour b = new MeetingParticipantDSCBehaviour(
					this, "MeetingParticipantDSCBehaviour");
			addBehaviour(b);
			System.out.println(getAID().getName() + " started ...");
		} catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}
	}

	public void send(ELDAEventMSG event) {
		try {
			AID source = event.getSource();
			java.util.List<AID> target = event.getTarget();
			String language = event.getLanguage();
			ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);

			if (source != null) {
				msg.setSender(source);
			}

			if (target != null) {
				for (int i = 0; i < target.size(); i++) {
					msg.addReceiver(target.get(i));
				}
			}

			if (language != null) {
				msg.setLanguage(language);
			}
	
			//encapsulate the ELDAEvent inside the ACLMessage
			msg.setContentObject(event);
	
			send(msg);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void afterMove() {
		getContentManager().registerLanguage(new SLCodec(),
				FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		getContentManager().registerOntology(
				JADEManagementOntology.getInstance());
	}

	protected void afterClone() {
		getContentManager().registerLanguage(new SLCodec(),
				FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		getContentManager().registerOntology(
				JADEManagementOntology.getInstance());
	}
}