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

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public abstract class AbstractDependencyWatcher implements Runnable {

	protected AbstractDependencyRecord parentDeps;
	protected boolean takeDown;
	protected Thread t;
	protected boolean isActive;
	
	public AbstractDependencyWatcher (AbstractDependencyRecord parentDeps, String threadName) {
		this.isActive = false;
		this.parentDeps = parentDeps;
		takeDown = false;	
		t = new Thread(this,"ADW-Thread for "+threadName);
	}
	
	
	public boolean isActive() {
		return isActive;
	}
	
	
	public synchronized void takeDown() {
		takeDown = true;
		t.interrupt();
	}
	
	protected void changed() {
		//synchronized (parentDeps) {
			parentDeps.dependencyChanged(this);
		//}
	}
	
}
