package backsoft.ivascape.viewcontrol;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class VisualEdge {

    private final Label priceLabel = new Label("0.00");
    private final Line line = new Line();

    Label getPriceLabel() {
        return priceLabel;
    }
    Line getLine() {
        return line;
    }

    static final Color defaultColor = Color.CRIMSON;
    private static Color currentColor = defaultColor;
    public static void setColor(Color color){ currentColor = color; }

    private ChangeListener<Number> scaleListener = (val, no, nn) -> {
        line.setStrokeWidth(nn.doubleValue() / 10);
        priceLabel.setFont(Font.font(18 * nn.doubleValue() / 100));
    };

    ChangeListener<Number> getScaleListener() {
        return scaleListener;
    }

    private void rotatePriceLabel(){
        priceLabel.setRotate(Math.toDegrees(Math.atan(
                (line.getStartY()-line.getEndY()) /
                        (line.getStartX()-line.getEndX()))));
    }

    VisualEdge(DoubleProperty xStart, DoubleProperty yStart,
               DoubleProperty xEnd, DoubleProperty yEnd, DoubleProperty priceProp) {

        priceLabel.setVisible(false);
        priceLabel.setFont(Font.font(18));
        priceLabel.setMouseTransparent(true);
        priceLabel.setStyle("-fx-background-color : white");
        priceLabel.setTextFill(currentColor);
        priceLabel.setText(priceProp.asString("%.2f").getValue());

        line.setMouseTransparent(true);
        line.setStrokeWidth(10);
        line.setStroke(currentColor);

        line.startXProperty().bind(xStart);
        line.startYProperty().bind(yStart);
        line.endXProperty().bind(xEnd);
        line.endYProperty().bind(yEnd);
        priceLabel.layoutXProperty().bind(line.startXProperty().add(line.endXProperty())
                .subtract(priceLabel.widthProperty()).divide(2.0));
        priceLabel.layoutYProperty().bind(line.startYProperty().add(line.endYProperty())
                .subtract(priceLabel.heightProperty()).divide(2.0));

        rotatePriceLabel();

        line.startXProperty().addListener(ov -> rotatePriceLabel());
        line.startYProperty().addListener(ov -> rotatePriceLabel());
        line.endXProperty().addListener(ov -> rotatePriceLabel());
        line.endYProperty().addListener(ov -> rotatePriceLabel());
    }
}
