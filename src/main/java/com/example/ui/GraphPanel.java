package com.example.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private List<Integer> adjacencyList;
    private List<Integer> adjacencyIndices;

    public void setGraphData(List<Integer> adjacencyList, List<Integer> adjacencyIndices) {
        this.adjacencyList = adjacencyList;
        this.adjacencyIndices = adjacencyIndices;

        if (adjacencyIndices == null || adjacencyIndices.size() < 2) {
            throw new IllegalArgumentException("Za mało danych w adjacencyIndices.");
        }

        int maxIndex = adjacencyIndices.stream().mapToInt(i -> i).max().orElse(0);
        if (adjacencyList.size() < maxIndex) {
            throw new IllegalArgumentException("adjacencyList ma za mało elementów względem adjacencyIndices (max index = " + maxIndex + ").");
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (adjacencyList == null || adjacencyIndices == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int nodeCount = adjacencyIndices.size() - 1;
        int radius = 10;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        int circleRadius = Math.min(panelWidth, panelHeight) / 3;
        System.out.printf("Rysuję %d wierzchołków i %d krawędzi\n", nodeCount, adjacencyList.size());
        Point[] nodePositions = new Point[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount;
            int x = (int) (centerX + circleRadius * Math.cos(angle));
            int y = (int) (centerY + circleRadius * Math.sin(angle));
            nodePositions[i] = new Point(x, y);
        }


        for (int i = 0; i < nodeCount; i++) {
            int start = adjacencyIndices.get(i);
            int end = adjacencyIndices.get(i + 1);

            if (start < 0 || end > adjacencyList.size()) {
                System.err.println("Błąd indeksu: i = " + i + ", start = " + start + ", end = " + end + ", adjacencyList.size() = " + adjacencyList.size());
                continue;
            }

            for (int j = start; j < end; j++) {
                int neighbor = adjacencyList.get(j);

               

                if (i < neighbor) { // uniknij podwójnego rysowania
                    g2.drawLine(nodePositions[i].x, nodePositions[i].y, nodePositions[neighbor].x, nodePositions[neighbor].y);
                }
            }
        }

        // Rysuj wierzchołki
        g2.setColor(Color.BLUE);
        for (int i = 0; i < nodeCount; i++) {
            Point p = nodePositions[i];
            g2.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
        }
    }
    public void resetGraph() {
        // Wyczyść dane grafu
        this.adjacencyList = null;
        this.adjacencyIndices = null;
        repaint();
    }
}