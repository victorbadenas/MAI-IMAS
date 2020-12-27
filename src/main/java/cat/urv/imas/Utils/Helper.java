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

//import jade.util.Logger;
//import java.util.logging.LogManager;
//import java.util.logging.Logger;
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
        String filename = getFilenameFromPetition(petition);
        return new File("files/" + filename).exists();
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

    public static void writeFile(String fileName, AppConfig application, ArrayList<String> requestConfig, ArrayList<double[]> content, long timeElapsed) {
        new File("./results").mkdir();
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(application.toString());
            writer.println("Elapsed Time:      " + timeElapsed + " ms\n");
            for (int i=0; i<content.get(0).length; i++){
                StringBuilder sb = new StringBuilder("**********   SUBREQUEST #").append(i + 1).append("    **********\n");
                sb.append("Input:   ").append(requestConfig.get(i + 1)).append("\n");
                sb.append("Output:  ");
                for (int j=0; j<content.size(); j++) {
                    if (j < content.size() - 1) sb.append(content.get(j)[i]).append(",");
                    else sb.append(content.get(j)[i]);
                }
                sb.append("\n****************************************\n");
                writer.println(sb.toString());
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not write results file: " + e.toString());
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