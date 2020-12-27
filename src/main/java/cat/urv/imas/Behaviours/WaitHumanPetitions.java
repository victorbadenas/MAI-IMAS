package cat.urv.imas.Behaviours;

import cat.urv.imas.Utils.Helper;
import cat.urv.imas.Agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

public class WaitHumanPetitions extends CyclicBehaviour {
    private final UserAgent myAgent;
    private ConsoleReader reader;

    public WaitHumanPetitions(UserAgent a) {
        super(a);
        myAgent = a;
        try {
            reader = new ConsoleReader();
            reader.setPrompt("\u001B[36mhuman\u001B[0m> ");
            reader.clearScreen();
        } catch (Throwable t) {
            t.printStackTrace();
            reader = null;
        }
    }

    @Override
    public void action() {
        String petition = null;
        boolean validPetition = false;

        while (!validPetition) {
            Helper.log("Waiting for a human petition...");
            try {
                reader.addCompleter(new StringsCompleter("I_configuration1.txt", "I_configuration2.txt", "I_car_configuration_10.txt", "I_car_configuration_15.txt", "I_car_configuration_5.txt"));
                reader.addCompleter(new StringsCompleter("D_requests_quality.txt", "D_requests_tipper.txt", "D_car_requests.txt"));
                petition = reader.readLine().trim();
            } catch (Exception e) {
                Scanner sc = new Scanner(System.in);
                petition = sc.next().trim();
            }
            validPetition = Helper.isValidPetition(myAgent, petition);
            if (!validPetition) {
                Helper.error("Invalid petition! Try again.");
            }
        }

        Helper.sendMessage(myAgent, ACLMessage.REQUEST, "ManagerAgent@" + myAgent.getContainerController().getName(), petition);
        ACLMessage response = Helper.receiveMessage(myAgent);
        if (response.getPerformative() == ACLMessage.CONFIRM) {
            Helper.log(response.getContent());
        } else {
            Helper.error(response.getContent());
        }
    }
}