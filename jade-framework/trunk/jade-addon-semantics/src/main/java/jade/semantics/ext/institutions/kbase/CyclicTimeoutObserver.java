/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France Télécom

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



package jade.semantics.ext.institutions.kbase;

/*
 * Created by Carole Adam, 21 April 2008
 */

import jade.semantics.interpreter.SemanticInterpreterBehaviour;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.kbase.observers.ObserverAdapter;
import jade.semantics.lang.sl.grammar.Formula;


/**
 * This special observer is used when an observer can wait several 
 * periods of timeout (with intermediate behaviour when they end)
 * before doing the final timeout behaviour. Actually it gives several
 * chances for the observed formula to become true.
 * 
 * Abstract methods to be overwritten:
 * 	  - intermediateTimeout() : method triggered at the end of each 
 * 	  intermediate timeout period
 *    - finalTimeout() : method triggered at the end of the final
 *    timeout period
 * 
 * Attributes (configuration of observer) :
 *    - numberOfTimeouts
 *    - durationOfTimeouts
 * 
 * @author wdvh2120
 * @version 1.0 date 21 April 2008
 */
public class CyclicTimeoutObserver extends ObserverAdapter {

	/**************
	 * ATTRIBUTES *
	 **************/
	
	// number of periods before final timeout behaviour
	private int numberOfTimeouts;
	
	// duration in milliseconds between each call of intermediateTimeout() method
	private Long durationOfTimeouts;

	// interpreter useful in cases when the behaviour is to interpret a formula
	private SemanticInterpreterBehaviour mySIB;
	
	
	/****************
	 * CONSTRUCTORS *
	 ****************/
	
	public CyclicTimeoutObserver(KBase kbase, Formula observedFormula, long timeout,int counter, SemanticInterpreterBehaviour sib) {
		super(kbase, observedFormula, timeout);
		numberOfTimeouts = counter;
		durationOfTimeouts = timeout;
		mySIB = sib;
	}
	
	// same with null sib when useless - warning NullPointerExceptions if access to sib ...
	public CyclicTimeoutObserver(KBase kbase, Formula observedFormula, long timeout,int counter) {
		this(kbase,observedFormula,timeout,counter,null);
	}
	
	
	/*********************
	 * *** ACCESSORS *** *
	 *********************/

	/**
	 * Method to get the interpreter behaviour associated to this observer
	 * @return the interpreter attribute of this observer
	 */
	public SemanticInterpreterBehaviour getInterpreter() {
		if (mySIB != null) {
			return mySIB;
		}
		System.err.println("SIB is null in CyclicTimeoutObserver !!");
		return null;
	}
	
	/**********************************
	 * STANDARD METHODS FOR OBSERVERS *
	 **********************************/
	
	// action when the waited formula is finally observed
	@Override
	public void action(QueryResult value) {
		// disable timeout and remove this observer
		disableTimeout();
		getMyKBase().removeObserver(this);
		// by default do nothing
		// children methods should call super() and then specify a specific behaviour
	}
	
	
	@Override
	public final void timeout() {
		//super.timeout();
		if (isEnabledTimeout()) {
			if (numberOfTimeouts >0) {
				anycaseTimeout();
				intermediateTimeout();
				// launch the observer again with decreased counter
				Observer obs = 
					new CyclicTimeoutObserver(getMyKBase(),getObservedFormula(),durationOfTimeouts,numberOfTimeouts-1,mySIB);
				getMyKBase().addObserver(obs);
				obs.update(null);
			}
			else {
				anycaseTimeout();
				finalTimeout();
				getMyKBase().removeObserver(this);
			}
		}
	}
	
	/************************************
	 * ******* ABSTRACT METHODS ******* *
	 * to be overwritten  in subclasses *
	 ************************************/
	
	public void intermediateTimeout() {
		System.err.println("intermediate timeout");
	}

	public void finalTimeout() {
		System.err.println("final timeout");
		// remove the observer and disable its timeout
		disableTimeout();
		getMyKBase().removeObserver(this);
	}

	// to specify behaviour that should be performed anyway
	// be the timeout intermediate or final
	public void anycaseTimeout() {
		System.err.println("anycase timeout");
	}
	
}
