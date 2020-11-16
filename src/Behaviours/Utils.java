package Behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.io.File;

public class Utils {
    private static final String log = "[%s] %s";

    /**
     * Send message to Agent.
     * @param agent Agent that sends the message
     * @param type Type of the message (Should be an ACLMessage constant)
     * @param receiver The receiver of the message. To get the name of an agent use getLocalName() function
     * @param content Content of the message
     */
    public static void sendMessage(Agent agent, int type, String receiver, String content) {
        ACLMessage msg = new ACLMessage(type);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        AID a = new AID(receiver);
        msg.addReceiver(a);
        msg.setContent(content);
        agent.send(msg);
    }

    /**
     * Same as sendMessage function but without receiver, because the receiver will be the Agent that has sent the message.
     * @param agent Agent that sends the message
     * @param msg Message to be responded
     * @param type Type of the message (Should be an ACLMessage constant)
     * @param content Content of the message
     */
    public static void sendReply(Agent agent, ACLMessage msg, int type, String content) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(type);
        reply.setContent(content);
        agent.send(reply);
    }

    /**
     * Receive a message
     * @param a Agent that receives the message
     * @return The message received by a
     */
    public static ACLMessage receiveMessage (Agent a) {
        return a.blockingReceive();
    }

    /**
     * Prints a log
     * @param a Agent that prints the log
     * @param message Message of the log
     */
    public static void log (Agent a, String message) {
        System.out.println(String.format(log, a.getLocalName(), message));
    }

    /**
     * Check if a human petition is valid or not
     * @param a Agent that wants to check a petition
     * @param petition Petition to validate
     * @return True if valid, false if not valid
     */
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