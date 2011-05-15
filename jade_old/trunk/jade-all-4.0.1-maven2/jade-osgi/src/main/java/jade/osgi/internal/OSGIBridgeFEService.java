package jade.osgi.internal;

import jade.core.Agent;
import jade.core.FEService;
import jade.core.ServiceHelper;
import jade.osgi.OSGIBridgeHelper;

public class OSGIBridgeFEService extends FEService {
	
	private AgentManager agentManager;

	@Override
	public String getBEServiceClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceHelper getHelper(Agent a) {
		return new OSGIBridgeHelperImpl(getAgentManager());
	}

	@Override
	public String getName() {
		return OSGIBridgeHelper.SERVICE_NAME;
	}
	
	private AgentManager getAgentManager() {
		if(agentManager == null) {
			agentManager = JadeActivator.getInstance().getAgentManager();
		}
		return agentManager;
	}

}
