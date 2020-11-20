package core.graph;

public interface Graph {

    public abstract int numOfEdges();

    public abstract int numOfVertexes();

    public abstract void addEdge(int start, int end);

    public abstract Iterable<GraphEdge> adj(int v);


}
