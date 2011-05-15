/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * DummyAgent.java
 * Created on 4 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
//Import required Java classes 
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticCapabilities;

import javax.swing.SwingUtilities;

/**
@author Giovanni Caire - CSELT S.p.A
@version $Date: 2004/08/20 10:05:34 $ $Revision: 2.3 $
*/

public class DummyAgent extends SemanticAgent 
{
    private transient DummyAgentGui myGui;

    String fileName = null;
//    public static void main(String args[]) {
//     DummyAgent d = new DummyAgent();
//     d.setup();
//    }
    public DummyAgent() {
        super(new DummyCapabilities());
    }
    
    public DummyAgent(SemanticCapabilities sc) {
    	super(sc);
    }

    // Extends the Agent setup method
    @Override
	public void setup()
    {
        
        testArgs();
        if (getArguments() != null && getArguments().length > 0) {
            fileName = getArguments()[0].toString();
        }
        super.setup();
        ///////////////////////////////
        // Create and display agent GUI
        myGui = new DummyAgentGui(this);
        myGui.showCorrect();

    
    }

        @Override
		protected void takeDown() {
        disposeGUI();
    }

    public DummyAgentGui getGui()
    {
        return myGui;
    }

  @Override
protected void beforeMove() {
      disposeGUI();
  }

  @Override
protected void afterMove() {
      restoreGUI();
  }

  @Override
protected void afterClone() {
      restoreGUI();
  }

  public void afterLoad() {
      restoreGUI();
  }

  public void beforeFreeze() {
      disposeGUI();
  }

  public void afterThaw() {
      restoreGUI();
  }

  public void beforeReload() {
      disposeGUI();
  }

  public void afterReload() {
      restoreGUI();
  }

    private void restoreGUI() {
    myGui = new DummyAgentGui(this);
    myGui.showCorrect();
    }

    private void disposeGUI() {
    if(myGui != null) {
        final DummyAgentGui gui = myGui;
        myGui = null;
        SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            gui.setVisible(false);
            gui.dispose();
        }
        });
    }
    }

    private void testArgs() {
        Object[] args = getArguments();
        if (args == null) {
            System.err.println("No predefined content file specified");
        } else if ( args.length > 1 ) {
            System.err.println("ERROR : agent should be run like this "
                    +"<agent name>:DummyAgent\\(<file_name>\\)");
            System.err.print("ERROR : current args are :");
            for (int i=0; i<args.length; i++) {System.err.print(args[i]+" ");}
            System.err.println();
            System.exit(1);
        } else {
            System.err.print("setup of DummyAgent(");
            for (int i=0; i<args.length; i++) {System.err.print(args[i]+" ");}
            System.err.println(")");
        }
    } // End of testArgs/0
    
    
    public String getFileName() {
        return fileName;
    }
}

