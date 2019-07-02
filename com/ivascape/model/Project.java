package ivascape.model;

import ivascape.handler.*;
import ivascape.logic.*;

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
    }

    public boolean isSaved() { return saved.get(); }

    public BooleanProperty savedProperty() { return saved; }

    public void setSaved(boolean value) { saved.setValue(value); }

    public IvaGraph getGraph() { return graph; }

    public boolean isEmpty() { return graph == null; }

    public void loadProject(File file, IvaGraph graph, CoorsMap map) {

        setFile(file);
        setSaved(true);
        this.graph = graph;
        coorsMap = map;
    }

    public void newProject() {

        coorsMap = new CoorsMap();
        saved.setValue(true);
        file = null;
        graph = new IvaGraph();
    }

    public void saveProject(){

        FileHandler.saveIt(file, graph, coorsMap);
        setSaved(true);
    }

    public Company getCompany(String title){

        for (Iterator<Company> i = graph.getVertexIterator(); i.hasNext();){

            Company c = i.next();
            if (c.getTitle().equals(title)) return c;
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

    public IvaGraph algorithmResult(){

        return (IvaGraph) GraphHandler.factory(graph).getPrimResult();
    }

    public boolean isGraphStrong(){

        return GraphHandler.factory(graph).isStrong();
    }

    public int linksAmount(){

        return GraphHandler.factory(graph).getEdgeSize();
    }


    public void addLink(Company one, Company two, double price){

        graph.addEdge(one, two, price);
        setSaved(false);
    }

    public void removeLink(Link link){

        graph.removeEdge(link);
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
}