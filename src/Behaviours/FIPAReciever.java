package Behaviours;
import Agents.FuzzyAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

enum ReceiverState {
    IDLE,
    FAILED,
    SUCCESS
}

public class FIPAReciever extends OneShotBehaviour{
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
        System.out.println("testing reciever");
    }
}