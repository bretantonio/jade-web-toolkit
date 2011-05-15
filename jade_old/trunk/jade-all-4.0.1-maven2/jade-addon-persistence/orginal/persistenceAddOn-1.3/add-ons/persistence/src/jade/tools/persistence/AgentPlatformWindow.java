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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import jade.core.AID;
import jade.core.Profile;
import jade.gui.AclGui;
import jade.gui.AgentTree;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.util.leap.LinkedList;
import jade.util.leap.Map;
import jade.util.leap.HashMap;
import jade.wrapper.StaleProxyException;
import jade.wrapper.ControllerException;

/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
class AgentPlatformWindow extends javax.swing.JInternalFrame implements GUIConstants {

	/** Creates new form AgentPlatformWindow */
	public AgentPlatformWindow(PersistenceManagerAgent ag, ActionProcessor ap) {
		myAgent = ag;
		myActionProcessor = ap;

		glass = new javax.swing.JComponent() {
			public void paint(Graphics g) {
				g.setColor(glassColor);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		glass.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				Toolkit.getDefaultToolkit().beep();
			}
		});

		originalGlassPane = getRootPane().getGlassPane();

		initComponents();

		// Remove the text from the toolbar buttons
		Component[] buttons = platformToolBar.getComponents();
		for(int i = 0; i < buttons.length; i++) {
			Component c = buttons[i];
			if(c instanceof JButton) {
				JButton b = (JButton)c;
				b.setText(null);
			}
		}

		myAgentTree = new AgentTree(getFont());
		//myAgentTree.register("FIPAAGENT", null, "/jade/tools/persistence/images/agentNode.gif");
		//myAgentTree.register("FROZENAGENT", null, "/jade/tools/persistence/images/freezeagent.gif");
		//myAgentTree.register("FROZENCONTAINER", null, "/jade/tools/persistence/images/frozenagents.gif");
		//myAgentTree.register("FIPACONTAINER", null, "/jade/tools/persistence/images/containerNode.gif");

		myRepositoryTree = new RepositoryTree("JADE");
		repositoriesTreeScroll.setViewportView(myRepositoryTree.getView());

		myRepositoryTree.getView().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent ev) {
				TreePath path = ev.getNewLeadSelectionPath();
				if(path != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
					RepositoryTree.ModelItem item = (RepositoryTree.ModelItem)node.getUserObject();
					if(item instanceof RepositoryTree.ContainerNode) {
						nodeChoice.setSelectedItem(item.getName());
					}
					else if(item instanceof RepositoryTree.RepositoryNode) {
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
						RepositoryTree.ModelItem parentItem = (RepositoryTree.ModelItem)parentNode.getUserObject();
						nodeChoice.setSelectedItem(parentItem.getName());
						repositoryChoice.setSelectedItem(item.getName());
					}
				}
			}
		});

		myRepositoryTable = new RepositoryTable();
		repositoryDetailsScroll.setViewportView(myRepositoryTable.getView());

		ImageIcon icon = new ImageIcon(getClass().getResource("/jade/tools/persistence/images/agentNode.gif"));
		treeTabs.insertTab("Agents", icon, new JScrollPane(myAgentTree.tree), "Show live agents tree", 0);

		bodySplit.setDividerLocation((int)treeTabs.getPreferredSize().getWidth());

	}

	public void reloadAgent() {
		String[] names = getSelectedAgents();
		if (names != null && names.length > 0) {
			String repository = getSelectedRepository();
			for(int i = 0; i < names.length; i++) {
				AID agentID = new AID(names[i], AID.ISGUID);

				myAgent.reloadAgent(agentID, repository);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No agent selected in the Agent tree");
		}
	}

	public void saveAgent() {
		String[] names = getSelectedAgents();
		if (names != null && names.length > 0) {
			String repository = getSelectedRepository();
			for(int i = 0; i < names.length; i++) {
				AID agentID = new AID(names[i], AID.ISGUID);

				myAgent.saveAgent(agentID, repository);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No agent selected in the Agent tree");
		}
	}

	public void saveContainer() {
		String[] names = getSelectedContainers();
		if (names != null && names.length > 0) {
			String repository = getSelectedRepository();
			for(int i = 0; i < names.length; i++) {
				myAgent.saveContainer(names[i], repository);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No container selected in the Agent tree.");
		}
	}

	public void loadAgent() {
		String[] names = getSelectedSavedAgents();
		if (names != null && names.length > 0) {
			String containerName = getSelectedNode();
			String repository = getSelectedRepository();
			for(int i = 0; i < names.length; i++) {
				AID agentID = new AID(names[i], AID.ISGUID);
				myAgent.loadAgent(agentID, repository, containerName);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No agent selected in the Repository Details panel.");
		}
	}

	public void loadContainer() {
		String[] names = getSelectedSavedContainers();
		if (names != null && names.length > 0) {
			String repository = getSelectedRepository();
			for(int i = 0; i < names.length; i++) {
				myAgent.loadContainer(names[i], repository);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No container selected in the Repository Details panel.");
		}
	}

	public void deleteAgent() {
		String containerName = getSelectedNode();
		String repository = getSelectedRepository();

		String[] savedNames = getSelectedSavedAgents();
		if (savedNames != null) {
			for(int i = 0; i < savedNames.length; i++) {
				AID agentID = new AID(savedNames[i], AID.ISGUID);
				myAgent.deleteAgent(agentID, containerName, repository);
			}
		}

		String[] frozenNames = getSelectedFrozenAgents();
		if (frozenNames != null) {
			for(int i = 0; i < frozenNames.length; i++) {
				AID agentID = new AID(frozenNames[i], AID.ISGUID);
				myAgent.deleteAgent(agentID, containerName, repository);
			}
		}
	}

	public void deleteContainer() {
		String where = getSelectedNode();
		String repository = getSelectedRepository();

		String[] names = getSelectedSavedContainers();
		if (names != null) {
			for(int i = 0; i < names.length; i++) {
				myAgent.deleteContainer(names[i], where, repository);
			}
		}
	}

	public void freezeAgent() {
		String where = getSelectedNode();
		String repository = getSelectedRepository();

		String[] names = getSelectedAgents();
		for(int i = 0; i < names.length; i++) {
			AID agentID = new AID(names[i], AID.ISGUID);
			myAgent.freezeAgent(agentID, where, repository);
		}
	}

	public void thawAgent() {
		String repository = getSelectedRepository();
		String[] containerNames = getSelectedContainers();
		if (containerNames != null && containerNames.length > 0) {
			String where = containerNames[0];

			String[] names = getSelectedFrozenAgents();
			if (names != null && names.length > 0) {
				for(int i = 0; i < names.length; i++) {
					AID agentID = new AID(names[i], AID.ISGUID);
					myAgent.thawAgent(agentID, where, repository);
				}
			}
			else {
				JOptionPane.showMessageDialog(this, "No frozen agent selected in the Repository Details panel");
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No container selected in the Agent tree.");
		}
	}

	public void activate(String nickname, Profile p, boolean isMain) {

		myProfile = p;
		addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {

			public void internalFrameClosed(InternalFrameEvent e) {
				try {
					if(myAgentHandle != null) {
						myAgentHandle.kill();
					}
					if(myContainer != null) {
						myContainer.kill();
					}
				}
				catch(StaleProxyException spe) {
					notifyException(spe, "Error in Container shutdown");
				}
			}
		});

		setVisible(true);
		connect(nickname, isMain);

	}

	public void deactivate() {
		setVisible(false);
		dispose();
	}


	// Methods invoked by the agent thread.

	public void showErrorDialog(final String text, final ACLMessage msg) {

		Runnable errorDisplayer = new Runnable() {
			public void run() {
				String messages[] = new String[3];
				messages[0] = text;
				messages[1] = "";
				messages[2] = "Do you want to view the ACL message ?";
				int answer = JOptionPane.showInternalConfirmDialog(AgentPlatformWindow.this, messages, "RMA Error !!!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
				switch(answer) {
				case JOptionPane.YES_OPTION:
					AclGui.showMsgInDialog(msg, null);
					break;
				default:
					break;
				}
			}
		};
		SwingUtilities.invokeLater(errorDisplayer);
	}

	public void resetTree() {
		Runnable resetIt = new Runnable() {

			public void run() {
				myAgentTree.clearLocalPlatform();
			}
		};
		SwingUtilities.invokeLater(resetIt);
	}

	public void addContainer(final String name, final InetAddress addr) {
		Runnable addIt = new Runnable() {
			public void run() {
				MutableTreeNode node = myAgentTree.createNewNode(name, 0);
				myAgentTree.addContainerNode((AgentTree.ContainerNode)node, "FIPACONTAINER", addr);
			}
		};
		SwingUtilities.invokeLater(addIt);
	}

	public void removeContainer(final String name) {

		// Remove a container from the tree model
		Runnable removeIt = new Runnable() {

			public void run() {
				myAgentTree.removeContainerNode(name);
			}
		};
		SwingUtilities.invokeLater(removeIt);
		myAgent.fetchRepositories();
	}

	public void addAgent(final String containerName, final AID agentID) {

		// Add an agent to the specified container
		Runnable addIt = new Runnable() {
			public void run() {

				String agentName = agentID.getName();
				AgentTree.Node node = myAgentTree.createNewNode(agentName, 1);
				Iterator add = agentID.getAllAddresses();
				String agentAddresses = "";
				while(add.hasNext())
					agentAddresses = agentAddresses + add.next() + " ";

				myAgentTree.addAgentNode((AgentTree.AgentNode)node, containerName, agentName, agentAddresses, "FIPAAGENT");
			}
		};
		SwingUtilities.invokeLater(addIt);
	}

	public void removeAgent(final String containerName, final AID agentID) {

		// Remove an agent from the specified container
		Runnable removeIt = new Runnable() {
			public void run() {
				String agentName = agentID.getName();
				myAgentTree.removeAgentNode(containerName, agentName);
			}
		};
		SwingUtilities.invokeLater(removeIt);
	}

	public void modifyAgent(final String containerName, final AID agentID, final String state, final String ownership) {

		// Remove an agent from the specified container
		Runnable modifyIt = new Runnable() {
			public void run() {
				String agentName = agentID.getName();
				myAgentTree.modifyAgentNode(containerName, agentName, null, state, ownership);
			}
		};
		SwingUtilities.invokeLater(modifyIt);
	}

	public void moveAgent(final String fromContainer, final String toContainer, final AID agentID) {

		// Move an agent from a container node to another
		Runnable moveIt = new Runnable() {
			public void run() {
				String agentName = agentID.getName();
				myAgentTree.moveAgentNode(fromContainer, toContainer, agentName);
			}
		};
		SwingUtilities.invokeLater(moveIt);
	}

	public void modifyFrozenAgent(final String oldContainer, final String newContainer, final AID agentID) {

		// Freeze an agent to the specified container
		Runnable freezeIt = new Runnable() {
			public void run() {
				String agentName = agentID.getName();
				myAgentTree.freezeAgentNode(oldContainer, newContainer, agentName);
			}
		};
		SwingUtilities.invokeLater(freezeIt);
	}

	public void modifyThawedAgent(final String oldContainer, final String newContainer, final AID agentID) {

		// Thaw an agent to the specified container
		Runnable thawIt = new Runnable() {
			public void run() {
				String agentName = agentID.getName();
				myAgentTree.thawAgentNode(oldContainer, newContainer, agentName);
			}
		};
		SwingUtilities.invokeLater(thawIt);
	}

	public void refreshLocalPlatformName(final String name) {

		Runnable refreshName = new Runnable() {
			public void run() {
				myAgentTree.refreshLocalPlatformName(name);
			}	
		};
		SwingUtilities.invokeLater(refreshName);
	}

	public void repositoriesFetched(final String[] nodes, final List repositoriesList) {

		Runnable treeUpdater = new Runnable() {
			public void run() {
				myRepositoryTree.clearNodes();
				for(int i = 0; i < nodes.length; i++) {
					myRepositoryTree.addContainerNode(nodes[i]);
					String[] repositories = (String[])repositoriesList.get(i);
					myRepositoryTree.addRepositoryNodes(nodes[i], repositories);

					updateRepositoryChoices(nodes, repositoriesList);
				}
			}
		};
		SwingUtilities.invokeLater(treeUpdater);
	}

	public void fetchRepositoriesFailed(final Throwable t) {

		Runnable errorDisplayer = new Runnable() {
			public void run() {
				notifyException(t, "Error while retrieving the repository list");
			}
		};
		SwingUtilities.invokeLater(errorDisplayer);
	}

	public void repositoryRead(final String[] savedAgents, final String[] frozenAgents, final String[] savedContainers) {

		Runnable detailsUpdater = new Runnable() {
			public void run() {
				CardLayout layout = (CardLayout)dbDetailsPanel.getLayout();
				layout.show(dbDetailsPanel, "Repository");

				myRepositoryTable.setData(savedAgents, frozenAgents, savedContainers);
			}
		};
		SwingUtilities.invokeLater(detailsUpdater);
	}

	public void readRepositoryFailed(final Throwable t) {

		Runnable errorDisplayer = new Runnable() {
			public void run() {
				notifyException(t, "Error while retrieving the repository content");
			}
		};
		SwingUtilities.invokeLater(errorDisplayer);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {//GEN-BEGIN:initComponents
		platformToolBar = new javax.swing.JToolBar();
		loadAgentButton = new javax.swing.JButton();
		saveAgentButton = new javax.swing.JButton();
		reloadAgentButton = new javax.swing.JButton();
		freezeAgentButton = new javax.swing.JButton();
		thawAgentButton = new javax.swing.JButton();
		deleteAgentButton = new javax.swing.JButton();
		saveContainerButton = new javax.swing.JButton();
		reloadContainerButton = new javax.swing.JButton();
		deleteContainerButton = new javax.swing.JButton();
		bodySplit = new javax.swing.JSplitPane();
		treeTabs = new javax.swing.JTabbedPane();
		leftPanel = new javax.swing.JPanel();
		reloadPanel = new javax.swing.JPanel();
		leftPadPanel = new javax.swing.JPanel();
		reloadButton = new javax.swing.JButton();
		rightPadPanel = new javax.swing.JPanel();
		repositoriesTreePanel = new javax.swing.JPanel();
		repositoriesTreeScroll = new javax.swing.JScrollPane();
		rightPanel = new javax.swing.JPanel();
		dbChooserPanel = new javax.swing.JPanel();
		nodeLabel = new javax.swing.JLabel();
		nodeChoice = new javax.swing.JComboBox();
		repositoryLabel = new javax.swing.JLabel();
		repositoryChoice = new javax.swing.JComboBox();
		dbDetailsPanel = new javax.swing.JPanel();
		nothingPanel = new javax.swing.JPanel();
		nothingLabel = new javax.swing.JLabel();
		repositoryPanel = new javax.swing.JPanel();
		repositoryDetailsScroll = new javax.swing.JScrollPane();

		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setFont(new java.awt.Font("Arial", 0, 11));
		platformToolBar.setFloatable(false);
		platformToolBar.setRollover(true);
		loadAgentButton.setAction(myActionProcessor.getAction(ACTION_LOADAGENT));
		loadAgentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/loadagent.gif")));
		platformToolBar.add(loadAgentButton);

		saveAgentButton.setAction(myActionProcessor.getAction(ACTION_SAVEAGENT));
		saveAgentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/saveagent.gif")));
		platformToolBar.add(saveAgentButton);

		reloadAgentButton.setAction(myActionProcessor.getAction(ACTION_RELOADAGENT));
		reloadAgentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/reloadagent.gif")));
		platformToolBar.add(reloadAgentButton);

		freezeAgentButton.setAction(myActionProcessor.getAction(ACTION_FREEZEAGENT));
		freezeAgentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/freezeagent.gif")));
		platformToolBar.add(freezeAgentButton);

		thawAgentButton.setAction(myActionProcessor.getAction(ACTION_THAWAGENT));
		thawAgentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/thawagent.gif")));
		platformToolBar.add(thawAgentButton);

		deleteAgentButton.setAction(myActionProcessor.getAction(ACTION_DELETEAGENT));
		deleteAgentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/deleteagent.gif")));
		platformToolBar.add(deleteAgentButton);

		saveContainerButton.setAction(myActionProcessor.getAction(ACTION_SAVECONTAINER));
		saveContainerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/savecontainer.gif")));
		platformToolBar.add(saveContainerButton);

		reloadContainerButton.setAction(myActionProcessor.getAction(ACTION_LOADCONTAINER));
		reloadContainerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/reloadcontainer.gif")));
		platformToolBar.add(reloadContainerButton);

		deleteContainerButton.setAction(myActionProcessor.getAction(ACTION_DELETECONTAINER));
		deleteContainerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/deletecontainer.gif")));
		platformToolBar.add(deleteContainerButton);

		getContentPane().add(platformToolBar, java.awt.BorderLayout.NORTH);

		bodySplit.setDividerSize(2);
		bodySplit.setContinuousLayout(true);
		treeTabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
		treeTabs.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
		treeTabs.setFont(new java.awt.Font("Dialog", 0, 10));
		leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.Y_AXIS));

		reloadPanel.setLayout(new javax.swing.BoxLayout(reloadPanel, javax.swing.BoxLayout.X_AXIS));

		reloadPanel.add(leftPadPanel);

		reloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/Refresh.gif")));
		reloadButton.setText("Refresh");
		reloadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reloadButtonActionPerformed(evt);
			}
		});

		reloadPanel.add(reloadButton);

		reloadPanel.add(rightPadPanel);

		leftPanel.add(reloadPanel);

		repositoriesTreePanel.setLayout(new java.awt.BorderLayout());

		repositoriesTreePanel.add(repositoriesTreeScroll, java.awt.BorderLayout.CENTER);

		leftPanel.add(repositoriesTreePanel);

		treeTabs.addTab("Repositories", new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/repositoryNode.gif")), leftPanel, "Show Repositories Tree");

		bodySplit.setLeftComponent(treeTabs);

		rightPanel.setLayout(new java.awt.BorderLayout());

		dbChooserPanel.setLayout(new javax.swing.BoxLayout(dbChooserPanel, javax.swing.BoxLayout.X_AXIS));

		dbChooserPanel.setBorder(new javax.swing.border.TitledBorder(null, "Selected Repository", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
		nodeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		nodeLabel.setLabelFor(nodeChoice);
		nodeLabel.setText("Node: ");
		nodeLabel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 6, 1, 1)));
		dbChooserPanel.add(nodeLabel);

		nodeChoice.setFont(new java.awt.Font("Dialog", 0, 10));
		nodeChoice.setModel(nodeChoiceModel);
		nodeChoice.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 3, 1, 12)));
		nodeChoice.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				nodeChoiceItemStateChanged(evt);
			}
		});

		dbChooserPanel.add(nodeChoice);

		repositoryLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		repositoryLabel.setLabelFor(repositoryChoice);
		repositoryLabel.setText("Repository: ");
		dbChooserPanel.add(repositoryLabel);

		repositoryChoice.setFont(new java.awt.Font("Dialog", 0, 10));
		repositoryChoice.setModel(repositoryChoiceModel);
		repositoryChoice.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 3, 1, 6)));
		repositoryChoice.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				repositoryChoiceItemStateChanged(evt);
			}
		});

		dbChooserPanel.add(repositoryChoice);

		rightPanel.add(dbChooserPanel, java.awt.BorderLayout.NORTH);

		dbDetailsPanel.setLayout(new java.awt.CardLayout());

		dbDetailsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Repository Details", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
		nothingPanel.setLayout(new java.awt.GridLayout(1, 1));

		nothingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		nothingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		nothingLabel.setText("<No Repository Selected>");
		nothingLabel.setEnabled(false);
		nothingPanel.add(nothingLabel);

		dbDetailsPanel.add(nothingPanel, "Nothing");

		repositoryPanel.setLayout(new java.awt.BorderLayout());

		repositoryDetailsScroll.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		repositoryPanel.add(repositoryDetailsScroll, java.awt.BorderLayout.CENTER);

		dbDetailsPanel.add(repositoryPanel, "Repository");

		rightPanel.add(dbDetailsPanel, java.awt.BorderLayout.CENTER);

		bodySplit.setRightComponent(rightPanel);

		getContentPane().add(bodySplit, java.awt.BorderLayout.CENTER);

		pack();
	}//GEN-END:initComponents

	private void nodeChoiceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_nodeChoiceItemStateChanged
		if(evt.getStateChange() == ItemEvent.SELECTED) {
			Object currentNode = evt.getItemSelectable().getSelectedObjects()[0];
			String[] currentRepositories = (String[])repositoriesMap.get(currentNode);

			String oldRepository = (String)repositoryChoice.getSelectedItem();
			repositoryChoiceModel.removeAllElements();
			for(int i = 0; i < currentRepositories.length; i++) {
				repositoryChoiceModel.addElement(currentRepositories[i]);
			}
			String newRepository = (String)repositoryChoice.getSelectedItem();

			// If the two repository names are the same, no event is generated in
			// the repository combo box, so we update the details view here
			if((oldRepository != null) && (oldRepository.equals(newRepository))) {
				myAgent.readRepository((String)currentNode, newRepository);
			}
		}

	}//GEN-LAST:event_nodeChoiceItemStateChanged

	private void repositoryChoiceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_repositoryChoiceItemStateChanged
		if(evt.getStateChange() == ItemEvent.SELECTED) {
			String currentNode = (String)nodeChoice.getSelectedItem();
			String currentRepository = (String)repositoryChoice.getSelectedItem();
			myAgent.readRepository(currentNode, currentRepository);
		}
	}//GEN-LAST:event_repositoryChoiceItemStateChanged

	private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
		myAgent.fetchRepositories();
	}//GEN-LAST:event_reloadButtonActionPerformed

	private void shade() {
		getRootPane().setGlassPane(glass);
		glass.setVisible(true);
	}

	private void unshade() {
		getRootPane().setGlassPane(originalGlassPane);
		originalGlassPane.setVisible(false);
	}

	private void connect(final String nickname, final boolean isMain) {

		Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					jade.core.Runtime rt = jade.core.Runtime.instance();
					if(isMain) {
						myContainer = rt.createMainContainer(myProfile);
					}
					else {
						myContainer = rt.createAgentContainer(myProfile);
					}

					myContainer.start();
					myAgentHandle = myContainer.acceptNewAgent(nickname, myAgent);
					myAgentHandle.start();

					// OK -- Run the completion in the Event Dispatcher thread
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							unshade();
							setTitle("JADE Agent Platform [" + myContainer.getPlatformName() + "]");

							myAgent.fetchRepositories();
						}
					});
				}
				catch(final ControllerException ce) {
					// KO -- Run the exception notification in the Event Dispatcher thread
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							glassColor = errorGlassColor;
							notifyException(ce, "Error contacting the Agent Container");
							deactivate();
						}
					});
				}
				catch(final Throwable t) {
					// KO -- Run the exception notification in the Event Dispatcher thread
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							glassColor = errorGlassColor;
							notifyException(t, "General Error");
							deactivate();
						}
					});
				}
			}
		});

		glassColor = busyGlassColor;
		shade();
		t.start();
	}

	/**
	 * Retrieve the currently selected container in the Node combo box of the Selected Repository panel.
	 * This can never be null
	 */
	private String getSelectedNode() {
		return (String)nodeChoice.getSelectedItem();
	}

	/**
	 * Retrieve the currently selected repository in the Repository combo box of the Selected Repository panel.
	 * This can never be null
	 */
	private String getSelectedRepository() {
		return (String)repositoryChoice.getSelectedItem();
	}

	/**
	 * Retrieve the currently selected agents in the Agent Tree or null if no agent is selected.
	 */
	private String[] getSelectedAgents() {
		TreePath[] paths = myAgentTree.tree.getSelectionModel().getSelectionPaths();
		if (paths != null) {
			List names = new LinkedList();
			for(int i = 0; i < paths.length; i++) {
				AgentTree.Node node = (AgentTree.Node)paths[i].getLastPathComponent();
				if(node instanceof AgentTree.AgentNode) {
					AgentTree.AgentNode an = (AgentTree.AgentNode)node;
					names.add(an.getName());
				}            
			}
			String[] result = new String[names.size()];
			for(int i = 0; i < result.length; i++) {
				result[i] = (String)names.get(i);
			}

			return result;
		}
		else {
			return null;
		}
	}

	/**
	 * Retrieve the currently selected containers in the Agent Tree or null if no container is selected.
	 */
	private String[] getSelectedContainers() {
		TreePath[] paths = myAgentTree.tree.getSelectionModel().getSelectionPaths();
		if (paths != null) {
			List names = new LinkedList();
			for(int i = 0; i < paths.length; i++) {
				AgentTree.Node node = (AgentTree.Node)paths[i].getLastPathComponent();
				if(node instanceof AgentTree.ContainerNode) {
					AgentTree.ContainerNode cn = (AgentTree.ContainerNode)node;
					names.add(cn.getName());
				}
			}
			String[] result = new String[names.size()];
			for(int i = 0; i < result.length; i++) {
				result[i] = (String)names.get(i);
			}

			return result;
		}
		else {
			return null;
		}
	}

	/**
	 * Retrieve the currently selected saved agents in the Repository Details panel.
	 */
	private String[] getSelectedSavedAgents() {
		return myRepositoryTable.getSelectedSavedAgents();
	}

	/**
	 * Retrieve the currently selected frozen agents in the Repository Details panel.
	 */
	private String[] getSelectedFrozenAgents() {
		return myRepositoryTable.getSelectedFrozenAgents();
	}

	/**
	 * Retrieve the currently selected saved containers in the Repository Details panel.
	 */
	private String[] getSelectedSavedContainers() {
		return myRepositoryTable.getSelectedSavedContainers();
	}

	private void updateRepositoryChoices(final String[] nodes, final List repositoriesList) {
		nodeChoiceModel.removeAllElements();
		repositoriesMap.clear();
		for(int i = 0; i < nodes.length; i++) {
			repositoriesMap.put(nodes[i], repositoriesList.get(i));
			nodeChoiceModel.addElement(nodes[i]);
		}
	}

	private void notifyException(Throwable t, String title) {
		String exceptionMsg = (t.getMessage() != null) ? t.getMessage() : "";
		List messageRows = new LinkedList();
		messageRows.add("Error message:");
		messageRows.add(t.getClass().getName() + " [");
		int ROW_LENGTH = 80;
		int rows = (exceptionMsg.length() / ROW_LENGTH) + 1;
		for(int i = 0; i < rows; i++) {
			int endIdx = Math.min(ROW_LENGTH*(i+1), exceptionMsg.length());
			messageRows.add("  " + exceptionMsg.substring(ROW_LENGTH*i, endIdx));
		}
		messageRows.add("]");
		JOptionPane.showInternalMessageDialog(this, messageRows.toArray(), title, JOptionPane.ERROR_MESSAGE);
	}


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JSplitPane bodySplit;
	private javax.swing.JPanel dbChooserPanel;
	private javax.swing.JPanel dbDetailsPanel;
	private javax.swing.JButton deleteAgentButton;
	private javax.swing.JButton deleteContainerButton;
	private javax.swing.JButton freezeAgentButton;
	private javax.swing.JPanel leftPadPanel;
	private javax.swing.JPanel leftPanel;
	private javax.swing.JButton loadAgentButton;
	private javax.swing.JComboBox nodeChoice;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel nothingLabel;
	private javax.swing.JPanel nothingPanel;
	private javax.swing.JToolBar platformToolBar;
	private javax.swing.JButton reloadAgentButton;
	private javax.swing.JButton reloadButton;
	private javax.swing.JButton reloadContainerButton;
	private javax.swing.JPanel reloadPanel;
	private javax.swing.JPanel repositoriesTreePanel;
	private javax.swing.JScrollPane repositoriesTreeScroll;
	private javax.swing.JComboBox repositoryChoice;
	private javax.swing.JScrollPane repositoryDetailsScroll;
	private javax.swing.JLabel repositoryLabel;
	private javax.swing.JPanel repositoryPanel;
	private javax.swing.JPanel rightPadPanel;
	private javax.swing.JPanel rightPanel;
	private javax.swing.JButton saveAgentButton;
	private javax.swing.JButton saveContainerButton;
	private javax.swing.JButton thawAgentButton;
	private javax.swing.JTabbedPane treeTabs;
	// End of variables declaration//GEN-END:variables

	private Profile myProfile;
	private jade.wrapper.AgentContainer myContainer;
	private jade.wrapper.AgentController myAgentHandle;
	private PersistenceManagerAgent myAgent;
	private ActionProcessor myActionProcessor;
	private AgentTree myAgentTree;
	private RepositoryTree myRepositoryTree;
	private RepositoryTable myRepositoryTable;
	private DefaultComboBoxModel nodeChoiceModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel repositoryChoiceModel = new DefaultComboBoxModel();
	private Map repositoriesMap = new HashMap();

	// The semi-transparent glass pane used to disable the window during long operations
	private JComponent glass;
	private Color glassColor;
	private Color busyGlassColor = new Color(0, 128, 128, 64);
	private Color errorGlassColor = new Color(255, 0 , 0, 64);

	// The original, transparent glass pane
	private Component originalGlassPane;

}
