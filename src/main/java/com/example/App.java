package com.example;

import com.example.model.Graph;
import com.example.ui.Frame;
import com.example.ui.LanguageManager;
import com.example.utils.CSRRGParser;
import com.example.model.Group;
import com.example.utils.TXTParser;

import java.util.ArrayList;
import java.util.Locale;

public class App {
    public static void main(String[] args) {
        new Frame();


        //testy dla parserów
        CSRRGParser csrrg = new CSRRGParser("graphs/graf.csrrg");
        ArrayList<Group> parted = TXTParser.parse("graphs/graf_2.txt");
        Graph graph = new Graph(csrrg.getVerticesList2().toArray().length, csrrg.getAdjacencyList4(), csrrg.getAdjacencyIndices5());

        System.out.println("CSRRG loaded graph:");

        System.out.println(graph.getVertexCount());
        System.out.println(graph.getAdjacencyList().toString());
        System.out.println(graph.getAdjacencyIndices().toString());

        System.out.println("TXT loaded parted graph:");
        for(Group group : parted) {
            System.out.println(group.getGroupId());
            ArrayList<ArrayList<Integer>> pairs = group.getAdjacencyPairs();
            for(ArrayList<Integer> pair : pairs) {
                System.out.println(pair.toString());
            }
        }



    }
}