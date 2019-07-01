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
import static ivascape.view.serve.MyAlerts.MyAlertType.*;
public class FileWorker {


    public static Pair<Graph, Map> loadFile(final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp")
        );

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.open"));

        if (IvascapeProject.getFile() == null)

            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
        else
            fileChooser.setInitialDirectory(new File(IvascapeProject.getFile().getParent()));

        File file = fileChooser.showOpenDialog(ownerStage);
        if (file == null) return null;

        if (!IvascapeProject.isSaved() && IvascapeProject.companiesAmount() > 0) {

            if (getAlert(CLOSE_UNSAVED, ownerStage).getResult().getButtonData().isCancelButton()) {

                return null;
            }
        }

        return openIt(file,ownerStage);
    }

    private static Pair<Graph, Map> openIt(File file, Stage ownerStage){

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

                getAlert(LOAD_FAILED, ownerStage);
                e.printStackTrace();
                return null;
            }

            boolean checked =  result instanceof Graph && coors instanceof HashMap;

            if (checked) {
                IvascapeProject.setFile(file);
                return new Pair<>((Graph) result, (HashMap) coors);
            }
            else {
                fis.close();
                oin.close();
                throw new IOException("!SAS!");
            }

        } catch (ClassNotFoundException | IOException e){

            getAlert(LOAD_FAILED, ownerStage, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveProject(final Stage ownerStage, File file){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp"));

        if (file == null)
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));
        else {
            fileChooser.setInitialFileName(IvascapeProject.getFile().getName());
            fileChooser.setInitialDirectory(new File(IvascapeProject.getFile().getParent()));
        }

        return saveIt(
                fileChooser.showSaveDialog(ownerStage),
                IvascapeProject.getGraph(),
                IvascapeProject.getVerCoorsMap());
    }

    public static void exportToXLS(Graph graph, final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.xls"),"*.xls")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));

        ExcelWorker.saveItAsXLS(graph, fileChooser.showSaveDialog(ownerStage));
    }

    public static boolean saveIt(File file, Graph graph, Map<String,Pair<Double,Double>> verCoorsMap){

        if (file == null) return false;

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

            getAlert(SAVE_FAILED, null);
            e.printStackTrace();
            return false;
        }

        IvascapeProject.setFile(file);

        return true;
    }
}
