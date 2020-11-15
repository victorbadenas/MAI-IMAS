package Behaviours;
import Agents.FuzzyAgent;
import Utils.InferenceResult;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
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
                msg = this.myAgent.blockingReceive();
                if (msg != null) {
                    try {
                        if (msg.getPerformative() == ACLMessage.REQUEST){
                            this.requestMsg = msg;
                            String args = msg.getContent();

                            double[] inferenceArguments = myAgent.parseDoubleMessage(args, " ");

                            ACLMessage response = msg.createReply();
                            response.setPerformative(ACLMessage.AGREE);
                            this.myAgent.send(response);

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
                ACLMessage resultFailed = this.requestMsg.createReply();
                resultFailed.setPerformative(ACLMessage.FAILURE);
                resultFailed.setContent((String) this.result);
                this.myAgent.send(resultFailed);
                this.state = ReceiverState.IDLE;
                break;

            case SUCCESS:
                try {
                    ACLMessage resultSuccess = this.requestMsg.createReply();
                    resultSuccess.setPerformative(ACLMessage.INFORM);
                    resultSuccess.setContentObject(this.result);
                    this.myAgent.send(resultSuccess);
                    this.state = ReceiverState.IDLE;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}