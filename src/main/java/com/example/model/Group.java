package com.example.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Group {
    private final int groupId;
    private ArrayList<ArrayList<Integer>> adjacencyPairs;
    private Set<Integer> vertices;
    
    public Group(int id) {
        this.groupId = id;
        this.adjacencyPairs = new ArrayList<ArrayList<Integer>>();
        this.vertices = new HashSet<>();
    }

    //gettery i settery
    public int getGroupId() {
        return groupId;
    }
    public ArrayList<ArrayList<Integer>> getAdjacencyPairs() {
        return adjacencyPairs;
    }

    public Set<Integer> getVertices() {
        return vertices;
    }

    public void addAdjacencyPair(ArrayList<Integer> adjacencyPair) {
        this.adjacencyPairs.add(adjacencyPair);
    }

    public void addVertex(int vertex) {
        this.vertices.add(vertex);
    }

    public Group deepCopy() {
        Group group = new Group(groupId);
        for (ArrayList<Integer> adjacencyPair : adjacencyPairs) {
            group.addAdjacencyPair(new ArrayList<>(adjacencyPair));
        }
        return group;
    }


}
