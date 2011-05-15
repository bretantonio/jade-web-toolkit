/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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
package jade.webservice.utils;

import jade.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;


//#APIDOC_EXCLUDE_FILE

class CompilerProcessManager extends Thread {
	
	private static Logger logger = Logger.getMyLogger(CompilerProcessManager.class.getName());;

	private Process subProc;
	private BufferedReader br;
	private BufferedReader brErr;
	private Thread errorManager;
	
	public CompilerProcessManager(Process p) {
		subProc = p;
		br = new BufferedReader(new InputStreamReader(subProc.getInputStream()));
		errorManager = startErrorManager();
	}
	
	public void run() {
		while (true) {
			try {
				// Check if the sub-process is still alive
				subProc.exitValue();
				break;
			}
			catch (IllegalThreadStateException itse) {
				// The sub-process is still alive --> go on
			}
			
			try {
				handleLine(br.readLine());
			}
			catch (Exception e) {
				e.printStackTrace();
			}				
		}
		
		stopErrorManager();
	}

	private void handleLine(String line) {
		if (line != null) {
			logger.log(Logger.FINE, "compiler: "+line);
		}
	}
	
	private Thread startErrorManager() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					brErr = new BufferedReader(new InputStreamReader(subProc.getErrorStream()));
					while (true) {
						handleLine(brErr.readLine());
					}
				}
				catch (Exception e) {
					// The process has terminated. Do nothing
				}
			}
		} );
		
		t.start();
		return t;
	}
	
	private void stopErrorManager() {
		try {
			brErr.close();
			errorManager.interrupt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}			
}

