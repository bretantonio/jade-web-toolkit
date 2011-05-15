package jade.osgi.service.runtime.internal;

import jade.osgi.internal.AgentManager;
import jade.wrapper.ContainerController;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class JadeRuntimeServiceFactory implements ServiceFactory {

	private ContainerController container;
	private AgentManager agentManager;
	private Map<Long,JadeRuntimeServiceImpl> usedJadeServices = new HashMap<Long, JadeRuntimeServiceImpl>();

	public JadeRuntimeServiceFactory(ContainerController container, AgentManager agentManager) {
		this.container = container;
		this.agentManager = agentManager;
	}

	public Object getService(Bundle bundle, ServiceRegistration registration) {
		JadeRuntimeServiceImpl jadeService = new JadeRuntimeServiceImpl(container, agentManager, bundle);
		usedJadeServices.put(bundle.getBundleId(), jadeService);
		return jadeService;
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		usedJadeServices.remove(bundle.getBundleId());
		if(service instanceof JadeRuntimeServiceImpl) {
			// FIXME do something?
//			JadeRuntimeServiceImpl jadeService = (JadeRuntimeServiceImpl) service;
//			try {
//				jadeService.removeAgents();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}

}