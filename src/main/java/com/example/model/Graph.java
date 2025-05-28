package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int vertexCount;
    private ArrayList<Integer> adjacencyList;
    private ArrayList<Integer> adjacencyIndices;

    //konstruktor
    public Graph(int vertexCount, List<Integer> adjacencyList, List<Integer> adjacencyIndices) {
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>(adjacencyList);
        this.adjacencyIndices = new ArrayList<>(adjacencyIndices);
    }


    //gettery

    public int getVertexCount() {
        return vertexCount;
    }

    public List<Integer>[] getNeighbors() {
        @SuppressWarnings("unchecked")
        List<Integer>[] neighbors = new ArrayList[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            neighbors[i] = new ArrayList<>();

            // Sprawdź czy indeks i nie przekracza rozmiaru listy adjacencyIndices
            if (i >= adjacencyIndices.size()) {
                continue; // Pomijamy wierzchołki, dla których nie mamy danych sąsiedztwa
            }

            int start = adjacencyIndices.get(i);
            int end;

            // Sprawdź, czy indeks i+1 nie przekracza rozmiaru listy
            if (i + 1 < adjacencyIndices.size()) {
                end = adjacencyIndices.get(i + 1);
            } else {
                // Jeśli to ostatni wierzchołek, użyj rozmiaru listy sąsiedztwa jako końca
                end = adjacencyList.size();
            }

            for (int j = start; j < end && j < adjacencyList.size(); j++) {
                neighbors[i].add(adjacencyList.get(j));
            }
        }

        return neighbors;
    }
    public ArrayList<Integer> getAdjacencyList() {
        return adjacencyList;
    }

    public ArrayList<Integer> getAdjacencyIndices() {
        return adjacencyIndices;
    }







}


