package ivascape.controller;

import ivascape.model.Company;
import ivascape.model.Graph;
import ivascape.model.Link;
import ivascape.model.Pair;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class IvascapeProject {

    private static final BooleanProperty saved = new SimpleBooleanProperty(false);

    private static String projectName;

    private static File file = null;

    private static Map<String,Pair<Double,Double>> verCoorsMap;

    public static Map<String, Pair<Double, Double>> getVerCoorsMap() {
        return verCoorsMap;
    }

    public static void setVerCoorsMap(Map<String, Pair<Double, Double>> verCoorsMap) {
        IvascapeProject.verCoorsMap = verCoorsMap;
    }

    public static File getFile() {
        return file;
    }

    public static void setFile(File file) {

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

    private static Graph<Company,Link> project;

    public static Graph<Company,Link> getProject() {

        return project;
    }

    public static boolean isEmpty(){

        return project == null;
    }

    public static void setProject(Graph graph){

        if (project == null) {

            project = graph;
        }

    }

    public static void EraseProject(){

        project = null;
        saved.setValue(true);
    }

    public static void NewProject(){

        if (project == null) {
            project = new Graph<>();
            projectName = "Empty Project";
            file = null;
        }
    }

    public static Company getCompany(String title){

        for (int i = 0; i < project.getVerSize(); i++){

            if (project.getVer(i).getTitle().equals(title))

                return project.getVer(i);
        }
        return null;
    }

    public static Link getLink(Company companyOne, Company companyTwo){

        return project.getEdge(companyOne,companyTwo);
    }

    public static ArrayList<String> getCompaniesListExcept(Company exCompany){

        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < project.getVerSize(); i++){

            if (project.getVer(i) != exCompany)

                output.add(project.getVer(i).getTitle());
        }
        return output;
    }

    public static ArrayList<String> getCompaniesList(){

        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < project.getVerSize(); i++)

            output.add(project.getVer(i).getTitle());

        return output;
    }

    public static Graph<Company,Link> algorithmResult(){

        return (new GraphWorker<>(project)).getPrimResult();
    }

    public static boolean isProjectStrongGraph(){

        return (new GraphWorker<>(project)).isStrong();
    }

    public static int linksAmount(){

        return (new GraphWorker<>(project)).getEdgeSize();
    }

    public static int companiesAmount(){

        return project.getVerSize();
    }

    public static Link addLink(Company one, Company two, double price){

        project.addEdge(one,two,new Link(one,two,price));

        setSaved(false);

        return project.getEdge(one,two);
    }

    public static void delLink(Link link){

        project.delEdge(link.getOne(),link.getTwo());
        setSaved(false);
    }

    public static void addCompany(Company company){

        project.addVer(company);
        setSaved(false);
    }

    public static void delCompany(Company company){

        project.delVer(company);
        setSaved(false);
    }

    public static GraphWorker<Company,Link> getGraphWorker(){

        return new GraphWorker<>(project);
    }

    public static Iterator<Company> getCompanyIterator(){

        return (new GraphWorker<>(project)).getVerIterator();
    }

    public static Iterator<Company> getCompanySortedIterator(){

        return (new GraphWorker<>(project)).getSortedVerIterator(Comparator.comparing(Company::getTitle));
    }
}
