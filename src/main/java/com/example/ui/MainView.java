package com.example.ui;

import com.example.utils.LanguageManager;
import com.example.ui.GraphPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MainView {
    private JPanel mainPanel;
    private JSlider slider1;
    private JSlider slider2;
    private JLabel marginValueLabel;
    private JLabel subGraphsLabel;
    private JButton divideButton;
    private JButton resetViewButton;
    private JLabel numOfDividesLabel;
    private JLabel marginLabel;
    private AppFrame appFrame;
    private JPanel graphContainer;
    private GraphPanel graphPanel;

    public MainView() {
        graphPanel = new GraphPanel();
        setupExampleGraph();

        if (graphContainer != null) {
            graphContainer.setLayout(new BorderLayout());
            graphContainer.add(graphPanel, BorderLayout.CENTER);
        }


        // Slider 1
        if (slider1 != null && marginValueLabel != null) {
            marginValueLabel.setText(String.valueOf(slider1.getValue()));
            slider1.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = slider1.getValue();
                    marginValueLabel.setText(String.valueOf(value));
                }
            });
        }

        // Slider 2
        if (slider2 != null && subGraphsLabel != null) {
            subGraphsLabel.setText(String.valueOf(slider2.getValue()));
            slider2.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = slider2.getValue();
                    subGraphsLabel.setText(String.valueOf(value));
                }
            });
        }

        // Przykładowa obsługa przycisków

        if (resetViewButton != null) {
            resetViewButton.addActionListener(e -> {
                setupExampleGraph();  // przywróć przykładowy trójkąt
                //Początkowe ustawienia sliderów
                slider1.setValue(10);
                slider2.setValue(2);
                JOptionPane.showMessageDialog(mainPanel, LanguageManager.get("dialog.reset"));
            });
        }
    }

    // Przykładowa metoda do ustawienia danych grafu - usuń gdy będziesz mieć prawdziwe dane
    private void setupExampleGraph() {
        // Przykładowy graf: 0-1, 1-2, 2-0 (trójkąt)
        List<Integer> adjacencyList = Arrays.asList(1, 2, 0, 2, 0, 1);
        List<Integer> adjacencyIndices = Arrays.asList(0, 2, 4, 6);

        graphPanel.setGraphData(adjacencyList, adjacencyIndices);
    }

    // Metoda do ustawienia prawdziwych danych grafu
    public void setGraphData(List<Integer> adjacencyList, List<Integer> adjacencyIndices) {
        if (graphPanel != null) {
            graphPanel.setGraphData(adjacencyList, adjacencyIndices);
        }
    }

    public void applyLanguage() {
        if (numOfDividesLabel != null) {
            numOfDividesLabel.setText(LanguageManager.get("label.subgraphs"));
        }
        if (marginLabel != null) {
            marginLabel.setText(LanguageManager.get("label.margin"));
        }
        if (resetViewButton != null) {
            resetViewButton.setText(LanguageManager.get("button.reset"));
        }
        if (divideButton != null) {
            divideButton.setText(LanguageManager.get("button.divide"));
        }
    }

    public void setAppFrame(AppFrame frame) {
        this.appFrame = frame;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
    public JButton getDivideButton() {
        return divideButton;
    }
    public int getSelectedMargin() {
        return slider1.getValue();
    }

    public int getSelectedSubGraphsCount() {
        return slider2.getValue();
    }
}