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

    private static final String HELP_MSG = "Usage: [I_|D_]<filename>\n\n" +
                                          "\t I\t\tInitialize the system using a configuration file.\n\t\t\tE.g. I_configuration1.txt\n" +
                                          "\t D\t\tRun inference in an initialized system using an input file.\n\t\t\tE.g. D_requests_tipper.txt\n" +
                                          "\t help\t\tShows this help output\n\n" +
                                          "You can find several configuration and input files in the 'files' directory.\n" + 
                                          "Autocomplete mode ON: You can use <tab> in order to show different autocomplete options.";
    private static final String EXIT_MSG = "In order to exit the system, please run: <Ctrl> + <C> + <Enter>";
    private static final String INVALID_MSG = "Invalid petition -- \nTry 'help' for further information.";

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

        Helper.log("Waiting for a human petition...");

        while (!validPetition) {
            try {
                reader.addCompleter(new StringsCompleter("I_configuration1.txt", 
                                                         "I_configuration2.txt", 
                                                         "I_car_configuration_5.txt", 
                                                         "I_car_configuration_10.txt", 
                                                         "I_car_configuration_15.txt"));
                reader.addCompleter(new StringsCompleter("D_requests_quality.txt", 
                                                         "D_requests_tipper.txt", 
                                                         "D_car_requests.txt"));
                petition = reader.readLine().trim();
            } catch (Exception e) {
                Scanner sc = new Scanner(System.in);
                petition = sc.next().trim();
            }
            validPetition = Helper.isValidPetition(myAgent, petition);
            this.cliOptions(validPetition, petition);
        }

        Helper.sendMessage(myAgent, ACLMessage.REQUEST, "ManagerAgent@" + myAgent.getContainerController().getName(), petition);
        ACLMessage response = Helper.receiveMessage(myAgent);
        if (response.getPerformative() == ACLMessage.CONFIRM) {
            Helper.log(response.getContent());
        } else {
            Helper.error(response.getContent());
        }
    }

    private void cliOptions(boolean validPetition, String petition) {
        if (!validPetition) {
            if ("help".equals(petition.toLowerCase())) Helper.log(HELP_MSG);
            else if ("exit".equals(petition.toLowerCase()) || "quit".equals(petition.toLowerCase())) Helper.error(EXIT_MSG);
            else Helper.error(INVALID_MSG);
        }
    }
}