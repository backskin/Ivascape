package ivascape.controller;

import ivascape.MainApp;
import ivascape.logic.Pair;
import ivascape.models.CoorsMap;
import ivascape.models.IvaGraph;
import ivascape.models.Project;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import static ivascape.view.serve.MyAlerts.*;
import static ivascape.view.serve.MyAlerts.MyAlertType.*;

public class FileHandler {

    private static Project project = Project.getInstance();

    public static Pair<IvaGraph, CoorsMap> loadFile(final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.ivp"), "*.ivp")
        );

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.open"));

        if (project.getFile() == null)

            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
        else
            fileChooser.setInitialDirectory(new File(project.getFile().getParent()));

        File file = fileChooser.showOpenDialog(ownerStage);
        if (file == null) return null;

        if (!project.isSaved() && project.getGraph().size() > 0) {

            if (getAlert(CLOSE_UNSAVED, ownerStage).getResult().getButtonData().isCancelButton()) {

                return null;
            }
        }

        return openIt(file,ownerStage);
    }

    private static Pair<IvaGraph, CoorsMap> openIt(File file, Stage ownerStage){

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

            boolean checked =  result instanceof IvaGraph && coors instanceof CoorsMap;

            if (checked) {
                Project.getInstance().setFile(file);
                return new Pair<>((IvaGraph) result, (CoorsMap) coors);
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
            fileChooser.setInitialFileName(project.getFile().getName());
            fileChooser.setInitialDirectory(new File(project.getFile().getParent()));
        }

        return saveIt(
                fileChooser.showSaveDialog(ownerStage),
                project.getGraph(),
                project.getCoorsMap()
        );
    }

    public static void exportToXLS(IvaGraph graph, final Stage ownerStage){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(MainApp.bundle.getString("filewindow.title.save"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(MainApp.bundle.getString("filewindow.type.xls"),"*.xls")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));

        ExcelHandler.saveItAsXLS(graph, fileChooser.showSaveDialog(ownerStage));
    }

    public static boolean saveIt(File file, IvaGraph graph, CoorsMap map){

        if (file == null) return false;

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
            return false;
        }

        project.setFile(file);

        return true;
    }
}
