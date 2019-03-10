import graph.Graph;
import graph.Node;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Comparison {
    public static void main(String[] args) {
        Comparison comparison = new Comparison();
        int[][] D = comparison.computeDistanceFromFile();
        for (int i = 0; i < D.length; i++) {
            for (int j = 0; j < D[i].length; j++) {
                System.out.print(D[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Graph readFileAndProcess() {
        File file = new File("test.txt");
        Graph graph = new Graph();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line.trim().substring(0, 1).equals("%")) {
                line = reader.readLine();
            }
            String[] elements = line.split(" ");
            int n = Integer.parseInt(elements[0]);
            int m = Integer.parseInt(elements[2]);
            for (int i = 0; i < m; i++) {
                line = reader.readLine();
                elements = line.split(" ");
                String node1Str = elements[0];
                String node2Str = elements[1];
                int node1Int = Integer.parseInt(node1Str);
                int node2Int = Integer.parseInt(node2Str);
                if (graph.getMap().containsKey(node1Int)) {
                    if (graph.getMap().containsKey(node2Int)) {
                        graph.getMap().get(node1Int).getNeighbor().add(graph.getMap().get(node2Int));
                        graph.getMap().get(node2Int).getNeighbor().add(graph.getMap().get(node1Int));
                    } else {
                        Node node = new Node(node2Int);
                        graph.getMap().put(node2Int, node);
                        graph.getMap().get(node1Int).getNeighbor().add(node);
                        node.getNeighbor().add(graph.getMap().get(node1Int));
                    }
                } else {
                    Node node1 = new Node(node1Int);
                    graph.getMap().put(node1Int, node1);
                    if (graph.getMap().containsKey(node2Int)) {
                        node1.getNeighbor().add(graph.getMap().get(node2Int));
                        graph.getMap().get(node2Int).getNeighbor().add(node1);
                    } else {
                        Node node2 = new Node(node2Int);
                        graph.getMap().put(node2Int, node2);
                        node1.getNeighbor().add(node2);
                        node2.getNeighbor().add(node1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }

    public int[][] Dijkstra(Graph graph, int[][] D, int id) {
        Set<Integer> nodes = new HashSet<>();
        for (int key :
                graph.getMap().keySet()) {
            nodes.add(key);
        }

        while (!nodes.isEmpty()) {
            int min = extractMin(nodes, D[id]);
            nodes.remove(min);
            for (Node neighbor :
                    graph.getMap().get(min).getNeighbor()) {
                int neighborId = neighbor.getId() - 1;
                D[id][neighborId] = D[id][neighborId] > D[id][min - 1] + 1 ? D[id][min - 1] + 1 : D[id][neighborId];
            }
        }

        return D;
    }

    public int extractMin(Set<Integer> ids, int[] weights) {
        int min = 32766;
        for (int id :
                ids) {
            if (weights[id - 1] < min) {
                min = id;
            }
        }
        return min;
    }

    public int[][] computeDistanceFromFile() {
        Graph graph = readFileAndProcess();
        int[][] D = new int[graph.getMap().size()][graph.getMap().size()];
        for (int i = 0; i < D.length; i++) {
            for (int j = 0; j < D[i].length; j++) {
                if (i == j) D[i][j] = 0;
                else D[i][j] = 32765;
            }
        }
        for (int i = 0; i < graph.getMap().size(); i++) {
            D = Dijkstra(graph, D, i);
        }

        return D;
    }

}
