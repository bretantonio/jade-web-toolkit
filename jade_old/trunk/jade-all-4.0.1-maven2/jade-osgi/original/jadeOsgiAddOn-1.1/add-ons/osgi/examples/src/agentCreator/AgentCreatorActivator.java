package agentCreator;

import jade.osgi.service.runtime.JadeRuntimeService;
import jade.util.Logger;
import jade.wrapper.AgentController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class AgentCreatorActivator implements BundleActivator {

	private ServiceReference jadeRef;
	
	private static Logger logger = Logger.getMyLogger(AgentCreatorActivator.class.getName());

	public void start(BundleContext context) throws Exception {
		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());
		if(jadeRef != null) {
			JadeRuntimeService jrs = (JadeRuntimeService) context.getService(jadeRef);
			try {
				AgentController ac = jrs.createNewAgent("HelloAgent", "agentHolder.HelloAgent", null, "agentHolder");
				ac.start();
			} catch(Exception e) {
				logger.log(Logger.SEVERE, "Cannot start HelloAgent", e);
			}
		} else {
			logger.log(Logger.WARNING, "Cannot start HelloAgent: JadeRuntimeService cannot be found");
		}
	}

	public void stop(BundleContext context) throws Exception {
		if(jadeRef != null) {
			context.ungetService(jadeRef);
		}
	}

}
