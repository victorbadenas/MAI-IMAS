package Agents;
import Behaviours.FIPAReciever;
import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;

public class FuzzyAgent extends Agent {
    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    protected void setup() {
        // saying hello to the world
        System.out.println();
        System.out.println("Initializing User Agent with name: " + getAID().getLocalName());
        System.out.println();

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("FuzzyAgent");
        sd.setName(getName());
        sd.setOwnership("imas-platform");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            addBehaviour(new FIPAReciever(this));
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "[" + getLocalName() + "] - Cannot register with DF", e);
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }
}
