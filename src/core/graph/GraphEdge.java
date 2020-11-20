package core.graph;

public class GraphEdge {
    int start;
    int end;
    double weight;

    public GraphEdge(int start, int end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }
}
