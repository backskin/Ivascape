package ivascape.model;

public interface Complex<T> extends Comparable<T> {

    T getMating();

    T createMating();

    void setMating(T mate);
}