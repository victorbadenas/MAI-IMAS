package Utils;

public class InferenceResult {
    private boolean successful = false;
    private double result = -1.0d;

    public InferenceResult(boolean successful, double result){
        this.successful = successful;
        this.result = result;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public double getResult() {
        return result;
    }
}
