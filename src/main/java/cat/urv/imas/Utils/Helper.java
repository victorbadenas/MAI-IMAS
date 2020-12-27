package cat.urv.imas.Utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Helper {

    private static Logger myLogger = LogManager.getLogger();

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
        if (petition.length() < 3 || !(petition.startsWith("I_") || petition.startsWith("D_")))
            return false;
        return new File("files/" + petition.substring(2)).exists();
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
            this.error("Could not read file with name: '" + fileName + "'");
            return new ArrayList<String>();
        }
    }

    public static void writeFile(String fileName, ArrayList<double[]> content) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            for (int i=0; i<content.get(0).length; i++){
                for (int j=0; j<content.size(); j++) {
                    if (j < content.size() - 1) writer.print(content.get(j)[i] + ",");
                    else writer.println(content.get(j)[i]);
                }
            }
            writer.close();
        } catch (Exception e) {
            this.error("Could not write results file.");
        }
    }

    public static void debug(String message) {
        myLogger.debug(message);
    }

    /**
     * Prints a log
     *
     * @param a       Agent that prints the log
     * @param message Message of the log
     */
    public static void log(String message) {
        myLogger.info(message);
    }

    /**
     * Prints a log
     *
     * @param a       Agent that prints the log
     * @param message Message of the log
     */
    public static void warn(String message) {
        myLogger.warn(message);
    }

    /**
     * Prints a log
     *
     * @param a       Agent that prints the log
     * @param message Message of the log
     */
    public static void error(String message) {
        myLogger.error(message);
    }

    public static String arrayToString(double[] doubleArray) {
        StringBuilder sb = new StringBuilder();
        for (double d : doubleArray) {
            sb.append(d).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}