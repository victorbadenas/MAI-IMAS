package Agents;
import Behaviours.FIPAReciever;
import Utils.InferenceResult;
import jade.core.*;
import net.sourceforge.jFuzzyLogic.FIS;


public class FuzzyAgent extends Agent {
    private String fisFileName;
    private FIS fis;

    protected void setup(String fisFileName) {
        this.fisFileName = fisFileName;
        System.out.println("Initializing User Agent with name: " + getAID().getLocalName());

        System.out.println("Loading FIS file from: " + this.fisFileName);
        this.fis = FIS.load(this.fisFileName);
        System.out.println("FIS file loaded!");

        this.addBehaviour(new FIPAReciever(this));
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        fis = null;
        fisFileName = null;
        System.gc();
    }

    protected InferenceResult inferFCL(double humidity, double temperature) {
        try{
            this.fis.setVariable("humidity", humidity);
            this.fis.setVariable("temperature", temperature);
            this.fis.evaluate();
            return new InferenceResult(true, this.fis.getVariable("output").getLatestDefuzzifiedValue());
        } catch (Exception e) {
            System.out.println(String.format("ERROR: Error found when infering %d and %d", humidity, temperature));
            return new InferenceResult(false, this.fis.getVariable("output").getLatestDefuzzifiedValue());
        }
    }
}
