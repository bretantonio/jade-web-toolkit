/*
 * Created on May 27, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package test.security.permission;

import test.common.TestGroup;
import test.common.TesterAgent;

/**
 * 
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PermissionTesterAgent extends TesterAgent {
	/* (non-Javadoc)
	 * @see test.common.TesterAgent#getTestGroup()
	 */
	protected TestGroup getTestGroup() {
		return new TestGroup("test/security/permission/permissionTestsList.xml"); 
	}
}
