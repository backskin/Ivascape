package backsoft.ivascape.viewcontrol;

import javafx.beans.property.DoubleProperty;
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

    VisualEdge(DoubleProperty xStart, DoubleProperty yStart,
               DoubleProperty xEnd, DoubleProperty yEnd, DoubleProperty price) {

        priceLabel.setVisible(false);
        priceLabel.setFont(Font.font("System",12));
        priceLabel.setMouseTransparent(true);
        priceLabel.setStyle("-fx-background-color : white");
        priceLabel.setTextFill(currentColor);
        priceLabel.setText(price.asString("%.2f").getValue());

        line.setMouseTransparent(true);
        line.setStrokeWidth(5.0);
        line.setStroke(currentColor);

        line.startXProperty().bind(xStart);
        line.startYProperty().bind(yStart);
        line.endXProperty().bind(xEnd);
        line.endYProperty().bind(yEnd);

        priceLabel.setLayoutX((line.getStartX()+line.getEndX() - priceLabel.getWidth())/2.0);
        priceLabel.setLayoutY((line.getStartY()+line.getEndY() - priceLabel.getHeight())/2.0);

        line.startXProperty().addListener(ov ->
                priceLabel.setLayoutX((line.getStartX() + line.getEndX() - priceLabel.getWidth())/2.0));

        line.startYProperty().addListener(ov ->
                priceLabel.setLayoutY((line.getStartY() + line.getEndY() - priceLabel.getHeight())/2.0));

        line.endXProperty().addListener(ov ->
                priceLabel.setLayoutX((line.getStartX() + line.getEndX() - priceLabel.getWidth())/2.0));

        line.endYProperty().addListener(ov ->
                priceLabel.setLayoutY((line.getStartY() + line.getEndY() - priceLabel.getHeight())/2.0));
    }
}
