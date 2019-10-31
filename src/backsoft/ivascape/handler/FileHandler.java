package backsoft.ivascape.handler;

import backsoft.ivascape.logic.Triplet;
import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.viewcontrol.MyAlerts;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class FileHandler {

    public static Triplet<File, IvascapeGraph, CoorsMap> dialogLoad(File file){

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        Preferences.getCurrent().getBundle().getString("filewindow.type.ivp"), "*.ivp")
        );

        fileChooser.setTitle(Preferences.getCurrent().getBundle().getString("filewindow.title.open"));

        fileChooser.setInitialDirectory(
                new File(file == null ?
                        System.getProperty("user.home") + "\\Desktop"
                        : file.getParent()));

        File newfile = fileChooser.showOpenDialog(Loader.getMainStage());
        return openFile(newfile);
    }

    private static Triplet<File, IvascapeGraph, CoorsMap> openFile(File file) {

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

    public static File dialogSaveAs(File file, IvascapeGraph graph, CoorsMap map){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(Preferences.getCurrent().getBundle().getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        Preferences.getCurrent().getBundle().getString("filewindow.type.ivp"), "*.ivp"));

        if (file == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
            file = fileChooser.showSaveDialog(Loader.getMainStage());
        }
        else {
            fileChooser.setInitialFileName(file.getName());
            fileChooser.setInitialDirectory(new File(file.getParent()));
        }

        saveToFile(file, graph, map);
        return file;
    }

    public static void dialogExport(IvascapeGraph graph, final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(Preferences.getCurrent().getBundle().getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        Preferences.getCurrent().getBundle().getString("filewindow.type.xls"),"*.xls")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));

        ExcelHandler.saveItAsXLS(graph, fileChooser.showSaveDialog(ownerStage));
    }

    public static void saveToFile(File file, IvascapeGraph graph, CoorsMap map){

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
