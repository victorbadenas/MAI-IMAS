package Behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;

public class WaitHumanPetitions extends CyclicBehaviour {

    @Override
    public void action() {
        Scanner sc = new Scanner(System.in);

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
    }
}