package Agents;
import Behaviours.FIPAReciever;
import Utils.InferenceResult;
import Utils.Utils;
import jade.core.*;
import net.sourceforge.jFuzzyLogic.FIS;


public class FuzzyAgent extends Agent {
    private String fisFileName;
    private FIS fis;

    protected void setup() {
        Object[] args = getArguments();
        String fcl = args[0].toString();
        this.fisFileName = "files/" + fcl + ".fcl";
        Utils.log(this, "Initializing User Agent with name: " + getAID().getLocalName());
        this.loadFis();
        this.addBehaviour(new FIPAReciever(this));
    }

    private void loadFis(){
        Utils.log(this, "Loading FIS file from: " + this.fisFileName);
        try {
            this.fis = FIS.load(this.fisFileName);
            Utils.log(this, "FIS file loaded!");
        } catch (Exception e) {
            Utils.error(this, "FIS loading FAILED");
            Utils.error(this, e.getMessage());
        }
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
            for(int i=0; i<parts.length; i++) {
                doubleParts[i] = Double.parseDouble(parts[i]);
            }
        } catch (Exception e) {
            Utils.error(this, String.format("Value parsing failed. Expected comma separated values. Recieved %s", message));
            Utils.error(this, e.getMessage());
        }
        return doubleParts;
    }

    public InferenceResult inferFCL(double humidity, double temperature) {
        Utils.log(this, String.format("Infering from data: %f, %f", humidity, temperature));

        try{
            this.fis.setVariable("humidity", humidity);
            this.fis.setVariable("temperature", temperature);
            this.fis.evaluate();
            double numericalResult = this.fis.getVariable("duration_period").getLatestDefuzzifiedValue();
            Utils.log(this, String.format("Infered correctly. Result: %f", numericalResult));
            return new InferenceResult(true, numericalResult);
        } catch (Exception e) {
            Utils.error(this, String.format("ERROR: Error found when infering %f and %f", humidity, temperature));
            Utils.error(this, e.getMessage());
            return new InferenceResult(false, this.fis.getVariable("duration_period").getLatestDefuzzifiedValue());
        }
    }
}
