package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.logic.Pair;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    private static final Double defaultCircleRadius = 20.0;
    private static Color currentColor;
    static final Color defaultColor = Color.CRIMSON;
    private Pair<Double,Double> dragContext;

    Pair<Double, Double> getDragContext() {
        return dragContext;
    }
    void setDragContext(Pair<Double, Double> dragContext) {
        this.dragContext = dragContext;
    }

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
        circle.setRadius(defaultCircleRadius);
        circle.setFill(currentColor);
        xCenter.set(circle.getCenterX() + item.getLayoutX() + item.getWidth()/2);
        yCenter.set(circle.getCenterY() + item.getLayoutY() + item.getHeight()/2);

        item.layoutXProperty().addListener((o, ov, nv) ->
                xCenter.add((nv.doubleValue() - ov.doubleValue())));
        item.layoutYProperty().addListener((o, ov, nv) ->
                yCenter.add((nv.doubleValue() - ov.doubleValue())));
        item.widthProperty().addListener((o, ov, nv) ->
                xCenter.add((nv.doubleValue() - ov.doubleValue())/2));
        item.heightProperty().addListener((o, ov, nv) ->
                yCenter.add((nv.doubleValue() - ov.doubleValue())/2));
    }

    void setTitle(StringProperty title) {
        name.textProperty().bind(title);
    }

    VBox getItem() { return item; }

    Circle getCircle() { return circle; }

    void setXY(double xCoors, double yCoors){
        item.setLayoutX(xCoors);
        item.setLayoutY(yCoors);
    }
    void moveXY(double xAdd, double yAdd){
        item.setLayoutX(Math.max(0, item.getLayoutX()+xAdd));
        item.setLayoutY(Math.max(0, item.getLayoutY()+yAdd));
    }

    private ChangeListener<Number> scaleListener = (o, ov, nv) -> {
        getCircle().setRadius(VisualVertex.defaultCircleRadius * nv.doubleValue() / 100);
        double scale = nv.doubleValue() / ov.doubleValue();
        setXY(
                item.getLayoutX() * scale,
                item.getLayoutY() * scale);
        name.fontProperty().setValue(Font.font("Arial", 14 * nv.doubleValue() / 100));
    };

    ChangeListener<Number> getScaleListener() {
        return scaleListener;
    }
}