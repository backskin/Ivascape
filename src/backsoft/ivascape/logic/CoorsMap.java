package backsoft.ivascape.logic;

import java.io.Serializable;
import java.util.HashMap;

import static backsoft.ivascape.logic.CoorsMap.*;

public class CoorsMap extends HashMap<String, Coors> {

    public static class Coors implements Serializable {
        public double x;
        public double y;

        public Coors(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Coors(Coors copyOf){
            this.x = copyOf.x;
            this.y = copyOf.y;
        }
    }
}
