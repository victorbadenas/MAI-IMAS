package Behaviours;

import Agents.ManagerAgent;
import Utils.Config;
import Utils.utils;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.List;
import java.io.Serializable;

enum ReceiverState {
    IDLE,
    WAITING,
    FAILED,
    SUCCESS
}

public class ReceiverBehaviour extends CyclicBehaviour {
    private ManagerAgent myAgent;
    private ReceiverState state;
    private Serializable result;

    public ReceiverBehaviour(ManagerAgent a) {
        super(a);
        myAgent = a;
    }

    @Override
    public void action() {
        ACLMessage  msg;
        switch(state) {
            case IDLE:
                msg = myAgent.blockingReceive();
                if(msg != null) {
                    if(msg.getPerformative()== ACLMessage.REQUEST){
                        String content = msg.getContent();
                        myAgent.getLogger().log(Logger.INFO, "Agent "+myAgent.getLocalName()+" - Received I-order request ["+content+"] received from "+msg.getSender().getLocalName());
                        Config config = new Config(content);
                        myAgent.createFuzzyAgents(config);
                        reply.setPerformative(ACLMessage.INFORM);
                        this.state = ReceiverState.WAITING;
                    }
                }
                break;

            case WAITING:
                msg = myAgent.blockingReceive(5000);
                if(msg != null) {
                    if(msg.getPerformative()== ACLMessage.REQUEST) {
                        String content = msg.getContent();
                        myAgent.getLogger().log(Logger.INFO, "Agent "+myAgent.getLocalName()+" - Received D-order request ["+content+"] received from "+msg.getSender().getLocalName());
                        List<String> inputs = readFile(content);

                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

                        for (String input: inputs) {
                            msg.setContent(input);
                            for (String fuzzyAgent : myAgent.getFuzzyAgents()) {
                                msg.addReceiver(fuzzyAgent);
                                myAgent.send(msg);
                            }
                        }
                    } else if (msg.getPerformative()== ACLMessage.INFORM) {
                        //FUZZY RESPONSE
                        double result = (double) msg.getContent();
                        String sender = msg.getSender().getLocalName();
                        myAgent.addResult(sender, result);
                    } else if (msg.getPerformative()== ACLMessage.FAILURE) {

                    }
                }
                break;
        }
        myAgent.send(reply);
    }
}