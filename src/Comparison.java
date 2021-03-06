

import graph.Graph;
import graph.Node;

import java.io.*;
import java.util.*;

public class Comparison {
    Set<int[]> set = new HashSet<>();
    public Graph graph = new Graph();


    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.println("Please choose mode: 1. Read from file  2. Random Graph");

        String mode = scan.nextLine();
        mode = mode.trim();
        Comparison comparison = new Comparison();
        int[][] D = comparison.computeDistanceFromFile(mode);
        Integer[] a = new Integer[] {1,2,6,8,10};
        List<Integer> r = new ArrayList<>();

        System.out.println("Please choose request mode: 1. Fixed Requests 2. Random Uniform 3. Markov chain");

        mode = scan.nextLine();
        if( mode.equals("1")){
            r = Arrays.asList(a);
            System.out.println("The requests will be 1 2 6 8 10");
        }
        else if( mode.equals("2")){

            System.out.println("Please choose number of requests:");
            int num = Integer.parseInt(scan.nextLine().trim());
            r = RandomRequest(D[0].length,num);
        }
        else{

            System.out.println("Please choose number of requests:");
            int num = Integer.parseInt(scan.nextLine().trim());
            r = MarkovRequest(D[0].length,num,comparison.graph);
        }

        System.out.println("Please choose initial configuration mode: 1. Fixed  2. Random");
        String conf_mode= scan.nextLine().trim();


         // request list
        if(conf_mode.equals("1"))
            a = new Integer[] {1,2,3,4};
        else{
            a = RandomConf(D[0].length);
        }
        Arrays.sort(a);
        System.out.println("The initial configuration is:"+a[0] +" "+a[1]+" "+ a[2]+" "+a[3]);
        List<Integer> C0 = Arrays.asList(a);//C0
        System.out.println("Distance matrix:");
        boolean unreached = false;
        for (int i = 0; i < D.length; i++) {
            for (int j = 0; j < D[i].length; j++) {
                if(D[i][j] == 32765)
                    unreached = true;
                System.out.print(D[i][j] + " ");
            }
            System.out.println();
        }
        if(unreached){
            System.out.println("The graph contains unreached node. Related request cannot feed.");
            System.out.println("Please run the program again.");
            System.exit(0);
        }
        //opt
        List<MyEntry<Integer, Map<String, Integer>>> opt = comparison.OPT(D, r, C0);
        MyEntry lastRound = opt.get(opt.size()-1);

        MyEntry test = opt.get(2);
        Map<String, Integer> map1 = (Map)test.getValue();
        Map<String, Integer> map = (Map)lastRound.getValue();
        int costOfOPT = Integer.MAX_VALUE;
        for(String conf:map.keySet()){
            if(map.get(conf)<costOfOPT)
                costOfOPT = map.get(conf);

        }
        System.out.println("OPT result: "+costOfOPT);

        Stack<String> wfa_stack = comparison.WFA(opt,D);
        int costOfWFA = comparison.computeTotalCost(wfa_stack,D);
        System.out.println("WFA result: "+costOfWFA);

        //greedy
        List<int[]> greedy = comparison.Greedy(D,r,C0);
        int costOfGreedy =0;
        for(int j=0;j<greedy.size();j++){
            costOfGreedy = costOfGreedy + greedy.get(j)[1];
        }
        System.out.println("Greedy result: "+costOfGreedy);

    }

    public int[] FindDifferent(String[] array1, String[] array2){

        int[] res = new int[2];



        Set<String> set1= new HashSet<>();
        Set<String> set2= new HashSet<>();
        Set<String> total_set_1= new HashSet<>();
        Set<String> total_set_2= new HashSet<>();

        for(String s: array1){
            set1.add(s);
            total_set_1.add(s);
            total_set_2.add(s);
        }

        for(String s: array2){
            set2.add(s);
            total_set_1.add(s);
            total_set_2.add(s);
        }

        total_set_1.removeAll(set1);
        total_set_2.removeAll(set2);

        for(String s: total_set_1){
            res[0] = Integer.parseInt(s);
        }
        for(String s: total_set_2){
            res[1] = Integer.parseInt(s);
        }

        return res;
    }


    public int computeTotalCost (Stack<String> stack, int D[][]){
        int res = 0;

        String[] array = new String[stack.size()];
        stack.copyInto(array);
        for(int i =1;i< array.length;i++){
            String conf1 = array[i-1];
            String conf2= array[i];
            int[] position = FindDifferent(conf1.split(","),conf2.split(","));


            if(position[0]==0&&position[1] ==0){
                continue;
            }
            else{
                res = res + D[position[0]-1][position[1]-1];

            }


        }
        return  res;

    }

    public static Graph RandomGraph(int n, double p){
        Graph graph = new Graph();

        for (int i=1;i<=n;i++){
            Node node = new Node(i);
            graph.getMap().put(i,node);
        }
        for (int i =1; i<= n-1;i++){
            for (int j =i+1;j<=n;j++){
                double random = Math.random();
                if(random <= p){
                    Node node1 = graph.getMap().get(i);
                    Node node2 = graph.getMap().get(j);
                    node1.getNeighbor().add(node2);
                    node2.getNeighbor().add(node1);

                }
            }
        }

        return graph;
    }


    public static Integer[] RandomConf(int n){
        Integer[] conf = {0,0,0,0};
        for(int i=0;i<4;i++){
            int request = 1 + (int)(Math.random() * (n));


            boolean hasThisNode =false;
            for(int j = 0;j<4;j++){
                if(request == conf[j]){
                    hasThisNode = true;
                    break;
                }
            }
            if(!hasThisNode)
                conf[i] = new Integer(request);
            else{
                i--;
            }

        }


        return conf;

    }
    public static List<Integer> RandomRequest(int n, int num){
        List<Integer> requests = new ArrayList<>();
        for(int i=0;i<num;i++){
            Integer request = 1 + (int)(Math.random() * (n));
            requests.add(request);

        }

        return  requests;


    }

    public static List<Integer> MarkovRequest(int n, int num, Graph graph){
        List<Integer> requests = new ArrayList<>();
        Integer lastRequest =-1;

        Integer request = 1 + (int)(Math.random() * (n));
        requests.add(request);
        lastRequest = request;
        for(int i=1;i<num;i++){

            Node node = graph.getMap().get(lastRequest);
            int degree = node.getNeighbor().size();
            int random = (int)(Math.random() * (degree+1));
            if(random == 0){
                request = lastRequest;
            }
            else{

                request = node.getNeighbor().get(random-1).getId();
            }
            requests.add(request);
            lastRequest = request;

        }

        return  requests;


    }






    public Graph readFileAndProcess(String path) {
        File file = new File(path);
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
        int min_id = -1;
        for (int id :
                ids) {
            if (weights[id - 1] < min) {
                min = weights[id-1];
                min_id = id;
            }
        }
        return min_id;
    }



    public int[][] computeDistanceFromFile(String mode) {
        Graph graph = new Graph();
        Scanner scan = new Scanner(System.in);
        if(mode.equals("1")){
            System.out.println("Please choose the graph file:");
            System.out.println("1) klein-b1  30 nodes, 60 edges\n" +
                    "2) Ragusa18  23 nodes, 64 edges\n" +
                    "3) n3c4-b3   20 nodes, 60 edges\n" +
                    "4) lap_25    25 nodes, 97 edges\n" +
                    "5) football  35 nodes, 118 edges");
            String choose = scan.nextLine().trim();
            String path = "";
            switch(choose){
                case "1":
                    path = "klein-b1.txt";
                    break;
                case"2":
                    path = "Ragusa18.txt";
                    break;
                case"3":
                    path = "n3c4-b3.txt";
                    break;
                case"4":
                    path = "lap_25.txt";
                    break;
                default:
                    path = "football.txt";
                    break;

            }




            graph = readFileAndProcess(path);
            this.graph = graph;
        }
        else{


            System.out.println("Please choose n:");
            int n = Integer.parseInt(scan.nextLine().trim());
            System.out.println("Please choose p:");
            double p = Double.parseDouble(scan.nextLine().trim());

            graph = RandomGraph(n,p);
            this.graph = graph;
        }



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

//        int[][] test = {{0,1,2,4},
//                        {1,0,2,3},
//                        {2,2,0,3},
//                        {4,3,3,0}};
//        return test;

        return D;
    }

    public List<int[]> Greedy(int[][] D, List<Integer> r, List<Integer> C0) {

        int[] conf = new int[C0.size()];

        for(int i =0;i<C0.size();i++){
            conf[i] = C0.get(i);
        }
        List<int[]> res = new ArrayList<>();
        for (int request :
                r) {
            int[] temp = new int[2];// [0]  position  [1] cost
            temp =  extractMinConf(D, request, conf);

            for(int j =0; j<conf.length;j++){
                if(conf[j] == temp[0]){
                    conf[j] = request;
                    Arrays.sort(conf);
                    break;
                }
            }
            res.add(temp);
        }
        return res;
    }

    public int[] extractMinConf(int[][] D, int r, int[] C0) {
        int Min = 32766;
        int[] res = {0,0};
        for (int pos :
                C0) {
            if (D[r - 1][pos - 1] < Min) {
//                Min = pos;
                Min = D[r-1][pos-1];
                res[0] = pos;
                res[1] = Min;

            }
        }
        return res;
    }

    public List<Integer> computeOPT(int[][] D, LinkedList<Integer> r, LinkedList<Integer> C0) {
        return null;
    }

    public List<MyEntry<Integer, Map<String, Integer>>> OPT(int[][] D, List<Integer> r, List<Integer> C0) {
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
//            fullSort(vertices, 0, vertices.length - 1);
            int distance = computeDis(vertices, C0, D);
            Arrays.sort(vertices);
            dist.put(toString(vertices), distance);
        }
        /*
            compute the matrix of work funciton algo
         */
        List<MyEntry<Integer, Map<String, Integer>>> matrix = new LinkedList<>();
        MyEntry<Integer, Map<String, Integer>> entry = new MyEntry<>(0, dist);
        matrix.add(entry);
        dist = new HashMap<>();
        int last = 0;
        for (int request :
                r) {
            for (int[] combo :
                    set) {
                Arrays.sort(combo);

                dist.put(toString(combo), workFunciton( ((LinkedList<MyEntry<Integer, Map<String, Integer>>>) matrix).getLast().getValue(), request, combo, D));
            }
            entry = new MyEntry<>(request, dist);
            matrix.add(entry);
            dist = new HashMap<>();
        }
        return matrix;
    }

    public int getMin(List<String> list, Map<String, Integer> dist){
        int min = Integer.MAX_VALUE;
        for(String s: list){
            if(dist.get(s)<min){
                min = dist.get(s);
            }
        }
        return min;
    }

    public Stack<String> WFA (List<MyEntry<Integer, Map<String, Integer>>> matrix, int[][] D){

        String last_conf_1 ="";

        Stack<String> res_stack = new Stack<>();
        for(int i =0;i<matrix.size();i++){

            int min_cost = Integer.MAX_VALUE;
            Integer r = matrix.get(i).getKey();
//            Set<String> last_conf = matrix.get(i).getValue().keySet();
            if( i == 0){
                for(String init_conf:matrix.get(0).getValue().keySet()){

                        int cost = matrix.get(0).getValue().get(init_conf);
                        if(cost <min_cost){
                            last_conf_1 = init_conf;
                            min_cost = cost;
                            if(res_stack.isEmpty()){
                                res_stack.push(init_conf);
                            }
                            else{
                                res_stack.pop();
                                res_stack.push(init_conf);

                            }

                        }
                }

            }
            else{

                Map<String,Integer> possible_last_confs = computepossiblelastconf(last_conf_1,r.toString(),D);
                for(String s:possible_last_confs.keySet()){
                    int cost = possible_last_confs.get(s) + matrix.get(i-1).getValue().get(s);
                    if(cost <min_cost){
                        last_conf_1 = s;
                        min_cost = cost;
                        if(res_stack.size()>i){
                            res_stack.pop();
                            res_stack.push(s);
                        }
                        else{
                            res_stack.push(s);
                        }
                    }
                }

            }


        }



        return res_stack;
    }
//
//    public List<MyEntry<Integer, Map<String, Integer>>> WFA2(int[][] D, List<Integer> r, List<Integer> C0) {
//        int[] a = new int[D.length];
//
//        int cost = 0;
//
//        for (int i = 0; i < D.length; i++) {
//            a[i] = i + 1;
//        }
//        permutation(a, 0, 0, C0.size(), C0.size(), a.length, C0.size());
//        /*
//            compute distance between initial vertex set and every possible set.
//         */
//        Map<String, Integer> dist = new HashMap<>();
//        for (int[] vertices :
//                set) {
////            fullSort(vertices, 0, vertices.length - 1);
//            int distance = computeDis(vertices, C0, D);
//            Arrays.sort(vertices);
//            dist.put(toString(vertices), distance);
//        }
//        /*
//            compute the matrix of work funciton algo
//         */
//        List<MyEntry<Integer, Map<String, Integer>>> matrix = new LinkedList<>();
//        MyEntry<Integer, Map<String, Integer>> entry = new MyEntry<>(0, dist);
//        matrix.add(entry);
//        dist = new HashMap<>();
//        int last = 0;
//        for (int request :
//                r) {
//            for (int[] combo :
//                    set) {
//                Arrays.sort(combo);
//                dist.put(toString(combo), workFunciton( ((LinkedList<MyEntry<Integer, Map<String, Integer>>>) matrix).getLast().getValue(), request, combo, D));
//            }
//            int conf_n_1 = Integer.MAX_VALUE;
//            for(String conf: dist.keySet()){
//                List<String> possible_conf = computepossiblelastconf(conf,Integer.toString(request));
//
//
//                System.out.println();
//
//
//
//            }
//
//            entry = new MyEntry<>(request, dist);
//            matrix.add(entry);
//            dist = new HashMap<>();
//        }
//        return matrix;
//    }


    public Map<String,Integer> computepossiblelastconf(String conf_n, String request, int D[][]){

        Map<String,Integer> res = new HashMap<>();
        String origin_conf = conf_n;
        String[] array = conf_n.split(",");

        for(int i =0;i<array.length;i++){
            if(array[i].equals(request)){

                res = new HashMap<>();
                res.put(conf_n,0);
                return res;
            }
            int[] tmp = new int[array.length];

            for(int j =0; j<tmp.length;j++){
                tmp[j] = Integer.parseInt(array[j]);
            }

            tmp[i] = Integer.parseInt(request);
            Arrays.sort(tmp);
            String new_conf = "";
            new_conf = toString(tmp);
            res.put(new_conf, D[Integer.parseInt(request)-1][Integer.parseInt(array[i])-1]);





        }

        return res;




    }

    public int[] toArray(String conf){

        String[] chars = conf.split(",");
        int[] res = new int [chars.length];
        for(int i =0;i<chars.length;i++){
            res[i] = Integer.parseInt(chars[i]);
        }
        return res;
    }





    public int workFunciton(Map<String, Integer> matrix, int r, int[] combo, int[][] D) {
        Combo c = new Combo(combo);
        Combo temp = new Combo(combo);
        int min = 32766;
        if (c.contains(r)) {
            min = matrix.get(c.toString());
            for (int i = 0; i < combo.length; i++) {
//                if (combo[i] == r) continue;
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

    public List<int[]>  fullSort(int[] arr, int start, int end) {
        List<int[]> res = new ArrayList<>();
        // 递归终止条件
        if (start == end) {

            res.add(arr.clone());

            return res;
        }
        for (int i = start; i <= end; i++) {
            swap(arr, i, start);
            res.addAll(fullSort(arr, start + 1, end));
            swap(arr, i, start);
        }
        return res;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public Set<int[]> GenerateAllCandidatesConf(int r, int[][] D, int k){
        Set<int[]> res = new HashSet<>();




        return res;



    }

    /*
        Cus there are some key like "34" or "43". They are same clique.
        But we only store one of the combinations into matrix. it's allowed to get both of the combinations from matrix though.
     */
    public int roughlyGet(Map<String, Integer> map, String s){

        String[] temp = s.split(",");
        int[] int_arry = new int[temp.length];

        for(int i =0;i<temp.length;i++){
            int_arry[i] = Integer.parseInt(temp[i]);
        }

        Arrays.sort(int_arry);

        String search = toString(int_arry);
        return map.get(search);

    }


    public int roughlyGet1(Map<String, Integer> map, String s){
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
            builder.append(a+",");
        }
        String res = builder.toString();
        res = res.substring(0,res.length()-1);
        return res;
    }

    public String toString(String[] arr) {
        StringBuilder builder = new StringBuilder();
        for (String a :
                arr) {
            builder.append(a+",");
        }
        String res = builder.toString();
        res = res.substring(0,res.length()-1);
        return res;
    }




    public int computeDis(int[] vertices, List<Integer> C0, int[][] D) {
        List<Integer> s = new ArrayList<>();
        List<Integer> v = new ArrayList<>();
        /*
            <origin server name, sorted list<origin server name, dist>>
         */
        HashMap<Integer, List<Map.Entry<Integer, Integer>>> dist = new HashMap<>();
        for (Integer num :
                C0) {
            s.add(num);
        }
        for (Integer num :
                vertices) {
            if (C0.contains(num)) {

                s.remove(num);
            } else {
                v.add(num);
            }
        }
        if(v.size() ==0)
            return 0;

        //s C0 remove redun
        int[] temp = new int[v.size()];
        for(int i =0;i<v.size();i++){
            temp[i] = v.get(i);
        }

        int minDistance = Integer.MAX_VALUE;

        List<int[]> candidatesConfig = new ArrayList<>();
        candidatesConfig = fullSort(temp,0,temp.length-1);

        for(int[] can :candidatesConfig){
            // calculate C0 to each candidate distance
            int dis = 0;
            for(int i = 0;i<s.size();i++){
                int t = D[can[i]-1][s.get(i)-1];
                dis = dis + t;
            }
            if (dis <minDistance)
                minDistance = dis;
        }
        return minDistance;

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
        Arrays.sort(temp);
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

    public Set<int[]> permutation1(int[] a, int begin0, int begin, int mid1, int mid2, int end, int selectNum, int r) {

        Set<int[]> res = new HashSet<>();

        int[] temp = new int[selectNum];
        System.arraycopy(a, begin0, temp, 0, selectNum);
        boolean canInsert = false;
        for(int i:temp){
            if(i == r){
                canInsert = true;
                break;
            }
        }
        if(canInsert){
            Arrays.sort(temp);
            res.add(temp);

        }


        for (int t = begin; t < mid1; t++) {
            for (int j = mid2; j < end; j++) {
                int temp0 = a[t];
                a[t] = a[j];
                a[j] = temp0;

                res.addAll(permutation1(a, begin0, t + 1, mid1, j + 1, end, selectNum, r));

                a[j] = a[t];
                a[t] = temp0;
            }
        }

        return res;
    }



}
