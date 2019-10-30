package backsoft.ivascape;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;


public class FXApp extends Application {


    public static void main(String [] args){

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        Loader.setMainStage(stage);
        Loader.loadStartWindow();
        Loader.reloadApp();

        Preferences.current().setWindowParams(stage);
    }
}
