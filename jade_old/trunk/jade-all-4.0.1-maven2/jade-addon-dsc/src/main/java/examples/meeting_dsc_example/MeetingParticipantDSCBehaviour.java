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
public class MeetingParticipantDSCBehaviour extends
		DistilledStateChartBehaviour {
	private MeetingParticipant myAgent;
	public DistilledStateChartTransition t1;
	public DistilledStateChartTransition t2;
	public DistilledStateChartTransition t3;
	public PRINCIPALSTATE principalstate;

	public MeetingParticipantDSCBehaviour(MeetingParticipant anAgent,
			String aName) {
		super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
				false);
		this.myAgent = anAgent;
		principalstate = new PRINCIPALSTATE(anAgent, "PRINCIPALSTATE");
		addInitialState(DistilledStateChartTransition.DEEP_HISTORY);
		addState(principalstate);
		setDefaultDeepHistoryEntrance(principalstate);
		principalstate.started.createT1();
		principalstate.appointmentchecked.createT2();
		principalstate.appointmentchecked.createT3();
		addTransition(t1, principalstate.started);
		addTransition(t2, principalstate.appointmentchecked);
		addTransition(t3, principalstate.appointmentchecked);
	}

	private class PRINCIPALSTATE extends DistilledStateChartBehaviour {
		private MeetingParticipant myAgent;
		Hashtable myCalendar = new java.util.Hashtable();
		Appointment currentAppointment;
		public STARTED started;
		public APPOINTMENTCHECKED appointmentchecked;

		public PRINCIPALSTATE(MeetingParticipant anAgent, String aName) {
			super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
					false);
			this.myAgent = anAgent;
			started = new STARTED(anAgent, "STARTED");
			appointmentchecked = new APPOINTMENTCHECKED(anAgent,
					"APPOINTMENTCHECKED");
			addInitialState(started);
			addState(appointmentchecked);
		}

		private String getKey(java.util.Calendar date) {
			return "" + date.get(date.DAY_OF_MONTH) + "/"
					+ date.get(date.MONTH) + "/" + date.get(date.YEAR);
		}

		private class STARTED extends Behaviour {
			private MeetingParticipant myAgent;

			public STARTED(MeetingParticipant anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT1() {
				t1 = new DistilledStateChartTransition("T1",
						principalstate.appointmentchecked) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof Propose)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							checkAppointment((Propose) event.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			private void checkAppointment(ELDAEvent e) {

				System.out.println("AGENT " + myAgent.getName()
						+ ": checking a possible appointment...");
				Propose p = (Propose) e;
				currentAppointment = (Appointment) p.getData();

				if (myCalendar.containsKey(getKey(currentAppointment.getDate()))) {
					java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
					target.add(new AID("MeetingBroker", AID.ISLOCALNAME));
					RejectProposal msg = new RejectProposal(myAgent.getAID(),
							target, null);
					myAgent.send(msg);

					java.util.ArrayList<AID> target2 = new java.util.ArrayList<AID>();
					target2.add(myAgent.getAID());
					Cancel msg2 = new Cancel(myAgent.getAID(), target2, null);
					myAgent.send(msg2);

					System.out
							.println("AGENT "
									+ myAgent.getName()
									+ ": appointment on "
									+ currentAppointment.getDate().getTime()
									+ " isn't possible! sent reject proposal to Meeting Broker and cancel to himself...");
				} else {
					java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
					target.add(new AID("MeetingBroker", AID.ISLOCALNAME));
					AcceptProposal msg = new AcceptProposal(myAgent.getAID(),
							target, null);
					myAgent.send(msg);
					System.out
							.println("AGENT "
									+ myAgent.getName()
									+ ": appointment on "
									+ currentAppointment.getDate().getTime()
									+ " is possible! sent accept proposal to Meeting Broker...");
				}
			}
		}

		private class APPOINTMENTCHECKED extends Behaviour {
			private MeetingParticipant myAgent;

			public APPOINTMENTCHECKED(MeetingParticipant anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT2() {
				t2 = new DistilledStateChartTransition("T2",
						principalstate.started) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof Confirm)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							fixAppointment((Confirm) event.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT3() {
				t3 = new DistilledStateChartTransition("T3",
						principalstate.started) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof Cancel)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
					}
				};
			}

			private void fixAppointment(ELDAEvent e) {
				myCalendar.put(getKey(currentAppointment.getDate()),
						currentAppointment);
				System.out.println("AGENT " + myAgent.getName()
						+ ": appointment on "
						+ currentAppointment.getDate().getTime()
						+ " successfully fixed!");
			}
		}
	}
}