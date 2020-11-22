package Behaviours;

import Utils.Helper;
import Agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;

public class WaitHumanPetitions extends CyclicBehaviour {
    private final UserAgent myAgent;

    public WaitHumanPetitions(UserAgent a) {
        super(a);
        myAgent = a;
    }

    @Override
    public void action() {
        Scanner sc = new Scanner(System.in);
        String petition = null;
        boolean validPetition = false;

        while (!validPetition) {
            Helper.log(myAgent, "Waiting for a human petition...");
            petition = sc.next();
            validPetition = Helper.isValidPetition(myAgent, petition);
            if (!validPetition) {
                Helper.error(myAgent, "Invalid petition! Try again.");
            }
        }

        Helper.sendMessage(myAgent, ACLMessage.REQUEST, "ManagerAgent@" + myAgent.getContainerController().getName(), petition);
        ACLMessage response = Helper.receiveMessage(myAgent);
        if (response.getPerformative() == ACLMessage.CONFIRM) {
            Helper.log(myAgent, response.getContent());
        } else {
            Helper.error(myAgent, response.getContent());
        }
    }
}