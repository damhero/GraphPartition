package com.example;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame {
    private JPanel mainPanel;
    private JSlider slider1;
    private JSlider slider2;
    private JMenuBar menuBar1;
    private JMenu menuPlik;
    private JMenuItem menuItemWczytaj;
    private JLabel marginLabel;
    private JLabel subGraphsLabel;
    private JButton wykonajPodziałButton;
    private JButton resetujWidokButton;

    public MainFrame() {
        // Obsługa menu
        if (menuItemWczytaj != null) {
            menuItemWczytaj.addActionListener(e -> {
                JOptionPane.showMessageDialog(mainPanel, "Kliknięto 'Wczytaj'");
            });
        }

        // Slider 1
        if (slider1 != null && marginLabel != null) {
            // Ustaw wartość początkową
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
            // Ustaw wartość początkową
            marginLabel.setText(String.valueOf(slider2.getValue()));

            slider2.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = slider2.getValue();
                    subGraphsLabel.setText(String.valueOf(value));
                }
            });
        }
    }



    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JMenuBar getMenuBar() {
        return menuBar1;
    }
}