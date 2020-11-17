package Behaviours;

import Utils.utils;
import Agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;

enum InitiatorState {
    IDLE,
    INITIALIZED
}

public class WaitHumanPetitions extends CyclicBehaviour {
    private UserAgent myAgent;
    private InitiatorState state;

    public WaitHumanPetitions(UserAgent a) {
        super(a);
        myAgent = a;
        state = InitiatorState.IDLE;
    }

    @Override
    public void action() {
        Scanner sc = new Scanner(System.in);
        String petition;
        do {
            log(myAgent, "Waiting for a human petition...");
            petition = sc.next();
        } while (!validPetition(petition, this.state));

        this.state = INITIALIZED;
        sendMessage(myAgent, ACLMessage.REQUEST, "ManagerAgent@imas-platform", petition);

        if (petition.startsWith("I_")) {
            // wait message from Agents.ManagerAgent
            ACLMessage response = receiveMessage(myAgent);
            if (response.getPerformative() == ACLMessage.CONFIRM) {
                log(myAgent, "The system has been successfully initialized.");
            }
        } else {
            ACLMessage response = receiveMessage(myAgent);
            //TODO: Here we should show the final results
            if (response.getPerformative() == ACLMessage.CONFIRM) {
                log(myAgent, "The final results are in file '" + response.getContent() + "'.");
            }
        }
    }
}