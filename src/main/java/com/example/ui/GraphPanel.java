package com.example.ui;

import com.example.model.Graph;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.BasicStroke;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public class GraphPanel extends JPanel {
    private Graph graph;
    private Map<Integer, Point2D> vertexPositions = new HashMap<>();
    private boolean layoutGenerated = false;
    private double zoomLevel = 1.0;
    private Point2D panOffset = new Point2D.Double(0, 0);
    private Point lastMousePos;
    private boolean graphLoaded = false;
    // Mapowanie grup podziału wierzchołków
    private Map<Integer, Integer> vertexGroups = new HashMap<>();
    private boolean partitionApplied = false;
    private List<Color> groupColors = new ArrayList<>();



    // Kolory i rozmiary
    private static final Color VERTEX_COLOR = new Color(54, 128, 210); // SteelBlue
    private static final Color EDGE_COLOR = new Color(206, 203, 203);
    private static final int BASE_VERTEX_SIZE = 5;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setupMouseListeners();
    }

    /**
     * Ustawia dane grafu bezpośrednio, bez potrzeby tworzenia obiektu Graph.
     * Użyteczne gdy mamy już przygotowane dane w odpowiednim formacie.
     *
     * @param vertexCount liczba wierzchołków w grafie
     * @param adjacencyList lista sąsiedztwa zawierająca połączenia między wierzchołkami
     * @param adjacencyIndices indeksy w liście sąsiedztwa dla każdego wierzchołka
     */
    public void setGraphData(int vertexCount, List<Integer> adjacencyList, List<Integer> adjacencyIndices, boolean bool) {
        try {
            Graph tempGraph = new Graph(vertexCount, adjacencyList, adjacencyIndices);
            setGraph(tempGraph);
            this.graphLoaded = bool;
        } catch (Exception e) {
            System.out.println("Błąd podczas ustawiania danych grafu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setGraphData(int vertexCount, List<Integer> adjacencyList, List<Integer> adjacencyIndices) {
        this.setGraphData(vertexCount, adjacencyList, adjacencyIndices, true);
    }


    public void setGraph(Graph graph) {
        if (this.graph != null && graph != null &&
                this.graph.getVertexCount() == graph.getVertexCount()) {
            // Zachowaj stare pozycje
        } else {
            // Wyczyść pozycje tylko jeśli zmienił się graf
            vertexPositions.clear();
            layoutGenerated = false;
        }

        this.graph = graph;
//        this.graphLoaded = (graph != null);  // Ustaw flagę na true, jeśli graf nie jest null

        // Generuj layout od razu przy ustawianiu grafu
        if (!layoutGenerated) {
            generateLayout();
            layoutGenerated = true;
        }

        revalidate();
        repaint();
    }

    /**
     * Zastosowuje wyniki podziału grafu do wizualizacji
     *
     * @param partitionGroups mapa przypisująca wierzchołki do grup (klucz: indeks wierzchołka, wartość: grupa)
     * @param groupCount liczba grup w podziale
     */
    public void applyPartition(Map<Integer, Integer> partitionGroups, int groupCount) {
        // Wyczyść poprzednie dane podziału
        this.vertexGroups.clear();

        // Zastosuj nowy podział
        if (partitionGroups != null) {
            this.vertexGroups.putAll(partitionGroups);
            this.partitionApplied = true;
        } else {
            this.partitionApplied = false;
        }

        // Generowanie kolorów dla grup
        generateGroupColors(groupCount);

        // Odświeżenie widoku
        repaint();
    }
    /**
     * Generuje listę kolorów dla poszczególnych grup podziału
     *
     * @param groupCount liczba grup w podziale
     */
    private void generateGroupColors(int groupCount) {
        groupColors.clear();

        // Generowanie różnych kolorów dla grup
        for (int i = 0; i < groupCount; i++) {
            // Użycie HSB dla równomiernego rozłożenia kolorów
            float hue = (float) i / groupCount;
            Color color = Color.getHSBColor(hue, 0.7f, 0.9f);
            groupColors.add(color);
        }
    }

    /**
     * Zwraca kolor dla określonej grupy
     *
     * @param groupId identyfikator grupy
     * @return kolor przypisany do grupy
     */
    public Color getGroupColor(int groupId) {
        if (groupId >= 0 && groupId < groupColors.size()) {
            return groupColors.get(groupId);
        }
        return VERTEX_COLOR; // domyślny kolor wierzchołka
    }


    // Dodaj również metodę sprawdzającą stan grafu
    public boolean isGraphLoaded() {
        return graphLoaded && graph != null;
    }


    private void generateLayout() {
        try {if (graph == null) return;

            int width = getWidth() > 0 ? getWidth() : 800;
            int height = getHeight() > 0 ? getHeight() : 600;
            int vertexCount = graph.getVertexCount();

            // Dla dużych grafów używamy prostokątnego układu zamiast koła
            if (vertexCount > 1000) {
                generateRectangularLayout(width, height, vertexCount);
            } else {
                generateCircularLayout(width, height, vertexCount);
            }

            // Algorytm force-directed z parametrami dostosowanymi do rozmiaru grafu
            applyForceDirectedLayout(width, height, vertexCount);

            // Końcowe rozproszenie dla lepszej czytelności
            if (vertexCount > 5000) {
                applyAdditionalSpacing(width, height, vertexCount);
            }

            centerGraph(width, height);} catch (Exception e) {
            System.out.println("Błąd podczas generowania layoutu: " + e.getMessage());
            e.printStackTrace();

        }

    }

    private void generateRectangularLayout(int width, int height, int vertexCount) {
        // Oblicz wymiary siatki dla lepszego rozkładu
        double aspectRatio = (double) width / height;
        int cols = (int) Math.ceil(Math.sqrt(vertexCount * aspectRatio));
        int rows = (int) Math.ceil((double) vertexCount / cols);

        // Zwiększ obszar użycia dla lepszego rozproszenia
        double usableWidth = width * 0.85;
        double usableHeight = height * 0.85;
        double startX = (width - usableWidth) / 2;
        double startY = (height - usableHeight) / 2;

        double cellWidth = usableWidth / cols;
        double cellHeight = usableHeight / rows;

        Random rand = new Random(42); // Stały seed dla powtarzalności

        for (int i = 0; i < vertexCount; i++) {
            int row = i / cols;
            int col = i % cols;

            // Dodaj losowe przesunięcie w obrębie komórki
            double randomOffsetX = (rand.nextDouble() - 0.5) * cellWidth * 0.8;
            double randomOffsetY = (rand.nextDouble() - 0.5) * cellHeight * 0.8;

            double x = startX + col * cellWidth + cellWidth / 2 + randomOffsetX;
            double y = startY + row * cellHeight + cellHeight / 2 + randomOffsetY;

            vertexPositions.put(i, new Point2D.Double(x, y));
        }
    }

    private void generateCircularLayout(int width, int height, int vertexCount) {
        // Oryginalny układ kołowy dla mniejszych grafów
        double radius = Math.min(width, height) * 0.35;
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        for (int i = 0; i < vertexCount; i++) {
            double angle = 2 * Math.PI * i / vertexCount;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(i, new Point2D.Double(x, y));
        }
    }

    private void applyForceDirectedLayout(int width, int height, int vertexCount) {
        // Dostosowane parametry dla różnych rozmiarów grafów
        double k = Math.sqrt((width * height) / vertexCount);
        int iterations;
        double temperature;
        int skipFactor;

        if (vertexCount > 20000) {
            iterations = Math.min(15, Math.max(5, 100 / (int)Math.sqrt(vertexCount)));
            temperature = width / 12.0;
            skipFactor = Math.max(5, vertexCount / 200);
        } else if (vertexCount > 5000) {
            iterations = Math.min(25, Math.max(10, 200 / (int)Math.sqrt(vertexCount)));
            temperature = width / 8.0;
            skipFactor = Math.max(3, vertexCount / 300);
        } else {
            iterations = Math.min(60, Math.max(30, 300 / (int)Math.sqrt(vertexCount)));
            temperature = width / 6.0;
            skipFactor = Math.max(1, vertexCount / 300);
        }

        double coolingFactor = 0.92;
        double repulsionStrength = k * (vertexCount > 10000 ? 2.0 : 1.2);
        double attractionStrength = 1.0 / (k * (vertexCount > 10000 ? 4.0 : 2.5));

        // Force-directed layout
        for (int iter = 0; iter < iterations; iter++) {
            Map<Integer, Point2D.Double> forces = new HashMap<>();
            for (int i = 0; i < vertexCount; i++) {
                forces.put(i, new Point2D.Double(0, 0));
            }

            // Siły odpychania z większym skipFactor dla dużych grafów
            for (int i = 0; i < vertexCount; i += skipFactor) {
                Point2D v1 = vertexPositions.get(i);
                for (int j = i + skipFactor; j < vertexCount; j += skipFactor) {
                    Point2D v2 = vertexPositions.get(j);
                    double dx = v1.getX() - v2.getX();
                    double dy = v1.getY() - v2.getY();
                    double distSq = dx * dx + dy * dy;
                    double dist = Math.sqrt(distSq);

                    if (dist < 0.1) dist = 0.1;
                    double force = repulsionStrength / dist;

                    dx = (dx / dist) * force;
                    dy = (dy / dist) * force;

                    forces.get(i).x += dx;
                    forces.get(i).y += dy;
                    forces.get(j).x -= dx;
                    forces.get(j).y -= dy;
                }
            }

            // Siły przyciągania tylko dla krawędzi
            ArrayList<Integer> adjacencyList = graph.getAdjacencyList();
            ArrayList<Integer> adjacencyIndices = graph.getAdjacencyIndices();
            int adjIndicesSize = adjacencyIndices.size();
            for (int i = 0; i < vertexCount; i++) {
                if (i >= adjIndicesSize) break;
                Point2D v1 = vertexPositions.get(i);

                // Pobierz indeksy początku i końca listy sąsiedztwa dla wierzchołka i
                int startIdx = (i > 0) ? adjacencyIndices.get(i - 1) : 0;
                int endIdx = adjacencyIndices.get(i);

                // Iteruj przez wszystkich sąsiadów wierzchołka i
                for (int j = startIdx; j < endIdx; j++) {
                    int neighbor = adjacencyList.get(j);
                    if (neighbor > i) { // Unikaj podwójnego liczenia krawędzi
                        Point2D v2 = vertexPositions.get(neighbor);
                        double dx = v1.getX() - v2.getX();
                        double dy = v1.getY() - v2.getY();
                        double dist = Math.sqrt(dx * dx + dy * dy);

                        if (dist > 0.1) {
                            dx *= attractionStrength;
                            dy *= attractionStrength;

                            forces.get(i).x -= dx;
                            forces.get(i).y -= dy;
                            forces.get(neighbor).x += dx;
                            forces.get(neighbor).y += dy;
                        }
                    }
                }
            }

            // Zastosuj siły z ograniczeniem
            double maxMove = temperature * 2;
            for (int i = 0; i < vertexCount; i++) {
                Point2D pos = vertexPositions.get(i);
                Point2D.Double force = forces.get(i);

                // Ograniczenie maksymalnego ruchu
                double magnitude = Math.sqrt(force.x * force.x + force.y * force.y);
                if (magnitude > maxMove) {
                    force.x = (force.x / magnitude) * maxMove;
                    force.y = (force.y / magnitude) * maxMove;
                }

                // Zachowaj w granicach z większym marginesem
                double margin = vertexCount > 10000 ? 0.05 : 0.1;
                double newX = Math.min(Math.max(width * margin, pos.getX() + force.x), width * (1 - margin));
                double newY = Math.min(Math.max(height * margin, pos.getY() + force.y), height * (1 - margin));
                pos.setLocation(newX, newY);
            }

            temperature *= coolingFactor;
        }
    }

    private void applyAdditionalSpacing(int width, int height, int vertexCount) {
        // Dodatkowe rozproszenie dla bardzo dużych grafów
        Map<Integer, Point2D.Double> adjustments = new HashMap<>();
        for (int i = 0; i < vertexCount; i++) {
            adjustments.put(i, new Point2D.Double(0, 0));
        }

        // Znajdź obszary o dużej gęstości i je rozproś
        double gridSize = Math.min(width, height) / 20.0;
        Map<String, Integer> densityMap = new HashMap<>();

        // Policz gęstość w siatce
        for (Point2D pos : vertexPositions.values()) {
            int gridX = (int) (pos.getX() / gridSize);
            int gridY = (int) (pos.getY() / gridSize);
            String key = gridX + "," + gridY;
            densityMap.put(key, densityMap.getOrDefault(key, 0) + 1);
        }

        // Rozproś wierzchołki w gęstych obszarach
        Random rand = new Random(123);
        for (int i = 0; i < vertexCount; i++) {
            Point2D pos = vertexPositions.get(i);
            int gridX = (int) (pos.getX() / gridSize);
            int gridY = (int) (pos.getY() / gridSize);
            String key = gridX + "," + gridY;

            int density = densityMap.getOrDefault(key, 0);
            if (density > 5) { // Jeśli obszar jest gęsty
                double spreadForce = Math.min(50, density * 2);
                double angle = rand.nextDouble() * 2 * Math.PI;
                double dx = Math.cos(angle) * spreadForce;
                double dy = Math.sin(angle) * spreadForce;

                double newX = Math.min(Math.max(width * 0.05, pos.getX() + dx), width * 0.95);
                double newY = Math.min(Math.max(height * 0.05, pos.getY() + dy), height * 0.95);
                pos.setLocation(newX, newY);
            }
        }
    }

    private void centerGraph(int width, int height) {
        // Znajdź środek grafu
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Point2D pos : vertexPositions.values()) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
        }

        double offsetX = (width - (maxX + minX)) / 2;
        double offsetY = (height - (maxY + minY)) / 2;

        // Przesuń wszystkie wierzchołki
        for (Point2D pos : vertexPositions.values()) {
            pos.setLocation(pos.getX() + offsetX, pos.getY() + offsetY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Zapisz oryginalną transformację
        AffineTransform originalTransform = g2d.getTransform();

        // Zastosuj transformację (przesunięcie i przybliżenie)
        AffineTransform transform = new AffineTransform();
        transform.translate(panOffset.getX(), panOffset.getY());
        transform.scale(zoomLevel, zoomLevel);
        g2d.transform(transform);

        // Dla bardzo dużych grafów rysuj mniej krawędzi przy małym zoomie
        if (graph.getVertexCount() > 10000 && zoomLevel < 0.3) {
            drawEdgesSimplified(g2d);
        } else {
            drawEdges(g2d);
        }

        drawVertices(g2d);

        // Przywróć oryginalną transformację
        g2d.setTransform(originalTransform);
    }
    public Map<Integer, Point2D> getVertexPositions() {
        return new HashMap<>(vertexPositions);
    }

    public void setVertexPositions(Map<Integer, Point2D> positions) {
        if (positions != null) {
            this.vertexPositions = new HashMap<>(positions);
            layoutGenerated = true;
            repaint();
        }
    }

    public void clearVisualization() {
        this.graph = null;
        this.vertexPositions.clear();
        this.layoutGenerated = false;
        resetView();
        revalidate();
        repaint();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lastMousePos = e.getPoint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lastMousePos != null) {
                    Point current = e.getPoint();
                    // Oblicz przesunięcie w przestrzeni ekranu
                    double dx = current.x - lastMousePos.x;
                    double dy = current.y - lastMousePos.y;

                    // Przekształć przesunięcie do przestrzeni grafu (uwzględnij skalę)
                    dx /= zoomLevel;
                    dy /= zoomLevel;

                    panOffset.setLocation(
                            panOffset.getX() + dx,
                            panOffset.getY() + dy
                    );

                    lastMousePos = current;
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            // Zapisz pozycję kursora przed przybliżeniem
            Point mousePos = e.getPoint();
            double mouseX = mousePos.x / zoomLevel - panOffset.getX();
            double mouseY = mousePos.y / zoomLevel - panOffset.getY();

            // Zmień poziom przybliżenia
            double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            zoomLevel *= zoomFactor;
            zoomLevel = Math.max(0.05, Math.min(zoomLevel, 10.0));

            // Dostosuj przesunięcie, aby utrzymać kursor w tym samym miejscu grafu
            panOffset.setLocation(
                    mousePos.x / zoomLevel - mouseX,
                    mousePos.y / zoomLevel - mouseY
            );

            repaint();
        });
    }
    public void resetView() {
        zoomLevel = 1.0;
        panOffset.setLocation(0, 0);
        repaint();
    }

    private void drawEdges(Graphics2D g2d) {
        if (graph == null || vertexPositions.isEmpty()) return;

        float strokeWidth = Math.max(0.5f, 1.0f);
        g2d.setStroke(new BasicStroke(strokeWidth));

        List<Integer>[] neighbors = graph.getNeighbors();
        if (neighbors == null) return;

        // Rysowanie krawędzi
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] == null) continue;

            Point2D p1 = vertexPositions.get(i);
            if (p1 == null) continue;

            double x1 = p1.getX();
            double y1 = p1.getY();

            for (int neighbor : neighbors[i]) {
                // Rysuj każdą krawędź tylko raz
                if (neighbor > i) {
                    Point2D p2 = vertexPositions.get(neighbor);
                    if (p2 == null) continue;

                    double x2 = p2.getX();
                    double y2 = p2.getY();

                    // Ustawienie koloru krawędzi
                    if (partitionApplied && vertexGroups.containsKey(i) && vertexGroups.containsKey(neighbor)) {
                        int group1 = vertexGroups.get(i);
                        int group2 = vertexGroups.get(neighbor);

                        if (group1 == group2) {
                            g2d.setColor(getGroupColor(group1).darker());
                        } else {
                            g2d.setColor(Color.RED);
                        }
                    } else {
                        g2d.setColor(EDGE_COLOR);
                    }

                    g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
                }
            }
        }
    }


    private void drawEdgesSimplified(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200, 128));
        ArrayList<Integer> adjacencyList = graph.getAdjacencyList();
        ArrayList<Integer> adjacencyIndices = graph.getAdjacencyIndices();
        int vertexCount = graph.getVertexCount();
        int skipFactor = Math.max(1, (int)(vertexCount / 5000));

        for (int i = 0; i < vertexCount; i += skipFactor) {
            Point2D p1 = vertexPositions.get(i);
            if (p1 == null) continue;

            int startIdx = (i > 0) ? adjacencyIndices.get(i - 1) : 0;
            int endIdx = adjacencyIndices.get(i);

            for (int j = startIdx; j < endIdx; j += skipFactor) {
                if (j < adjacencyList.size()) {
                    int neighbor = adjacencyList.get(j);
                    if (neighbor % skipFactor == 0) {
                        Point2D p2 = vertexPositions.get(neighbor);
                        if (p2 != null) {
                            g2d.drawLine(
                                    (int) p1.getX(), (int) p1.getY(),
                                    (int) p2.getX(), (int) p2.getY()
                            );
                        }
                    }
                }
            }
        }
    }    /**
     * Resetuje podział grafu i przywraca domyślną wizualizację
     */
    public void resetPartition() {
        partitionApplied = false;
        vertexGroups.clear();
        repaint();
    }

    /**
     * Zwraca informację czy graf ma zastosowany podział
     *
     * @return true jeśli podział został zastosowany, false w przeciwnym razie
     */
    public boolean isPartitionApplied() {
        return partitionApplied;
    }

    /**
     * Eksportuje dane podziału do formatu umożliwiającego dalsze przetwarzanie
     *
     * @return mapa wierzchołków i ich grup
     */
    public Map<Integer, Integer> getPartitionData() {
        return new HashMap<>(vertexGroups);
    }
    private void drawVertices(Graphics2D g2d) {
        if (graph == null || vertexPositions.isEmpty()) return;

        int vertexCount = graph.getVertexCount();
        int vertexSize = (int) (BASE_VERTEX_SIZE);

        for (int i = 0; i < vertexCount; i++) {
            Point2D pos = vertexPositions.get(i);
            if (pos != null) {
                int x = (int) pos.getX();
                int y = (int) pos.getY();

                // Wybór koloru w zależności od zastosowanego podziału
                if (partitionApplied && vertexGroups.containsKey(i)) {
                    int groupId = vertexGroups.get(i);
                    g2d.setColor(getGroupColor(groupId));
                } else {
                    g2d.setColor(VERTEX_COLOR);
                }

                g2d.fillOval(x - vertexSize, y - vertexSize, 2 * vertexSize, 2 * vertexSize);

                // Rysowanie obramowania wierzchołka
                g2d.setColor(Color.BLACK);
                g2d.drawOval(x - vertexSize, y - vertexSize, 2 * vertexSize, 2 * vertexSize);
            }
        }
    }
    /**
     * Eksportuje kolory grup do wykorzystania w innych komponentach
     *
     * @return lista kolorów przypisanych do grup
     */
    public List<Color> getGroupColors() {
        return new ArrayList<>(groupColors);
    }


}