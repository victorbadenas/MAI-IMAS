package Agents;

import Behaviours.ManagerBehaviour;
import Utils.AppConfig;
import Utils.Helper;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.HashMap;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;

public class ManagerAgent extends Agent {
    HashMap<String, AppConfig> applications;

    protected void setup() {
        this.applications = new HashMap<>();
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ManagerAgent");
        sd.setName(getName());
        sd.setOwnership("IMAS_group");
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
            doDelete();
        }

        this.addBehaviour(new ManagerBehaviour(this));
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
    }

    public void createFuzzyAgents(AppConfig appConfig) {
        this.killOldApp(appConfig.getApplication());

        ContainerController cc = getContainerController();
        String[] fuzzySettings = appConfig.getFuzzySettings();
        String[] fuzzyAgents = new String[appConfig.getNumberOfAgents()];
        AgentController[] agentControllers = new AgentController[appConfig.getNumberOfAgents()];
        for (int i = 0; i < fuzzySettings.length; i++) {
            Object[] args = new Object[]{fuzzySettings[i]};
            fuzzyAgents[i] = "FuzzyAgent" + i;
            try {
                agentControllers[i] = cc.createNewAgent(fuzzyAgents[i], "Agents.FuzzyAgent", args);
                try {
                    agentControllers[i].start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        appConfig.setControllers(agentControllers);
        appConfig.setFuzzyAgents(fuzzyAgents);
        this.applications.put(appConfig.getApplication(), appConfig);
    }

    private void killOldApp(String application) {
        for (HashMap.Entry<String, AppConfig> app : applications.entrySet()) {
            if (application.equals(app.getKey())) {
                for (AgentController ac : app.getValue().getControllers()) {
                    try {
                        ac.kill();
                    } catch (StaleProxyException e) {
                        Helper.error(this, "Error while killing agent.");
                    }
                }
            }
        }
    }

    public boolean existsApplication(String application) {
        for (HashMap.Entry<String, AppConfig> app : applications.entrySet()) {
            if (application.equals(app.getKey())) return true;
        }
        return false;
    }

    public AppConfig getApplication(String application) {
        for (HashMap.Entry<String, AppConfig> app : applications.entrySet()) {
            if (application.equals(app.getKey())) return app.getValue();
        }
        return null;
    }
}