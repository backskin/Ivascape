package backsoft.ivascape.viewcontrol;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class VisualVertex {

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

    private static final Double defaultCircleRadius = 20.0;
    private static Color currentColor;
    static final Color defaultColor = Color.CRIMSON;

    public static void setColor(Color color) {
        VisualVertex.currentColor = color;
    }

    private final DoubleProperty xCenter = new SimpleDoubleProperty();
    private final DoubleProperty yCenter = new SimpleDoubleProperty();
    DoubleProperty xCenterProperty() { return xCenter; }
    DoubleProperty yCenterProperty() { return yCenter; }
    double x() { return item.getLayoutX(); }
    double y() { return item.getLayoutY(); }

    @FXML
    private void initialize(){

        name.setMouseTransparent(true);
        item.setPickOnBounds(false);
        shadow.setMouseTransparent(true);
        stackPane.setPickOnBounds(false);
        circle.radiusProperty().addListener((observable, oldValue, newValue) -> {
            shadow.setRadius(shadow.getRadius() * (newValue.doubleValue() / oldValue.doubleValue()));
            name.fontProperty().setValue(Font.font("Arial",
                    name.fontProperty().getValue().getSize()*(newValue.doubleValue() / oldValue.doubleValue())));
        });
        circle.setRadius(defaultCircleRadius);
        circle.setFill(currentColor);
        item.layoutXProperty().addListener(
                (ov, oldval, newval) -> xCenter.setValue(xCenter.get()
                + (newval.doubleValue() - oldval.doubleValue())));
        item.layoutYProperty().addListener(
                (ov, oldval,newval) -> yCenter.setValue(yCenter.get()
                + (newval.doubleValue() - oldval.doubleValue())));
        item.widthProperty().addListener(
                (ov, oldval, newval) -> xCenter.setValue(xCenter.get()
                + 0.5*(newval.doubleValue() - oldval.doubleValue())));
        item.heightProperty().addListener(
                (ov, oldval, newval) -> yCenter.setValue(yCenter.get()
                + 0.5*(newval.doubleValue() - oldval.doubleValue())));
    }

    void setTitle(String title) { name.setText(title); }

    VBox getItem() { return item; }

    Circle getCircle() { return circle; }

    void setAllCoors(double xCoors, double yCoors){
        item.setLayoutX(xCoors);
        item.setLayoutY(yCoors);
    }

    ChangeListener<Number> getScaleListener() {
        return (observableValue, number, t1) -> {
            getCircle().setRadius(
                    VisualVertex.defaultCircleRadius
                            + VisualVertex.defaultCircleRadius *
                            (t1.doubleValue() - 100.0) / 120.0);
            setAllCoors(
                    getItem().getLayoutX()*(t1.doubleValue()/number.doubleValue()),
                    getItem().getLayoutY()*(t1.doubleValue()/number.doubleValue())
            );
        };
    }
}