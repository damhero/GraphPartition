package com.example.model;

import com.example.utils.LanguageManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class PartitionAlg {
    private ArrayList<Integer> verticiesPart; // Tablica przypisująca wierzchołek do podgrafu
    private int numParts; // Liczba części, podstawowo 2
    private int margin; // Margines błędu w procentach, podstawowo 10

    // Wywołanie z argumentami
    public PartitionAlg(Graph graph, int numParts, int margin) {
        this.numParts = numParts;
        this.margin = margin;
        this.verticiesPart = new ArrayList<Integer>();
        for (int i = 0; i < graph.getVertexCount(); i++) {
            this.verticiesPart.add(0); // Inicjalizacja tablicy zerami
        }
        this.spectralPartition(graph);
    }

    // Przeciążanie dla wywołania bez argumentów
    public PartitionAlg(Graph graph) {
        this(graph, 2, 10);
    }

    public PartitionAlg(Graph graph, int numParts) {
        this(graph, numParts, 10);
    }

    private void spectralPartition(Graph graph) {
        int V = graph.getVertexCount();
        try {
            System.out.println("Rozpoczynam podział spektralny grafu o " + V + " wierzchołkach...");

            double[] fielder = computeFiedlerVectorImplicit(graph, V);

            // Przygotowanie struktury do sortowania wierzchołków
            ArrayList<IndexedValue> values = new ArrayList<IndexedValue>();
            for (int i = 0; i < V; i++) {
                values.add(new IndexedValue(i, fielder[i]));
            }

            // Sortowanie wartości według wektora Fiedlera
            values.sort(IndexedValue::compareTo);

            // Dla dwóch części, próbujemy znaleźć optymalny punkt podziału
            if (numParts == 2) {
                findOptimalBipartition(graph, values, V);
            } else {
                // Dla więcej niż dwóch części, używamy standardowego podziału
                int baseSize = V / numParts;
                int remainder = V % numParts;

                int currentPosition = 0;
                for (int p = 0; p < numParts; p++) {
                    int partSize = baseSize + (p < remainder ? 1 : 0);
                    for (int i = 0; i < partSize; i++) {
                        this.verticiesPart.set(values.get(currentPosition + i).getIndex(), p);
                    }
                    currentPosition += partSize;
                }
            }

            this.evaluatePartition(graph, margin);

        } catch (Exception e) {
            System.err.println("[!] Błąd podczas podziału w SpectralPartition: " + e + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Metoda znajdująca optymalny punkt podziału dla dwóch części
    private void findOptimalBipartition(Graph graph, ArrayList<IndexedValue> sortedValues, int V) {
        double idealSize = V / 2.0;
        double allowedDeviation = (margin / 100.0) * idealSize;
        int minCutSize = (int)Math.ceil(idealSize - allowedDeviation);
        int maxCutSize = (int)Math.floor(idealSize + allowedDeviation);

        // Zapewniamy, że minCutSize i maxCutSize są w granicach [1, V-1]
        minCutSize = Math.max(1, minCutSize);
        maxCutSize = Math.min(V - 1, maxCutSize);

        int bestCutPoint = V / 2; // Domyślnie dzielimy po prostu w połowie
        int minCutEdges = Integer.MAX_VALUE;

        System.out.println("Testuję punkty podziału od " + minCutSize + " do " + maxCutSize);

        boolean[] tempPartition = new boolean[V];

        // Sprawdzamy różne punkty podziału w ramach dozwolonego marginesu
        for (int cutPoint = minCutSize; cutPoint <= maxCutSize; cutPoint++) {
            for (int i = 0; i < V; i++) {
                int vertexIndex = sortedValues.get(i).getIndex();
                tempPartition[vertexIndex] = (i < cutPoint);
            }

            // Liczymy liczbę przeciętych krawędzi
            int cutEdges = countCutEdges(graph, tempPartition);

            // Jeśli znaleźliśmy lepszy podział, zapamiętujemy go
            if (cutEdges < minCutEdges) {
                minCutEdges = cutEdges;
                bestCutPoint = cutPoint;
            }

            // Informacja o postępie co 10% testów
            if ((cutPoint - minCutSize) % Math.max(1, (maxCutSize - minCutSize) / 10) == 0) {
                System.out.println("Przetestowano punkt " + cutPoint + ", najlepszy cut: " + minCutEdges);
            }
        }

        System.out.println("Znaleziono optymalny punkt: " + bestCutPoint + " z " + minCutEdges + " przecięciami");

        // Zastosuj najlepszy znaleziony podział
        for (int i = 0; i < V; i++) {
            int vertexIndex = sortedValues.get(i).getIndex();
            if (i < bestCutPoint) {
                this.verticiesPart.set(vertexIndex, 0);
            } else {
                this.verticiesPart.set(vertexIndex, 1);
            }
        }
    }

    //Funkcja obliczjąca ilość przeciętych krawędzi
    private int countCutEdges(Graph graph, boolean[] partition) {
        int V = graph.getVertexCount();
        int cutEdges = 0;

        for (int i = 0; i < V; i++) {
            if (i >= graph.getAdjacencyIndices().size() - 1) break;

            int startIdx = graph.getAdjacencyIndices().get(i);
            int endIdx = graph.getAdjacencyIndices().get(i + 1);

            // Sprawdzamy tylko sąsiadów tego wierzchołka
            for (int j = startIdx; j < endIdx; j++) {
                if (j >= graph.getAdjacencyList().size()) break;

                int neighbor = graph.getAdjacencyList().get(j);
                // Jeśli sąsiad w innej partycji, zwiększamy licznik
                if (partition[i] != partition[neighbor]) {
                    cutEdges++;
                }
            }
        }

        return cutEdges / 2; // Dzielimy przez 2, bo każda krawędź jest liczona dwukrotnie
    }

    //Obliczanie wektora Fiedlera bez tworzenia pełnej macierzy
    private static double[] computeFiedlerVectorImplicit(Graph graph, int V) {
        double[] x = new double[V];
        double[] y = new double[V];
        double[] prevX = new double[V];

        // Inicjalizacja wektora losowymi wartościami
        Random rand = new Random();
        for (int i = 0; i < V; i++) {
            x[i] = rand.nextDouble() - 0.5;
        }

        // Usunięcie składowej stałej (ortogonalizacja do pierwszego wektora własnego)
        double sum = 0;
        for (int i = 0; i < V; i++) sum += x[i];
        double avg = sum / V;
        for (int i = 0; i < V; i++) x[i] -= avg;

        // Normalizacja wektora startowego
        double norm = norm(x, V);
        for (int i = 0; i < V; i++) x[i] /= norm;

        // Iteracja potęgowa z deflacją
        int maxIter = 300; //można zwiększyć liczbę iteracji dla większej zbieżności lub zmniejszyć dla szybszego działania
        double EPSILON = 1e-10;
        double lambda = 0.0;

        System.out.println("Rozpoczynam iteracje potęgowe...");
        for (int iter = 0; iter < maxIter; iter++) {
            // Zachowujemy poprzedni wektor x do sprawdzenia zbieżności
            System.arraycopy(x, 0, prevX, 0, V);

            //Mnożenie macierzy "w locie" bez jej tworzenia
            implicitLaplacianMultiply(graph, x, y, V);

            // Usunięcie komponentu wzdłuż pierwszego wektora własnego (wektor stały)
            sum = 0;
            for (int i = 0; i < V; i++) sum += y[i];
            avg = sum / V;
            for (int i = 0; i < V; i++) y[i] -= avg;

            // Obliczenie wartości własnej (przybliżenie)
            double dotProduct = 0;
            for (int i = 0; i < V; i++) {
                dotProduct += x[i] * y[i];
            }
            lambda = dotProduct;

            // Normalizacja
            norm = norm(y, V);
            if (norm < EPSILON) break;

            for (int i = 0; i < V; i++) x[i] = y[i] / norm;

            // Sprawdzenie zbieżności
            double diff = 0;
            for (int i = 0; i < V; i++) {
                diff += Math.abs(x[i] - prevX[i]);
            }

            if (diff < EPSILON) {
                System.out.println("Zbieżność osiągnięta po " + iter + " iteracjach. Lambda = " + lambda);
                break;
            }

            // Informacja o postępie co 50 iteracji
            if (iter % 50 == 0) {
                System.out.println("Iteracja " + iter + ", różnica = " + String.format("%.2e", diff));
            }
        }

        return x;
    }

    //Mnożenie macierzy Laplace'a przez wektor bez tworzenia macierzy
    private static void implicitLaplacianMultiply(Graph graph, double[] x, double[] result, int V) {
        // Wyzeruj wektor wynikowy
        Arrays.fill(result, 0.0);

        // Dla każdego wierzchołka i
        for (int i = 0; i < V; i++) {
            // Oblicz stopień wierzchołka (ile ma sąsiadów)
            int degree = 0;
            if (i < graph.getAdjacencyIndices().size() - 1) {
                int startIdx = graph.getAdjacencyIndices().get(i);
                int endIdx = graph.getAdjacencyIndices().get(i + 1);
                degree = endIdx - startIdx;
            }

            // Dodaj składnik z przekątnej: degree * x[i]
            result[i] += degree * x[i];

            // Odejmij składniki od sąsiadów: -1 * x[sąsiad]
            if (i < graph.getAdjacencyIndices().size() - 1) {
                int startIdx = graph.getAdjacencyIndices().get(i);
                int endIdx = graph.getAdjacencyIndices().get(i + 1);

                for (int j = startIdx; j < endIdx; j++) {
                    if (j < graph.getAdjacencyList().size()) {
                        int neighbor = graph.getAdjacencyList().get(j);
                        result[i] -= x[neighbor]; // zamiast -1 w macierzy Laplace'a
                    }
                }
            }
        }
    }

    // Funkcja obliczająca normę (długość wektora)
    private static double norm(double[] vector, int V) {
        double sum = IntStream.range(0, V).parallel().mapToDouble(i -> vector[i] * vector[i]).sum();
        return Math.sqrt(sum);
    }


    public void evaluatePartition(Graph graph, int margin) {
        int V = graph.getVertexCount();
        int cutEdges = 0;

        int[] partVerticesCounter = new int[numParts];
        for (int p = 0; p < numParts; p++) partVerticesCounter[p] = 0;

        for (int i = 0; i < V; i++) {
            int part = this.verticiesPart.get(i);
            if (part < 0 || part >= numParts) {
                System.err.println("[!] Błąd, wierzchołek przypisany do nieistniejącej części: " + i + " -> " + part);
                System.exit(1);
            }
            partVerticesCounter[part]++;

            if (i >= graph.getAdjacencyIndices().size() - 1) continue;
            int startIdx = graph.getAdjacencyIndices().get(i);
            int endIdx = (i + 1 < graph.getAdjacencyIndices().size())
                    ? graph.getAdjacencyIndices().get(i + 1)
                    : graph.getAdjacencyList().size();

            for (int j = startIdx; j < endIdx; j++) {
                int neighbor = graph.getAdjacencyList().get(j);
                if (neighbor >= 0 && neighbor < V &&
                        this.verticiesPart.get(i) != this.verticiesPart.get(neighbor)) {
                    cutEdges++;
                }
            }
        }

        cutEdges /= 2;

        // === ZAPIS DO PLIKU ===
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File evalFile = new File(outputDir, "partition_eval.txt");
        if (evalFile.exists()) {
            evalFile.deleteOnExit();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(evalFile))) {
            writer.println(LanguageManager.get("analyze.cutedges") + cutEdges); //"Liczba przeciętych krawędzi: "

            double idealVertices = (double) V / numParts;
            double allowedDeviation = (margin / 100.0) * idealVertices;

            boolean ok = true;
            for (int p = 0; p < numParts; p++) {
                double percentage = (partVerticesCounter[p] / idealVertices) * 100;
                writer.printf("%s %d %s: %d (%.2f%%)%n",LanguageManager.get("analyze.part"), p, LanguageManager.get("analyze.vertices"), partVerticesCounter[p], percentage);

                if (Math.abs(partVerticesCounter[p] - idealVertices) > allowedDeviation) {
                    ok = false;
                }
            }

            writer.println(ok ? LanguageManager.get("analyze.goodmargin") : LanguageManager.get("analyze.badmargin"));

        } catch (IOException e) {
            System.err.println("[!] Błąd zapisu ewaluacji: " + e.getMessage());
        }
    }

    public int getMargin(){
        return margin;
    }
}