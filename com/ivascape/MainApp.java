package ivascape;

import ivascape.controller.IvascapeProject;
import ivascape.model.Pair;
import ivascape.view.main.RootLayoutController;
import ivascape.view.serve.StartWindowController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static ivascape.view.serve.MyAlerts.*;

public class MainApp extends Application {

    private static Pair<Double,Double> XY = null;

    private static Pair<Double,Double> windowsize = null;

    public static int currentTab = 0;

    public static ResourceBundle bundle;

    public static final Locale ruLoc = new Locale("ru","RU");
    public static final Locale enLoc = new Locale("en", "US");

    private static Stage mainStage;

    public static void main(String [] args){

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        greetings();

        mainStage = stage;
        mainStage.getIcons().add(new Image("resources/ico.png"));
        mainStage.setOnCloseRequest(this::onExit);

        loadApp();

        mainStage.setMinWidth(mainStage.getWidth());
        mainStage.setMinHeight(mainStage.getHeight());

    }

    private void restart() throws IOException {

        greetings();
    }

    private void onExit(WindowEvent event){

        if (IvascapeProject.isEmpty() || IvascapeProject.isSaved()){
            if (getAlert(MyAlertType.ON_EXIT,mainStage).getResult().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
                event.consume();
        } else

            if (getAlert(MyAlertType.ON_EXIT,mainStage, "NOTSAVED").getResult().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)

            event.consume();
    }

    private void greetings() throws IOException {

        Reader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(Locale.getDefault().equals(enLoc) ?
                        "translate_en_US.properties" :
                        "translate_ru_RU.properties"), StandardCharsets.UTF_8));

        bundle = new PropertyResourceBundle(reader);//ResourceBundle.getBundle(bundlePath);

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/serve/StartWindow.fxml"),
                    bundle);

            BorderPane startPage = loader.load();
            Stage startStage = new Stage();
            startStage.getIcons().add(new Image("resources/ico.png"));
            startStage.setTitle(bundle.getString("welcome"));
            startStage.setScene(new Scene(startPage));
            startStage.setResizable(false);

            StartWindowController SWcontroller = loader.getController();
            SWcontroller.setStartStage(startStage);

            startStage.showAndWait();

            if (SWcontroller.isTerminated()) {
                Platform.exit();
                System.exit(0);
            }
            if (SWcontroller.isRestart()){
                restart();
            }

        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,mainStage);
            e.printStackTrace();
        }
    }

    public void reloadApp() throws IOException {

        XY = new Pair<>(mainStage.getX(),mainStage.getY());
        windowsize = new Pair<>(mainStage.getWidth(),mainStage.getHeight());
        mainStage.close();
        loadApp();
    }

    private void loadApp() throws IOException {

        Reader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(Locale.getDefault().equals(enLoc) ?
                        "translate_en_US.properties" :
                        "translate_ru_RU.properties"), StandardCharsets.UTF_8));

        bundle = new PropertyResourceBundle(reader);//ResourceBundle.getBundle(bundlePath);

        mainStage.setTitle(bundle.getString("program_name"));

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/main/RootLayout.fxml"),
                    bundle);
            loader.load();

            if (XY != null){
                mainStage.setX(XY.getOne());
                mainStage.setY(XY.getTwo());
            }

            if (windowsize != null){

                mainStage.setWidth(windowsize.getOne());
                mainStage.setHeight(windowsize.getTwo());
            }

            RootLayoutController RLController = loader.getController();
            RLController.setMainApp(this);
            RLController.initMainWindow(mainStage);
            RLController.reloadView();

        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,mainStage);
            e.printStackTrace();
        }
    }
}
