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

    private IvascapeGraph graph;
    private CoorsMap coorsMap;
    private GraphHandler<Company, Link, IvascapeGraph> graphHandler;
    private final BooleanProperty saved;
    private final IntegerProperty companiesAmount;
    private final IntegerProperty linksAmount;
    private File file;

    private Project(){

        coorsMap = new CoorsMap();
        saved = new SimpleBooleanProperty(true);
        companiesAmount = new SimpleIntegerProperty(0);
        linksAmount = new SimpleIntegerProperty(0);
        file = null;
        graph = new IvascapeGraph();
        graphHandler = new IvascapeGraphHandler(graph);
    }

    public static Project get(){

        if (instance == null) instance = new Project();
        return instance;
    }

    public CoorsMap getCoorsMap() { return coorsMap; }

    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    public boolean isSaved() { return saved.get(); }
    public void setSaved(boolean value) { saved.setValue(value); }
    public BooleanProperty savedProperty() { return saved; }

    public IntegerProperty linksAmountProperty() { return linksAmount; }
    public IntegerProperty companiesAmountProperty() { return companiesAmount; }

    public IvascapeGraph getGraph() { return graph; }

    public boolean isEmpty() { return graph == null || graph.size() < 1; }

    public boolean load(Triplet<File, IvascapeGraph, CoorsMap> triplet) {

        if (triplet == null) return false;

        setSaved(true);
        setFile(triplet.getOne());
        this.graph = triplet.getTwo();
        coorsMap = triplet.getThree();
        companiesAmount.setValue(graph.size());
        graphHandler = new IvascapeGraphHandler(graph);
        linksAmount.setValue(graphHandler.getEdgeSize());

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

    public int amountOfCompanies() {
        return companiesAmount.get();
    }

    public int amountOfLinks(){
        return linksAmount.get();
    }

    public void add(Company company){
        if (getCompany(company.getTitle()) == null)
            graph.addVertex(company);
        else getCompany(company.getTitle()).asCopyOf(company);
        setSaved(false);
        companiesAmount.setValue(companiesAmount.get()+1);
    }

    public void add(Company one, Company two, double price){
        if (graph.getEdge(one,two) == null)
            graph.addEdge(one, two, price);
        else
            graph.getEdge(one,two).setPrice(price);
        saved.setValue(false);
        linksAmount.setValue(linksAmount.get() + 1);
    }

    public void remove(Link link){

        if (graph.removeEdge(link)) {
            saved.setValue(false);
            linksAmount.setValue(linksAmount.get() - 1);
        }
    }

    public void remove(Company company){

        coorsMap.remove(company.hashCode());
        if (graph.removeVertex(company)) {
            saved.setValue(false);
            linksAmount.setValue(graphHandler.getEdgeSize());
        }
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