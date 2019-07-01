package ivascape.model;

import java.io.Serializable;

public class Pair<One, Two> implements Serializable {

    private One one;
    private Two two;

    public Pair(One one, Two two){

        this.one = one;
        this.two = two;
    }

    public Pair(){

        this.one = null;
        this.two = null;
    }

    public One getOne() {
        return one;
    }

    public Two getTwo() {
        return two;
    }

    public void setOne(One one) {
        this.one = one;
    }

    public void setTwo(Two two) {
        this.two = two;
    }
}