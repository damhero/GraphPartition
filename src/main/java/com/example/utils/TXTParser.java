package com.example.utils;

import com.example.model.Group;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class TXTParser {

    public static ArrayList<Group> parse(String fileName) {

        ArrayList<Group> partedGraph = new ArrayList<Group>();

     try{
         InputStream is = TXTParser.class.getResourceAsStream("/" + fileName);
        if(is == null) throw new IllegalAccessException("Nie znaleziono pliku: " + fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        int vertexCount = 0; //jeżeli potrzbowalibyśmy to są zliczone wierzchołki, ale nigdzie nie przekazane
        //iteracja po macierzy, zliczanie wierzchołków
        while((line = br.readLine()) != null){
            if(line.trim().isEmpty()) break; //pusta linia => koniec macierzy

            //w każdej przetwarzanej linii trzeba zliczyć ilość wierzchołków
            vertexCount += (int) Arrays.stream(line.split("\\s+")).filter(x -> x.equals("1")).count();
        }

        int currentGroup = -1;
        Group tempGroup = null;
        while((line = br.readLine()) != null){
            //jeżeli była linijka z grupą to tworzymy nową grupę
            if(line.trim().startsWith("grupa")) {
                currentGroup = Integer.parseInt(line.split("\\s+")[1]);
                tempGroup = new Group(currentGroup);
                continue;
            }

            //jeżeli linia jest pusta to koniec grupy i zapisujemy jako podgraf
            if(line.trim().isEmpty()){
                if(tempGroup == null) continue;
                partedGraph.add(tempGroup.deepCopy());
                tempGroup = null;
                continue;
            }

            //przetwarzanie par
            String [] pair = line.split("-");
            int a = Integer.parseInt(pair[0]);
            int b = Integer.parseInt(pair[1]);
            if(tempGroup != null) {
                tempGroup.addVertex(a);
                tempGroup.addVertex(b);
                tempGroup.addAdjacencyPair((new ArrayList<Integer>(Arrays.asList(a, b))));
            }

        }
        br.close();

    } catch (Exception e) {
        //TODO: komunikaty na ekranie.
        System.err.println("[!] Błąd podczas parsowania TXT" + e);
        System.exit(1);
    }
    return partedGraph;

    }
}
