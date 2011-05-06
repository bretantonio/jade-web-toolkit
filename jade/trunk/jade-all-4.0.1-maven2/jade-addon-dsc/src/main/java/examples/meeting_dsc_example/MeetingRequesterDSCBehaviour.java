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
import javax.swing.JOptionPane;

/**
 * @author G. Fortino, F. Rango
 */
public class MeetingRequesterDSCBehaviour extends DistilledStateChartBehaviour {
	private MeetingRequester myAgent;
	public DistilledStateChartTransition t1;
	public DistilledStateChartTransition t2;
	public DistilledStateChartTransition t3;
	public PRINCIPALSTATE principalstate;

	public MeetingRequesterDSCBehaviour(MeetingRequester anAgent, String aName) {
		super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
				false);
		this.myAgent = anAgent;
		principalstate = new PRINCIPALSTATE(anAgent, "PRINCIPALSTATE");
		addInitialState(DistilledStateChartTransition.DEEP_HISTORY);
		addState(principalstate);
		setDefaultDeepHistoryEntrance(principalstate);
		principalstate.started.createT1();
		principalstate.started.createT2();
		principalstate.started.createT3();
		addTransition(t1, principalstate.started);
		addTransition(t2, principalstate.started);
		addTransition(t3, principalstate.started);
	}

	private class PRINCIPALSTATE extends DistilledStateChartBehaviour {
		private MeetingRequester myAgent;
		Appointment currentAppointment;
		public STARTED started;

		public PRINCIPALSTATE(MeetingRequester anAgent, String aName) {
			super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
					false);
			this.myAgent = anAgent;
			started = new STARTED(anAgent, "STARTED");
			addInitialState(started);
		}

		public void initialAction() {
			sendRequest(null);
		}

		private void sendRequest(ELDAEvent e) {

			System.out.println("MEETING REQUESTER: sending a request...");

			if (currentAppointment == null) {
				// get appointment description
				String description = JOptionPane.showInputDialog(null,
						"Appointment Description:",
						"Insert Appointment Description",
						JOptionPane.INFORMATION_MESSAGE);

				// get appointment date
				String d = JOptionPane.showInputDialog(null,
						"Appointment Date (dd/mm/yyyy):",
						"Insert Appointment Date",
						JOptionPane.INFORMATION_MESSAGE);
				StringTokenizer st = new StringTokenizer(d, "/");
				int day = Integer.parseInt(st.nextToken());
				int month = Integer.parseInt(st.nextToken());
				int year = Integer.parseInt(st.nextToken());
				Calendar date = Calendar.getInstance();
				date.set(Calendar.DAY_OF_MONTH, day);
				date.set(Calendar.MONTH, month - 1);
				date.set(Calendar.YEAR, year);

				// get participants list
				String numberOfParticipants = JOptionPane.showInputDialog(null,
						"Number of Participants:",
						"Insert Number of Participants",
						JOptionPane.INFORMATION_MESSAGE);
				int n = Integer.parseInt(numberOfParticipants);
				java.util.ArrayList<AID> participantsList = new java.util.ArrayList<AID>();
				for (int i = 1; i <= n; i++) {
					String nickname = JOptionPane.showInputDialog(null,
							"Nickname of Participant Agent " + i + ":",
							"Insert Nickname of Participant Agent " + i,
							JOptionPane.INFORMATION_MESSAGE);
					participantsList.add(new AID(nickname, AID.ISLOCALNAME));
				}

				// send request
				currentAppointment = new Appointment(participantsList, date,
						description);
				java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
				target.add(new AID("MeetingBroker", AID.ISLOCALNAME));
				Request msg = new Request(myAgent.getAID(), target,
						currentAppointment);
				myAgent.send(msg);
			} else {
				// get new participants list
				String numberOfParticipants = JOptionPane.showInputDialog(null,
						"Number of New Participants:",
						"Insert Number of New Participants",
						JOptionPane.INFORMATION_MESSAGE);
				int n = Integer.parseInt(numberOfParticipants);
				java.util.ArrayList<AID> participantsList = new java.util.ArrayList<AID>();
				for (int i = 1; i <= n; i++) {
					String nickname = JOptionPane.showInputDialog(null,
							"Nickname of New Participant Agent " + i + ":",
							"Insert Nickname of New Participant Agent " + i,
							JOptionPane.INFORMATION_MESSAGE);
					participantsList.add(new AID(nickname, AID.ISLOCALNAME));
				}

				// send request
				currentAppointment = new Appointment(participantsList,
						currentAppointment.getDate(), currentAppointment
								.getDescription());
				java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
				target.add(new AID("MeetingBroker", AID.ISLOCALNAME));
				Request msg = new Request(myAgent.getAID(), target,
						currentAppointment);
				myAgent.send(msg);
			}

			System.out
					.println("MEETING REQUESTER: request sent successfully for appointment on "
							+ currentAppointment.getDate().getTime());
		}

		private class STARTED extends Behaviour {
			private MeetingRequester myAgent;

			public STARTED(MeetingRequester anAgent, String aName) {
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
						principalstate.started) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof AskForRequest)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendRequest((AskForRequest) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
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
						if ((content != null) && (content instanceof Cancel)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							meetingCanceled((Cancel) event.getContentObject());
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
						if ((content != null) && (content instanceof Confirm)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							meetingDone((Confirm) event.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			private void meetingCanceled(ELDAEvent e) {
				System.out.println("MEETING REQUESTER: meeting canceled...");
			}

			private void meetingDone(ELDAEvent e) {
				System.out.println("MEETING REQUESTER: meeting done...");
			}
		}
	}
}