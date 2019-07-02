package ivascape.controller;

import ivascape.MainApp;
import ivascape.logic.Triplet;
import ivascape.models.CoorsMap;
import ivascape.models.IvaGraph;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import static ivascape.view.serve.MyAlerts.*;
import static ivascape.view.serve.MyAlerts.MyAlertType.*;

public class FileHandler {

    public static Triplet<File, IvaGraph, CoorsMap> loadFile(File file, final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp")
        );

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.open"));

        if (file == null) {

            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
            file = fileChooser.showOpenDialog(ownerStage);
        }
        else
            fileChooser.setInitialDirectory(new File(file.getParent()));

        return openIt(file,ownerStage);
    }

    private static Triplet<File, IvaGraph, CoorsMap> openIt(File file, Stage ownerStage) {

        if (file == null) return null;
        FileInputStream fis;
        ObjectInputStream oin;
        try {
            fis = new FileInputStream(file.getAbsolutePath());
            oin = new ObjectInputStream(fis);

            Object result;
            Object coors;

            result = oin.readObject();
            coors = oin.readObject();

            fis.close();
            oin.close();

            if (result instanceof IvaGraph && coors instanceof CoorsMap) {
                return new Triplet<>(file, (IvaGraph) result, (CoorsMap) coors);

            } else {
                throw new ClassCastException();
            }

        } catch (ClassNotFoundException | IOException e){

            getAlert(LOAD_FAILED, ownerStage);
            return null;
        }
    }

    public static File saveAs(final Stage ownerStage, File file, IvaGraph graph, CoorsMap map){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp"));

        if (file == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
            file = fileChooser.showSaveDialog(ownerStage);
        }
        else {
            fileChooser.setInitialFileName(file.getName());
            fileChooser.setInitialDirectory(new File(file.getParent()));
        }

        saveIt(file, graph, map);
        return file;
    }

    public static void exportToXLS(IvaGraph graph, final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        MainApp.bundle.getString("filewindow.type.xls"),"*.xls")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));

        ExcelHandler.saveItAsXLS(graph, fileChooser.showSaveDialog(ownerStage));
    }

    public static void saveIt(File file, IvaGraph graph, CoorsMap map){

        if (file == null) return;

        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            fos = new FileOutputStream(file.getAbsolutePath());
            oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.writeObject(map);
            oos.flush();
            oos.close();

        } catch (IOException e){

            getAlert(SAVE_FAILED, null);
            e.printStackTrace();
        }
    }
}
