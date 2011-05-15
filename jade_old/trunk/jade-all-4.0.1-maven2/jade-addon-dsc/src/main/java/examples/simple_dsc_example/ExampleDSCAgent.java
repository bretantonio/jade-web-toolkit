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

import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.*;

/**
 * @author G. Fortino, F. Rango
 */
public class ExampleDSCAgent extends Agent {

	protected void setup() {
		try {

			System.out.println(getAID().getName() + " started...");
			getContentManager().registerLanguage(new SLCodec(),
					FIPANames.ContentLanguage.FIPA_SL0);
			DFService.register(this, getDFAgentDescription());

			// 1. Create composite states, eventually indicating the initial action
			DistilledStateChartBehaviour adsc = new DistilledStateChartBehaviour(this, "ADSC", MessageTemplate.MatchConversationId("DistilledStateChartBehaviour"), false){
				public void initialAction(){
					System.out.println("ENTER ADSC");
				}
			};
			DistilledStateChartBehaviour dsc1 = new DistilledStateChartBehaviour(this, "DSC1", MessageTemplate.MatchConversationId("DistilledStateChartBehaviour"), false){
				public void initialAction(){
					System.out.println("ENTER DSC1");
				}
			};
			DistilledStateChartBehaviour dsc2 = new DistilledStateChartBehaviour(this, "DSC2", MessageTemplate.MatchConversationId("DistilledStateChartBehaviour"), false);

			// 2. Create "empty" simple states
			Behaviour s1 = new SimpleStateBehaviour(this, "S1");
			Behaviour s2 = new SimpleStateBehaviour(this, "S2");
			Behaviour s3 = new SimpleStateBehaviour(this, "S3");
			Behaviour s4 = new SimpleStateBehaviour(this, "S4");

			// 3. Add sub-states and set the "Default Deep/Shallow History Entrance"
			adsc.addInitialState(dsc1);
			adsc.addState(dsc2);
			dsc1.addInitialState(s1);
			dsc1.addState(s2);
			dsc1.setDefaultDeepHistoryEntrance(s1);
			dsc2.addInitialState(DistilledStateChartTransition.DEEP_HISTORY);
			dsc2.addState(s3);
			dsc2.addState(s4);
			dsc2.setDefaultDeepHistoryEntrance(s3);
			dsc2.setDefaultShallowHistoryEntrance(s4);

			// 4. Create transitions
			DistilledStateChartTransition t12 = new DistilledStateChartTransition("T12", s2){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("T12");
				}
			};
			DistilledStateChartTransition t21 = new DistilledStateChartTransition("T21", s1){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("T21");
				}
			};
			DistilledStateChartTransition t34 = new DistilledStateChartTransition("T34", s4){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("T34");
				}
			};
			DistilledStateChartTransition t43 = new DistilledStateChartTransition("T43", s3){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("T43");
				}
			};
			DistilledStateChartTransition td1d2 = new DistilledStateChartTransition("TD1D2", dsc2){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.AGREE) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("TD1D2");
				}
			};
			DistilledStateChartTransition th21 = new DistilledStateChartTransition("TH21", dsc1, DistilledStateChartTransition.DEEP_HISTORY){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.CONFIRM) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("TH21");
				}
			};
			DistilledStateChartTransition th12 = new DistilledStateChartTransition("TH12", dsc2, DistilledStateChartTransition.SHALLOW_HISTORY){
				public boolean trigger(Behaviour source, ACLMessage msg) {
					if (msg != null && msg.getPerformative() == ACLMessage.CONFIRM) {
						return true;
					}
					return false;
				}
				public void action(ACLMessage msg){
					System.out.println("TH12");
				}
			};

			// 5. Add transitions
			adsc.addTransition(t12, s1);
			adsc.addTransition(t21, s2);
			adsc.addTransition(t34, s3);
			adsc.addTransition(t43, s4);
			adsc.addTransition(td1d2, dsc1);
			adsc.addTransition(th21, dsc2);
			adsc.addTransition(th12, dsc1);

			// 6. Create the root according with the DSC template
			DistilledStateChartBehaviour root = DistilledStateChartBehaviour.createRootForDSCTemplate(adsc);

			// 7. Activate the root
			addBehaviour(root);
		}
		catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}
	}

	private DFAgentDescription getDFAgentDescription() {
		ServiceDescription sd = new ServiceDescription();
		sd.setName("x");
		sd.setType("x");
		sd.setOwnership(getLocalName());

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);
		dfd.addProtocols("fipa-request fipa-Contract-Net");
		return dfd;
	}

}
