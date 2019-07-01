package ivascape.controller;

import ivascape.model.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.util.*;

public class IvascapeProject {

    private static final BooleanProperty saved = new SimpleBooleanProperty(true);

    private static String projectName;
    private static Graph<Company,Link> graph;

    private static File file = null;

    private static Map<String, Pair<Double,Double>> verCoorsMap;

    public static Map<String, Pair<Double, Double>> getVerCoorsMap() {
        return verCoorsMap;
    }

    public static void setVerCoorsMap(Map<String, Pair<Double, Double>> vers) {
        verCoorsMap = vers;
    }

    public static File getFile() {
        return file;
    }

    static void setFile(File file) {

        IvascapeProject.file = file;

        setProjectName(file.getName());

        saved.setValue(true);
    }

    public static String getProjectName() {
        return projectName;
    }

    public static boolean isSaved() {
        return saved.get();
    }

    public static BooleanProperty savedProperty() {
        return saved;
    }

    public static void setSaved(boolean saved) {
        IvascapeProject.saved.setValue(saved);
    }

    private static void setProjectName(String projectName) {

        IvascapeProject.projectName = projectName;
    }


    public static Graph getGraph() {

        return graph;
    }

    public static boolean isEmpty(){

        return graph == null;
    }

    public static void setGraph(Graph<Company, Link> instance){

        if (graph == null) {

            graph = instance;
        }
    }

    public static void eraseProject(){

        graph = null;
        saved.setValue(true);
        verCoorsMap = new HashMap<>();
        file = null;
    }

    public static void newProject(){

        if (graph == null) {
            graph = new IvaGraph();
            verCoorsMap = new HashMap<>();
            saved.setValue(true);
            projectName = "Empty Project";
            file = null;
        }
    }

    public static void saveProject(){

        FileWorker.saveIt(file, graph, verCoorsMap);
    }

    public static Company getCompany(String title){

        for (int i = 0; i < graph.getVerSize(); i++){

            if (graph.getVer(i).getTitle().equals(title))

                return graph.getVer(i);
        }
        return null;
    }

    public static Link getLink(Company companyOne, Company companyTwo){

        return graph.getEdge(companyOne,companyTwo);
    }

    public static ArrayList<String> getCompaniesListExcept(Company exCompany){

        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < graph.getVerSize(); i++){

            if (graph.getVer(i) != exCompany)

                output.add(graph.getVer(i).getTitle());
        }
        return output;
    }

    public static ArrayList<String> getCompaniesList(){

        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < graph.getVerSize(); i++)

            output.add(graph.getVer(i).getTitle());

        return output;
    }

    public static IvaGraph algorithmResult(){

        return (IvaGraph) GraphWorker.init(graph).getPrimResult();
    }

    public static boolean isProjectStrongGraph(){

        return GraphWorker.init(graph).isStrong();
    }

    public static int linksAmount(){

        return GraphWorker.init(graph).getEdgeSize();
    }

    public static int companiesAmount(){

        return graph.getVerSize();
    }

    public static Link addLink(Company one, Company two, double price){

        graph.addEdge(one,two,new Link(one,two,price));

        setSaved(false);

        return graph.getEdge(one,two);
    }

    public static void delLink(Link link){

        graph.delEdge(link.getOne(),link.getTwo());
        setSaved(false);
    }

    public static void addCompany(Company company){

        graph.addVer(company);
        setSaved(false);
    }

    public static void delCompany(Company company){

        graph.delVer(company);
        setSaved(false);
    }

    public static List<IvaGraph> getComponents(){

       return GraphWorker.init(graph).getConnectComponents();
    }

    public static Iterator<Company> getCompanyIterator(){

        return GraphWorker.init(graph).getVerIterator();
    }

    public static Iterator<Company> getCompanySortedIterator(){

        return GraphWorker.init(graph).getSortedVerIterator(Comparator.comparing(Company::getTitle));
    }
}
