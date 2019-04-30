package ivascape.controller;

import ivascape.MainApp;
import ivascape.model.Company;
import ivascape.model.Graph;
import ivascape.model.Link;
import ivascape.model.Pair;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static ivascape.view.serve.MyAlerts.*;

public class FileWorker {


    public static Pair<Graph<Company, Link>, Map<String,Pair<Double,Double>>> loadFile(final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp")
        );

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.open"));

        if (IvascapeProject.getFile() == null)

            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else
            fileChooser.setInitialDirectory(new File(IvascapeProject.getFile().getParent()));

        File file = fileChooser.showOpenDialog(ownerStage);

        if (file == null) return null;

        return openIt(file,ownerStage);
    }

    private static Pair<Graph<Company, Link>, Map<String,Pair<Double,Double>>> openIt(File file, Stage ownerStage){

        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            ObjectInputStream oin = new ObjectInputStream(fis);

            Object result;
            Object coors;

            try {
                result = oin.readObject();
                coors = oin.readObject();

            } catch (InvalidClassException e){

                fis.close();
                oin.close();

                getAlert(MyAlertType.LOAD_FAILED,ownerStage);
                e.printStackTrace();
                return null;
            }


            boolean checked =  result instanceof Graph && coors instanceof HashMap;

            if (checked) {
                IvascapeProject.setFile(file);
                Pair<Graph<Company, Link>, Map<String,Pair<Double,Double>>> pair = new Pair<>();
                pair.setOne((Graph<Company, Link>)result);
                pair.setTwo((HashMap<String, Pair<Double, Double>>)coors);
                return pair;
            }
            else {
                fis.close();
                oin.close();
                throw new IOException("!SAS!");
            }

        } catch (ClassNotFoundException | IOException e){

            getAlert(MyAlertType.LOAD_FAILED,ownerStage);
            e.printStackTrace();
            return null;
        }
    }

    public static Pair<File,String> Choosing(final Stage ownerStage, File oldWay) {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp"),
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.xls"),"*.xls")
        );

        if (oldWay == null)

            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else {
            fileChooser.setInitialFileName(oldWay.getName());
            fileChooser.setInitialDirectory(new File(oldWay.getParent()));
        }

        return new Pair<>(
                fileChooser.showSaveDialog(ownerStage),
                fileChooser.getSelectedExtensionFilter() != null ? fileChooser.getSelectedExtensionFilter().getExtensions().get(0) : "");
    }

    public static boolean saveProject(final Stage ownerStage) {

        Pair<File,String> choice = Choosing(ownerStage,IvascapeProject.getFile());

        if (choice.getOne() != null)

            if (choice.getTwo().equals("*.ivp"))

                return saveProject(ownerStage,choice.getOne());
            else

                return ExcelWorker.saveProjectAsXLS(choice.getOne());
        else
            return false;
    }

    public static boolean saveProject(Stage ownerStage, File file){

        if (saveIt(file,IvascapeProject.getProject(), IvascapeProject.getVerCoorsMap(), ownerStage)) {
            IvascapeProject.setFile(file);
            return true;
        }
        return false;
    }

    public static boolean saveIt(File file, Graph graph, Map<String,Pair<Double,Double>> verCoorsMap, Stage ownerStage){

        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            fos = new FileOutputStream(file.getAbsolutePath());
            oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.writeObject(verCoorsMap);
            oos.flush();
            oos.close();

        } catch (IOException e){

            getAlert(MyAlertType.SAVE_FAILED,ownerStage);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
