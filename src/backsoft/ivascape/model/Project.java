package backsoft.ivascape.model;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.GraphHandler;
import backsoft.ivascape.handler.IvaGraphHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Project implements Serializable {

    private static Project instance;

    public static Project get(){

        if (instance == null)
            instance = new Project();

        return instance;
    }

    private IvascapeGraph graph;
    private CoorsMap coorsMap;
    private GraphHandler<Company, Link, IvascapeGraph> graphHandler;
    private final BooleanProperty saved;
    private File file;

    private Project(){

        coorsMap = new CoorsMap();
        saved = new SimpleBooleanProperty(true);
        file = null;
        graph = new IvascapeGraph();
        graphHandler = new IvaGraphHandler(graph);
    }

    public CoorsMap getCoorsMap() { return coorsMap; }

    public File getFile() { return file; }

    public void setFile(File file) {

        this.file = file;
    }

    public boolean isSaved() { return saved.get(); }

    public BooleanProperty savedProperty() { return saved; }

    public void setSaved(boolean value) { saved.setValue(value); }

    public IvascapeGraph getGraph() { return graph; }

    public boolean isEmpty() { return graph == null; }

    public void load(File file, IvascapeGraph graph, CoorsMap map) {

        setFile(file);
        setSaved(true);
        this.graph = graph;
        coorsMap = map;
    }

    public static void newProject() {
        instance = new Project();
    }

    public void saveProject(){

        FileHandler.saveIt(file, graph, coorsMap);
        setSaved(true);
    }

    public Company getCompany(String title){

        for (Iterator<Company> i = graph.getVertexIterator(); i.hasNext();){

            Company c = i.next();
            if (c.getTitle().equals(title))
                return c;
        }
        return null;
    }

    public Link getLink(Company companyOne, Company companyTwo){

        return graph.getEdge(companyOne,companyTwo);
    }

    public List<String> getCompaniesList(){

        List<String> output = new ArrayList<>();

        for (Iterator<Company> i = graph.getVertexIterator(); i.hasNext();){

            output.add(i.next().getTitle());
        }

        return output;
    }

    public IvascapeGraph applyPrimAlgorithm(){

       return graphHandler.getPrimResult();
    }

    public boolean isGraphStrong(){

        return graphHandler.isStrong();
    }

    public int linksAmount(){

        return graphHandler.getEdgeSize();
    }

    public void add(Company company){

        graph.addVertex(company);
        setSaved(false);
    }

    public void add(Company one, Company two, double price){

        graph.addEdge(one, two, price);
        setSaved(false);
    }

    public void remove(Link link){

        graph.removeEdge(link);
        setSaved(false);
    }

    public void remove(Company company){

        coorsMap.remove(company.hashCode());
        graph.removeVertex(company);
        setSaved(false);
    }

    public Iterator<Company> getIteratorOfComs(){

        return graph.getVertexIterator();
    }

    public Iterator<Link> getIteratorOfLinks(Company company){

        return graph.getEdgeIteratorForVertex(company);
    }

    public List<IvascapeGraph> getComponents(){

        return graphHandler.getConnectComponents();
    }
}