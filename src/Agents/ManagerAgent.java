package Agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

public class ManagerAgent extends Agent {
    protected void setup() {
        // saying hello to the world
        System.out.println();
        System.out.println("Initializing Manager Agent with name: " + getAID().getLocalName());
        System.out.println();

        // registering agent to DF
        DFAgentDescription dfd = new DFAgentDescription();
        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}