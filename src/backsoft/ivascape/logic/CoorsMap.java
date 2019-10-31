package backsoft.ivascape.logic;

import java.util.HashMap;

import static backsoft.ivascape.logic.CoorsMap.*;

public class CoorsMap extends HashMap<Integer, Coors> {

    public static class Coors{
        public double x;
        public double y;

        public Coors(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}