package Behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.io.File;

public class Utils {
    private static final String log = "[%s] %s";

    public static void sendMessage(Agent agent, int type, String receiver, String content) {
        ACLMessage msg = new ACLMessage(type);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        AID a = new AID(receiver);
        msg.addReceiver(a);
        msg.setContent(content);
        agent.send(msg);
    }

    public static void sendReply(Agent agent, ACLMessage msg, int type, String content) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(type);
        reply.setContent(content);
        agent.send(reply);
    }

    public static ACLMessage receiveMessage (Agent a) {
        return a.blockingReceive();
    }

    public static void log (Agent a, String message) {
        System.out.println(String.format(log, a.getLocalName(), message));
    }

    public static boolean validPetition (Agent a, String petition) {
        String filename = petition.substring(2);
        File checkFile = new File("files/" + filename);

        if ((petition.startsWith("I_") || petition.startsWith("D_")) && checkFile.exists()) {
            Utils.log(a, "Sending petition " + petition + " to ManagerAgent.");
            return true;
        } else {
            Utils.log(a, "Invalid petition: " + petition);
            return false;
        }
    }
}