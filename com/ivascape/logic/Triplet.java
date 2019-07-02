package ivascape.logic;

public class Triplet<A,B,C> {

    private A one;
    private B two;
    private C three;

    public Triplet(A one, B two, C three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public A getOne() {
        return one;
    }
    public B getTwo() {
        return two;
    }
    public C getThree() {
        return three;
    }

}
