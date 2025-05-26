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

    public ArrayList<Integer> getAdjacencyList() {
        return adjacencyList;
    }

    public ArrayList<Integer> getAdjacencyIndices() {
        return adjacencyIndices;
    }







}


