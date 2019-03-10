package graph;

import java.util.LinkedList;

public class Node {
    private int id;
    private LinkedList<Node> neighbor;

    public Node(int id) {
        this.id = id;
        this.neighbor = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LinkedList<Node> getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(LinkedList<Node> neighbor) {
        this.neighbor = neighbor;
    }
}
