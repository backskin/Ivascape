package ivascape.view.serve;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class VisualEdge {

    private class scaleListener implements ChangeListener<Number>{

        final Label price;

        final Line line;

        scaleListener(Label price,Line line) {

            this.line = line;
            this.price = price;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            line.setStrokeWidth(5.0*(newValue.doubleValue())/100.0);

            price.setFont(Font.font("System",
                    12 + 12 * (newValue.doubleValue() - 100.0) / 200.0));
        }
    }

    class priceVisibleListener implements ChangeListener<Boolean>{

        final Label price;

        priceVisibleListener(Label price){

            this.price = price;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            price.setVisible(newValue);
        }
    }

    private final ChangeListener[] myOwnListener = new ChangeListener[2];

    public ChangeListener getListener(int index) {
        return myOwnListener[index];
    }

    public void setListeners() {
        this.myOwnListener[1] = new scaleListener(price,line);
        this.myOwnListener[0] = new priceVisibleListener(price);
    }

    private final Label price = new Label("0.0");

    public void setPrice(double price){

        this.price.setText("$" + Double.toString(price));
    }

    public Label getPrice() {
        return price;
    }

    private final Line line = new Line();

    public Line getLine() {
        return line;
    }

    public static final Color defaultColor = Color.CRIMSON;

    private static Color edgeColor = defaultColor;

    public static void setColor(Color color){

        edgeColor = color;
    }

    public static void resetColor(){

        edgeColor = defaultColor;
    }

    private VisualEdge(DoubleProperty xStart, DoubleProperty yStart, DoubleProperty xEnd, DoubleProperty yEnd) {

        price.setVisible(false);
        price.setFont(Font.font("System",12));
        price.setMouseTransparent(true);
        price.setStyle("-fx-background-color : white");
        price.setTextFill(edgeColor);

        line.setMouseTransparent(true);
        line.setStrokeWidth(5.0);
        line.setStroke(edgeColor);

        xStart.addListener((ov, oldval, newval)-> {
            line.setStartX(newval.doubleValue());
            price.setLayoutX((newval.doubleValue()+line.getEndX() - price.getWidth())/2.0);
        });

        yStart.addListener((ov, oldval, newval)-> {
            line.setStartY(newval.doubleValue());
            price.setLayoutY((newval.doubleValue()+line.getEndY() - price.getHeight())/2.0);
        });

        xEnd.addListener((ov, oldval, newval)-> {
            line.setEndX(newval.doubleValue());
            price.setLayoutX((line.getStartX()+newval.doubleValue() - price.getWidth())/2.0);
        });

        yEnd.addListener((ov, oldval, newval)-> {
            line.setEndY(newval.doubleValue());
            price.setLayoutY((line.getStartY()+newval.doubleValue() - price.getHeight())/2.0);
        });

        line.setStartX(xStart.doubleValue());
        line.setStartY(yStart.doubleValue());
        line.setEndX(xEnd.doubleValue());
        line.setEndY(yEnd.doubleValue());

        price.setLayoutX((line.getStartX()+line.getEndX() - price.getWidth())/2.0);
        price.setLayoutY((line.getStartY()+line.getEndY() - price.getHeight())/2.0);
    }

    public VisualEdge(VisualVertex start, VisualVertex end, double price){

        this(start.xCenterProperty(),start.yCenterProperty(),end.xCenterProperty(),end.yCenterProperty());
        this.price.setText("$" + Double.toString(price));

    }
}
