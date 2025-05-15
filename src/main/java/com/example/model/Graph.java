package com.example.model;

import java.util.ArrayList;

public class Graph {
    private int vertexCount;
    private ArrayList<Integer> adjacencyList;
    private ArrayList<Integer> adjacencyIndices;

    //konstruktor
    public Graph(int vertexCount, ArrayList<Integer> adjacencyList, ArrayList<Integer> adjacencyIndices) {
        this.vertexCount = vertexCount;
        this.adjacencyList = adjacencyList;
        this.adjacencyIndices = adjacencyIndices;
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


