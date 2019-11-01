package backsoft.ivascape.handler;

import backsoft.ivascape.logic.Triplet;
import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.model.IvascapeGraph;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import static backsoft.ivascape.handler.AlertHandler.AlertType.LOAD_ISSUE;
import static backsoft.ivascape.handler.AlertHandler.AlertType.SAVE_ISSUE;

public class FileHandler {

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

        return openFile(fileChooser.showOpenDialog(Loader.getMainStage()));
    }

    private static Triplet<File, IvascapeGraph, CoorsMap> openFile(File file) {

        if (file == null) return null;

        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            ObjectInputStream oin = new ObjectInputStream(fis);
            Object result = oin.readObject();
            Object coors = oin.readObject();
            oin.close();
            fis.close();

            if (result instanceof IvascapeGraph && coors instanceof CoorsMap)
                return new Triplet<>(file, (IvascapeGraph) result, (CoorsMap) coors);
            else throw new ClassCastException();

        } catch (ClassCastException | ClassNotFoundException | IOException e){

            AlertHandler.makeAlert(LOAD_ISSUE).customContent("\n"+e.getLocalizedMessage()).show();
            throw new RuntimeException();
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

    public static void saveToFile(File file, IvascapeGraph graph, CoorsMap map){

        if (file == null) return;

        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();

        } catch (IOException e){
            AlertHandler.makeAlert(SAVE_ISSUE).customContent("\n"+e.getLocalizedMessage()).show();
            throw new RuntimeException();
        }
    }
}
