package com.example.ui;

import com.example.model.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Panel wizualizujący graf po partycjonowaniu, pokazujący kolorowane komponenty.
 * Wykorzystuje podobne mechanizmy nawigacji jak GraphPanel.
 */
public class GraphPostPartitionPanel extends JPanel {
    private Graph graph;
    private List<PartitionInfo> partitions;
    private Map<Integer, Point2D> vertexPositions = new HashMap<>();
    private boolean layoutGenerated = false;
    private AppFrame parentFrame;

    // Stałe wartości konfiguracyjne
    private static final int BASE_VERTEX_SIZE = 5;
    private static final int MAX_VERTEX_SIZE = 15;
    private static final int MIN_VERTEX_SIZE = 2;
    private static final double ZOOM_THRESHOLD_FOR_LABELS = 0.8;
    private static final int VERTEX_THRESHOLD_FOR_LABELS = 1000;

    // Kolory dla komponentów (max 10 kolorów, potem się powtarzają)
    private static final Color[] COMPONENT_COLORS = {
            new Color(64, 8, 175), // fioletowy
            new Color(255, 100, 224), // różowy
            new Color(100, 255, 100), // zielony
            new Color(255, 200, 100), // pomarańczowy
            new Color(200, 100, 255), // fioletowy
            new Color(100, 255, 255), // cyjan
            new Color(255, 100, 255), // magenta
            new Color(255, 255, 100), // żółty
            new Color(150, 150, 150), // szary
            new Color(100, 255, 200)  // miętowy
    };

    // Zmienne stanu widoku
    private double zoomLevel = 1.0;
    private Point2D panOffset = new Point2D.Double(0, 0);
    private Point lastMousePos;
    private Set<Integer> visibleVertices = new HashSet<>();
    private Rectangle2D viewportBounds;

    /**
     * Tworzy nowy panel wizualizacji grafu po partycjonowaniu.
     */
    public GraphPostPartitionPanel(AppFrame parentFrame) {
        this.parentFrame = parentFrame;
        setBackground(Color.WHITE);
        setupMouseListeners();
    }

    /**
     * Klasa reprezentująca informacje o partycji grafu.
     */
    public static class PartitionInfo {
        private Map<Integer, List<Integer>> componentVertices;

        public PartitionInfo(Map<Integer, List<Integer>> componentVertices) {
            this.componentVertices = componentVertices;
        }

        public Map<Integer, List<Integer>> getComponentVertices() {
            return componentVertices;
        }
    }

    /**
     * Interfejs do aktualizacji informacji o widoku grafu.
     */
    public interface PartitionViewListener {
        void updateViewZoomInfo(double zoomLevel, int visibleVertices, int totalVertices);
    }

    private PartitionViewListener viewListener;

    /**
     * Ustawia graf i partycje do wizualizacji.
     * Zachowuje pozycje wierzchołków jeśli graf ma tę samą liczbę wierzchołków.
     */
    public void setGraph(Graph graph, List<PartitionInfo> partitions) {
        if (this.graph != null && graph != null &&
                this.graph.getVertexCount() == graph.getVertexCount()) {
            // Zachowaj stare pozycje
        } else {
            // Wyczyść pozycje tylko jeśli zmienił się graf
            vertexPositions.clear();
            layoutGenerated = false;
        }

        this.graph = graph;
        this.partitions = partitions;

        // Generuj layout od razu przy ustawianiu grafu
        if (!layoutGenerated && graph != null && partitions != null && !partitions.isEmpty()) {
            generateLayout();
            layoutGenerated = true;
        }

        updateViewport();
        revalidate();
        repaint();
    }

    /**
     * Ustawia graf na podstawie danych z AppFrame.
     */
    public void setGraphFromAppFrame() {
        if (parentFrame != null && parentFrame.isGraphLoaded()) {
            int vertexCount = parentFrame.getCurrentVertexCount();
            List<Integer> adjacencyList = parentFrame.getCurrentAdjacencyList();
            List<Integer> adjacencyIndices = parentFrame.getCurrentAdjacencyIndices();

            if (vertexCount > 0 && adjacencyList != null && adjacencyIndices != null) {
                Graph newGraph = new Graph(vertexCount, adjacencyList, adjacencyIndices);
                setGraph(newGraph, this.partitions);
            }
        }
    }

    /**
     * Ustawia partycje grafu.
     */
    public void setPartitions(List<PartitionInfo> partitions) {
        this.partitions = partitions;
        if (graph != null && !layoutGenerated) {
            generateLayout();
            layoutGenerated = true;
        }
        updateViewport();
        repaint();
    }

    /**
     * Generuje layout grafu bazujący na partycjach.
     * Każda partycja jest umieszczona w osobnym obszarze ekranu.
     */
    private void generateLayout() {
        if (graph == null || partitions == null || partitions.isEmpty()) return;

        vertexPositions.clear();
        int width = getWidth() > 0 ? getWidth() : 800;
        int height = getHeight() > 0 ? getHeight() : 600;

        // Użyj komponentów z ostatniego podziału
        Map<Integer, List<Integer>> components = partitions.get(partitions.size()-1).getComponentVertices();
        int componentCount = components.size();
        int cols = (int) Math.ceil(Math.sqrt(componentCount));
        int index = 0;

        for (Map.Entry<Integer, List<Integer>> entry : components.entrySet()) {
            int row = index / cols;
            int col = index % cols;

            double areaX = width * 0.8 / cols;
            double areaY = height * 0.8 / cols;
            double centerX = width * 0.1 + col * areaX + areaX/2;
            double centerY = height * 0.1 + row * areaY + areaY/2;

            // Losowe pozycje w obrębie obszaru komponentu
            for (int v : entry.getValue()) {
                double x = centerX + (Math.random() - 0.5) * areaX * 0.8;
                double y = centerY + (Math.random() - 0.5) * areaY * 0.8;
                vertexPositions.put(v, new Point2D.Double(x, y));
            }
            index++;
        }
    }

    /**
     * Konfiguruje obsługę zdarzeń myszy do nawigacji po grafie.
     */
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
                    double dx = (current.x - lastMousePos.x) / zoomLevel;
                    double dy = (current.y - lastMousePos.y) / zoomLevel;

                    panOffset.setLocation(
                            panOffset.getX() + dx,
                            panOffset.getY() + dy
                    );

                    lastMousePos = current;
                    updateViewport();
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            zoomLevel *= zoomFactor;
            zoomLevel = Math.max(0.05, Math.min(zoomLevel, 10.0));
            updateViewport();
            repaint();
        });
    }

    /**
     * Aktualizuje informacje o widocznym obszarze grafu.
     */
    private void updateViewport() {
        try {
            AffineTransform inverse = viewTransform().createInverse();
            Point2D topLeft = inverse.transform(new Point(0, 0), null);
            Point2D bottomRight = inverse.transform(new Point(getWidth(), getHeight()), null);

            viewportBounds = new Rectangle2D.Double(
                    topLeft.getX(), topLeft.getY(),
                    bottomRight.getX() - topLeft.getX(),
                    bottomRight.getY() - topLeft.getY()
            );

            // Znajdź widoczne wierzchołki
            visibleVertices.clear();
            for (Map.Entry<Integer, Point2D> entry : vertexPositions.entrySet()) {
                if (viewportBounds.contains(entry.getValue())) {
                    visibleVertices.add(entry.getKey());
                }
            }

            if (viewListener != null) {
                viewListener.updateViewZoomInfo(zoomLevel,
                        visibleVertices.size(),
                        graph != null ? graph.getVertexCount() : 0);
            }
        } catch (Exception e) {
            viewportBounds = null;
            visibleVertices.clear();
            if (viewListener != null) {
                viewListener.updateViewZoomInfo(zoomLevel, 0,
                        graph != null ? graph.getVertexCount() : 0);
            }
        }
    }

    /**
     * Zwraca transformację widoku bazującą na przesunięciu i poziomie przybliżenia.
     */
    private AffineTransform viewTransform() {
        AffineTransform transform = new AffineTransform();
        transform.scale(zoomLevel, zoomLevel);
        transform.translate(panOffset.getX(), panOffset.getY());
        return transform;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || partitions == null || partitions.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;

        // Włącz antyaliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Zastosuj transformację widoku
        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(viewTransform());

        // Rysuj graf
        drawEdges(g2d);
        drawVertices(g2d);

        // Przywróć oryginalną transformację
        g2d.setTransform(originalTransform);
    }

    /**
     * Rysuje krawędzie grafu z uwzględnieniem partycji.
     */
    private void drawEdges(Graphics2D g2d) {
        if (graph == null || partitions == null || partitions.isEmpty()) return;

        PartitionInfo lastPartition = partitions.get(partitions.size()-1);
        Map<Integer, List<Integer>> components = lastPartition.getComponentVertices();
        List<Integer>[] neighbors = graph.getNeighbors();

        // Stwórz mapę vertex -> component dla łatwiejszego dostępu
        Map<Integer, Integer> vertexToComponent = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : components.entrySet()) {
            for (Integer vertex : entry.getValue()) {
                vertexToComponent.put(vertex, entry.getKey());
            }
        }

        g2d.setStroke(new BasicStroke(Math.max(0.5f, (float)(1.0f / Math.sqrt(zoomLevel)))));

        for (int i = 0; i < neighbors.length; i++) {
            Point2D p1 = vertexPositions.get(i);
            if (p1 == null || !visibleVertices.contains(i)) continue;

            Color edgeColor = getComponentColor(vertexToComponent.getOrDefault(i, 0)).darker();
            g2d.setColor(edgeColor);

            for (int neighbor : neighbors[i]) {
                if (neighbor > i) { // Rysuj każdą krawędź tylko raz
                    Point2D p2 = vertexPositions.get(neighbor);
                    if (p2 != null && visibleVertices.contains(neighbor)) {
                        g2d.drawLine(
                                (int) p1.getX(), (int) p1.getY(),
                                (int) p2.getX(), (int) p2.getY()
                        );
                    }
                }
            }
        }
    }

    /**
     * Rysuje wierzchołki grafu z uwzględnieniem partycji.
     */
    private void drawVertices(Graphics2D g2d) {
        if (graph == null || partitions == null || partitions.isEmpty()) return;

        PartitionInfo lastPartition = partitions.get(partitions.size()-1);
        Map<Integer, List<Integer>> components = lastPartition.getComponentVertices();
        int vertexSize = calculateVertexSize();

        // Stwórz mapę vertex -> component dla łatwiejszego dostępu
        Map<Integer, Integer> vertexToComponent = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : components.entrySet()) {
            for (Integer vertex : entry.getValue()) {
                vertexToComponent.put(vertex, entry.getKey());
            }
        }

        // Ustaw czcionkę tylko jeśli będziemy rysować etykiety
        boolean drawLabels = zoomLevel > ZOOM_THRESHOLD_FOR_LABELS && graph.getVertexCount() < VERTEX_THRESHOLD_FOR_LABELS;
        if (drawLabels) {
            g2d.setFont(g2d.getFont().deriveFont((float)(8 / Math.sqrt(zoomLevel))));
        }

        for (Map.Entry<Integer, Point2D> entry : vertexPositions.entrySet()) {
            int vertex = entry.getKey();
            Point2D pos = entry.getValue();
            if (pos != null && visibleVertices.contains(vertex)) {
                Color vertexColor = getComponentColor(vertexToComponent.getOrDefault(vertex, 0));
                g2d.setColor(vertexColor);
                g2d.fillOval(
                        (int) (pos.getX() - vertexSize/2),
                        (int) (pos.getY() - vertexSize/2),
                        vertexSize, vertexSize
                );

                // Obramowanie wierzchołka
                g2d.setColor(vertexColor.darker());
                g2d.drawOval(
                        (int) (pos.getX() - vertexSize/2),
                        (int) (pos.getY() - vertexSize/2),
                        vertexSize, vertexSize
                );

                // Rysuj etykietę z numerem wierzchołka jeśli zoom jest wystarczająco duży
                if (drawLabels) {
                    g2d.setColor(Color.BLACK);
                    String label = String.valueOf(vertex);
                    g2d.drawString(label,
                            (int) (pos.getX() + vertexSize/2 + 2),
                            (int) (pos.getY() + vertexSize/2));
                }
            }
        }
    }

    /**
     * Oblicza rozmiar wierzchołka w zależności od poziomu przybliżenia.
     */
    private int calculateVertexSize() {
        // Dynamiczny rozmiar wierzchołka w zależności od zoomu
        double scaledSize = BASE_VERTEX_SIZE / Math.sqrt(zoomLevel);
        return (int) Math.max(MIN_VERTEX_SIZE, Math.min(scaledSize, MAX_VERTEX_SIZE));
    }

    /**
     * Zwraca kolor dla danego komponentu.
     */
    private Color getComponentColor(int componentIndex) {
        return COMPONENT_COLORS[Math.abs(componentIndex) % COMPONENT_COLORS.length];
    }

    /**
     * Zwraca kopię mapy pozycji wierzchołków.
     */
    public Map<Integer, Point2D> getVertexPositions() {
        return new HashMap<>(vertexPositions);
    }

    /**
     * Ustawia pozycje wierzchołków na podstawie przekazanej mapy.
     */
    public void setVertexPositions(Map<Integer, Point2D> positions) {
        if (positions != null) {
            this.vertexPositions = new HashMap<>(positions);
            layoutGenerated = true;
            updateViewport();
            repaint();
        }
    }

    /**
     * Ustawia listener do aktualizacji informacji o widoku.
     */
    public void setViewListener(PartitionViewListener listener) {
        this.viewListener = listener;
    }

    /**
     * Resetuje widok grafu do początkowego stanu.
     */
    public void resetView() {
        zoomLevel = 1.0;
        panOffset.setLocation(0, 0);
        updateViewport();
        repaint();
    }

    /**
     * Aktualizuje layout grafu na podstawie aktualnych partycji.
     */
    public void updateLayout() {
        if (graph != null && partitions != null && !partitions.isEmpty()) {
            generateLayout();
            updateViewport();
            repaint();
        }
    }

    /**
     * Metoda importująca pozycje wierzchołków z GraphPanel.
     * Umożliwia zachowanie układu grafu z poprzedniego widoku.
     */
    public void importPositionsFromGraphPanel(GraphPanel graphPanel) {
        if (graphPanel != null && graph != null) {
            Map<Integer, Point2D> positions = graphPanel.getVertexPositions();
            if (positions != null && !positions.isEmpty()) {
                setVertexPositions(positions);
            }
        }
    }
}