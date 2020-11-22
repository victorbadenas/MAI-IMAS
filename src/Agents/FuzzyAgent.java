package Agents;
import Behaviours.FuzzyAgentBehaviour;
import Utils.InferenceResult;
import Utils.Helper;
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

    protected void setup() throws ExceptionInInitializerError{
        Object[] args = getArguments();
        String fcl = args[0].toString();
        this.fisFileName = "files/" + fcl + ".fcl";
        Helper.log(this, "Initializing User Agent with name: " + getAID().getLocalName());
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
        Helper.log(this, "Loading FIS file from: " + this.fisFileName);
        try {
            this.fis = FIS.load(this.fisFileName);
            if (this.fis == null) {
                throw new ExceptionInInitializerError("FIS loading has returned null");
            }
            this.getInputVariableNames();
            this.getOutputVariableNames();
            Helper.log(this, "FIS file loaded!");
        } catch (Exception e) {
            Helper.error(this, "FIS loading FAILED");
            Helper.error(this, e.getMessage());
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
        Helper.log(this, "Killing fuzzy agent with FIS file: " + this.fisFileName);
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
        fis = null;
        fisFileName = null;
        System.gc();
        Helper.log(this, "Agent killed.");
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
            Helper.error(this, String.format("Value parsing failed. Expected comma separated values. Recieved %s", message));
            Helper.error(this, e.getMessage());
        }
        return doubleParts;
    }

    public InferenceResult inferFCL(double[] inputValues) {
        double[] outputValues = new double[this.outputVariableNames.length];
        Helper.log(this, "Inferring from data: " + Helper.arrayToString(inputValues));
        try {
            for (int i=0; i< this.inputVariableNames.length; i++) {
                this.fis.setVariable(this.inputVariableNames[i], inputValues[i]);
            }

            this.fis.evaluate();

            for (int i=0; i < this.outputVariableNames.length; i++) {
                outputValues[i] = this.fis.getVariable(this.outputVariableNames[i]).getLatestDefuzzifiedValue();
            }

            Helper.log(this, "Inferred correctly. Result: " + Helper.arrayToString(outputValues));

            return new InferenceResult(true, outputValues);
        } catch (Exception e) {
            Helper.error(this, "ERROR: Error found when inferring");
            Helper.error(this, e.getMessage());
            return new InferenceResult(false, new double[0]);
        }
    }
}
