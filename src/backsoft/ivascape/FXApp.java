package backsoft.ivascape;

import backsoft.ivascape.handler.Loader;
import javafx.application.Application;
import javafx.stage.Stage;

public class FXApp extends Application {

    public static void main(String [] args){ launch(); }

    @Override
    public void start(Stage stage) { Loader.start(stage); }
}
