package com.example.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class PreferencesForm {
    private JPanel prefPanel;
    private JLabel resolutionLabel;
    private JComboBox<String> resolutionComboBox;
    private JLabel themeLabel;
    private JComboBox<String> themeComboBox;
    private JComboBox<String> languageComboBox;
    private JLabel languageLabel;
    private JButton backButton;

    public PreferencesForm() {
        // Ustawiamy dostępne języki

        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) languageComboBox.getSelectedItem();
                if ("Polski".equals(selected)) {
                    LanguageManager.setLanguage(Locale.forLanguageTag("pl"));
                } else if ("English".equals(selected)) {
                    LanguageManager.setLanguage(Locale.ENGLISH);
                }
                applyLanguage();
            }
        });

        applyLanguage(); // Ustawienie tekstów na start
    }

    public void applyLanguage() {
        resolutionLabel.setText(LanguageManager.get("label.resolution"));
        themeLabel.setText(LanguageManager.get("label.theme"));
        languageLabel.setText(LanguageManager.get("label.language"));
        backButton.setText(LanguageManager.get("button.back"));

        // Możesz też ustawić wartości ComboBoxów, jeśli muszą być przetłumaczone
    }

    // Gettery
    public JPanel getPrefPanel() {
        return prefPanel;
    }

    public JComboBox<String> getResolutionComboBox() {
        return resolutionComboBox;
    }

    public JComboBox<String> getThemeComboBox() {
        return themeComboBox;
    }

    public JComboBox<String> getLanguageComboBox() {
        return languageComboBox;
    }

    public JButton getBackButton() {
        return backButton;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}