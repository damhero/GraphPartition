package com.example;

import com.example.model.Graph;
import com.example.model.PartitionAlg;
import com.example.ui.AppFrame;
import com.example.utils.CSRRGParser;
import com.example.model.Group;
import com.example.utils.TXTParser;
import com.example.utils.BINParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.example.utils.ThemeManager;

public class App {
    public static void main(String[] args) throws URISyntaxException {
        ThemeManager.setupDefault();
        new AppFrame();


        //testy dla Txt Parser
        File txtFile = new File(App.class.getClassLoader().getResource("graphs/graf_2.txt").toURI());
        System.out.println("TXT loaded parted graph:");
        ArrayList<Group> parted = TXTParser.parse(txtFile);
        for(Group group : parted) {
            System.out.println(group.getGroupId());
            ArrayList<ArrayList<Integer>> pairs = group.getAdjacencyPairs();
            for(ArrayList<Integer> pair : pairs) {
                System.out.println(pair.toString());
            }
        }

        //test dla BIN parser
        File binFile = new File(App.class.getClassLoader().getResource("graphs/graf_2.bin").toURI());
        System.out.println("BIN loaded parted graph:");
        ArrayList<Group> partedBin = BINParser.parse(binFile);
        for(Group group : partedBin) {
            System.out.println(group.getGroupId());
            ArrayList<ArrayList<Integer>> pairs = group.getAdjacencyPairs();
            for(ArrayList<Integer> pair : pairs) {
                System.out.println(pair.toString());
            }
        }


//        //testy dla CSRRG Parser
//        File csrrgFile = new File(App.class.getClassLoader().getResource("graphs/graf5.csrrg").toURI());
//        try {
//            CSRRGParser csrrg = new CSRRGParser(csrrgFile);
//            Graph graph = new Graph(csrrg.getVerticesList2().toArray().length, csrrg.getAdjacencyList4(), csrrg.getAdjacencyIndices5());
//            System.out.println(graph.getVertexCount());
//            System.out.println(graph.getAdjacencyList().toString());
//            System.out.println(graph.getAdjacencyIndices().toString());
//            System.out.println("CSRRG loaded graph:");


//            //testy dla podziału grafu graf.csrrg
//            PartitionAlg partition = new PartitionAlg(graph, 10, 7);
//        } catch (IOException e) {
//            //TODO komunikat błędu
//            e.printStackTrace(); // albo pokazanie komunikatu użytkownikowi
//        }






    }
}