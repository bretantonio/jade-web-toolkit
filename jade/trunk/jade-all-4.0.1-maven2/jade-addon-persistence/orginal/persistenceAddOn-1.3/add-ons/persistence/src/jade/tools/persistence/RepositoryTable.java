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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;

import jade.core.persistence.SavedAgent;
import jade.core.persistence.SavedContainer;
import jade.core.persistence.FrozenAgent;

import jade.util.leap.List;
import jade.util.leap.LinkedList;
import jade.util.leap.Map;
import jade.util.leap.HashMap;


/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
class RepositoryTable {

    public static class ModelItem {

        public static final Long FROZEN_AGENT = new Long(1);
        public static final Long SAVED_AGENT = new Long(2);
        public static final Long SAVED_CONTAINER = new Long(3);


        public ModelItem(String n, Long k) {
            myName = n;
            myKind = k;
        }

        public void setName(String n) {
            myName = n;
        }

        public String getName() {
            return myName;
        }

        public void setKind(Long k) {
            myKind = k;
        }
        
        public Long getKind() {
            return myKind;
        }

        private String myName;
        private Long myKind;

    } // End of ModelItem class

    private static Map icons;
    static {
        icons = new HashMap();
        icons.put(RepositoryTable.ModelItem.FROZEN_AGENT, new ImageIcon(RepositoryTable.class.getResource("/jade/tools/persistence/images/freezeagent.gif")));
        icons.put(RepositoryTable.ModelItem.SAVED_AGENT, new ImageIcon(RepositoryTable.class.getResource("/jade/tools/persistence/images/saveagent.gif")));
        icons.put(RepositoryTable.ModelItem.SAVED_CONTAINER, new ImageIcon(RepositoryTable.class.getResource("/jade/tools/persistence/images/savecontainer.gif")));
    }

    /** Creates a new instance of RepositoryTable */
    public RepositoryTable() {


        myModel = new DefaultListModel();

        myView = new JList(myModel);
        myView.setCellRenderer(new DefaultListCellRenderer() {
            
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                // Set the base attributes
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                RepositoryTable.ModelItem item = (RepositoryTable.ModelItem)value;
                setIcon((ImageIcon)icons.get(item.getKind()));
                setText(item.getName());

                return this;
            }

        });
    }

    public JList getView() {
        return myView;
    }

    public ListModel getModel() {
        return myModel;
    }

    public void setData(String[] savedAgents, String[] frozenAgents, String[] savedContainers) {
        myModel.clear();
        for(int i = 0; i < savedAgents.length; i++) {
            myModel.addElement(new RepositoryTable.ModelItem(savedAgents[i], RepositoryTable.ModelItem.SAVED_AGENT));
        }

        for(int i = 0; i < frozenAgents.length; i++) {
            myModel.addElement(new RepositoryTable.ModelItem(frozenAgents[i], RepositoryTable.ModelItem.FROZEN_AGENT));
        }

        for(int i = 0; i < savedContainers.length; i++) {
            myModel.addElement(new RepositoryTable.ModelItem(savedContainers[i], RepositoryTable.ModelItem.SAVED_CONTAINER));
        }
    }

    public String[] getSelectedSavedAgents() {
        Object[] objs = myView.getSelectedValues();
        List savedAgents = new LinkedList();
        for(int i = 0; i < objs.length; i++) {
            RepositoryTable.ModelItem item = (RepositoryTable.ModelItem)objs[i];
            if(item.getKind() == RepositoryTable.ModelItem.SAVED_AGENT) {
                savedAgents.add(item.getName());
            }
        }
        String[] result = new String[savedAgents.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = (String)savedAgents.get(i);
        }

        return result;
    }
    
    public String[] getSelectedFrozenAgents() {
        Object[] objs = myView.getSelectedValues();
        List frozenAgents = new LinkedList();
        for(int i = 0; i < objs.length; i++) {
            RepositoryTable.ModelItem item = (RepositoryTable.ModelItem)objs[i];
            if(item.getKind() == RepositoryTable.ModelItem.FROZEN_AGENT) {
                frozenAgents.add(item.getName());
            }
        }
        String[] result = new String[frozenAgents.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = (String)frozenAgents.get(i);
        }

        return result;
    }

    public String[] getSelectedSavedContainers() {
        Object[] objs = myView.getSelectedValues();
        List savedContainers = new LinkedList();
        for(int i = 0; i < objs.length; i++) {
            RepositoryTable.ModelItem item = (RepositoryTable.ModelItem)objs[i];
            if(item.getKind() == RepositoryTable.ModelItem.SAVED_CONTAINER) {
                savedContainers.add(item.getName());
            }
        }
        String[] result = new String[savedContainers.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = (String)savedContainers.get(i);
        }

        return result;
    }


    private JList myView;
    private DefaultListModel myModel;
}
