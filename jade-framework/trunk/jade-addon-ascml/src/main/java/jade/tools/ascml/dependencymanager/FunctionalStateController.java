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

package jade.tools.ascml.dependencymanager;

import java.util.Vector;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IAgentTypeDependency;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.dependency.ISocietyInstanceDependency;
import jade.tools.ascml.absmodel.dependency.ISocietyTypeDependency;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;
import jade.util.Logger;

/**
 * This class watches the functional dependencies for all models
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class FunctionalStateController extends AbstractDependencyController{

	public FunctionalStateController(AgentLauncher launcher) {
		super(launcher);
	}

	protected Vector<IDependency> getDependenciesFromModel(IAbstractRunnable societyInstanceModel) {
		ISocietyInstance parentSoc = (ISocietyInstance) societyInstanceModel.getParentModel();
		IFunctional myFunctional = parentSoc.getFunctional();
		Vector<IDependency> result = new Vector<IDependency>();
		result.addAll(myFunctional.getDependencyList());
		return result;
	}

	protected AbstractDependencyRecord getNewRecord(IAbstractRunnable absRunnable) {
		return new FunctionalRecord(absRunnable, launcher);
	}

	protected void noDependencies(IAbstractRunnable absRunnable) {
		if (launcher.myLogger.isLoggable(Logger.INFO)) {
			launcher.myLogger.info("Settings Status to Functional for "+absRunnable.toString());
		}
		absRunnable.setStatus(new Functional());
	}

	protected void handleActiveDependency(IDependency oneDep) {
		//We don't take care of them at this point, active dependencies are handled 
		// in FunctionalRecord.checkState();
	}

}
