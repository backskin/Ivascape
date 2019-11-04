package backsoft.ivascape.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Company implements Serializable, Comparable<Company> {

    private transient SimpleStringProperty titleProp;
    private String ID;
    private String title;
    private Double money;
    private String address;
    private LocalDate date;

    public static Company createCompany(){
        return new Company(UUID.randomUUID().toString(), "no name", "", .0, LocalDate.now());
    }

    private Company(String ID, String title, String address, double money, LocalDate date){
        this.ID = ID;
        this.title = title;
        this.money = money;
        this.address = address;
        this.date = date;
    }

    public String getID() { return ID; }

    public StringProperty titleProperty() {
        if (titleProp == null) titleProp = new SimpleStringProperty(title);
        else if (!titleProp.getValue().equals(title)) titleProp.setValue(title);
        return titleProp;
    }

    public String getTitle() { return title; }
    public Company setTitle(String newTitle) {
        title = newTitle;
        titleProperty().setValue(newTitle);
        return this;
    }

    public double getMoney() {
        return money;
    }
    public Company setMoney(double money) {
        this.money = money;
        return this;
    }

    public String getAddress() {
        return address;
    }
    public Company setAddress(String address) {
        this.address = address;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public Company setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Company)) return false;
        return title.equals(((Company)o).title);
    }

    @Override
    public int compareTo(Company o) {
            return title.compareTo(o.title);
    }
}
