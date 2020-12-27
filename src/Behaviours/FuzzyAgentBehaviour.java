package Behaviours;
import Agents.FuzzyAgent;
import Utils.InferenceResult;
import Utils.Helper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.Serializable;
import java.util.ArrayList;


public class FuzzyAgentBehaviour extends CyclicBehaviour {
    enum ReceiverState {
        IDLE,
        FAILED,
        SUCCESS
    }

    private final FuzzyAgent myAgent;
    private ReceiverState state;
    private ACLMessage requestMsg;
    private StringBuilder result;
//    private String result;

    public FuzzyAgentBehaviour(FuzzyAgent a) {
        super(a);
        this.myAgent = a;
        this.state = ReceiverState.IDLE;
    }

    @Override
    public void action() {
        ACLMessage msg;
        switch(state) {
            case IDLE:
                msg = Helper.receiveMessage(this.myAgent);
                if (msg != null) {
                    try {
                        if (msg.getPerformative() == ACLMessage.REQUEST) {
                            this.requestMsg = msg;
                            String[] args = msg.getContent().split(" ");
                            this.result = new StringBuilder();

                            for (String query : args) {
                                double[] inferenceArguments = this.myAgent.parseDoubleMessage(query, ",");
                                InferenceResult res = this.myAgent.inferFCL(inferenceArguments);
                                if (res.isSuccessful()) {
                                    this.state = ReceiverState.SUCCESS;
                                    this.result.append(res.getResultString()).append(" ");
                                } else {
                                    this.state = ReceiverState.FAILED;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Helper.error(this.myAgent, String.format("Error in message %s", msg));
                        Helper.error(this.myAgent, e.getMessage());
                    }
                }
                break;
            case FAILED:
                Helper.sendReply(this.myAgent, this.requestMsg, ACLMessage.FAILURE, String.valueOf(this.result));
                this.state = ReceiverState.IDLE;
                break;

            case SUCCESS:
                Helper.sendReply(this.myAgent, this.requestMsg, ACLMessage.INFORM, String.valueOf(this.result));
                this.state = ReceiverState.IDLE;
                break;
        }
    }
}