package Agents;

import Agents.FuzzyAgent;
//import Behaviours.ReceiverBehaviour;
import Behaviours.ManagerBehaviour;
import Behaviours.Utils;
import Utils.Config;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.util.Logger;
import java.util.HashMap;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.List;
import java.util.Set;

public class ManagerAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    HashMap<String, List<double>> fuzzyAgents;
    private String aggregation;

    protected void setup() {
        Utils.log(this, "Hello World!");

        DFAgentDescription dfd = new DFAgentDescription();
        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        this.addBehaviour(new ManagerBehaviour(this));
    }

    public void createFuzzyAgents(Config config) {
        this.aggregation = config.aggregation;
        ContainerController cc = getContainerController();
        String[] fuzzyAgents = config.getFuzzySettings().split(",");
        Object args = new Object[1];
        args[0] = config.getApplication();
        for(String fuzzyAgent : fuzzyAgents) {
            try {
                this.addFuzzyAgent(fuzzyAgent);
                AgentController ac = cc.createNewAgent(fuzzyAgent, "Agents.FuzzyAgent", args);
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

    public Logger getLogger() {
        return myLogger;
    }

    public void addFuzzyAgent(String fuzzyAgent) {
        this.fuzzyAgents.put(fuzzyAgent, new List<double>());
    }

    public Set<String> getFuzzyAgents() {
        return this.fuzzyAgents.keySet();
    }

    public void addResult(String fuzzyAgent, double result) {
        this.fuzzyAgents.get(fuzzyAgent).append(result);
    }

}