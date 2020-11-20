package core.graph;


import java.util.*;

public class UndirectedWeightedGraph implements Graph {
    private final int numOfVertexes; //顶点个数
    private int numOfEdges;//边的个数
    private LinkedList<GraphEdge>[] adj;//某个顶点临近的所有顶点


    private boolean[] visited;


    public UndirectedWeightedGraph(int numOfVertexes) {
        this.numOfVertexes = numOfVertexes;

        this.adj = new LinkedList[numOfVertexes];
        for (int i = 0; i < adj.length; i++) {
            this.adj[i] = new LinkedList<>();
        }

    }

    @Override
    public int numOfEdges() {
        return this.numOfEdges;
    }

    @Override
    public int numOfVertexes() {
        return this.numOfVertexes;
    }

    /**
     * 在有向图中加入一条边
     *
     * @param start 边的起点
     * @param end   边的终点
     */
    @Override
    public void addEdge(int start, int end) {

        //先判断输入的顶点是否是合法的
        if (!isVertexLegal(start) || !isVertexLegal(end)) {
            return;
        }

        //判断这条边是否已经存在
        for (GraphEdge edge : this.adj[start]
        ) {
            if (edge.end == end) {
                return;
            }

        }

        //在邻接表中插入这条边
        this.adj[start].add(new GraphEdge(start, end, 1));
        this.adj[end].add(new GraphEdge(end, start, 1));

        this.numOfEdges++;
    }

    public void addEdge(int start, int end, double weight) {

        //先判断输入的顶点是否是合法的
        if (!isVertexLegal(start) || !isVertexLegal(end)) {
            return;
        }

        //判断这条边是否已经存在
        for (GraphEdge edge : this.adj[start]
        ) {
            if (edge.end == end) {
                edge.weight = weight;
                return;
            }

        }

        for (GraphEdge edge : this.adj[end]
        ) {
            if (edge.end == start) {
                edge.weight = weight;
                return;
            }

        }

        //在邻接表中插入这条边
        this.adj[start].add(new GraphEdge(start, end, weight));
        this.adj[end].add(new GraphEdge(end, start, weight));

        this.numOfEdges++;
    }

    @Override
    public Iterable<GraphEdge> adj(int v) {
        if (!isVertexLegal(v)) {
            return null;
        }
        return this.adj[v];
    }

    /**
     * 判断输入的顶点v是否在合法的范围内(0<=v<=numOfVertexes-1)
     *
     * @param v 输入的顶点
     * @return 如果合法，返回true。如果不合法，返回false
     */
    private boolean isVertexLegal(int v) {
        return v >= 0 && v <= this.numOfVertexes - 1;
    }


    public GeneralPath[] shortestPath(int start) {

        int[] edgeTo = new int[this.numOfVertexes()];
        double[] minDistance = new double[this.numOfVertexes()];//记录每一个顶点到该顶点的最短距离
        this.visited = new boolean[this.numOfVertexes()];

        //先将minDistance[]全部用最大的一个数值代替
        Arrays.fill(minDistance, Double.MAX_VALUE);

        minDistance[start] = 0;//将起点的最短距离设置为0

        GeneralPath[] result = new GeneralPath[this.numOfVertexes];

        while (true) {
            int v = findShortestUnvisitedVertex(visited, minDistance);
            if (v == Integer.MIN_VALUE) {
                break;
            }
            this.visited[v] = true;

            for (GraphEdge nextEdge : adj(v)
            ) {
                if (nextEdge.weight + minDistance[v] <= minDistance[nextEdge.end]) {
                    minDistance[nextEdge.end] = nextEdge.weight + minDistance[v];
                    edgeTo[nextEdge.end] = v;

                }

            }

        }

        for (int end = 0; end < this.numOfVertexes; end++) {
            result[end] = generatePath(edgeTo, start, end, minDistance);
        }


        return result;


    }


    private int findShortestUnvisitedVertex(boolean[] visited, double[] minDistance) {

        //先判断是否已经全部访问过
        boolean allVisited = true;
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                allVisited = false;
                break;
            }
        }

        //如果已经全部访问过，则返回一个奇怪的值
        if (allVisited) {
            return Integer.MIN_VALUE;
        }

        double currentMin = Double.MAX_VALUE;
        int index = Integer.MIN_VALUE;

        for (int i = 0; i < minDistance.length; i++) {
            if (minDistance[i] < currentMin && !visited[i]) {
                index = i;
                currentMin = minDistance[i];

            }

        }

        return index;


    }

    private GeneralPath generatePath(int[] edgeTo, int start, int end, double[] minDistance) {

        //实例化path变量
        GeneralPath path = new GeneralPath();

        if (minDistance[end] == Double.MAX_VALUE) {
            path.route = null;
            path.length = Double.MAX_VALUE;
        } else {
            path.length = minDistance[end];

            Stack<Integer> stack = new Stack<>();

            int current = end;

            while (current != start) {
                stack.push(current);
                current = edgeTo[current];
            }

            stack.push(start);

            LinkedList<Integer> linkedList = new LinkedList<>();

            while (!stack.empty()) {
                linkedList.add(stack.pop());
            }

            path.route = linkedList;

        }


        return path;


    }

}
