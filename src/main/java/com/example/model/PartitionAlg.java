package com.example.model;

import java.util.ArrayList;

public class PartitionAlg {
    private ArrayList<Integer> verticiesPart; // Tablica przypisująca wierzchołek do podgrafu
    private int numParts; //liczba części, podstawowo 2


    //wywołanie z argumentami
    public PartitionAlg(Graph graph, int numParts, int margin) {
        this.numParts = numParts;
        this.verticiesPart = new ArrayList<Integer>(graph.getVertexCount());
        //TODO: logika dzielenia
    }

    //przeciążanie dla wywołania bez argumentów
    public PartitionAlg(Graph graph) {
        this(graph, 2, 10);
    }

    public PartitionAlg(Graph graph, int numParts) {
        this(graph, numParts, 10);
    }

    //TODO: przemyśleć algorytm, żeby działanie było optymalne
}
