package backsoft.ivascape.model;

import backsoft.ivascape.logic.Complex;

import java.io.Serializable;

public class Link implements Serializable, Complex<Company, Link> {

    private final Company start;
    private final Company end;
    private Double price;
    private Link mate = null;

    public Link(Company start, Company end, double price) {

        this.start = start;
        this.end = end;
        this.price = Math.round(price*100)/100.0;
    }

    @Override
    public int compareTo(Link o) {

        return Double.compare(this.price, o.price);
    }

    public void setPrice(double price) {

        this.price = Math.round(price*100)/100.0;
    }

    public Double getPrice() { return price;}

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