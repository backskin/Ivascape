package backsoft.ivascape.model;

import backsoft.ivascape.logic.Complex;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.Serializable;

public class Link implements Serializable, Complex<Company, Link> {

    private static class SerializableDoubleProperty extends SimpleDoubleProperty implements Serializable{
        SerializableDoubleProperty(double initValue){
            super(initValue);
        }
    }

    private final Company start;
    private final Company end;
    private DoubleProperty price = new SerializableDoubleProperty(0);
    private Link mate = null;

    public Link(Company start, Company end, double price) {

        this.start = start;
        this.end = end;
        this.price.setValue(price);
    }

    @Override
    public int compareTo(Link o) {

        return price.getValue().compareTo(o.getPrice());
    }

    public void setPrice(double price) {

        this.price.setValue(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public Double getPrice() { return price.getValue();}

    @Override
    public Company one() { return start; }

    @Override
    public Company two() { return end; }

    @Override
    public Link getMating() { return mate; }

    @Override
    public void setMating(Link mate) { this.mate = mate; }

    @Override
    public Link createMating() {

        mate = new Link(end, start, price.getValue());
        return mate;
    }
}