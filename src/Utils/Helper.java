package Utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import jade.util.Logger;


public class Helper {

    private static final String logFormat = "[%s] %s";
    private static Logger myLogger = Logger.getMyLogger("IMAS_group");

    /**
     * Send message to Agent.
     *
     * @param agent    Agent that sends the message
     * @param type     Type of the message (Should be an ACLMessage constant)
     * @param receiver The receiver of the message. To get the name of an agent use getLocalName() function
     * @param content  Content of the message
     */
    public static void sendMessage(Agent agent, int type, String receiver, String content) {
        ACLMessage msg = new ACLMessage(type);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.addReceiver(new AID(receiver));
        msg.setContent(content);
        agent.send(msg);
    }

    /**
     * Same as sendMessage function but without receiver, because the receiver will be the Agent that has sent the message.
     *
     * @param agent   Agent that sends the message
     * @param msg     Message to be responded
     * @param type    Type of the message (Should be an ACLMessage constant)
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
     *
     * @param a Agent that receives the message
     * @return The message received by a
     */
    public static ACLMessage receiveMessage(Agent a) {
        return a.blockingReceive();
    }

    public static boolean isValidPetition(Agent a, String petition) {
        String filename = getFilenameFromPetition(petition);
        File checkFile = new File("files/" + filename);

        return (petition.startsWith("I_") || petition.startsWith("D_")) && checkFile.exists();
    }

    public static String getFilenameFromPetition(String petition) {
        return petition.substring(2);
    }

    public static ArrayList<String> readFile(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            ArrayList<String> inputs = new ArrayList<String>();

            String line = br.readLine();
            while (line != null) {
                inputs.add(line);
                line = br.readLine();
            }
            br.close();
            return inputs;
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }

    public static void writeFile(String fileName, double[] content) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            for(Double d : content) {
                writer.println(d);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Could not write results file.");
        }
    }

    /**
     * Prints a log
     *
     * @param a       Agent that prints the log
     * @param message Message of the log
     */
    public static void log(Agent a, String message) {
        myLogger.log(Logger.INFO, String.format(logFormat, a.getLocalName(), message));
    }

    /**
     * Prints a log
     *
     * @param a       Agent that prints the log
     * @param message Message of the log
     */
    public static void warn(Agent a, String message) {
        myLogger.log(Logger.WARNING, String.format(logFormat, a.getLocalName(), message));
    }

    /**
     * Prints a log
     *
     * @param a       Agent that prints the log
     * @param message Message of the log
     */
    public static void error(Agent a, String message) {
        myLogger.log(Logger.SEVERE, String.format(logFormat, a.getLocalName(), message));
    }

    public static String arrayToString(double[] doubleArray) {
        StringBuilder sb = new StringBuilder();
        for (double s : doubleArray) {
            sb.append(String.valueOf(s)).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}