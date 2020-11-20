package core.graph;

import java.util.LinkedList;

public class GeneralPath {
    public LinkedList<Integer> route;
    public double length;

    @Override
    public String toString() {
        return String.format("路径是%s,长度是%f", route.toString(), length);
    }
}
