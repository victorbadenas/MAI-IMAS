package cat.urv.imas.Utils;

import jade.wrapper.AgentController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.Serializable;

public class AppConfig implements Serializable {
    private String title;
    private String application;
    private int numberOfAgents;
    private String[] fuzzySettings;
    private String aggregation;
    private String[] fuzzyAgents;
    private AgentController[] controllers;

    public AppConfig(String fileName) {
        File xmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nList = document.getElementsByTagName("SimulationSettings");
            Node nNode = nList.item(0);
            Element rootElement = (Element) nNode;

            this.title = rootElement.getElementsByTagName("title").item(0).getTextContent();
            this.application = rootElement.getElementsByTagName("application").item(0).getTextContent();
            this.numberOfAgents = Integer.parseInt(rootElement.getElementsByTagName("fuzzyagents").item(0).getTextContent());
            this.fuzzySettings = rootElement.getElementsByTagName("fuzzySettings").item(0).getTextContent().split(",");
            this.aggregation = rootElement.getElementsByTagName("aggregation").item(0).getTextContent();
        } catch (Exception e) {
            System.err.println("Error parsing Config file: \n" + e.getMessage());
        }
    }

    public void setFuzzyAgents(String[] fuzzyAgents) {
        this.fuzzyAgents = fuzzyAgents;
    }

    public void setControllers(AgentController[] controllers) {
        this.controllers = controllers;
    }

    public String getTitle() {
        return title;
    }

    public String getApplication() {
        return application;
    }

    public String[] getFuzzyAgents() {
        return fuzzyAgents;
    }

    public String[] getFuzzySettings() {
        return fuzzySettings;
    }

    public String getAggregation() {
        return aggregation;
    }

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    public AgentController[] getControllers() {
        return controllers;
    }
}