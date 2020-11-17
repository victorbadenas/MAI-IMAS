package Behaviours;
import Agents.FuzzyAgent;
import Utils.InferenceResult;
import Utils.Utils;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.Serializable;

enum ReceiverState {
    IDLE,
    FAILED,
    SUCCESS
}

public class FIPAReciever extends CyclicBehaviour {
    private FuzzyAgent myAgent;
    private ReceiverState state;
    private ACLMessage requestMsg;
    private Serializable result;

    public FIPAReciever(FuzzyAgent a) {
        super(a);
        this.myAgent = a;
        this.state = ReceiverState.IDLE;
    }

    @Override
    public void action() {
        ACLMessage msg;
        switch(state) {
            case IDLE:
                msg = Utils.receiveMessage(this.myAgent);
                if (msg != null) {
                    try {
                        if (msg.getPerformative() == ACLMessage.REQUEST){
                            this.requestMsg = msg;
                            String args = msg.getContent();

                            double[] inferenceArguments = myAgent.parseDoubleMessage(args, ",");

                            InferenceResult res = myAgent.inferFCL(inferenceArguments[0], inferenceArguments[1]);
                            if (res.isSuccessful()) {
                                this.state = ReceiverState.SUCCESS;
                            }
                            else {
                                this.state = ReceiverState.FAILED;
                            }
                            this.result = res.getResult();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case FAILED:
                Utils.sendReply(this.myAgent, this.requestMsg, ACLMessage.FAILURE, String.valueOf(this.result));
                this.state = ReceiverState.IDLE;
                break;

            case SUCCESS:
                Utils.sendReply(this.myAgent, this.requestMsg, ACLMessage.INFORM, String.valueOf(this.result));
                this.state = ReceiverState.IDLE;
                break;
        }
    }
}