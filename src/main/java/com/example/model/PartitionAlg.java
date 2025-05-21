package com.example.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class PartitionAlg {
    private ArrayList<Integer> verticiesPart; // Tablica przypisująca wierzchołek do podgrafu
    private int numParts; //liczba części, podstawowo 2


    //wywołanie z argumentami
    public PartitionAlg(Graph graph, int numParts, int margin) {
        this.numParts = numParts;
        this.verticiesPart = new ArrayList<Integer>();
        this.spectralPartition(graph);
    }

    //przeciążanie dla wywołania bez argumentów
    public PartitionAlg(Graph graph) {
        this(graph, 2, 10);
    }

    public PartitionAlg(Graph graph, int numParts) {
        this(graph, numParts, 10);
    }

    private void spectralPartition(Graph graph){
        int V = graph.getVertexCount();
        try {
            double [] laplacian = PartitionAlg.computeLaplacian(graph);
            double [] fielder = computeFielderVector(laplacian, V);
            ArrayList<IndexedValue> values = new ArrayList<IndexedValue>();
            for (int i = 0; i < V; i++) {
                values.add(new IndexedValue(i, fielder[i]));
            }

            //sortowanie wartości według wektora Fieldera
            values.sort(IndexedValue::compareTo);

            int base_size = V / numParts;
            int remainder = V % numParts;

            int currentPosition = 0;
            for(int p=0; p<numParts; p++){
                int part_size = base_size + (p < remainder ? 1 : 0);
                for(int i = 0 ; i < part_size; i++){
                    this.verticiesPart.set(values.get(currentPosition + i).getIndex(), p);
                }
                currentPosition += part_size;
            }

        } catch (Exception e){
            System.err.println("[!] Bład podczas podziału w SpectralPartition" + e);
            System.exit(1);
        }

    }

    private static double [] computeFielderVector(double [] laplacian, int V){
        double [] x = new double[V];
        double [] y = new double[V];

        //inicjalizacja losowymi
        for (int i = 0; i < V; i++) {
            Random rand = new Random();
            x[i] = rand.nextDouble() - 0.5;
        }

        double sum = 0;
        for (int i = 0; i < V; i++) sum+=x[i];
        double avg = sum/V;
        for (int i = 0; i < V; i++) x[i] -= avg;

        //iteracja potęgowa
        int maxIter = 100;
        double EPSILON = 1e-6;

        for(int iter = 0; iter < maxIter; iter++){
            //mnożenie macierzy przez wektor
            PartitionAlg.multiplyMatrixByVector(laplacian, x, y, V);

            // Usunięcie komponentu wzdłuż pierwszego wektora własnego (wektor stały)
            sum = 0;
            for (int i = 0; i < V; i++) sum+=y[i];
            avg = sum/V;
            for (int i = 0; i < V; i++) y[i] -= avg;

            //normalizacja
            double normValue = PartitionAlg.norm(y,V);
            if(normValue < EPSILON) break;

            for(int i = 0; i < V; i++) x[i] = y[i] / normValue;

        }

        return x;
    }


    //funkcja obliczająca normę (długośc wektora)
    private static double norm(double [] vector, int V){
        double sum = 0;
        for(int i = 0; i < V; i++) sum+=vector[i]*vector[i];
        return Math.sqrt(sum);
    }


    private static double [] computeLaplacian(Graph graph){
        int V = graph.getVertexCount();
        double [] laplacian = new double[V*V];
        int [] degree = new int[V];

        //zlicznie liczby krawędzi dla każdego wierzchołka
        for(int i = 0; i < V; i++){
            for(int j = graph.getAdjacencyIndices().get(i); j < graph.getAdjacencyIndices().get(i+1); j++){
                degree[i]++;
            }
        }

        //Inicjalizacja macierzy zerami
        for(int i = 0; i < V*V; i++){
            for(int j = 0; j < V; j++){
                laplacian[i] = 0;
            }
        }

        //Wypełnianie macierzy Laplace'a
        for(int i = 0; i < V; i++){
            laplacian[i*V + i] = degree[i]; //Wartości na przekątnej
            for(int j = graph.getAdjacencyIndices().get(i); j < graph.getAdjacencyIndices().get(i+1); j++){
                if(j == graph.getAdjacencyList().size()) break;
                int neighbor = graph.getAdjacencyList().get(j);
                laplacian[i*V + neighbor] = -1;
            }
        }
        return laplacian;
    }

    //wielowątkowe mnożenie macierzy przez wektor
    private static void multiplyMatrixByVector(double [] matrix, double [] vector, double [] result, int V) {
        IntStream.range(0, V).parallel().forEach(i -> {
            double sum = 0;
            for (int j = 0; j < V; j++) {
                sum+= matrix[i*V+j]*vector[j];
            }
            result[i] = sum;
        });
    }
}
