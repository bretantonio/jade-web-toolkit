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
import java.util.Map;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.*;

import jade.util.leap.Iterator;
import jade.util.leap.List;

/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
class RepositoryTree {

    public interface ModelItem {
        String getName();

    }


    public class PlatformNode implements ModelItem {

        public PlatformNode(String name) {
            myName = name;
        }
        
        public String getName() {
            return myName;
        }
        
        private String myName;
        
    } // End of PlatformNode class


    public class ContainerNode implements ModelItem {

        public ContainerNode(String n) {
            myName = n;
        }
        
        public String getName() {
            return myName;
        }
        
        private String myName;
        
    } // End of ContainerNode class


    public class AgentNode implements ModelItem {
        
        public AgentNode(String n) {
            myName = n;
        }
        
        public String getName() {
            return myName;
        }
        
        private String myName;
        
    } // End of AgentNode class


    public class RepositoryNode implements ModelItem {

        public RepositoryNode(String n) {
            myName = n;
        }

        public String getName() {
            return myName;
        }

        private String myName;

    } // End of RepositoryNode class


    public class SavedAgentNode implements ModelItem {

        public SavedAgentNode(String n) {
            myName = n;
        }

        public String getName() {
            return myName;
        }
        
        private String myName;
        
    } // End of SavedAgentNode class


    public class FrozenAgentNode implements ModelItem {

        public FrozenAgentNode(String n) {
            myName = n;
        }

        public String getName() {
            return myName;
        }

        private String myName;

    } // End of FrozenAgentNode class


    public class SavedContainerNode implements ModelItem {

        public SavedContainerNode(String n) {
            myName = n;
        }

        public String getName() {
            return myName;
        }

        private String myName;

    } // End of SavedContainerNode


    private static class Renderer extends DefaultTreeCellRenderer {

        public static Map icons = new HashMap();
        private static java.awt.Font myFont = new java.awt.Font("Dialog", 0, 11);

        static {
            icons.put(RepositoryTree.PlatformNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/platformNode.gif")));
            icons.put(RepositoryTree.ContainerNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/containerNode.gif")));
            icons.put(RepositoryTree.AgentNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/agentNode.gif")));
            icons.put(RepositoryTree.RepositoryNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/repositoryNode.gif")));
            icons.put(RepositoryTree.SavedAgentNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/saveagent.gif")));
            icons.put(RepositoryTree.FrozenAgentNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/freezeagent.gif")));
            icons.put(RepositoryTree.SavedContainerNode.class, new ImageIcon(Renderer.class.getResource("/jade/tools/persistence/images/savecontainer.gif")));
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            // Set the basic attributes according to the selected state etc.
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            ModelItem item = (ModelItem)node.getUserObject();
            setText(item.getName());
            setFont(myFont);
            setIcon((ImageIcon)icons.get(item.getClass()));

            return this;
        }

    } // End of Renderer class



    /** Creates a new instance of RepositoryTree */
    public RepositoryTree(String platformName) {
        myRoot = new RepositoryTree.PlatformNode(platformName);
        myModel = new DefaultTreeModel(new DefaultMutableTreeNode(myRoot));
        myView = new JTree(myModel);
        myView.setCellRenderer(new RepositoryTree.Renderer());
        myView.setShowsRootHandles(true);
        myView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public JTree getView() {
        return myView;
    }

    public TreeModel getModel() {
        return myModel;
    }

    public void clearNodes() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)myModel.getRoot();
        root.removeAllChildren();
        myModel.reload();
    }

    public void addContainerNode(String name) {
        MutableTreeNode root = (MutableTreeNode)myModel.getRoot();
        MutableTreeNode child = new DefaultMutableTreeNode(new RepositoryTree.ContainerNode(name));
        myModel.insertNodeInto(child, root, root.getChildCount());
    }

    public void addRepositoryNodes(String containerName, String[] repositoryNames) {
        MutableTreeNode parent = findContainerNode(containerName);
        for(int i = 0; i < repositoryNames.length; i++) {
            String childName = repositoryNames[i];
            MutableTreeNode child = new DefaultMutableTreeNode(new RepositoryTree.RepositoryNode(childName));
            myModel.insertNodeInto(child, parent, parent.getChildCount());
        }
    }


    private MutableTreeNode findContainerNode(String name) {
        MutableTreeNode root = (MutableTreeNode)myModel.getRoot();
        for(int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(i);
            RepositoryTree.ModelItem item = (RepositoryTree.ModelItem)child.getUserObject();
            if(name.equals(item.getName())) {
                return child;
            }
        }
        
        // Not found
        return null;
    }

    private JTree myView;
    private DefaultTreeModel myModel;

    private RepositoryTree.PlatformNode myRoot;

}
