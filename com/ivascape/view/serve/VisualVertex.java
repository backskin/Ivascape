package ivascape.view.serve;

import ivascape.model.Company;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class VisualVertex {

    private static class vertexScaleListener implements ChangeListener<Number> {

        final VisualVertex vertex;

        vertexScaleListener(VisualVertex visualVertex){

            this.vertex = visualVertex;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            vertex.getCircle().setRadius(
                    VisualVertex.defaultCircleRadius
                            + VisualVertex.defaultCircleRadius *
                            (newValue.doubleValue() - 100.0) / 120.0);

            vertex.setAllCoors(
                    vertex.getItem().getLayoutX()*(newValue.doubleValue()/oldValue.doubleValue()),
                    vertex.getItem().getLayoutY()*(newValue.doubleValue()/oldValue.doubleValue())
            );
        }
    }

    public static final Double defaultCircleRadius = 20.0;

    private vertexScaleListener myOwnListener;

    public vertexScaleListener getScaleListener() {
        return myOwnListener;
    }

    public void setScaleListener() {
        this.myOwnListener = new vertexScaleListener(this);
    }

    private static final DoubleProperty circleRadius = new SimpleDoubleProperty(60.0);

    public static void setCircleRadius(double crcRadius) {

        circleRadius.set(crcRadius);
    }

    public static final Color defaultColor = Color.CRIMSON;

    private static Color circleColor;

    public static void setColor(Color color) {
        VisualVertex.circleColor = color;
    }

    public static void resetColor(){

        VisualVertex.circleColor = defaultColor;
    }

    @FXML
    private Circle circle;

    @FXML
    private VBox item;

    @FXML
    private Label name;

    @FXML
    private Circle shadow;

    @FXML
    private StackPane stackPane;

    private final DoubleProperty xCenter = new SimpleDoubleProperty();

    private final DoubleProperty yCenter = new SimpleDoubleProperty();

    public VisualVertex(){
    }

    public DoubleProperty xCenterProperty() {
        return xCenter;
    }

    public double x() { return getItem().getLayoutX(); }

    public double y() { return getItem().getLayoutY(); }

    public DoubleProperty yCenterProperty() {
        return yCenter;
    }

    @FXML
    private void initialize(){

        name.setMouseTransparent(true);
        item.setPickOnBounds(false);
        shadow.setMouseTransparent(true);
        stackPane.setPickOnBounds(false);
        circle.radiusProperty().addListener((observable, oldValue, newValue) -> {
            shadow.setRadius(shadow.getRadius() * (newValue.doubleValue() / oldValue.doubleValue()));
            name.fontProperty().setValue(Font.font("Arial", name.fontProperty().getValue().getSize()*(newValue.doubleValue() / oldValue.doubleValue())));
        });
        circle.setRadius(circleRadius.doubleValue());
        circle.setFill(circleColor);

        item.layoutXProperty().addListener((ov,oldval,newval)->xCenter.setValue(xCenter.get()+((Double) newval-(Double) oldval)));

        item.layoutYProperty().addListener((ov,oldval,newval)->yCenter.setValue(yCenter.get()+((Double) newval-(Double) oldval)));

        item.widthProperty().addListener((ov,oldval,newval)->xCenter.setValue(xCenter.get()+0.5*((Double) newval-(Double) oldval)));

        item.heightProperty().addListener((ov,oldval,newval)->yCenter.setValue(yCenter.get()+0.5*((Double) newval-(Double) oldval)));

    }

    public void setTitle(String title) {

        name.setText(title);
    }

    public VBox getItem() {

        return item;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setAllCoors(double xCoors, double yCoors){

        item.setLayoutX(xCoors);
        item.setLayoutY(yCoors);
    }


    private void setVerCM(VisualVertex vertex, Company company){

        final ContextMenu contextMenu = new ContextMenu();

        MenuItem addLink = new MenuItem("add link");

        addLink.setOnAction(event -> {});

        contextMenu.getItems().add(new MenuItem("add link"));
        contextMenu.getItems().add(new MenuItem("edit..."));
        contextMenu.getItems().add(new MenuItem("delete"));
        vertex.getCircle().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY)
                contextMenu.show(vertex.getCircle(), event.getScreenX()+10, event.getScreenY()+10);
        });
    }

}

