package Agents;
import Behaviours.FIPAReciever;
import Utils.InferenceResult;
import Utils.Utils;
import jade.core.*;
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
        Utils.log(this, "Initializing User Agent with name: " + getAID().getLocalName());
        this.loadFis();
        this.addBehaviour(new FIPAReciever(this));
    }

    private void loadFis() throws ExceptionInInitializerError{
        Utils.log(this, "Loading FIS file from: " + this.fisFileName);
        try {
            this.fis = FIS.load(this.fisFileName);
            if (this.fis == null) {
                throw new ExceptionInInitializerError("FIS loading has returned null");
            }
            this.getInputVariableNames();
            this.getOutputVariableNames();
            Utils.log(this, "FIS file loaded!");
        } catch (Exception e) {
            Utils.error(this, "FIS loading FAILED");
            Utils.error(this, e.getMessage());
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
        Utils.log(this, "Killing fuzzy agent with FIS file: " + this.fisFileName);
        super.takeDown();
        fis = null;
        fisFileName = null;
        System.gc();
        Utils.log(this, "Agent killed.");
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
            Utils.error(this, String.format("Value parsing failed. Expected comma separated values. Recieved %s", message));
            Utils.error(this, e.getMessage());
        }
        return doubleParts;
    }

    public InferenceResult inferFCL(double[] inputValues) {
        double[] outputValues = new double[this.outputVariableNames.length];
        Utils.log(this, String.format("Inferring from data: " + Utils.arrayToString(inputValues)));
        try {
            for (int i=0; i< this.inputVariableNames.length; i++) {
                this.fis.setVariable(this.inputVariableNames[i], inputValues[i]);
            }

            this.fis.evaluate();

            for (int i=0; i < this.outputVariableNames.length; i++) {
                outputValues[i] = this.fis.getVariable(this.outputVariableNames[i]).getLatestDefuzzifiedValue();
            }

            Utils.log(this, String.format("Inferred correctly. Result: " + Utils.arrayToString(outputValues)));

            return new InferenceResult(true, outputValues);
        } catch (Exception e) {
            Utils.error(this, String.format("ERROR: Error found when inferring"));
            Utils.error(this, e.getMessage());
            return new InferenceResult(false, new double[0]);
        }
    }
}
