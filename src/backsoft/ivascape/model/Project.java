package backsoft.ivascape.model;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.GraphHandler;
import backsoft.ivascape.handler.IvascapeGraphHandler;

import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.logic.Triplet;
import backsoft.ivascape.viewcontrol.ViewUpdater;
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

        saved = new SimpleBooleanProperty(false);
        companiesAmount = new SimpleIntegerProperty(0);
        linksAmount = new SimpleIntegerProperty(0);
        erase();
    }

    public static Project get(){
        if (instance == null) instance = new Project();
        return instance;
    }
    public void erase(){
        graph = new IvascapeGraph();
        graphHandler = new IvascapeGraphHandler(graph);
        file = null;
        coorsMap = new CoorsMap();
        saved.setValue(true);
        companiesAmount.setValue(0);
        linksAmount.setValue(0);
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

    public void add(Company company){
        if (getCompany(company.hashCode()) == null) {
            graph.addVertex(company);
            companiesAmount.setValue(companiesAmount.getValue()+1);
            ViewUpdater.current().getGVController().add(company);
        }
        else getCompany(company.hashCode()).asCopyOf(company);
        setSaved(false);
    }

    public void add(Company one, Company two, double price){
        if (one == null || two == null || one.equals(two)) return;
        if (graph.getEdge(one,two) == null) {
            Link newLink = new Link(one, two, price);
            graph.addEdge(one, two, newLink);
            linksAmount.setValue(linksAmount.get() + 1);
            ViewUpdater.current().getGVController().add(newLink);
        }
        else {
            graph.getEdge(one, two).setPrice(price);
            saved.setValue(false);
        }
    }

    public void remove(Link link){

        if (graph.removeEdge(link.one(), link.two())) {
            linksAmount.setValue(linksAmount.get() - 1);
            ViewUpdater.current().getGVController().remove(link);
        }
    }

    public void remove(Company company){

        coorsMap.remove(company.hashCode());
        for (Iterator<Link> iterator = graph.getEdgeIteratorForVertex(company); iterator.hasNext();){
            remove(iterator.next());
        }

        if (graph.removeVertex(company)) {
            companiesAmount.setValue(companiesAmount.getValue()-1);
            linksAmount.setValue(graphHandler.getEdgeSize());
            ViewUpdater.current().getGVController().remove(company);
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