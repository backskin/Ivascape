package backsoft.ivascape.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.time.LocalDate;

public class Company implements Serializable, Comparable<Company> {

    private String title;
    private Double moneyCapital;
    private String address;
    private LocalDate date;

    public Company(String title, String address, double moneyCapital, LocalDate date){

        this.title = title;

        this.moneyCapital = Math.round(moneyCapital * 100) / 100.0;
        this.address = address;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMoneyCapital() {
        return moneyCapital;
    }

    private void setMoneyCapital(double moneyCapital) {
        this.moneyCapital = moneyCapital;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDate() {
        return date;
    }

    private void setDate(LocalDate date) {
        this.date = date;
    }

    void asCopyOf(Company other){
        setTitle(other.title);
        setAddress(other.address);
        setDate(other.date);
        setMoneyCapital(other.moneyCapital);
    }

    @Override
    public int compareTo(Company o) {
        return title.compareTo(o.title);
    }
}
