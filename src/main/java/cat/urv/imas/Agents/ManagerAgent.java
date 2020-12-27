package cat.urv.imas.Agents;

import cat.urv.imas.Behaviours.ManagerBehaviour;
import cat.urv.imas.Utils.AppConfig;
import cat.urv.imas.Utils.Helper;
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
import java.util.UUID;

public class ManagerAgent extends Agent {
    HashMap<String, AppConfig> applications;

    protected void setup() {
        this.applications = new HashMap<String, AppConfig>();
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
            UUID uuid = UUID.randomUUID();
            fuzzyAgents[i] = "FuzzyAgent_" + i + "_" + appConfig.getApplication() + "_" + uuid;
            try {
                agentControllers[i] = cc.createNewAgent(fuzzyAgents[i], "cat.urv.imas.Agents.FuzzyAgent", args);
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
        AppConfig app = this.getApplication(application);
        if (app != null) {
            for (AgentController ac : app.getControllers()) {
                try {
                    ac.kill();
                } catch (StaleProxyException e) {
                    Helper.error("Error while killing agent.");
                }
            }
        }
    }

    public boolean existsApplication(String application) {
        return this.getApplication(application) != null;
    }

    public AppConfig getApplication(String application) {
        for (HashMap.Entry<String, AppConfig> app : applications.entrySet()) {
            if (application.equals(app.getKey())) return app.getValue();
        }
        return null;
    }
}