package Behaviours;

import Agents.ManagerAgent;
import Utils.utils;
import Utils.Config;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;


enum InitiatorState {
    IDLE,
    INITIALIZED
}

public class ManagerBehaviour extends CyclicBehaviour {
    private ManagerAgent myAgent;
    private InitiatorState state;

    public ManagerBehaviour (ManagerAgent agent) {
        super(agent);
        myAgent = agent;
        state = InitiatorState.IDLE;
    }

    @Override
    public void action() {
        ACLMessage msg = receiveMessage(myAgent);
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                String petition = msg.getContent();
                log(myAgent, "Petition " + petition + " received from UserAgent.");

                if (validPetition(petition, this.state)) {
                    if (petition.startsWith("I_")) {
                        initializeFuzzyAgents(petition);
                        this.state = INITIALIZED;
                        sendReply(myAgent, msg, ACLMessage.CONFIRM, null);
                    } else {
                        int agentsNumber, inputs = runFuzzyInference(petition);
                        waitForResults(agentsNumber, inputs);
                        double result = aggregateResults();
                        writeFile("files/result.txt", String.valueOf(result));
                        sendReply(myAgent, msg, ACLMessage.CONFIRM, "files/result.txt");
                    }
                }                
            }
        }
    }

    private void initializeFuzzyAgents(String petition) {
        Config config = new Config("files/" + getFilenameFromPetition(petition));
        myAgent.createFuzzyAgents(config);
    }

    private (int, int) runFuzzyInference(String petition) {
        ArrayList<String> inputs = readFile(content);
        int agentsNumber = myAgent.getFuzzyAgents(inputs[0]);
        for (int j = 0; j < agentsNumber; j++) {
            for (int i = 1; i < inputs.size(); i++)
                sendMessage(myAgent, ACLMessage.REQUEST, "FuzzyAgent" + String.valueOf(i) + "@imas-platform", inputs[i]);
        }
        return (agentsNumber, inputs.size()-1)
    }

    private void waitForResults(String application, int agentsNumber, int inputsNumber) {
        int i = agentsNumber * inputsNumber;
        while (i > 0) {
            ACLMessage response = receiveMessage(myAgent);
            myAgent.addResult(application, response.getSender().getName(), Double.valueOf(response.getContent()));
            i--;
        }
    }

    private double aggregateResults(String application, int agentsNumber, int inputsNumber) {
        double total = 0.0;
        if (myAgent.getAggregation().equals("average")) {
            for (int i = 0; i < agentsNumber; j++) {
                double agentTotalResult = 0.0;
                for (double result : myAgent.getResults(application, "FuzzyAgent" + String.valueOf(i))) {
                    agentTotalResult += result;
                }
                total += (agentTotalResult / inputsNumber);
            }
            total \= agentsNumber;
        }
        return total;
    }
}
