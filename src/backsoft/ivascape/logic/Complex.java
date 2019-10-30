package backsoft.ivascape.logic;

public interface Complex<Val, T extends Complex<Val, T>> extends Comparable<T> {

    Val one();
    Val two();
    T getMating();
    T createMating();
    void setMating(T mate);
}