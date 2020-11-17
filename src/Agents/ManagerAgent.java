package Agents;

import Agents.FuzzyAgent;
//import Behaviours.ReceiverBehaviour;
import Behaviours.ManagerBehaviour;
import Utils.utils;
import Utils.Config;
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
import java.util.Set;

public class ManagerAgent extends Agent {

    private class AgentConfig {
        private HashMap<String, ArrayList<double>> results;
        private String aggregation;

        public AgentConfig(String aggregation) {
            this.results = new HashMap<_>();
            this.aggregation = aggregation;
        }

        public void addFuzzyAgent(String name) {
            this.results.put(name, new ArrayList<double>());
        }

        public void addResult(String name, double result) {
            this.results.put(name, result);
        }

        public void getAggregation() {
            return this.aggregation;
        }

        public ArrayList<double> getResults(String name) {
            return this.results.get(name);
        } 
    }

    HashMap<String, AgentConfig> fuzzyAgents;

    protected void setup() {
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

    public void createFuzzyAgents(Config config) {
        String[] fuzzySettings = config.getFuzzySettings().split(",");
        ContainerController cc = getContainerController();
        this.fuzzyAgents.put(config.getApplication(), new AgentConfig(config.getAggregation()));

        for (int i = 0; i < config.getFuzzyAgents(); i++) {
            try {
                Object args = new Object[1];
                args[0] = fuzzySettings[i];
                String agentName = new String("FuzzyAgent" + String.valueOf(i));
                this.fuzzyAgent.get(config.getApplication()).addFuzzyAgent(agentName);
                AgentController ac = cc.createNewAgent(agentName, "Agents.FuzzyAgent", args);
                try {
                    ac.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }

    public int getFuzzyAgents(String application) {
        if (this.fuzzyAgents.get(application) == null) {
            return 0;
        } else {
            return this.fuzzyAgents.get(application).results.size();
        }
    }

    public void addResult(String application, String fuzzyAgent, double result) {
        if (this.fuzzyAgents.get(application) != null) {
            this.fuzzyAgents.get(application).addResult(fuzzyAgent, result);
        }
    }

    public String getAggregation(String application) {
        if (this.fuzzyAgents.get(application) != null) {
            return this.fuzzyAgents.get(application).getAggregation();
        }
        return new String();
    }

    public ArrayList<double> getResults(String application, String fuzzyAgent) {
        if (this.fuzzyAgents.get(application) != null) {
            return this.fuzzyAgents.get(application).getResults(fuzzyAgent);
        }
        return null;
    }
}