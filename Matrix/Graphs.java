import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Graphs {
    private static boolean weighted;
    private static boolean directed;
    private static double[][] graph = new double[26][26];
    private static List<String> data = new ArrayList<String>();
    private static List<String> data_output = new ArrayList<String>();
    private static String input;
    private static final int UNINITIALIZED = -1;

    public static void main(String args[]) {
        for (int index = 0; index < 26; index++) {
            Arrays.fill(graph[index], UNINITIALIZED);
        }
        
        if (args.length > 0) {
            for (int index = 0; index < args.length; index++) {
                input = args[index];
                readInFile(input);
                readGraph();
                printGraph();
                data.clear();
                data_output.clear();
            }
        } else {
            System.out.print("Input file name: ");
            Scanner keyboard = new Scanner(System.in);
            input = keyboard.nextLine();
            readInFile(input);
            readGraph();
            printGraph();
            data.clear();
            data_output.clear();
        }
    }

    /**
     *  hasEdge
     *  
     *  This method will take in two parameters for the vertexes for the edge to check if the edge is there.
     *  Method will return false if the edge does not exist and return true if the edge does exit.
     *
     *  @param first_vertex The starting vertex for the edge.
     *  @param second_vertex The ending vertex for the edge.
     *  @return Whether the edge exists. 
     */
    private static boolean hasEdge(char first_vertex, char second_vertex) {
        int first_index = (int) Character.toUpperCase(first_vertex) - 65;
        int second_index = (int) Character.toUpperCase(second_vertex) - 65;

        return graph[first_index][second_index] > 0;
    }

    /**
     *  addEdge
     *  
     *  This method will take in two parameters for the vertexes for the edge to add.
     *  Method will overwrite and existing edge if the edge already exists for weighted graphs and 
     *  return true if the addition was successful, returning false if the graph already has an edge
     *  and is not weighted.
     *
     *  @param first_vertex The starting vertex for the edge.
     *  @param second_vertex The ending vertex for the edge.
     *  @return Whether the addition was successful. 
     */
    private static boolean addEdge(char first_vertex, char second_vertex, double weighting) {
        int first_index = (int) Character.toUpperCase(first_vertex) - 65;
        int second_index = (int) Character.toUpperCase(second_vertex) - 65;

        // vertex not found
        if (graph[first_index][0] == UNINITIALIZED || graph[second_index][0] == UNINITIALIZED) { 
            data_output.add("Vertex not found");
            return false;
        }

        // edge already exists
        if (graph[first_index][second_index] != 0 || graph[second_index][first_index] != 0) { 
            if (!weighted) {
                return false;
            }
        }

        // directed graph
        if (directed) {
            graph[first_index][second_index] = weighting;
            return true;
        } else {
            graph[first_index][second_index] = weighting;
            graph[second_index][first_index] = weighting;
            return true;
        }
    }

    /**
     *  deleteEdge
     *  
     *  This method will take in two parameters for the vertexes for the edge to remove.
     *  Method will return false if the edge does not exist and return true if the remove was successful.
     *
     *  @param first_vertex The starting vertex for the edge.
     *  @param second_vertex The ending vertex for the edge.
     *  @return Whether the removal was successful. 
     */
    private static boolean deleteEdge(char first_vertex, char second_vertex) {
        int first_index = (int) Character.toUpperCase(first_vertex) - 65;
        int second_index = (int) Character.toUpperCase(second_vertex) - 65;

        if (!hasEdge(first_vertex, second_vertex)) {
            return false;
        } else {
            if (directed) {
                graph[first_index][second_index] = 0;
            } else {
                graph[first_index][second_index] = 0;
                graph[second_index][first_index] = 0;
            }
        }
        return true;
    }

    /**
     *  addVertex
     *  
     *  This method will take in one parameter for the vertex to be added.
     *  Method will return false if the vertex already exists and return true if the addition was successful.
     *
     *  @param vertex The new vertex to be added.
     *  @return Whether the addition was successful. 
     */
    private static boolean addVertex(char vertex) {
        int newVertex = (int) Character.toUpperCase(vertex) - 65;

        if (graph[newVertex][0] != UNINITIALIZED) { // vertex already exists
            return false;
        } else if (newVertex == 0) {
            graph[0][0] = 0;
            return true;
        } else {
            for (int index = 0; index < 26; index++) {
                if (graph[0][index] != UNINITIALIZED) {
                    graph[newVertex][index] = 0;
                    graph[index][newVertex] = 0;
                }
            }
            return true;
        }
    }

    /**
     *  deleteVertex
     *  
     *  This method will take in one parameter for the vertex to be removed.
     *  Method will return false if the vertex does not exists and return true if the removal was successful.
     *
     *  @param vertex The new vertex to be added.
     *  @return Whether the removal was successful. 
     */
    private static boolean deleteVertex(char vertex) {
        int newVertex = (int) Character.toUpperCase(vertex) - 65;

        if (graph[0][newVertex] == UNINITIALIZED) {
            return false;
        }

        for (int index = 0; index < 26; index++) {
            graph[newVertex][index] = UNINITIALIZED;
            graph[index][newVertex] = UNINITIALIZED;
        }
        return true;
    }

    /**
     *  isSparse
     *  
     *  This method will determine whether the ratio between the total possible edges and the number of edges
     *  is less that 15%.
     *
     *  @return Whether the ratio is less than 15%.
     */
    private static boolean isSparse() {
        int possible = countVertices() * (countVertices() - 1);
        int total = countEdges();
        if (possible == 0 | total == 0)
            return true;
        return total / possible < .15;
    }

    /**
     *  isDense
     *  
     *  This method will determine whether the ratio between the total possible edges and the number of edges
     *  is greater that 85%.
     *
     *  @return Whether the ratio is greater than 85%.
     */
    private static boolean isDense() {
        int possible = countVertices() * (countVertices() - 1);
        int total = countEdges();
        if (possible == 0 | total == 0)
            return false;
        return total / possible > .85;
    }

    /**
     *  countVertices
     *  
     *  This method will count the number of verticies in the graph.
     *
     *  @return The count of verticies.
     */
    private static int countVertices() {
        int count = 0;

        for (int index = 0; index < 26; index++) {
            if (graph[0][index] != UNINITIALIZED) {
                count++;
            }
        }
        return count;
    }

    /**
     *  countEdges
     *  
     *  This method will count the number of edges in the graph.
     *
     *  @return The count of edge.
     */
    private static int countEdges() {
        int count = 0;

        for (int row = 0; row < 26; row++) {
            for (int column = 0; column < 26; column++) {
                if (graph[row][column] != UNINITIALIZED) {
                    count++;
                }
            }
        }

        if (!directed) {
            count = count / 2;
        }
        return count;
    }

    /**
     *  isConnected
     *  
     *  This method will determine if all edges from the graph connect to all others. 
     *  Will return false if the graph is directed.
     *
     *  @return Whether the graph is connected.
     */
    private static boolean isConnected() {
        List<Character> visited = new ArrayList<Character>();
        List<Character> neighbors = new ArrayList<Character>();

        if (directed) {
            data_output.add("Unable to compute isConnected for a directed graph.");
            return false;
        } else {
            char value;
            int index = 0;
            while (graph[0][index] <= 0) {
                index++;
            }
            value = (char)(65 + index);
            neighbors.add(value);
            
            while (neighbors.size() > 0) {
                char current = neighbors.get(0);
                neighbors.remove(0);
                visited.add(current);

                for (int column = 0; column < 26; column++) {
                    if (graph[current - 65][column] > 0) {
                        value = (char)(65 + column);
                        if (neighbors.contains(value) || visited.contains(value)) {
                            continue;
                        } else {
                            neighbors.add(value);
                        }
                    }
                }
            }

            if (visited.size() < countVertices()) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     *  isFullyConnected
     *  
     *  This method will determine if all edges from the graph connect to all others with step size of 1. 
     *  Will return false if the graph is directed.
     *
     *  @return Whether the graph is fully connected.
     */
    private static boolean isFullyConnected() {
        if (directed) {
            data_output.add("Unable to compute isFullyConnected for a directed graph.");
            return false;
        } else {
            for (int row = 0; row < 26; row++) {
                for (int column = 0; column < 26; column++) {
                    if (graph[row][column] == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     *  readInFile
     *
     *  This method is a helper method to read in the file and put all files into an arraylist for Processing.
          */
      private static void readInFile(String filename) {
        Scanner input;

        try {
            File file = new File(filename);
            input = new Scanner(file);

            boolean verticesPortion = false;
            boolean dataPortion = false;
            boolean functionPortion = false;

            while (input.hasNextLine()) {
                data.add(input.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Please input another file.");
        }
    }

    /**
     *  readGraph
     *
     *  This method will read the data stored into the arraylist and process the data preforming any opertions that
     *  were listed in the file after adding all edges and verticies provided. 
     */
    private static void readGraph() {
        boolean vertexPortion = false;
        boolean dataPortion = false;
        boolean functionPortion = false;

        for (String current_line: data) {
            if (current_line.startsWith("*"))
                continue;
            switch(current_line) {
                case "weighted":
                    weighted = true;
                    break;
                case "unweighted":
                    weighted = false;
                    break;
                case "directed":
                    directed = true;
                    break;
                case "undirected":
                    directed = false;
                    break;
                case "begin":
                    vertexPortion = true;
                    break;
                case "end":
                    vertexPortion = false;
                    dataPortion = false;
                    functionPortion = true;
                    break;
                default:
                    if (vertexPortion) {
                        String verticies[] = current_line.split("\\s");
                        for (String vertex : verticies) {
                            addVertex(vertex.charAt(0));
                        }
                        vertexPortion = false;
                        dataPortion = true;
                    } else if (dataPortion) {
                        String input[] = current_line.split("\\s");
                        if (!weighted) {
                            addEdge(input[0].charAt(0), input[1].charAt(0), 1);
                        } else {
                            addEdge(input[0].charAt(0), input[1].charAt(0), Double.parseDouble(input[2]));
                        }
                    } else if (functionPortion) {
                        String data[] = current_line.split("\\s");
                        boolean returnedResult;
                        switch(data[0]) {
                            case "hasEdge":
                                returnedResult = hasEdge(data[1].charAt(0), data[2].charAt(0));
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "addEdge":
                                if (!weighted) {
                                    returnedResult = addEdge(data[1].charAt(0), data[2].charAt(0), 1);
                                } else {
                                    returnedResult = addEdge(data[1].charAt(0), data[2].charAt(0), Double.valueOf(data[3]));
                                }
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "deleteEdge":
                                returnedResult = deleteEdge(data[1].charAt(0), data[2].charAt(0));
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "addVertex":
                                returnedResult = addVertex(data[1].charAt(0));
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "deleteVertex":
                                returnedResult = deleteVertex(data[1].charAt(0));
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "isSparse":
                                returnedResult = isSparse();
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "isDense":
                                returnedResult = isDense();
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "countVertices":
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Found " + countVertices() + " verticies");
                                break;
                            case "countEdges":
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Found " + countEdges() + " edges");
                                break;
                            case "isConnected":
                                returnedResult = isConnected();
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "isFullyConnected":
                                returnedResult = isFullyConnected();
                                data_output.add("Processing:      " + current_line);
                                data_output.add("Output Result:   " + returnedResult);
                                break;
                            case "printGraph":
                                printGraph();
                                break;
                            default:
                                data_output.add("Expected Result: " + current_line);
                                break;
                        }
                    } else {
                        data_output.add("COULD NOT PROCESS " + current_line);
                    }
            }
        }
    }

    /**
     *. displayGraph
     *
     *  This method will print out the current graph to the screen.
     */
    private static void displayGraph() {
        for (double[] row : graph) {
            for (double i : row) {
                if (i == UNINITIALIZED) {
                    System.out.print("-");
                    System.out.print("\t");
                } else {
                    System.out.print(i);
                    System.out.print("\t");
                }
            }
            System.out.println();
        }
    }

    /**
     *. printGraph
     *
     *  This method will create an output file for the results of the input file.
     */
    private static void printGraph() {
        try {
            String temp[] = input.split(".txt");
            FileWriter file = new FileWriter(temp[0] + "_output.txt");
            PrintWriter output = new PrintWriter(file);

            output.println("* Graphs Output File");
            output.println("* By: Matt Kline");
            output.println("* File: " + temp[0] + "_output.txt");

            for (String current_line: data_output) {
                output.println(current_line);
            }
            output.close();
        } catch (IOException e) {
            System.out.println("File not found. Please input another file.");
        }
    }
}