import graph.Graph;
import graph.Node;

import java.io.*;
import java.util.*;

public class Comparison {
    Set<int[]> set = new HashSet<>();

    public static void main(String[] args) {
        Comparison comparison = new Comparison();
        int[][] D = comparison.computeDistanceFromFile();
        Integer[] a = new Integer[] {3, 4, 2, 3, 1};
        List<Integer> r = Arrays.asList(a);
        a = new Integer[] {1, 2};
        List<Integer> C0 = Arrays.asList(a);
        Map<Integer, Map<String, Integer>> matrix = comparison.WFA(D, r, C0);
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

    public List<Integer> computeGreedy(int[][] D, List<Integer> r, List<Integer> C0) {
        List<Integer> answer = new LinkedList<>();
        for (int request :
                r) {
            int nearestServer = extractMin(D, request, C0);
            answer.add(nearestServer);
        }
        return answer;
    }

    public int extractMin(int[][] D, int r, List<Integer> C0) {
        int Min = 32766;
        for (int pos :
                C0) {
            if (D[r - 1][pos - 1] < Min) {
                Min = pos;
            }
        }
        return Min;
    }

    public List<Integer> computeOPT(int[][] D, LinkedList<Integer> r, LinkedList<Integer> C0) {
        return null;
    }

    public Map<Integer, Map<String, Integer>> WFA(int[][] D, List<Integer> r, List<Integer> C0) {
        int[] a = new int[D.length];
        for (int i = 0; i < D.length; i++) {
            a[i] = i + 1;
        }
        permutation(a, 0, 0, C0.size(), C0.size(), a.length, C0.size());
        /*
            compute distance between initial vertex set and every possible set.
         */
        Map<String, Integer> dist = new HashMap<>();
        for (int[] vertices :
                set) {
            int distance = computeDis(vertices, C0, D);
            dist.put(toString(vertices), distance);
        }
        /*
            compute the matrix of work funciton algo
         */
        Map<Integer, Map<String, Integer>> matrix = new HashMap<>();
        matrix.put(0, dist);
        dist = new HashMap<>();
        int last = 0;
        for (int request :
                r) {
            for (int[] combo :
                    set) {
                dist.put(toString(combo), workFunciton(matrix.get(last), request, combo, D));
            }
            matrix.put(request, dist);
            last = request;
            dist = new HashMap<>();
        }
        return matrix;
    }

    public int workFunciton(Map<String, Integer> matrix, int r, int[] combo, int[][] D) {
        Combo c = new Combo(combo);
        Combo temp = new Combo(combo);
        int min = 32766;
        if (c.contains(r)) {
            min = matrix.get(c.toString());
            for (int i = 0; i < combo.length; i++) {
                if (combo[i] == r) continue;
                temp = new Combo(combo);
                for (int j = 0; j < D.length; j++) {
                    if (c.contains(j + 1)) {
                        continue;
                    } else {
                        temp.substitute(i, j + 1);
                        int w = roughlyGet(matrix, temp.toString());
                        int dis = 0;
                        if (w != -1) {
                            dis = w + D[combo[i] - 1][j];
                        }
                        if (min > dis) {
                            min = dis;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < combo.length; i++) {
                temp.substitute(i, r);
                int w = roughlyGet(matrix, temp.toString());
                int dis = 0;
                if (w != -1) {
                    dis = w + D[combo[i] - 1][r - 1];
                }
                if (min > dis) min = dis;
                temp = new Combo(combo);
            }
        }
        return min;
    }

    /*
        Cus there are some key like "34" or "43". They are same clique.
        But we only store one of the combinations into matrix. it's allowed to get both of the combinations from matrix though.
     */
    public int roughlyGet(Map<String, Integer> map, String s){
        for (Map.Entry<String, Integer> entry:
                map.entrySet()){
            Set<String> key = new HashSet<String>(Arrays.asList(entry.getKey().split("")));
            boolean flag = true;
            for (String str :
                    s.split("")) {
                if (!key.contains(str)){
                    flag =false;
                    break;
                }
            }
            if (flag) return entry.getValue();
        }
        return -1;
    }

    public String toString(int[] arr) {
        StringBuilder builder = new StringBuilder();
        for (int a :
                arr) {
            builder.append(a);
        }
        return builder.toString();
    }

    public int computeDis(int[] vertices, List<Integer> C0, int[][] D) {
        Set<Integer> s = new HashSet<>();
        Set<Integer> v = new HashSet<>();
        /*
            <origin server name, sorted list<origin server name, dist>>
         */
        HashMap<Integer, List<Map.Entry<Integer, Integer>>> dist = new HashMap<>();
        for (int num :
                C0) {
            s.add(num);
        }
        for (int num :
                vertices) {
            if (C0.contains(num)) {
                s.remove(num);
            } else {
                v.add(num);
            }
        }
        /*
            Compute the shortest distance.
         */
        for (int vetex :
                v) {
            /*
                <position that origin server exchange, distance>
             */
            List<Map.Entry<Integer, Integer>> list = buildSortedList(s, D[vetex-1]);
            updateMap(dist, list);
        }
        int sum = 0;
        for (List<Map.Entry<Integer, Integer>> list :
                dist.values()) {
            sum += list.get(0).getValue();
        }
        return sum;
    }

    public void updateMap(HashMap<Integer, List<Map.Entry<Integer, Integer>>> dist, List<Map.Entry<Integer, Integer>> list) {
        int pos = list.get(0).getKey();
        if (dist.containsKey(pos)) {
            if (dist.get(pos).get(0).getValue() > list.get(0).getValue()) {
                List<Map.Entry<Integer, Integer>> temp = dist.get(pos);
                temp.remove(0);
                dist.put(pos, list);
                updateMap(dist, temp);
            } else {
                list.remove(0);
                updateMap(dist, list);
            }
        } else {
            dist.put(list.get(0).getKey(), list);
        }
    }

    public List<Map.Entry<Integer, Integer>> buildSortedList(Set<Integer> C0, int[] D) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i :
                C0) {
            map.put(i - 1, D[i - 1]);//real coordination
        }
        list.addAll(map.entrySet());
        Collections.sort(list, (mp1, mp2) -> mp1.getValue() > mp2.getValue() ? 1 : -1);
        return list;
    }

    public void permutation(int[] a, int begin0, int begin, int mid1, int mid2, int end, int selectNum) {

        int[] temp = new int[selectNum];
        System.arraycopy(a, begin0, temp, 0, selectNum);
        set.add(temp);

        for (int t = begin; t < mid1; t++) {
            for (int j = mid2; j < end; j++) {
                int temp0 = a[t];
                a[t] = a[j];
                a[j] = temp0;

                permutation(a, begin0, t + 1, mid1, j + 1, end, selectNum);

                a[j] = a[t];
                a[t] = temp0;
            }
        }
    }

}
