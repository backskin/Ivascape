package backsoft.ivascape.logic;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {

    private A one;
    private B two;

    public Pair(A one, B two) {
        this.one = one;
        this.two = two;
    }

    public A getOne() {
        return one;
    }
    public B getTwo() {
        return two;
    }
}