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


package jade.tools.persistence;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;


/**
 *
 * @author  Giovanni Rimassa - FRAMeTech
 */
class ExitAction extends AbstractAction {

    /** Creates a new instance of RemoveRepositoryAction */
    public ExitAction(PersistenceManagerGUI gui) {

        myGUI = gui;

        // Fill the required properties
        putValue(NAME, "Exit Application");
        putValue(SHORT_DESCRIPTION, "Exit the Persistence Management Console");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        myGUI.shutdown();
    }
    
    private PersistenceManagerGUI myGUI;
    
}
