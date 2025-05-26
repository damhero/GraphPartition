package com.example.utils;

import java.io.*;
import java.util.ArrayList;

public class CSRRGParser {
    //postać macierzowa
    private int maxVertices1;
    private ArrayList<Integer> verticesList2;
    private ArrayList<Integer> verticesPlacement3;
    //postać Grafu
    private ArrayList<Integer> adjacencyList4;
    private ArrayList<Integer> adjacencyIndices5;

    public CSRRGParser(File csrrgFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csrrgFile)))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                ArrayList<Integer> parsedLine = parseLineToList(line);

                switch (lineNumber) {
                    case 0 -> setMaxVertices1(parsedLine.getFirst());
                    case 1 -> setVerticesList2(parsedLine);
                    case 2 -> setVerticesPlacement3(parsedLine);
                    case 3 -> setAdjacencyList4(parsedLine);
                    case 4 -> setAdjacencyIndices5(parsedLine);
                    default -> System.err.println("Nieoczekiwana liczba linii w pliku: " + lineNumber);
                }
                lineNumber++;
            }

            // Walidacja długości list
            if (adjacencyIndices5 == null || adjacencyList4 == null) {
                throw new IllegalStateException("Dane są niekompletne.");
            }
            if (adjacencyIndices5.getLast() > adjacencyList4.size()) {
                throw new IllegalStateException("Ostatni indeks w adjacencyIndices przekracza rozmiar adjacencyList.");
            }

        } catch (Exception e) {
            throw new IOException("Nieprawidłowy format pliku CSRRG: " + e.getMessage(), e);
        }
    }

    //setterty
    public void setMaxVertices1(int maxVertices1) {
        this.maxVertices1 = maxVertices1;
    }

    public void setVerticesList2(ArrayList<Integer> verticesList2) {
        this.verticesList2 = verticesList2;
    }

    public void setVerticesPlacement3(ArrayList<Integer> verticesPlacement3) {
        this.verticesPlacement3 = verticesPlacement3;
    }

    public void setAdjacencyList4(ArrayList<Integer> adjacencyList4) {
        this.adjacencyList4 = adjacencyList4;
    }

    public void setAdjacencyIndices5(ArrayList<Integer> adjacencyIndices5) {
        this.adjacencyIndices5 = adjacencyIndices5;
    }

    //gettery

    public int getMaxVertices1() {
        return maxVertices1;
    }

    public ArrayList<Integer> getVerticesList2() {
        return verticesList2;
    }

    public ArrayList<Integer> getVerticesPlacement3() {
        return verticesPlacement3;
    }

    public ArrayList<Integer> getAdjacencyList4() {
        return adjacencyList4;
    }

    public ArrayList<Integer> getAdjacencyIndices5() {
        return adjacencyIndices5;
    }

    //funkcje pomocnicze
    ArrayList<Integer> parseLineToList(String line) {
        ArrayList<Integer> parsedLine = new ArrayList<>();
        String[] tokens = line.split(";");
        for (String token : tokens) {
            parsedLine.add(Integer.parseInt(token));
        }
        return parsedLine;
    }

}
