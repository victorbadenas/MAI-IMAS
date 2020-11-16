package Behaviours;

import Agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;

public class WaitHumanPetitions extends CyclicBehaviour {
    public WaitHumanPetitions (UserAgent agent) {
        super(agent);
        myAgent = agent;
    }

    @Override
    public void action() {
        Scanner sc = new Scanner(System.in);
        String petition;
        do {
            Utils.log(myAgent, "Waiting for a human petition...");
            petition = sc.next();
        } while (!Utils.validPetition(myAgent, petition));

        // send petition to Manager Agent
        Utils.sendMessage(myAgent, ACLMessage.REQUEST, "ManagerAgent@imas-platform", petition);

        if (petition.startsWith("I_")) {
            // wait message from Agents.ManagerAgent
            ACLMessage response = Utils.receiveMessage(myAgent);
            if (response.getPerformative() == ACLMessage.CONFIRM) {
                Utils.log(myAgent, "The system has been successfully initialized.");
            }
        } else {
            ACLMessage response = Utils.receiveMessage(myAgent);
            //TODO: Here we should show the final results
            if (response.getPerformative() == ACLMessage.CONFIRM) {
                Utils.log(myAgent, "The final results are in file XXXXXXX.");
            }
        }
    }
}

/*

        System.out.println("Waiting a petition...");
        String petition = sc.next();

        System.out.println("Sending petition to Manager: " + petition);
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent(petition);
        msg.addReceiver(new AID("ManagerAgent", AID.ISLOCALNAME));
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        myAgent.send(msg);
        ACLMessage reply = myAgent.blockingReceive();
        System.out.println("Reply: " + reply.getContent());

*/

/*
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;

*/