package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.logic.CoorsMap;
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
    private VBox pane;
    @FXML
    private Label titleLabel;

    private static final Double defaultCircleRadius = 20.0;
    private static Color currentColor;
    static final Color defaultColor = Color.CRIMSON;
    private Pair<Double,Double> dragContext;

    Pair<Double, Double> getDragContext() {
        return dragContext;
    }
    void setDragContext(Double x, Double y) { this.dragContext = new Pair<>(x, y); }

    public static void setColor(Color color) {
        VisualVertex.currentColor = color;
    }

    private final DoubleProperty xCenter = new SimpleDoubleProperty(0);
    private final DoubleProperty yCenter = new SimpleDoubleProperty(0);
    DoubleProperty xCenterProperty() { return xCenter; }
    DoubleProperty yCenterProperty() { return yCenter; }

    @FXML
    private void initialize(){

        titleLabel.setMouseTransparent(true);
        pane.setPickOnBounds(false);
        circle.setRadius(defaultCircleRadius);
        circle.setFill(currentColor);

        xCenter.bind(pane.widthProperty().divide(2.0).add(pane.layoutXProperty()));
        yCenter.bind(pane.layoutYProperty().add(circle.layoutYProperty()));
    }

    void setTitle(StringProperty title) {
        titleLabel.textProperty().bind(title);
    }

    VBox getPane() { return pane; }

    Circle getCircle() { return circle; }

    void setXY(CoorsMap.Coors coors){
        pane.setLayoutX(coors.x);
        pane.setLayoutY(coors.y);
    }

    void moveXY(double xAdd, double yAdd){
        pane.setLayoutX(Math.max(0, pane.getLayoutX()+xAdd));
        pane.setLayoutY(Math.max(0, pane.getLayoutY()+yAdd));
    }

    ChangeListener<Number> scaleListener = (o, ov, nv) -> {
        double scale = nv.doubleValue() / ov.doubleValue();
        pane.setLayoutX(pane.getLayoutX() * scale);
        pane.setLayoutY(pane.getLayoutY() * scale);
        circle.setRadius(.20 * nv.doubleValue());
        titleLabel.setFont(Font.font(.14 * nv.doubleValue()));
    };
}