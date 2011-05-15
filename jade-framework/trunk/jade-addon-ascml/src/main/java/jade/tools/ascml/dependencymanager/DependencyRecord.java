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

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.*;

import java.util.HashSet;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class DependencyRecord extends AbstractDependencyRecord{

	public DependencyRecord(IAbstractRunnable absRunnable) {
		super(absRunnable);
	}

	protected void checkStatus() {
		if (neededWatcherTypes.size() == 0 && neededAgentNames.size() == 0 && neededSocietyNames.size() == 0) {
			//All Deps fulfilled
			if (absRunnable.getStatus()==null) {
				absRunnable.setStatus(new Starting());
			}
		}
	}
}
