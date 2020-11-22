package Behaviours;

import Agents.ManagerAgent;
import Utils.Helper;
import Utils.AppConfig;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class ManagerBehaviour extends CyclicBehaviour {
    private final ManagerAgent myAgent;
    private ArrayList<double[]> results;
    private double[] aggregatedResults;

    public ManagerBehaviour (ManagerAgent agent) {
        super(agent);
        myAgent = agent;
    }

    @Override
    public void action() {
        // wait UserAgent message
        ACLMessage msg = Helper.receiveMessage(myAgent);
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                String petition = msg.getContent();
                Helper.log(myAgent, "Petition " + petition + " received from UserAgent.");
                if (petition.startsWith("I_")) {
                    initializeApplication(petition);
                    Helper.sendReply(myAgent, msg, ACLMessage.CONFIRM, "Agents successfully initialized.");
                } else {
                    ArrayList<String> requestConfig = Helper.readFile("files/" + Helper.getFilenameFromPetition(petition));
                    String applicationRequest = requestConfig.get(0);
                    if (myAgent.existsApplication(applicationRequest)) {
                        AppConfig application = myAgent.getApplication(applicationRequest);
                        runFuzzyInference(application, requestConfig);
                        waitForResults(application);
                        if (!results.isEmpty()) {
                            aggregateResults(application.getAggregation());
                            Helper.writeFile("files/result.txt", aggregatedResults);
                            Helper.sendReply(myAgent, msg, ACLMessage.CONFIRM, "files/result.txt");
                        }
                    } else {
                        Helper.sendReply(myAgent, msg, ACLMessage.FAILURE, "The application requested is not initialized.");
                    }
                }
            }
        }
    }

    private void waitForResults(AppConfig application) {
        results = new ArrayList<>();
        for (int i = 0; i < application.getNumberOfAgents(); i++) {
            ACLMessage response = Helper.receiveMessage(myAgent);
            if (response.getPerformative() == ACLMessage.INFORM) {
                String[] result = response.getContent().split(" ");
                double[] doubleResult = new double[result.length];
                for (int j = 0; j < doubleResult.length; j++) {
                    doubleResult[j] = Double.parseDouble(result[j]);
                }
                results.add(doubleResult);
            }
        }
    }

    private void initializeApplication(String petition) {
        AppConfig appConfig = new AppConfig("files/" + Helper.getFilenameFromPetition(petition));
        myAgent.createFuzzyAgents(appConfig);
    }

    private void runFuzzyInference(AppConfig application, ArrayList<String> requestConfig) {
        StringBuilder query = new StringBuilder();
        for (int i = 1; i < requestConfig.size(); i++) {
            query.append(requestConfig.get(i)).append(" ");
        }
        System.out.println(query);
        for (String fuzzyAgent : application.getFuzzyAgents()) {
            Helper.sendMessage(myAgent, ACLMessage.REQUEST, fuzzyAgent + "@" + myAgent.getContainerController().getName(), query.toString());
        }
    }

    private void aggregateResults(String aggregation) {
        int numberOfQueries = results.get(0).length;
        aggregatedResults = new double[numberOfQueries];
        switch (aggregation) {
            case "average":
                for (int i = 0; i < numberOfQueries; i++) {
                    for (double[] d : results) {
                        aggregatedResults[i] += d[i];
                    }
                    aggregatedResults[i] /= results.size();
                }
                break;
        }
    }
}
