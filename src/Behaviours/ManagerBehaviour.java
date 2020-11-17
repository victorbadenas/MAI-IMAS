package Behaviours;

import Agents.ManagerAgent;
import Utils.Utils;
import Utils.Config;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;


enum ManagerInitiatorState {
    IDLE,
    INITIALIZED
}

public class ManagerBehaviour extends CyclicBehaviour {
    private ManagerAgent myAgent;
    private ManagerInitiatorState state;

    public ManagerBehaviour (ManagerAgent agent) {
        super(agent);
        myAgent = agent;
        state = ManagerInitiatorState.IDLE;
    }

    @Override
    public void action() {
        ACLMessage msg = Utils.receiveMessage(myAgent);
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                String petition = msg.getContent();
                Utils.log(myAgent, "Petition " + petition + " received from UserAgent.");
                if (Utils.validPetition(myAgent, petition, this.state.ordinal())) {
                    if (petition.startsWith("I_")) {
                        initializeFuzzyAgents(petition);
                        this.state = ManagerInitiatorState.INITIALIZED;
                        Utils.sendReply(myAgent, msg, ACLMessage.CONFIRM, null);
                    } else {
                        ArrayList<String> inputs = Utils.readFile("files/" + Utils.getFilenameFromPetition(petition));
                        int agentsNumber = myAgent.getFuzzyAgents(inputs.get(0));
                        runFuzzyInference(inputs, agentsNumber);
                        waitForResults(inputs.get(0), agentsNumber, inputs.size()-1);
                        double result = aggregateResults(inputs.get(0), agentsNumber, inputs.size()-1);
                        Utils.writeFile("files/result.txt", String.valueOf(result));
                        Utils.sendReply(myAgent, msg, ACLMessage.CONFIRM, "files/result.txt");
                    }
                }                
            }
        }
    }

    private void initializeFuzzyAgents(String petition) {
        Config config = new Config("files/" + Utils.getFilenameFromPetition(petition));
        myAgent.createFuzzyAgents(config);
    }

    private void runFuzzyInference(ArrayList<String> inputs, int agentsNumber) {
        for (int j = 0; j < agentsNumber; j++) {
            for (int i = 1; i < inputs.size(); i++)
            Utils.sendMessage(myAgent, ACLMessage.REQUEST, "FuzzyAgent" + String.valueOf(j) + "@imas-platform", inputs.get(i));
        }
    }

    private void waitForResults(String application, int agentsNumber, int inputsNumber) {
        int i = agentsNumber * inputsNumber;
        while (i > 0) {
            ACLMessage response = Utils.receiveMessage(myAgent);
            if (response.getPerformative() == ACLMessage.INFORM) {
                myAgent.addResult(application, response.getSender().getName().split("@")[0], Double.valueOf(response.getContent()));
                i--;
            }
        }
    }

    private double aggregateResults(String application, int agentsNumber, int inputsNumber) {
        double total = 0.0;
        if (myAgent.getAggregation(application).equals("average")) {
            for (int i = 0; i < agentsNumber; i++) {
                double agentTotalResult = 0.0;
                for (double result : myAgent.getResults(application, "FuzzyAgent" + String.valueOf(i))) {
                    agentTotalResult += result;
                }
                total += (agentTotalResult / inputsNumber);
            }
            total = total / agentsNumber;
        }
        return total;
    }
}
