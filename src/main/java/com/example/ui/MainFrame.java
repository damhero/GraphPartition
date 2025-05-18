package com.example.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame {
    private JPanel mainPanel;
    private JSlider slider1;
    private JSlider slider2;
    private JLabel marginLabel;
    private JLabel subGraphsLabel;
    private JButton wykonajPodziałButton;
    private JButton resetujWidokButton;

    public MainFrame() {
        // Slider 1
        if (slider1 != null && marginLabel != null) {
            marginLabel.setText(String.valueOf(slider1.getValue()));
            slider1.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = slider1.getValue();
                    marginLabel.setText(String.valueOf(value));
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
        if (wykonajPodziałButton != null) {
            wykonajPodziałButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Wykonano podział"));
        }

        if (resetujWidokButton != null) {
            resetujWidokButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Zresetowano widok"));
        }
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
}