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
package jade.webservice.dynamicClient;

/**
 * Configuration class of a DynamicClient. For details see the WSDC user guide.
 * <p>
 * The default values are:
 * <li>noWrap: false
 * <li>safeMode: true
 * <li>packageName: -
 * <li>classPath: -
 * <li>tmpDir: system property <code>java.io.tmpdir</code>
 *  
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class DynamicClientProperties {

	private String packageName;
	private String tmpDir;
	private boolean noWrap;
	private boolean safeMode;
	private StringBuilder classPath;
	
	/**
	 * Create a DynamicClientProperties
	 */
	public DynamicClientProperties() {
		tmpDir = System.getProperty("java.io.tmpdir");
		noWrap = false;
		safeMode = true;
		packageName = null;
		classPath = null;
	}

	/**
	 * Return the package-name used to initialize the DynamicClient
	 * 
	 * @return package-name
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Set the package-name used to initialize the DynamicClient
	 * 
	 * @param packageName
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	/**
	 * Return the temp-dir used to initialize the DynamicClient
	 * 
	 * @return temp-dir
	 */
	public String getTmpDir() {
		return tmpDir;
	}
	
	/**
	 * Set the temp-dir used to initialize the DynamicClient
	 * 
	 * @param tmpDir
	 */
	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}
	
	/**
	 * Tests the no-wrap flag used to initialize the DynamicClient
	 * 
	 * @return true if no-wrap flag is setted, false otherwise
	 */
	public boolean isNoWrap() {
		return noWrap;
	}
	
	/**
	 * Set the no-wrap flag used to initialize the DynamicClient
	 * 
	 * @param noWrap
	 */
	public void setNoWrap(boolean noWrap) {
		this.noWrap = noWrap;
	}
	
	/**
	 * Tests the safe-mode flag used to initialize the DynamicClient
	 * 
	 * @return true if safe-mode flag is setted, false otherwise
	 */
	public boolean isSafeMode() {
		return safeMode;
	}
	
	/**
	 * Set the safe-mode flag used to initialize the DynamicClient
	 * 
	 * @param safeMode
	 */
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
	
	/**
	 * Return the class-path used to initialize the DynamicClient
	 * 
	 * @return class-path
	 */
	public StringBuilder getClassPath() {
		return classPath;
	}

	/**
	 * Set the class-path used to initialize the DynamicClient
	 * 
	 * @param classPath
	 */
	public void setClassPath(StringBuilder classPath) {
		this.classPath = classPath;
	}
}
