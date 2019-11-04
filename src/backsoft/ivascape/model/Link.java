package backsoft.ivascape.model;

import backsoft.ivascape.logic.Complex;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.Serializable;

public class Link implements Serializable, Complex<Company, Link> {


    private final Company start;
    private final Company end;
    private transient DoubleProperty priceProp;
    private double price;
    private Link mate = null;

    public Link(Company start, Company end, double price) {

        priceProp = new SimpleDoubleProperty(price);
        this.price = price;
        this.start = start;
        this.end = end;
    }

    @Override
    public int compareTo(Link o) {

        return Double.compare(price, o.price);
    }

    public DoubleProperty priceProperty() {
        if (priceProp == null) priceProp = new SimpleDoubleProperty(price);
        else if (Double.compare(priceProp.getValue(), price) != 0) priceProp.setValue(price);
        return priceProp;
    }

    public Double getPrice() { return price;}
    public void setPrice(double price) {
        this.price = price;
        priceProp.setValue(price);
    }

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

        mate = new Link(end, start, price);
        return mate;
    }
}