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


package jade.gui;

// Import required java classes
import java.util.Vector;

// Import required Jade classes
import jade.backward.Agent;
import jade.core.*;
import jade.core.behaviours.*;

/**
When a program instantiates a GUI, the Java programming language 
starts a new thread, different from the Agent thread.
The Agent thread generally is active because it has its tasks to perform 
and also the GUI thread is active, in respect to the Agent thread, because 
its behaviour depends on the user actions (e.g. pressing a button, using 
the nemu bar,...) and not only on the agent task.
Therefore, an appropriate mechanism is needed to manage the interaction 
between these two active threads.
It is not a good practice allowing one thread to just call the method of 
another thread because of the difference in the thread space.
What should be done, instead is one thread requesting the other to execute a 
method, each thread in its one execution space.
Since its common to have an agent with a GUI, this class is for this purpose.
This class extends the <code>jade.core.Agent </code> class: at the start-up 
it instantiate ad ad-hoc behaviour that manages a queue of 
<code>jade.gui.GuiEvent</code>,event objects that can be received by other threads.  
A thread (in particular a GUI)to notify an event to an Agent should create 
a new Object of type <code>jade.gui.GuiEvent</code>and pass it as a parameter 
to the call of the method <code>postGuiEvent</code> of the
<code>jade.gui.GuiAgent</code> object. Notice that an object of type 
<code>GuiEvent</code> has two mandatory attributes and an optional 
list of parameters that can be added to the event object.
After the method <code>postGuiEvent</code> is called,the agent reacts 
by waking up all its active behaviours, and in particular the one that causes
the Agent thread to execute the method <code>onGuiEvent</code>.

@see jade.core.Agent
@see jade.gui.GuiEvent
@author Giovanni Caire - CSELT S.p.A.
@version $Date: 2002/09/10 16:58:45 $ $Revision: 1.1 $
*/

public abstract class GuiAgent extends Agent
{
	/**
	@serial
	*/
	private Vector guiEventQueue;
	/**
	@serial
	*/
	private Boolean guiEventQueueLock;

	////////////////////////
	// GUI HANDLER BEHAVIOUR
	private class GuiHandlerBehaviour extends SimpleBehaviour
	{
		protected GuiHandlerBehaviour()
		{
			super(GuiAgent.this);
		}

		public void action()
		{
			if (!guiEventQueue.isEmpty())
			{
				GuiEvent ev = null;  				
				synchronized(guiEventQueueLock)
				{
					try
					{
						ev  = (GuiEvent) guiEventQueue.elementAt(0);
						guiEventQueue.removeElementAt(0);
					}
					catch (ArrayIndexOutOfBoundsException ex)
					{
						ex.printStackTrace(); // Should never happen
					}
				}			
				onGuiEvent(ev);
			}
			else
				block();
		}

		public boolean done()
		{
			return(false);
		}
	}

	//////////////
	// CONSTRUCTOR
	public GuiAgent()
	{
		super();
		guiEventQueue = new Vector();
		guiEventQueueLock = new Boolean(true);

		// Add the GUI handler behaviour
		Behaviour b = new GuiHandlerBehaviour();
		addBehaviour(b);
	}

	///////////////////////////////////////////////////////////////
	// PROTECTED METHODS TO POST AND GET A GUI EVENT FROM THE QUEUE
	public void postGuiEvent(GuiEvent e)
	{
		synchronized(guiEventQueueLock)
		{
			guiEventQueue.addElement( (Object) e );
			doWake();
		}
	}

	/////////////////////////////////////////////////////////////////////////
	// METHODS TO POST PREDEFINED EXIT AND CLOSEGUI EVENTS IN GUI EVENT QUEUE
	/*public void postExitEvent(Object g)
	{
		GuiEvent e = new GuiEvent(g, GuiEvent.EXIT);
		postGuiEvent(e);
	}

	public void postCloseGuiEvent(Object g)
	{
		GuiEvent e = new GuiEvent(g, GuiEvent.CLOSEGUI);
		postGuiEvent(e);
	}*/

	///////////////////////////////
	// METHOD TO HANDLE GUI EVENTS
	protected abstract void onGuiEvent(GuiEvent ev);
	
}