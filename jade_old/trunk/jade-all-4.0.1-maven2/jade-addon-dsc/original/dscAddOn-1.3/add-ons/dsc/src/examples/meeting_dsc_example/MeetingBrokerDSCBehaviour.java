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
public class MeetingBrokerDSCBehaviour extends DistilledStateChartBehaviour {
	private MeetingBroker myAgent;
	int contResponses;
	int contRequestsToMeetingRequester;
	WakerBehaviour timer;
	public DistilledStateChartTransition t1;
	public DistilledStateChartTransition t2;
	public DistilledStateChartTransition t3;
	public DistilledStateChartTransition t4;
	public DistilledStateChartTransition t5;
	public DistilledStateChartTransition t6;
	public DistilledStateChartTransition t7;
	public DistilledStateChartTransition t8;
	public DistilledStateChartTransition t9;
	public DistilledStateChartTransition t10;
	public DistilledStateChartTransition t11;
	public DistilledStateChartTransition t12;
	public DistilledStateChartTransition t13;
	public DistilledStateChartTransition t14;
	public DistilledStateChartTransition t15;
	public DistilledStateChartTransition t16;
	public NEGOTIATION negotiation;
	public ARRANGE arrange;

	public MeetingBrokerDSCBehaviour(MeetingBroker anAgent, String aName) {
		super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
				false);
		this.myAgent = anAgent;
		negotiation = new NEGOTIATION(anAgent, "NEGOTIATION");
		arrange = new ARRANGE(anAgent, "ARRANGE");
		addInitialState(DistilledStateChartTransition.DEEP_HISTORY);
		addState(negotiation);
		addState(arrange);
		setDefaultDeepHistoryEntrance(negotiation);
		negotiation.createT1();
		negotiation.started.createT2();
		negotiation.proposesent.createT3();
		arrange.createT4();
		arrange.started2.createT5();
		arrange.started2.createT6();
		arrange.started2.createT7();
		arrange.coordinated.createT8();
		arrange.coordinated.createT9();
		arrange.coordinated.createT10();
		arrange.excluded.createT11();
		arrange.excluded.createT12();
		arrange.excluded.createT13();
		arrange.timeout.createT14();
		arrange.timeout.createT15();
		arrange.timeout.createT16();
		addTransition(t1, negotiation);
		addTransition(t2, negotiation.started);
		addTransition(t3, negotiation.proposesent);
		addTransition(t4, arrange);
		addTransition(t5, arrange.started2);
		addTransition(t6, arrange.started2);
		addTransition(t7, arrange.started2);
		addTransition(t8, arrange.coordinated);
		addTransition(t9, arrange.coordinated);
		addTransition(t10, arrange.coordinated);
		addTransition(t11, arrange.excluded);
		addTransition(t12, arrange.excluded);
		addTransition(t13, arrange.excluded);
		addTransition(t14, arrange.timeout);
		addTransition(t15, arrange.timeout);
		addTransition(t16, arrange.timeout);
	}

	private class NEGOTIATION extends DistilledStateChartBehaviour {
		private MeetingBroker myAgent;
		public STARTED started;
		public PROPOSESENT proposesent;

		public NEGOTIATION(MeetingBroker anAgent, String aName) {
			super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
					false);
			this.myAgent = anAgent;
			started = new STARTED(anAgent, "STARTED");
			proposesent = new PROPOSESENT(anAgent, "PROPOSESENT");
			addInitialState(started);
			addState(proposesent);
		}

		public void createT1() {
			t1 = new DistilledStateChartTransition("T1", arrange,
					DistilledStateChartTransition.SHALLOW_HISTORY) {
				public boolean trigger(Behaviour sourceState, ACLMessage event) {
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
						initializeTimer((Propose) event.getContentObject());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}

		private void initializeTimer(ELDAEvent e) {
			if (timer != null) {
				myAgent.removeBehaviour(timer);
			}

			timer = new WakerBehaviour(myAgent, 30000) { // TIMEOUT = 30 s
				protected void onWake() {
					java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
					target.add(myAgent.getAID());
					TimeOut msg = new TimeOut(myAgent.getAID(), target, null);
					((MeetingBroker) myAgent).send(msg);
					System.out
							.println("MEETING BROKER: timer elapsed! timeout event sent successfully!");
				}
			};
			myAgent.addBehaviour(timer);

			System.out.println("MEETING BROKER: timer initialized");
		}

		private void sendPropose(ELDAEvent e) {

			System.out.println("MEETING BROKER: request arrived...");
			Request r = (Request) e;
			Appointment app = (Appointment) r.getData();
			contResponses = app.getParticipantsList().size();

			// send propose to himself and all the participants
			java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
			target.add(myAgent.getAID());
			for (int i = 0; i < app.getParticipantsList().size(); i++) {
				target.add(app.getParticipantsList().get(i));
			}
			Propose msg = new Propose(myAgent.getAID(), target, app);
			myAgent.send(msg);

			System.out
					.println("MEETING BROKER: propose sent successfully to himself and all the participants!");
		}

		private class STARTED extends Behaviour {
			private MeetingBroker myAgent;

			public STARTED(MeetingBroker anAgent, String aName) {
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
						negotiation.proposesent) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof Request)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendPropose((Request) event.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
		}

		private class PROPOSESENT extends Behaviour {
			private MeetingBroker myAgent;

			public PROPOSESENT(MeetingBroker anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT3() {
				t3 = new DistilledStateChartTransition("T3",
						negotiation.proposesent) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof Request)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendPropose((Request) event.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
		}
	}

	private class ARRANGE extends DistilledStateChartBehaviour {
		private MeetingBroker myAgent;
		ArrayList<AID> acceptedParticipants;
		public STARTED2 started2;
		public COORDINATED coordinated;
		public EXCLUDED excluded;
		public TIMEOUT timeout;

		public ARRANGE(MeetingBroker anAgent, String aName) {
			super(anAgent, aName, MessageTemplate.MatchLanguage(ELDAEvent.IN),
					false);
			this.myAgent = anAgent;
			started2 = new STARTED2(anAgent, "STARTED2");
			coordinated = new COORDINATED(anAgent, "COORDINATED");
			excluded = new EXCLUDED(anAgent, "EXCLUDED");
			timeout = new TIMEOUT(anAgent, "TIMEOUT");
			addInitialState(started2);
			addState(coordinated);
			addState(excluded);
			addState(timeout);
			setDefaultShallowHistoryEntrance(started2);
		}

		public void defaultShallowHistoryEntranceAction() {
			init(null);
		}

		public void createT4() {
			t4 = new DistilledStateChartTransition("T4",
					negotiation.proposesent) {
				public boolean trigger(Behaviour sourceState, ACLMessage event) {
					java.io.Serializable content = null;
					try {
						content = event.getContentObject();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if ((content != null)
							&& (content instanceof ArrangementDone)) {
						return true;
					}
					return false;
				}

				public void action(ACLMessage event) {
					try {
						completeArrangement((ArrangementDone) event
								.getContentObject());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}

		private void completeArrangement(ELDAEvent e) {
			if(acceptedParticipants.size() >= 2){
				//meeting completato

				//send CONFIRM to accepted participants and MeetingRequester
				java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
				target.addAll(acceptedParticipants);
				target.add(new AID("MeetingRequester", AID.ISLOCALNAME));
				Confirm msg = new Confirm(myAgent.getAID(), target, null);
				myAgent.send(msg);
				System.out.println("MEETING BROKER: meeting completed! sent confirm to accepted participants and Meeting Requester...");

				//azzeriamo le variabili per il prossimo meeting
				contRequestsToMeetingRequester = 0;
				acceptedParticipants = new java.util.ArrayList<AID>();
			}
			else{
				if(contRequestsToMeetingRequester > 2){
					//annulliamo il meeting

					//send CANCEL to accepted participants and MeetingRequester
					java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
					target.addAll(acceptedParticipants);
					target.add(new AID("MeetingRequester", AID.ISLOCALNAME));
					Cancel msg = new Cancel(myAgent.getAID(), target, null);
					myAgent.send(msg);
					System.out.println("MEETING BROKER: meeting cancelled! sent cancel to accepted participants and Meeting Requester...");

					//azzeriamo le variabili per il prossimo meeting
					contRequestsToMeetingRequester = 0;
					acceptedParticipants = new java.util.ArrayList<AID>();
				}
				else{
					//effettuiamo una richiesta di nuovi partecipanti
					//al MeetingRequester

					//ask for request to MeetingRequester
					contRequestsToMeetingRequester++;
					java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
					target.add(new AID("MeetingRequester", AID.ISLOCALNAME));
					AskForRequest msg = new AskForRequest(myAgent.getAID(), target, null);
					myAgent.send(msg);
					System.out.println("MEETING BROKER: ask for request to Meeting Requester...");
				}
			}
		}

		private void sendArrangementDone(ELDAEvent e) {
			java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
			target.add(myAgent.getAID());
			ArrangementDone msg = new ArrangementDone(myAgent.getAID(), target,
					null);
			myAgent.send(msg);
			System.out
					.println("MEETING BROKER: sent arrangement done event...");
		}

		private void excludeParticipant(ELDAEvent e) {
			// exclude current participant
			System.out.println("MEETING BROKER: agent "
					+ e.getSource().getName() + " excluded");
			contResponses--;
			if (contResponses == 0) {
				java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
				target.add(myAgent.getAID());
				ArrangementDone msg = new ArrangementDone(myAgent.getAID(),
						target, null);
				myAgent.send(msg);
				System.out
						.println("MEETING BROKER: sent arrangement done event...");
			}
		}

		private void init(ELDAEvent e) {
			contRequestsToMeetingRequester = 0;
			acceptedParticipants = new java.util.ArrayList<AID>();
		}

		private void acceptParticipant(ELDAEvent e) {
			// accept current participant
			acceptedParticipants.add(e.getSource());
			contResponses--;
			System.out.println("MEETING BROKER: agent "
					+ e.getSource().getName()
					+ " added successfully to participants!");
			if (contResponses == 0) {
				java.util.ArrayList<AID> target = new java.util.ArrayList<AID>();
				target.add(myAgent.getAID());
				ArrangementDone msg = new ArrangementDone(myAgent.getAID(),
						target, null);
				myAgent.send(msg);
				System.out
						.println("MEETING BROKER: sent arrangement done event...");
			}
		}

		private class STARTED2 extends Behaviour {
			private MeetingBroker myAgent;

			public STARTED2(MeetingBroker anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT5() {
				t5 = new DistilledStateChartTransition("T5",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof AcceptProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							acceptParticipant((AcceptProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT6() {
				t6 = new DistilledStateChartTransition("T6", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof RejectProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							excludeParticipant((RejectProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT7() {
				t7 = new DistilledStateChartTransition("T7", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof TimeOut)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendArrangementDone((TimeOut) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
		}

		private class COORDINATED extends Behaviour {
			private MeetingBroker myAgent;

			public COORDINATED(MeetingBroker anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT8() {
				t8 = new DistilledStateChartTransition("T8",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof AcceptProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							acceptParticipant((AcceptProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT9() {
				t9 = new DistilledStateChartTransition("T9", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof RejectProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							excludeParticipant((RejectProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT10() {
				t10 = new DistilledStateChartTransition("T10", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof TimeOut)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendArrangementDone((TimeOut) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
		}

		private class EXCLUDED extends Behaviour {
			private MeetingBroker myAgent;

			public EXCLUDED(MeetingBroker anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT11() {
				t11 = new DistilledStateChartTransition("T11", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof RejectProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							excludeParticipant((RejectProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT12() {
				t12 = new DistilledStateChartTransition("T12",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof AcceptProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							acceptParticipant((AcceptProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT13() {
				t13 = new DistilledStateChartTransition("T13", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof TimeOut)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendArrangementDone((TimeOut) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
		}

		private class TIMEOUT extends Behaviour {
			private MeetingBroker myAgent;

			public TIMEOUT(MeetingBroker anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT14() {
				t14 = new DistilledStateChartTransition("T14",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof AcceptProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							acceptParticipant((AcceptProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT15() {
				t15 = new DistilledStateChartTransition("T15", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null)
								&& (content instanceof RejectProposal)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							excludeParticipant((RejectProposal) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}

			public void createT16() {
				t16 = new DistilledStateChartTransition("T16", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						java.io.Serializable content = null;
						try {
							content = event.getContentObject();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ((content != null) && (content instanceof TimeOut)) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						try {
							sendArrangementDone((TimeOut) event
									.getContentObject());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
		}
	}
}