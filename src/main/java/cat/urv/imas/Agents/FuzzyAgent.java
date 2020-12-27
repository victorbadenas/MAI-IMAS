package cat.urv.imas.Agents;
import cat.urv.imas.Behaviours.FuzzyAgentBehaviour;
import cat.urv.imas.Utils.InferenceResult;
import cat.urv.imas.Utils.Helper;
import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import net.sourceforge.jFuzzyLogic.FIS;

import java.util.ArrayList;
import java.util.Arrays;


public class FuzzyAgent extends Agent {
    private String fisFileName;
    private FIS fis;
    private String[] inputVariableNames;
    private String[] outputVariableNames;

    private static final String FILES_DIR = "files";

    protected void setup() throws ExceptionInInitializerError{
        Object[] args = getArguments();
        String fcl = args[0].toString();
        this.fisFileName = FILES_DIR + "/" + fcl + ".fcl";
        Helper.log("Initializing FuzzyAgent with name: " + getAID().getLocalName());
        this.loadFis();
        this.register();
        this.addBehaviour(new FuzzyAgentBehaviour(this));
    }

    private void register() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("FuzzyAgent");
        sd.setName(getName());
        sd.setOwnership("IMAS_group");
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
            doDelete();
        }
    }

    private void loadFis() throws ExceptionInInitializerError{
        Helper.log("Loading FIS file from: " + this.fisFileName);
        try {
            this.fis = FIS.load(this.fisFileName);
            if (this.fis == null) {
                throw new ExceptionInInitializerError("FIS loading has returned null");
            }
            this.getInputVariableNames();
            this.getOutputVariableNames();
            Helper.debug("FIS file loaded!");
        } catch (Exception e) {
            Helper.error("FIS loading FAILED");
            Helper.error(e.getMessage());
        }
    }

    private void getInputVariableNames() {
        ArrayList<String> inputVariablesList = new ArrayList<String>();
        String[] fisData = this.fis.toStringFcl().split("\n");
        for (int i = Arrays.asList(fisData).indexOf("VAR_INPUT") + 1; i<fisData.length; i++){
            if (fisData[i].equals("END_VAR")){
                break;
            }
            String element = fisData[i].replace("\t", "").split(":")[0].replace(" ","");
            inputVariablesList.add(element);
        }
        this.inputVariableNames = inputVariablesList.toArray(new String[0]);
    }

    private void getOutputVariableNames() {
        ArrayList<String> outputVariableList = new ArrayList<String>();
        String[] fisData = this.fis.toStringFcl().split("\n");
        for (int i = Arrays.asList(fisData).indexOf("VAR_OUTPUT") + 1; i<fisData.length; i++){
            if (fisData[i].equals("END_VAR")){
                break;
            }
            String element = fisData[i].replace("\t", "").split(":")[0].replace(" ","");
            outputVariableList.add(element);
        }
        this.outputVariableNames = outputVariableList.toArray(new String[0]);
    }

    @Override
    protected void takeDown() {
        Helper.log("Killing fuzzy agent with FIS file: " + this.fisFileName);
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
        fis = null;
        fisFileName = null;
        System.gc();
        Helper.log("Agent killed.");
    }

    public double[] parseDoubleMessage(String message, String separator){
        String[] parts;
        double[] doubleParts = null;
        try {
            parts = message.split(separator);
            doubleParts = new double[parts.length];
            for(int i = 0; i < parts.length; i++) {
                doubleParts[i] = Double.parseDouble(parts[i]);
            }
        } catch (Exception e) {
            Helper.error(String.format("Value parsing failed. Expected comma separated values. Recieved %s", message));
            Helper.error(e.getMessage());
        }
        return doubleParts;
    }

    public InferenceResult inferFCL(double[] inputValues) {
        double[] outputValues = new double[this.outputVariableNames.length];
        Helper.log("Inferring from data: " + Helper.arrayToString(inputValues));
        try {
            for (int i=0; i< this.inputVariableNames.length; i++) {
                this.fis.setVariable(this.inputVariableNames[i], inputValues[i]);
            }

            this.fis.evaluate();

            for (int i=0; i < this.outputVariableNames.length; i++) {
                outputValues[i] = this.fis.getVariable(this.outputVariableNames[i]).getLatestDefuzzifiedValue();
            }

            Helper.log("Inferred correctly. Result: " + Helper.arrayToString(outputValues));

            return new InferenceResult(true, outputValues);
        } catch (Exception e) {
            Helper.error("Error found when inferring");
            Helper.error(e.getMessage());
            return new InferenceResult(false, new double[0]);
        }
    }
}
