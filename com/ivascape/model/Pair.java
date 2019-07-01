package ivascape.model;

import java.io.Serializable;

public class Pair<One, Two> extends javafx.util.Pair<One, Two> implements Serializable {

    public Pair(One one, Two two){
        super(one,two);
    }

    public Pair(){
        super(null, null);
    }
}