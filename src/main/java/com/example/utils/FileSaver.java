package com.example.utils;

import java.io.*;
import java.util.*;

public class FileSaver {

    // Zapisuje graf w formacie TXT
    public static void saveTxtFormat(File file, int vertexCount, List<Integer> adjacencyList, List<Integer> adjacencyIndices,
                                     Map<Integer, Integer> partitionData, boolean hasPartition) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Utworzenie macierzy sąsiedztwa
            int[][] adjacencyMatrix = new int[vertexCount][vertexCount];

            // Wypełnienie macierzy sąsiedztwa
            for (int i = 0; i < vertexCount; i++) {
                if (i >= adjacencyIndices.size() - 1) continue;

                int startIdx = adjacencyIndices.get(i);
                int endIdx = adjacencyIndices.get(i + 1);

                for (int j = startIdx; j < endIdx; j++) {
                    if (j >= adjacencyList.size()) continue;

                    int neighbor = adjacencyList.get(j);
                    adjacencyMatrix[i][neighbor] = 1;
                }
            }

            // Zapisanie macierzy sąsiedztwa
            for (int i = 0; i < vertexCount; i++) {
                for (int j = 0; j < vertexCount; j++) {
                    writer.print(adjacencyMatrix[i][j]);
                    if (j < vertexCount - 1) writer.print(" ");
                }
                writer.println();
            }

            // Dodanie pustej linii po macierzy
            writer.println();

            // Jeśli istnieje podział na grupy, zapisz informacje o grupach
            if (hasPartition) {
                // Organizacja krawędzi według grup
                Map<Integer, List<int[]>> groupEdges = new HashMap<>();

                // Przygotuj listy dla każdej grupy
                Set<Integer> uniqueGroups = new HashSet<>(partitionData.values());
                for (int groupId : uniqueGroups) {
                    groupEdges.put(groupId, new ArrayList<>());
                }

                // Przypisz krawędzie do odpowiednich grup
                for (int i = 0; i < vertexCount; i++) {
                    if (i >= adjacencyIndices.size() - 1) continue;

                    int startIdx = adjacencyIndices.get(i);
                    int endIdx = adjacencyIndices.get(i + 1);
                    Integer group1 = partitionData.getOrDefault(i, 0);

                    for (int j = startIdx; j < endIdx; j++) {
                        if (j >= adjacencyList.size()) continue;

                        int neighbor = adjacencyList.get(j);
                        if (neighbor > i) { // Unikaj duplikacji krawędzi
                            Integer group2 = partitionData.getOrDefault(neighbor, 0);

                            // Jeśli oba wierzchołki są w tej samej grupie, dodaj krawędź do tej grupy
                            if (group1.equals(group2)) {
                                groupEdges.get(group1).add(new int[]{i, neighbor});
                            }
                        }
                    }
                }

                // Zapisz grupy i ich krawędzie
                for (int groupId : uniqueGroups) {
                    writer.println("grupa " + groupId);

                    List<int[]> edges = groupEdges.get(groupId);
                    for (int[] edge : edges) {
                        writer.println(edge[0] + "-" + edge[1]);
                    }

                    writer.println(); // Pusta linia po grupie
                }
            }
        }
    }

    // Zapisuje graf w formacie BIN
    public static void saveBinFormat(File file, int vertexCount, List<Integer> adjacencyList, List<Integer> adjacencyIndices,
                                     Map<Integer, Integer> partitionData, boolean hasPartition) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            // Zapisz liczbę wierzchołków
            dos.writeInt(vertexCount);

            // Zapisz liczbę grup
            Set<Integer> uniqueGroups = new HashSet<>();
            if (hasPartition) {
                uniqueGroups.addAll(partitionData.values());
            }
            int groupCount = hasPartition ? uniqueGroups.size() : 0;
            dos.writeInt(groupCount);

            // Zapisz dane grafu w formacie binarnym
            // Najpierw zapisujemy liczbę elementów w każdej tablicy
            dos.writeInt(adjacencyList.size());
            dos.writeInt(adjacencyIndices.size());

            // Zapisz tablicę adjacencyList
            for (int neighbor : adjacencyList) {
                dos.writeInt(neighbor);
            }

            // Zapisz tablicę adjacencyIndices
            for (int index : adjacencyIndices) {
                dos.writeInt(index);
            }

            // Jeśli istnieje podział na grupy, zapisz informacje o grupach
            if (hasPartition) {
                // Organizacja krawędzi według grup
                Map<Integer, List<int[]>> groupEdges = new HashMap<>();

                // Przygotuj listy dla każdej grupy
                for (int groupId : uniqueGroups) {
                    groupEdges.put(groupId, new ArrayList<>());
                }

                // Przypisz krawędzie do odpowiednich grup
                for (int i = 0; i < vertexCount; i++) {
                    if (i >= adjacencyIndices.size() - 1) continue;

                    int startIdx = adjacencyIndices.get(i);
                    int endIdx = adjacencyIndices.get(i + 1);
                    Integer group1 = partitionData.getOrDefault(i, 0);

                    for (int j = startIdx; j < endIdx; j++) {
                        if (j >= adjacencyList.size()) continue;

                        int neighbor = adjacencyList.get(j);
                        if (neighbor > i) { // Unikaj duplikacji krawędzi
                            Integer group2 = partitionData.getOrDefault(neighbor, 0);

                            // Jeśli oba wierzchołki są w tej samej grupie, dodaj krawędź do tej grupy
                            if (group1.equals(group2)) {
                                groupEdges.get(group1).add(new int[]{i, neighbor});
                            }
                        }
                    }
                }

                // Zapisz dane dla każdej grupy
                for (int groupId : uniqueGroups) {
                    dos.writeInt(groupId); // ID grupy

                    List<int[]> edges = groupEdges.get(groupId);
                    dos.writeInt(edges.size()); // Liczba krawędzi w grupie

                    // Zapisz wszystkie krawędzie w grupie
                    for (int[] edge : edges) {
                        dos.writeInt(edge[0]); // Pierwszy wierzchołek
                        dos.writeInt(edge[1]); // Drugi wierzchołek
                    }
                }
            }
        }
    }
}