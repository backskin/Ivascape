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
    public void setFile(File file) { this.file = file; saved.setValue(true);}

    public boolean isSaved() { return saved.get(); }

    public BooleanProperty savedProperty() { return saved; }

    public IntegerProperty linksAmountProperty() { return linksAmount; }
    public IntegerProperty companiesAmountProperty() { return companiesAmount; }

    public IvascapeGraph getGraph() { return graph; }

    public boolean isEmpty() { return graph == null || graph.size() < 1; }

    public CoorsMap copyCoorsMap(){

        ViewUpdater.current().getGVController().normalScale();
        CoorsMap map = new CoorsMap();
        for (String s : coorsMap.keySet()) map.put(s, new CoorsMap.Coors(coorsMap.get(s)));
        ViewUpdater.current().getGVController().restoreScale();
        return map;
    }

    public boolean load(Triplet<File, IvascapeGraph, CoorsMap> triplet) {

        if (triplet == null) return false;

        saved.setValue(true);
        file = triplet.getOne();
        this.graph = triplet.getTwo();
        coorsMap = triplet.getThree();
        companiesAmount.setValue(graph.size());
        graphHandler = new IvascapeGraphHandler(graph);
        linksAmount.setValue(graph.getEdgeSize());

        return true;
    }

    public void saveProject(){

        FileHandler.saveToFile(file, graph, coorsMap);
        saved.setValue(true);
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

    public List<String> getCompaniesTitlesList(){

        List<String> output = new ArrayList<>();
        graph.getVertexIterator().forEachRemaining(company -> output.add(company.getTitle()));
        return output;
    }

    public IvascapeGraph applyPrimAlgorithm(){
       return graphHandler.getPrimResult();
    }

    public boolean isGraphStrong(){
        return graphHandler.isStrong_opti();
    }

    public void add(Company company){
        if (getCompany(company.getTitle()) == null) {
            graph.addVertex(company);
            companiesAmount.setValue(companiesAmount.getValue()+1);
            saved.setValue(false);
            ViewUpdater.current().getGVController().add(company);
        }
    }

    public void add(String titleOne, String titleTwo, double price){
        Company one = getCompany(titleOne);
        Company two = getCompany(titleTwo);

        if (one == null || two == null || one.equals(two)) return;
        Link newLink = graph.getEdge(one, two);
        if (newLink == null) {
            newLink = new Link(one, two, price);
            if (graph.addEdge(one, two, newLink)) {
                linksAmount.setValue(linksAmount.get() + 1);
                saved.setValue(false);
                ViewUpdater.current().getGVController().add(newLink);
            }
        }
        else {
            newLink.setPrice(price);
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

        if (company == null) return;
        coorsMap.remove(company.getID());
        ViewUpdater.current().getGVController().remove(company);

        if (graph.removeVertex(company)) {
            companiesAmount.setValue(companiesAmount.getValue()-1);
            linksAmount.setValue(graph.getEdgeSize());
        }
    }

    public Iterator<Company> getIteratorOfCompanies(){
        return graph.getVertexIterator();
    }

    public Iterator<Link> getIteratorOfLinksOf(Company company){
        return graph.getEdgeIteratorForVertex(company);
    }

    public List<IvascapeGraph> getComponents(){
        return graphHandler.getComponents_opti();
    }
}