package agentHolder;

import jade.core.Agent;
import jade.core.ServiceException;
import jade.osgi.OSGIBridgeHelper;
import jade.util.Logger;
import org.osgi.framework.BundleContext;

public class HelloAgent extends Agent {
	
	private static Logger logger = Logger.getMyLogger(HelloAgent.class.getName());
	
	@Override
	protected void setup() {
		System.out.println("Hello!");
		try {
			OSGIBridgeHelper afHelper = (OSGIBridgeHelper) getHelper(OSGIBridgeHelper.SERVICE_NAME);
			afHelper.init(this);
			BundleContext context = afHelper.getBundleContext();
			System.out.println(this.getLocalName() +" is packaged in bundle " + context.getBundle().getSymbolicName());
		} catch(ServiceException e) {
			logger.log(Logger.SEVERE, "Failure during setup", e);
		}
	}

}
