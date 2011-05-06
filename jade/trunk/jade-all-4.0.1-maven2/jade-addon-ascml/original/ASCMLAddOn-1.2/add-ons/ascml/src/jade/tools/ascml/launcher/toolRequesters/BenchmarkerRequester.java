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
package jade.tools.ascml.launcher.toolRequesters;

import jade.content.Concept;
import jade.core.AID;
import jade.domain.JADEAgentManagement.SniffOn;
import jade.tools.ascml.absmodel.IToolOption;
import jade.tools.ascml.launcher.AgentLauncher;

/**
 * 
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class BenchmarkerRequester extends ToolRequester {

	/* (non-Javadoc)
	 * @see jade.tools.ascml.launcher.toolRequesters.ToolRequester
	 */
	public BenchmarkerRequester(AgentLauncher launcher) {
		super(launcher);
	}

	/* (non-Javadoc)
	 * @see jade.tools.ascml.launcher.toolRequesters.ToolRequester#getToolOptionName()
	 */
	@Override
	public String getToolOptionName() {
		return IToolOption.TOOLOPTION_BENCHMARK;
	}

	/* (non-Javadoc)
	 * @see jade.tools.ascml.launcher.toolRequesters.ToolRequester#getAction(java.lang.String)
	 */
	@Override
	public Concept getAction(AID toolAID, String agent) {
        Concept result = new SniffOn();
        ((SniffOn)result).setSniffer(toolAID);
        ((SniffOn)result).addSniffedAgents(new AID(agent, AID.ISLOCALNAME));
		return result;
	}

	/* (non-Javadoc)
	 * @see jade.tools.ascml.launcher.toolRequesters.ToolRequester#getToolClass()
	 */
	@Override
	protected String getToolClass() {
		return "jade.tools.benchmarking.BenchmarkSnifferAgent";
	}

	/* (non-Javadoc)
	 * @see jade.tools.ascml.launcher.toolRequesters.ToolRequester#getToolPrefix()
	 */
	@Override
	protected String getToolPrefix() {
		return "ASCMLBencher";
	}

}
