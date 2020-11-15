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
        myAgent = a;
        state = ReceiverState.IDLE;
    }

    @Override
    public void action() {
        ACLMessage msg;
        switch(state) {
            case IDLE:
                msg = myAgent.blockingReceive();
                if (msg != null) {
                    // infer
                }
                break;
            case FAILED:
                ACLMessage resultFailed = requestMsg.createReply();
                resultFailed.setPerformative(ACLMessage.FAILURE);
                resultFailed.setContent((String) result);
                myAgent.send(resultFailed);
                state = ReceiverState.IDLE;
                break;

            case SUCCESS:
                try {
                    ACLMessage resultSuccess = requestMsg.createReply();
                    resultSuccess.setPerformative(ACLMessage.INFORM);
                    resultSuccess.setContentObject(result);
                    myAgent.send(resultSuccess);
                    state = ReceiverState.IDLE;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        System.out.println("testing reciever");
    }
}