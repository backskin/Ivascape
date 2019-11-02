package backsoft.ivascape.model;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.GraphHandler;
import backsoft.ivascape.handler.IvascapeGraphHandler;

import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.logic.Triplet;
import javafx.beans.property.*;

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
    private final GraphHandler<Company, Link, IvascapeGraph> graphHandler;
    private final BooleanProperty saved;
    private final IntegerProperty size;
    private File file;

    private Project(){

        coorsMap = new CoorsMap();
        saved = new SimpleBooleanProperty(true);
        size = new SimpleIntegerProperty(0);

        file = null;
        graph = new IvascapeGraph();
        graphHandler = new IvascapeGraphHandler(graph);
    }

    public CoorsMap getCoorsMap() { return coorsMap; }

    public File getFile() { return file; }

    public void setFile(File file) { this.file = file; }

    public boolean isSaved() { return isEmpty() || saved.get(); }

    public BooleanProperty savedProperty() { return saved; }
    public IntegerProperty sizeProperty() { return size; }
    public void setSaved(boolean value) {
        size.setValue(graph.size());
        saved.setValue(value);
    }

    public IvascapeGraph getGraph() { return graph; }

    public boolean isEmpty() { return graph == null || graph.size() < 1; }

    public boolean load(Triplet<File, IvascapeGraph, CoorsMap> triplet) {

        if (triplet == null) return false;

        setSaved(true);
        setFile(triplet.getOne());
        this.graph = triplet.getTwo();
        coorsMap = triplet.getThree();
        return true;
    }

    public static void newProject() {
        instance = new Project();
    }

    public void saveProject(){

        FileHandler.saveToFile(file, graph, coorsMap);
        setSaved(true);
    }

    public <T extends Comparable<T>> Company getCompany(T tag){

        for (Iterator<Company> i = graph.getVertexIterator(); i.hasNext();){
            Company c = i.next();
            Object tempTag = tag instanceof String ? c.getTitle() : tag instanceof Integer ? c.hashCode() : null;
            if (tag.equals(tempTag)) return c;
        }
        return null;
    }

    public Link getLink(Company companyOne, Company companyTwo){

        return graph.getEdge(companyOne,companyTwo);
    }

    public List<String> getCompaniesList(){

        List<String> output = new ArrayList<>();

        for (Iterator<Company> i = graph.getVertexIterator(); i.hasNext();)
            output.add(i.next().getTitle());

        return output;
    }

    public IvascapeGraph applyPrimAlgorithm(){

       return graphHandler.getPrimResult();
    }

    public boolean isGraphStrong(){

        return graphHandler.isStrong();
    }

    public int amountOfComs(){
        return graph.size();
    }

    public int amountOfLinks(){

        return graphHandler.getEdgeSize();
    }

    public void add(Company company){
        if (getCompany(company.getTitle()) == null)
            graph.addVertex(company);
        else getCompany(company.getTitle()).asCopyOf(company);
        setSaved(false);
    }

    public void add(Company one, Company two, double price){
        if (graph.getEdge(one,two) == null)
            graph.addEdge(one, two, price);
        else
            graph.getEdge(one,two).setPrice(price);
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

    public Iterator<Company> getIteratorOfCompanies(){

        return graph.getVertexIterator();
    }

    public Iterator<Link> getIteratorOfLinks(Company company){

        return graph.getEdgeIteratorForVertex(company);
    }

    public List<IvascapeGraph> getComponents(){

        return graphHandler.getConnectComponents();
    }
}