package backsoft.ivascape.viewcontrol;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class VisualEdge {

    ChangeListener<Boolean> getPriceListener(){
        return (observableValue, aBoolean, t1) -> priceLabel.setVisible(t1);
    }

    ChangeListener<Number> getScaleListener(){

        return (observableValue, number, t1) -> {
            line.setStrokeWidth(5.0*(t1.doubleValue())/100.0);
            priceLabel.setFont(Font.font("System",
                    12 + 12 * (t1.doubleValue() - 100.0) / 200.0));
        };
    }

    private final Label priceLabel = new Label("0.0");

    public void setPriceLabel(double priceLabel){

        this.priceLabel.setText("$" + priceLabel);
    }

    public Label getPriceLabel() {
        return priceLabel;
    }

    private final Line line = new Line();

    Line getLine() {
        return line;
    }

    static final Color defaultColor = Color.CRIMSON;

    private static Color edgeColor = defaultColor;

    public static void setColor(Color color){

        edgeColor = color;
    }

    public VisualEdge(DoubleProperty xStart, DoubleProperty yStart,
                      DoubleProperty xEnd, DoubleProperty yEnd, double price) {

        priceLabel.setVisible(false);
        priceLabel.setFont(Font.font("System",12));
        priceLabel.setMouseTransparent(true);
        priceLabel.setStyle("-fx-background-color : white");
        priceLabel.setTextFill(edgeColor);
        priceLabel.setText("$" + price);

        line.setMouseTransparent(true);
        line.setStrokeWidth(5.0);
        line.setStroke(edgeColor);

        xStart.addListener((ov, oldval, newval)-> {
            line.setStartX(newval.doubleValue());
            priceLabel.setLayoutX((newval.doubleValue()+line.getEndX() - priceLabel.getWidth())/2.0);
        });

        yStart.addListener((ov, oldval, newval)-> {
            line.setStartY(newval.doubleValue());
            priceLabel.setLayoutY((newval.doubleValue()+line.getEndY() - priceLabel.getHeight())/2.0);
        });

        xEnd.addListener((ov, oldval, newval)-> {
            line.setEndX(newval.doubleValue());
            priceLabel.setLayoutX((line.getStartX()+newval.doubleValue() - priceLabel.getWidth())/2.0);
        });

        yEnd.addListener((ov, oldval, newval)-> {
            line.setEndY(newval.doubleValue());
            priceLabel.setLayoutY((line.getStartY()+newval.doubleValue() - priceLabel.getHeight())/2.0);
        });

        line.setStartX(xStart.doubleValue());
        line.setStartY(yStart.doubleValue());
        line.setEndX(xEnd.doubleValue());
        line.setEndY(yEnd.doubleValue());

        priceLabel.setLayoutX((line.getStartX()+line.getEndX() - priceLabel.getWidth())/2.0);
        priceLabel.setLayoutY((line.getStartY()+line.getEndY() - priceLabel.getHeight())/2.0);
    }
}
