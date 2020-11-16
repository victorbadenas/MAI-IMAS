package Behaviours;

import Agents.ManagerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ManagerBehaviour extends CyclicBehaviour {
    //TODO This is just to test communication, can be deleted.
    private ManagerAgent myAgent;

    public ManagerBehaviour (ManagerAgent agent) {
        super(agent);
        myAgent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = Utils.receiveMessage(myAgent);
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                String petition = msg.getContent();
                Utils.log(myAgent, "Petition " + petition + " received from UserAgent.");

                // lo que calgui fer amb la petition

                Utils.sendReply(myAgent, msg, ACLMessage.CONFIRM, null);
            }
        }
    }
}
