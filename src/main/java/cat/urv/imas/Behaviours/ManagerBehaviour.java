package cat.urv.imas.Behaviours;

import cat.urv.imas.Agents.ManagerAgent;
import cat.urv.imas.Utils.Helper;
import cat.urv.imas.Utils.AppConfig;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class ManagerBehaviour extends CyclicBehaviour {
    private final ManagerAgent myAgent;
    private ArrayList<double[]> aggregatedResults;

    private static final String RESULT_FILE = "result.txt";
    private static final String FILES_DIR = "files";
    private static final String RESULT_FILE = "result.txt";

    public ManagerBehaviour (ManagerAgent agent) {
        super(agent);
        myAgent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = Helper.receiveMessage(myAgent);
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                String petition = msg.getContent();
                Helper.log("Petition " + petition + " received from UserAgent.");
                if (petition.startsWith("I_")) {
                    initializeApplication(petition);
                    Helper.sendReply(myAgent, msg, ACLMessage.CONFIRM, "Agents successfully initialized.");
                } else {
                    ArrayList<String> requestConfig = Helper.readFile(FILES_DIR + "/" + Helper.getFilenameFromPetition(petition));
                    String applicationRequest = requestConfig.get(0);
                    if (myAgent.existsApplication(applicationRequest)) {
                        AppConfig application = myAgent.getApplication(applicationRequest);
                        runFuzzyInference(application, requestConfig);
                        HashMap<String, ArrayList<double[]>> results = waitForResults(application);
                        if (!results.isEmpty()) {
                            aggregateResults(results, application.getAggregation());
                            Helper.writeFile(FILES_DIR + "/" + RESULT_FILE, aggregatedResults);
                            Helper.sendReply(myAgent, msg, ACLMessage.CONFIRM, "Inference results stored at '" + FILES_DIR + "/" + RESULT_FILE + "'");
                        }
                    } else {
                        Helper.sendReply(myAgent, msg, ACLMessage.FAILURE, "The application requested is not initialized.");
                    }
                }
            }
        }
    }

    private void initializeApplication(String petition) {
        AppConfig appConfig = new AppConfig(FILES_DIR + "/" + Helper.getFilenameFromPetition(petition));
        myAgent.createFuzzyAgents(appConfig);
    }

    private HashMap<String, ArrayList<double[]>> waitForResults(AppConfig application) {
        HashMap<String, ArrayList<double[]>> results = new HashMap<String, ArrayList<double[]>>();
        for (int i = 0; i < application.getNumberOfAgents(); i++) {
            ACLMessage response = Helper.receiveMessage(myAgent);
            if (response.getPerformative() == ACLMessage.INFORM) {
                String sender = response.getSender().getName();
                String[] result = response.getContent().split(" ");
                ArrayList<double[]> fuzzyAgentResponse = new ArrayList<double[]>();
                for (int k = 0; k<result.length; k++){
                    String[] valuesAsString = result[k].split(",");
                    for (int j = 0; j<valuesAsString.length; j++){
                        if (k == 0) fuzzyAgentResponse.add(new double[result.length]);
                        fuzzyAgentResponse.get(j)[k] = Double.parseDouble(valuesAsString[j]);
                    }
                }
                results.put(sender, fuzzyAgentResponse);
            }
        }
        return results;
    }

    private void runFuzzyInference(AppConfig application, ArrayList<String> requestConfig) {
        StringBuilder query = new StringBuilder();
        for (int i = 1; i < requestConfig.size(); i++) {
            query.append(requestConfig.get(i)).append(" ");
        }
        for (String fuzzyAgent : application.getFuzzyAgents()) {
            Helper.sendMessage(myAgent, ACLMessage.REQUEST, fuzzyAgent + "@" + myAgent.getContainerController().getName(), query.toString());
        }
    }

    private void aggregateResults(HashMap<String, ArrayList<double[]>> results, String aggregation) {
        int numFuzzyAgents = results.size();
        aggregatedResults = new ArrayList<double[]>();
        if (aggregation.equals("average")) {
            for (ArrayList<double[]> fuzzyAgentInferences : results.values()){
                for (int i = 0; i<fuzzyAgentInferences.size(); i++){
                    if (aggregatedResults.size() < i + 1) aggregatedResults.add(new double[fuzzyAgentInferences.get(i).length]);
                    for (int j = 0; j<fuzzyAgentInferences.get(i).length; j++){
                        aggregatedResults.get(i)[j] += fuzzyAgentInferences.get(i)[j];
                    }
                }
            }
            for (int i = 0; i < aggregatedResults.size(); i++) {
                for (int j = 0; j < aggregatedResults.get(i).length; j++) {
                    aggregatedResults.get(i)[j] /= numFuzzyAgents;
                }
            }
        }
    }
}
