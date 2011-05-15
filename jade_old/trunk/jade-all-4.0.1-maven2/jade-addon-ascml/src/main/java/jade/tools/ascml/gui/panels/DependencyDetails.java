/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.absmodel.dependency.*;
import jade.tools.ascml.model.jibx.dependency.AgentTypeDependency;
import jade.tools.ascml.model.jibx.dependency.AgentInstanceDependency;
import jade.tools.ascml.model.jibx.dependency.SocietyInstanceDependency;
import jade.tools.ascml.model.jibx.dependency.SocietyTypeDependency;
import jade.tools.ascml.events.DependencyChangedEvent;
import jade.tools.ascml.events.DependencyChangedListener;

public class DependencyDetails extends JPanel implements ActionListener, ItemListener
{
	private JButton buttonApply;

	private JComboBox comboBoxType;

	private DependencyChangedListener dependencyChangedListener;

	private IDependency dependency;
	private JPanel specialDependencyPanel;

	/**
	 *
	 * @param dependencyChangedListener  Listener to be informed, when the user presses the
	 *                                  Apply-button.
	 */
	public DependencyDetails(DependencyChangedListener dependencyChangedListener)
	{
		this.dependencyChangedListener = dependencyChangedListener;
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		buttonApply = ComponentFactory.createApplyButton("Apply Changes");
		buttonApply.addActionListener(this);

		comboBoxType = new JComboBox(new String[] {IDependency.AGENTINSTANCE_DEPENDENCY, IDependency.AGENTTYPE_DEPENDENCY, IDependency.SOCIETYINSTANCE_DEPENDENCY, IDependency.SOCIETYTYPE_DEPENDENCY });
		comboBoxType.setBackground(Color.WHITE);
		comboBoxType.addItemListener(this);

		specialDependencyPanel = new JPanel();
		specialDependencyPanel.setBackground(Color.WHITE);
	}

	public void init()
	{
		if ((dependency != null) && comboBoxType.isEnabled())
			comboBoxType.setSelectedItem(dependency.getType());

		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.setBackground(Color.WHITE);
		comboBoxPanel.add(new JLabel("Type of Dependency: "));
		comboBoxPanel.add(comboBoxType);

		this.removeAll();
		this.add(comboBoxPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(specialDependencyPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(buttonApply, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		this.revalidate();
	}

	public void setDependency(IDependency dependency)
	{
		this.dependency = dependency;
		if (this.dependency == null)
		{
			// A new dependency shall be created, default to AgentInstanceDependency
			comboBoxType.setEnabled(true);
			this.dependency = new AgentInstanceDependency();
			comboBoxType.setSelectedIndex(-1);
			comboBoxType.setSelectedIndex(0);
		}
		else
		{
			// An existing dependency shall be edited
			comboBoxType.setEnabled(false);
			String dependencyType = dependency.getType();
			if (dependencyType == IDependency.AGENTINSTANCE_DEPENDENCY)
				specialDependencyPanel = new DependencyAgentInstance((IAgentInstanceDependency)dependency);
			else if (dependencyType == IDependency.AGENTTYPE_DEPENDENCY)
				specialDependencyPanel = new DependencyAgentType((IAgentTypeDependency)dependency);
			else if (dependencyType == IDependency.SOCIETYINSTANCE_DEPENDENCY)
				specialDependencyPanel = new DependencySocietyInstance((ISocietyInstanceDependency)dependency);
			else if (dependencyType == IDependency.SOCIETYTYPE_DEPENDENCY)
				specialDependencyPanel = new DependencySocietyType((ISocietyTypeDependency)dependency);
		}
		init();
	}

	/**
	 * Get the result of the dependency-editing.
	 * @return  The result of the editing.
	 */
	public IDependency getResult()
	{
		return dependency;
	}

	public void itemStateChanged(ItemEvent evt)
	{
		if (evt.getStateChange() == ItemEvent.DESELECTED)
			return;

		String dependencyType = (String)evt.getItem();
		if (dependencyType == IDependency.AGENTINSTANCE_DEPENDENCY)
		{
			dependency = new AgentInstanceDependency();
			specialDependencyPanel = new DependencyAgentInstance((IAgentInstanceDependency)dependency);
		}
		else if (dependencyType == IDependency.AGENTTYPE_DEPENDENCY)
		{
			dependency = new AgentTypeDependency();
			specialDependencyPanel = new DependencyAgentType((IAgentTypeDependency)dependency);
		}
		else if (dependencyType == IDependency.SOCIETYINSTANCE_DEPENDENCY)
		{
			dependency = new SocietyInstanceDependency();
			specialDependencyPanel = new DependencySocietyInstance((ISocietyInstanceDependency)dependency);
		}
		else if (dependencyType == IDependency.SOCIETYTYPE_DEPENDENCY)
		{
			dependency = new SocietyTypeDependency();
			specialDependencyPanel = new DependencySocietyType((ISocietyTypeDependency)dependency);
		}

		init();
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonApply)
		{
            if (specialDependencyPanel instanceof DependencyAgentType)
				dependency = ((DependencyAgentType)specialDependencyPanel).getDependency();
			if (specialDependencyPanel instanceof DependencyAgentInstance)
				dependency = ((DependencyAgentInstance)specialDependencyPanel).getDependency();
			if (specialDependencyPanel instanceof DependencySocietyType)
				dependency = ((DependencySocietyType)specialDependencyPanel).getDependency();
			if (specialDependencyPanel instanceof DependencySocietyInstance)
				dependency = ((DependencySocietyInstance)specialDependencyPanel).getDependency();

			if (dependencyChangedListener != null)
					dependencyChangedListener.dependencyChanged(new DependencyChangedEvent(dependency));
		}
	}

}
