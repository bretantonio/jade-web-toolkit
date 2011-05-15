/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

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

package jsademos.emotionalagent;


import jade.semantics.ext.emotion.EmotionalCapabilities;
import jade.semantics.ext.emotion.behaviours.EmotionTemporalDecreaserBehaviour;
import jade.semantics.ext.emotion.tools.InterfaceTexteEmotion;
import jade.semantics.interpreter.SemanticAgent;

import java.io.FileReader;

/** Représente un agent JSA émotionnel*/
public class MyEmotionalAgent extends SemanticAgent {

	/**
	 * 
	 *
	 */
	public MyEmotionalAgent() {
		setSemanticCapabilities(new EmotionalCapabilities());
	}
	
	public void setup() {
		super.setup();
		
		InterfaceTexteEmotion textGui = new InterfaceTexteEmotion();
		((EmotionalCapabilities)getSemanticCapabilities()).getEmotionalState().addObserver(textGui);
		
		
//		addBehaviour(new WakerBehaviour(this, 1000) {
//			protected void onWake() {
				try {
					getSemanticCapabilities().interpret(new FileReader(getArguments()[0].toString()));
				}
				catch(Exception e) {e.printStackTrace();}
//			}
//		});

		/** Thread de mise à jour des émotions : toute les 2 minutes*/ 
		addBehaviour(new EmotionTemporalDecreaserBehaviour(this, 120000));
	}
}
