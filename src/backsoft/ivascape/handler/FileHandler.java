package backsoft.ivascape.handler;

import backsoft.ivascape.logic.Triplet;
import backsoft.ivascape.model.CoorsMap;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.viewcontrol.MyAlerts;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class FileHandler {

    public static Triplet<File, IvascapeGraph, CoorsMap> loadFile(File file){

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        Preferences.getBundle().getString("filewindow.type.ivp"), "*.ivp")
        );

        fileChooser.setTitle(Preferences.getBundle().getString("filewindow.title.open"));

        fileChooser.setInitialDirectory(
                new File(file == null ?
                        System.getProperty("user.home") + "\\Desktop"
                        : file.getParent()));

        file = fileChooser.showOpenDialog(Loader.getMainStage());
        return openIt(file);
    }

    private static Triplet<File, IvascapeGraph, CoorsMap> openIt(File file) {

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

            if (result instanceof IvascapeGraph && coors instanceof CoorsMap) {
                return new Triplet<>(file, (IvascapeGraph) result, (CoorsMap) coors);

            } else {
                throw new ClassCastException();
            }

        } catch (ClassCastException | ClassNotFoundException | IOException e){

            MyAlerts.getAlert(MyAlerts.AlertType.LOAD_FAILED);
            return null;
        }
    }

    public static File saveAs(IvascapeGraph graph, CoorsMap map){
        return saveAs(null, graph, map);
    }

    public static File saveAs(File file, IvascapeGraph graph, CoorsMap map){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(Preferences.getBundle().getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        Preferences.getBundle().getString("filewindow.type.ivp"), "*.ivp"));

        if (file == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
            file = fileChooser.showSaveDialog(Loader.getMainStage());
        }
        else {
            fileChooser.setInitialFileName(file.getName());
            fileChooser.setInitialDirectory(new File(file.getParent()));
        }

        saveIt(file, graph, map);
        return file;
    }

    public static void exportToXLS(IvascapeGraph graph, final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(Preferences.getBundle().getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        Preferences.getBundle().getString("filewindow.type.xls"),"*.xls")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));

        ExcelHandler.saveItAsXLS(graph, fileChooser.showSaveDialog(ownerStage));
    }

    public static void saveIt(File file, IvascapeGraph graph, CoorsMap map){

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

            MyAlerts.getAlert(MyAlerts.AlertType.SAVE_FAILED);
        }
    }
}
