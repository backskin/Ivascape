package ivascape.models;

import ivascape.controller.*;
import ivascape.logic.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {

    private static Project instance = null;
    public static Project getInstance(){

        if (instance == null) instance = new Project();
        return instance;
    }

    private IvaGraph graph;
    private CoorsMap coorsMap;
    private final BooleanProperty saved;
    private File file;

    private Project(){ saved = new SimpleBooleanProperty(true); }

    public CoorsMap getCoorsMap() { return coorsMap; }

    public void setCoorsMap(CoorsMap map) { coorsMap = map; }

    public File getFile() { return file; }

    public void setFile(File file) {

        this.file = file;
        saved.setValue(true);
    }

    public boolean isSaved() { return saved.get(); }

    public BooleanProperty savedProperty() { return saved; }

    public void setSaved(boolean value) { saved.setValue(value); }

    public IvaGraph getGraph() { return graph; }

    public boolean isEmpty() { return graph == null; }

    public void loadProject(IvaGraph graph, CoorsMap map) {

        if (!isSaved()) return;
        erase();
        this.graph = graph;
        coorsMap = map;
    }

    public void newProject() {

        if (isSaved()) {
            erase();
            graph = new IvaGraph();
        }
    }

    public void saveProject(){

        FileHandler.saveIt(file, graph, coorsMap);
    }

    public Company getCompany(String title){

        for (int i = 0; i < graph.size(); i++){

            if (graph.getVertex(i).getTitle().equals(title))

                return graph.getVertex(i);
        }
        return null;
    }

    public Link getLink(Company companyOne, Company companyTwo){

        return graph.getEdge(companyOne,companyTwo);
    }

    public List<String> getCompaniesList(){

        List<String> output = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++)

            output.add(graph.getVertex(i).getTitle());

        return output;
    }

    public IvaGraph algorithmResult(){

        return (IvaGraph) GraphHandler.factory(graph).getPrimResult();
    }

    public boolean isGraphStrong(){

        return GraphHandler.factory(graph).isStrong();
    }

    public int linksAmount(){

        return GraphHandler.factory(graph).getEdgeSize();
    }

    public Link addLink(Company one, Company two, double price){

        graph.addEdge(one,two,new Link(one,two,price));
        setSaved(false);
        return graph.getEdge(one, two);
    }

    public void removeLink(Link link){

        graph.removeEdge(link.one(), link.another());
        setSaved(false);
    }

    public void removeCompany(Company company){

        coorsMap.remove(company.getTitle());
        graph.removeVertex(company);
        setSaved(false);
    }

    public List<GenericGraph<Company,Link>> getComponents(){

       return GraphHandler.factory(graph).getConnectComponents();
    }

    public void modifyLink(Company one, Company another, Double price) {

        graph.getEdge(one,another).setPrice(price);
        setSaved(false);
    }

    private void erase(){

        coorsMap = new CoorsMap();
        saved.setValue(true);
        graph = null;
        file = null;
    }
}