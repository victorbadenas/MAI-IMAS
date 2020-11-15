package Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.Serializable;

public class Config implements Serializable {

    private String title;
    private String application;
    private int fuzzyagents;
    private String fuzzySettings;
    private String aggregation;

    public Config(String fileName) {
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

            this.setTitle(rootElement.getElementsByTagName("title").item(0).getTextContent());
            this.setApplication(rootElement.getElementsByTagName("application").item(0).getTextContent());
            this.setFuzzyAgents(Integer.parseInt(rootElement.getElementsByTagName("fuzzyagents").item(0).getTextContent()));
            this.setFuzzySettings(rootElement.getElementsByTagName("fuzzySettings").item(0).getTextContent());
            this.setAggregation(rootElement.getElementsByTagName("aggregation").item(0).getTextContent());
        } catch (Exception e) {
            System.err.println("Error parsing Config file: \n" + e.getMessage());
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public int getFuzzyAgents() {
        return fuzzyagents;
    }

    public void setFuzzyAgents(int fuzzyagents) {
        this.fuzzyagents = fuzzyagents;
    }

    public String getFuzzySettings() {
        return fuzzySettings;
    }

    public void setFuzzySettings(String fuzzySettings) {
        this.fuzzySettings = fuzzySettings;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String toString(){
        return "\n - Title: " + this.getTitle() + "\n" +
               " - Application: " + this.getApplication() + "\n" +
               " - Fuzzy Agents: " + String.valueOf(this.getFuzzyAgents()) + "\n" +
               " - Fuzzy Settings:  " + this.getFuzzySettings() + "\n" +
               " - Aggregation: " + this.getAggregation() + "\n";
    }
}