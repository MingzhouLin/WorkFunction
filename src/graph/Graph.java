package graph;

import java.util.HashMap;

public class Graph {
    private HashMap<Integer, Node> map;

    public Graph() {
        this.map = new HashMap<>();
    }

    public HashMap<Integer, Node> getMap() {
        return map;
    }
}
