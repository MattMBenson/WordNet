import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import sun.misc.Queue;

/**
 * Intellij Used
 * Word Net - Assignment 4
 *
 * @author Matthew Benson
 * @date 2020-11-25
 */

public class WordNet {


    /**
     * Global variables used through the program, isVisited = store if word was visited, amountOfSubgraphs = total amount of modules, words = array of labels corresponding to 2d arrays,
     * vectors = 2D array storing each words 50 vectors, a = 2D adjacentcy matrix storing true if a connection exists at i,j , weight = 2D parallel matrix storing the weight of a connection
     * or storing -1 for a non connection, n = total number of elements, ex 3000.
     */
    LinkedList<ArrayList<String>> topModules = new LinkedList();
    ArrayList<String> vertices = new ArrayList<>();
    ArrayList<String> search1, search2;

    boolean isVisited[];
    int amountOfSubGraphs = 0;
    String[] words;
    double[][] vectors;
    boolean[][] a;
    double[][] weight;
    int n;

    WordNet() {

        long start = System.currentTimeMillis();
        read_data(101, 3100);
        find_modules();
        System.out.print("Amount of Modules: ");
        System.out.println(amountOfSubGraphs);
        System.out.print("Top 20 Modules: ");
        topModules.printSubTreeSizes();
        System.out.println();
        Find_Shortest_Path_Test();

        ArrayList<edge> mst = MST(initQueue(),(int)topModules.head.priority); //initialized with a QUEUE and 1338 (top module size)
        System.out.print("MST Cost: ");
        double mstSize = 0;
        //get MST cost;
        for (int i=0;i<mst.size();i++){
            edge e = mst.get(i);
            mstSize+= mst.get(i).weight;
        }
        System.out.println(mstSize);
        printMST_textFile(mst);
        long end = System.currentTimeMillis();
        System.out.println("Execution Time (s): " + Math.round((end - start) / 1000.00));
    }

    /**
     * Reads data from the text-file and stores in respective global arrays / variables.
     * Creates the proper matrices to represent the graph.
     * finds the total amount of edges within the graph.
     *
     * @param from starting line to read from
     * @param to   ending line to read from
     */
    private void read_data(int from, int to) {
        //read line 'from' to line 'to'.
        int amount = (to - from) + 1;
        n = amount; //number of words
        words = new String[amount]; //size of 2999+1 = 3000
        vectors = new double[amount][50]; //change '50' to amount of vectors
        try {
            int count = 0;
            File file = new File("src/wordvector.txt");
            Scanner scan = new Scanner(file);
            for (int i = 0; i < from - 1; i++) {
                scan.nextLine(); //skips line * how many times in for loop
            }
            for (int row = 0; row < amount; row++) {
                words[row] = scan.next();
                for (int col = 0; col < 50; col++) { //change '50' to amount of vectors
                    vectors[row][col] = scan.nextDouble();
                }
            } //loop 2999 times

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        //creating weight matrix & finding edges
        //let '-1' = infinity
        double result;
        a = new boolean[amount][amount];
        weight = new double[amount][amount];
        isVisited = new boolean[n];
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < amount; j++) {
                result = euclideanDistance(vectors[i], vectors[j]);
                if (result > 3.0 || i == j) {
                    weight[i][j] = -1; // set -1 to be infinity
                    a[i][j] = false;
                } else if (result <= 3.0) {
                    weight[i][j] = result;
                    a[i][j] = true;
                }
            }
        }

        //amount of edges:
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[i][j])
                    count++;
            }
        }
        System.out.println("edges: " + count / 2);
    }

    /**
     * Returns the distance / weight between two words by calculating value
     * through each words 50 vectors
     *
     * @param a Word a with 50 vectors
     * @param b Word b with 50 vectors
     * @return calculated result
     */
    private double euclideanDistance(double[] a, double[] b) {
        double num = 0;
        for (int i = 0; i < a.length; i++) { //M in equation
            num = num + (Math.pow(a[i] - b[i], 2));
        }
        return Math.sqrt(num);
    }

    /**
     * This finds all the modules / subtrees that are connected
     * within the 3000x3000 2D array. Then proceeds to add
     * the top 20 modules (by size) into an ordered linked list.
     */
    private void find_modules() {
        String[] subtree;
        ArrayList<Integer> nodesFound = new ArrayList<>();
        ArrayList<Integer> island = new ArrayList<>();


        resetFlags();
        for (int i = 0; i < n; i++) {
            island.clear();
            ArrayList<String> traversal = new ArrayList<>();
            depthFirstSearch(i,island);

            if (!nodesFound.contains(island.get(0))) { //if node hasn't already been visited altogether
                for (int x=0;x<island.size();x++){
                    traversal.add(words[island.get(x)]);
                }
                topModules.insert(traversal,traversal.size());
                amountOfSubGraphs++;
                //inserts the island/subtree found into the linked list of the top 20
            }

            for (int j = 0; j < island.size(); j++) { //adds all the nodes in the subtree to foundNodes
                nodesFound.add(island.get(j));
            }
        }
    }

    /**
     * This helper function to the find_modules function uses a depth-first search
     * to obtain all the subtrees / modules found within the graph
     *
     * @param index  the value to start traversing at
     * @param module elements found get added to a current module which is used in find_modules.
     */
    private void depthFirstSearch(int index, ArrayList<Integer> module) {
        isVisited[index] = true;
        //adds the node visited to both the current subtree, which later is wiped
        //but also adds it to the list of all nodes found so far
        module.add(index);
        for (int i = 0; i < n; i++) {
            if (a[index][i] && !isVisited[i]) {
                //if the value in the adjacency matrix is true, and its not visited, visit it.
                depthFirstSearch(i, module);
            }
        }
    }

    /**
     * Finds the shortest path from fromWord -> toWord.
     * Implements an unweighted BFS and Dijkstra's algorithm.
     *
     * @param fromWord string to start at.
     * @param toWord   string to finish at.
     */
    private void find_shortest_path(String fromWord, String toWord) {
        int s = -1, e = -1; //can't be an index of -1, so used to check if words have been found or not
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(fromWord))
                s = i;
            if (words[i].equals(toWord))
                e = i;
        }
        if (s == -1 | e == -1) {
            System.out.println("Error: Unable to find specified words.");
            return;
        }

        System.out.print("Breadth-First Search Algorithm: ");
        //Breadth-First Algorithm
        Queue<Integer> que = new Queue<>();
        resetFlags();
        int[] distance = resetDistance();
        int[] path = resetPath();
        int v;
        int w;

        try {
            distance[s] = 0;
            que.enqueue(s);

            loop:
            while (!que.isEmpty()) {
                v = que.dequeue();
                isVisited[v] = true;
                for (int adjacentIndex = 0; adjacentIndex < n; adjacentIndex++) {
                    if (a[v][adjacentIndex] && !isVisited[adjacentIndex]) {
                        w = adjacentIndex;

                        if (distance[w] > distance[v] + 1) {
                            //we can improve w.dist by going through v to w
                            distance[w] = distance[v] + 1;
                            path[w] = v; //stores the path taken to get to W from V
                            que.enqueue(w);

                            if (isVisited[e]) {
                                break loop;
                            }

                        }
                    }
                }
            }

        } catch (InterruptedException i) {
            System.out.println("Interrupted Exception");
        }

        Stack<String> stack = new Stack<>();
        int endIndex = e;
        int currentIndex = endIndex;

        while (currentIndex != -1) {
            String word = words[currentIndex];
            stack.push(word);
            currentIndex = path[currentIndex];
        }

        String word;
        String arrow = " -> ";
        while (!stack.isEmpty()) {
            word = stack.pop();
            search1.add(word);
            if (stack.isEmpty()) {
                System.out.print(word);
            } else {
                System.out.print(word + arrow);
            }
        }
        System.out.println();


        //Dijkstra's Algorithm
        System.out.print("Dijkstra's Search Algorithm: ");
        Dijkstras_Algorithm(s, e);


    }

    /**
     * Helper method to clean up shortest_path function, performs a shortest_path algorithm using Dijkstra's algorithm between
     * word and index s, and e.
     * @param s index of 's'tart word.
     * @param e index of 'e'nd word.
     */
    private void Dijkstras_Algorithm(int s, int e) {
        LinkedList<Integer> PQ = new LinkedList<>();

        resetFlags();
        double[] distance = resetFloatArray();
        int[] path = resetPath();
        int v;
        int w;

        distance[s] = 0;
        PQ.add(s, 0);

        loop:
        while (!PQ.isEmpty()) {
            v = PQ.poll();
            isVisited[v] = true;
            for (int adjacentIndex = 0; adjacentIndex < n; adjacentIndex++) {
                if (a[v][adjacentIndex] && !isVisited[adjacentIndex]) {
                    if (a[v][adjacentIndex] && isVisited[adjacentIndex] == false) {
                        w = adjacentIndex;
                        if (distance[w] > (distance[v] + weight[v][w])) {
                            distance[w] = (distance[v] + weight[v][w]);
                            path[w] = v;
                            PQ.add(w, distance[w]);

                            if (isVisited[e]) {
                                break loop;
                            }
                        }
                    }
                }
            }
        }

        Stack<String> stack = new Stack<>();
        int endIndex = e;
        int currentIndex = endIndex;

        while (currentIndex != -1) {
            String word = words[currentIndex];
            stack.push(word);
            currentIndex = path[currentIndex];
        }

        String word;
        String arrow = " -> ";
        while (!stack.isEmpty()) {
            word = stack.pop();
            search2.add(word); ////////
            if (stack.isEmpty()) {
                System.out.print(word);
            } else {
                System.out.print(word + arrow);
            }
        }
        System.out.println();
    }

    /**
     * Function to return a double array full of '100000.00' used to keep track of distance in Dijkstra's Algo.
     * @return double array 'arr' filled with 10000.
     */
    private double[] resetFloatArray() {
        double[] arr = new double[n];
        Arrays.fill(arr, 100000.0);
        return arr;
    }

    /**
     * Finds the minimum spanning tree on the largest module
     * @param edges a queue of edges to add to a priority queue to be sorted
     * @param numVertices the amount of vertices needed to process, in our case 1338 since thats the since of the largest module
     * @return Array list of type 'edge' that stores information on each edge in the MST.
     */
    private ArrayList<edge> MST(Queue<edge> edges, int numVertices){
        DisjSets ds = new DisjSets(n); //3000
        PriorityQueue<edge> pq = new PriorityQueue<>(n, new edgeComparator());
        ArrayList<edge> mst = new ArrayList<>();

        //adds all the edges and sorts by weight
        try {
            while (!edges.isEmpty()) {
                edge e = edges.dequeue();
                pq.add(e);
            }
        }
        catch (InterruptedException e){
            System.out.println("Interrupted Exception");
        }

        while(mst.size() != numVertices -1){
            edge e = pq.poll();
            int uset = ds.find(e.u);
            int vset = ds.find(e.v);

            if (uset != vset){
                //accept the edge
                mst.add(e);
                ds.union(uset,vset);
            }
        }
        return mst;

    }

    /**
     * Helper method to initialize a queue to give to the MST based off vertices and edges found in the largest module
     * @return a queue full of edges found only in the largest module
     */
    private Queue<edge> initQueue()
    {
        Queue<edge> que = new Queue<>();

        for ( int i = 0; i < n; i++)
        {
            for (int j = i; j < n; j++)
            {
                boolean aT = false;
                boolean bT = false;
                //loop through values in top module
                ArrayList<String> traversal = topModules.head.data;
                if (traversal.contains(words[i])){
                    aT=true;
                }
                if (traversal.contains(words[j])){
                    bT=true;
                }
                if(a[i][j] && aT && bT)
                {
                    edge e = new edge();
                    e.u = i;
                    e.v = j;
                    e.weight = weight[i][j];
                    que.enqueue(e);
                    // Add vertices/words to a list once to get total number of vertices in graph
                    if(!vertices.contains(words[i]))
                    {
                        vertices.add(words[i]);
                    }
                    if(!vertices.contains(words[j]))
                    {
                        vertices.add(words[j]);
                    }
                }
            }
        }
        return que;

    }

    /**
     * Object of type 'edge' that stores information about vertex u,v and the weight inbetween them
     */
    class edge {
        int u;
        int v;
        double weight;
    }

    /**
     * an edge comparator used in the MST to sort a priority queue based on an edge in the proper order wanted.
     */
    class edgeComparator implements Comparator<edge>{
        public int compare(edge node1, edge node2)
        {
            if(node1.weight > node2.weight)
                return 1;
            else if (node1.weight < node2.weight)
                return -1;
            else
                return 0;
        }
    }


    /**
     * Sets all the elements of the isVisited array to false before use.
     */
    private void resetFlags() {
        for (int i = 0; i < n; i++) {
            isVisited[i] = false;
        }
    }

    /**
     * This function converts a traversal of the array which is currently
     * stored as an ArrayList as a bunch of integers. This functions returns
     * the traversal of the module according to index values converted using the words array.
     *
     * @param list Takes
     * @return string array of module
     */
    private String[] convert(ArrayList<Integer> list) {
        String[] temp = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            temp[i] = words[list.get(i)];
        }
        return temp;
    }

    /**
     * This function resets the path in an int array of size 'n' to all -1 (infinity)
     * @return the filled array of '-1'
     */
    private int[] resetPath() {
        int[] arr = new int[n];
        Arrays.fill(arr, -1);
        return arr;
    }

    /**
     * This function rests the distance by setting the value to 100000
     * @return the filled array of '100000'
     */
    private int[] resetDistance() {
        int[] arr = new int[n];
        Arrays.fill(arr, 100000);
        return arr;
    }

    /**
     * This function prints every edge to a text file in the format:
     * u, v, weight
     * etc..
     *
     * @param mst Arraylist of type edge containing all the edges found in the minimum spanning tree
     */
    private void printMST_textFile(ArrayList<edge> mst){
        try {
            FileWriter writer = new FileWriter("MST_Output.txt");
            writer.write("Minimum spanning tree found in the largest module, formatted as 'word1', 'word2', weight");

            for (int i=0;i<mst.size();i++){
                edge e = mst.get(i);
                writer.write(words[e.u]);
                writer.write(", ");
                writer.write(words[e.v]);
                writer.write(", ");
                writer.write(Double.toString(e.weight));
                writer.write("\n");
            }

            writer.close();
        }
        catch (IOException e){
            System.out.println("Some IO Exception");
        }

    }

    /**
     * This function tests a variety of shortest word paths and prints on screen whether
     * or not the result of the output is the same or not for both the Breadth-first algorithm or the Dijkstra's version.
     */
    private void Find_Shortest_Path_Test(){
        search1 = new ArrayList<>();
        search2 = new ArrayList<>();

        search1.clear();
        search2.clear();
        find_shortest_path("money", "future");

        if (search1.equals(search2)){
            System.out.println("Same!");
        }
        else{
            System.out.println("Different!");
        }System.out.println();

        search1.clear();
        search2.clear();
        find_shortest_path("village", "city");

        if (search1.equals(search2)){
            System.out.println("Same!");
        }
        else{
            System.out.println("Different!");
        }System.out.println();

        search1.clear();
        search2.clear();
        find_shortest_path("bad", "good");

        if (search1.equals(search2)){
            System.out.println("Same!");
        }
        else{
            System.out.println("Different!");
        }System.out.println();

        search1.clear();
        search2.clear();
        find_shortest_path("problem", "opportunity");

        if (search1.equals(search2)){
            System.out.println("Same!");
        }
        else{
            System.out.println("Different!");
        }System.out.println();
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        new WordNet();
    }
}
