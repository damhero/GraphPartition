package com.example.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

    public MainView() {
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
        if (divideButton != null) {
            divideButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Wykonano podział"));
        }

        if (resetViewButton != null) {
            resetViewButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Zresetowano widok"));
        }
    }
    public void applyLanguage() {
        numOfDividesLabel.setText(LanguageManager.get("label.subgraphs"));
        marginLabel.setText(LanguageManager.get("label.margin"));
        resetViewButton.setText(LanguageManager.get("button.reset"));
        divideButton.setText(LanguageManager.get("button.divide"));
    }
    public void setAppFrame(AppFrame frame) {
        this.appFrame = frame;
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }
}