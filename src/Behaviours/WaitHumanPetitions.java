package Behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;

import java.util.Scanner;

public class WaitHumanPetitions extends CyclicBehaviour {

    @Override
    public void action() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Waiting a petition...");
        String petition = sc.next();

        System.out.println("Sending petition to Manager: " + petition);
        // send petition to Manager Agent
        // wait message from Agents.ManagerAgent
    }
}
