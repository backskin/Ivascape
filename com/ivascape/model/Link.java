package ivascape.model;

import java.io.Serializable;

public class Link implements Complex<Link>, Serializable {

    private Link mate = null;

    private final Company one;

    private final Company two;

    private Double price;

    public Link(Company first, Company second, double price) {

        one = first;
        two = second;
        this.price = Math.round(price*100)/100.0;
    }

    @Override
    public int compareTo(Link o) {

        return Double.compare(this.price,o.price);
    }

    public void setPrice(double price) {

        this.price = Math.round(price*100)/100.0;
    }

    public Double getPrice() {
        return price;
    }

    public Company getOne() {
        return one;
    }

    public Company getTwo() {
        return two;
    }

    @Override
    public Link getMating() {

        return mate;
    }

    @Override
    public Link createMating() {

        mate = new Link(two,one,price);
        return mate;
    }

    @Override
    public void setMating(Link mate) {

        this.mate = mate;
    }
}